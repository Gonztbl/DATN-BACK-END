package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.FaceEmbedding;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.FaceEmbeddingRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class FaceRegistrationService {

    private static final String MODEL_VERSION = "arcface_r100_v1";

    @Autowired
    private FacePipelineService pipeline;

    @Autowired
    private FaceEmbeddingRepository embeddingRepository;

    @Autowired
    private FaceEmbeddingCacheService cacheService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a face: quality → detect → align → embed → save to DB.
     * Now saves metadata (model_version, quality_score, face_angle) for debugging.
     */
    @Transactional
    public FaceEmbedding registerFace(Mat image, Integer userId, String pose) throws Exception {
        log.info("Registering face for user {} with pose '{}'", userId, pose);

        // Validate pose
        if (!isValidPose(pose)) {
            throw new IllegalArgumentException("Invalid pose: " + pose + ". Must be front, left, or right.");
        }

        // Run the full pipeline with metadata
        FacePipelineService.PipelineResult result = pipeline.processWithMetadata(image);

        // Save to database with metadata
        FaceEmbedding faceEmbedding = new FaceEmbedding();
        faceEmbedding.setUserId(userId);
        faceEmbedding.setEmbedding(FaceEmbedding.toJson(result.embedding()));
        faceEmbedding.setPose(pose);
        faceEmbedding.setModelVersion(MODEL_VERSION);
        faceEmbedding.setQualityScore(result.qualityScore());
        faceEmbedding.setFaceAngle(result.faceAngle());

        FaceEmbedding saved = embeddingRepository.save(faceEmbedding);

        // Update user's isVerified status and promote KYC level to 3
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setVerified(true);
            user.setKycLevel(3); // Promote to KYC level 3 when face registration succeeds
            userRepository.save(user);
            log.info("User {} isVerified status updated to true and kycLevel promoted to 3", userId);
        }

        // Invalidate cache for this user
        cacheService.evict(userId);

        log.info("Face registered: id={}, user={}, pose={}, quality={}, angle={}",
                saved.getId(), userId, pose,
                String.format("%.1f", result.qualityScore()),
                String.format("%.1f°", result.faceAngle()));

        return saved;
    }

    private boolean isValidPose(String pose) {
        return "front".equalsIgnoreCase(pose)
                || "left".equalsIgnoreCase(pose)
                || "right".equalsIgnoreCase(pose);
    }
}
