package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.FaceEmbeddingDTO;
import com.vti.springdatajpa.dto.FaceRegisterResponse;
import com.vti.springdatajpa.dto.FaceVerifyResponse;
import com.vti.springdatajpa.entity.FaceEmbedding;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.FaceEmbeddingRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.FaceEmbeddingCacheService;
import com.vti.springdatajpa.service.FacePipelineService;
import com.vti.springdatajpa.service.FaceRegistrationService;
import com.vti.springdatajpa.service.FaceSimilarityService;
import com.vti.springdatajpa.service.FaceVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/face")
@Slf4j
public class FaceController {

    // Upload limits
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MAX_IMAGE_WIDTH = 1920;
    private static final int MAX_IMAGE_HEIGHT = 1080;

    @Autowired
    private FaceRegistrationService registrationService;

    @Autowired
    private FaceVerificationService verificationService;

    @Autowired
    private FacePipelineService pipeline;

    @Autowired
    private FaceSimilarityService similarityService;

    @Autowired
    private FaceEmbeddingRepository embeddingRepository;

    @Autowired
    private FaceEmbeddingCacheService cacheService;

    @Autowired
    private UserRepository userRepository;

    // ========================
    // 1. Register Face
    // ========================
    @PostMapping("/register")
    public ResponseEntity<FaceRegisterResponse> registerFace(
            @RequestParam("image") MultipartFile image,
            @RequestParam("userId") Integer userId,
            @RequestParam("pose") String pose) {
        try {
            validateUpload(image);
            Mat img = decodeAndValidateImage(image);
            FaceEmbedding saved = registrationService.registerFace(img, userId, pose);
            img.release();

            return ResponseEntity.ok(new FaceRegisterResponse(
                    saved.getId(), userId, pose, "Face registered successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new FaceRegisterResponse(null, userId, pose, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new FaceRegisterResponse(null, userId, pose, e.getMessage()));
        } catch (Exception e) {
            log.error("Error registering face", e);
            return ResponseEntity.internalServerError().body(
                    new FaceRegisterResponse(null, userId, pose, "Error: " + e.getMessage()));
        }
    }

    // ========================
    // 2. Verify Face
    // ========================
    @PostMapping("/verify")
    public ResponseEntity<FaceVerifyResponse> verifyFace(
            @RequestParam("image") MultipartFile image,
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "deviceId", required = false) String deviceId,
            HttpServletRequest request) {
        try {
            validateUpload(image);
            Mat img = decodeAndValidateImage(image);
            String ip = request.getRemoteAddr();

            FaceVerificationService.VerificationResult result = verificationService.verify(img, userId, ip, deviceId);
            img.release();

            FaceVerifyResponse response = new FaceVerifyResponse(
                    result.similarity(),
                    result.result(),
                    result.matchedPose(),
                    result.threshold(),
                    result.result().equals("PASS")
                            ? "Face verification passed"
                            : "Face verification failed");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new FaceVerifyResponse(0, "FAIL", null, 0, e.getMessage()));
        } catch (Exception e) {
            log.error("Error verifying face", e);
            return ResponseEntity.internalServerError().body(
                    new FaceVerifyResponse(0, "FAIL", null, 0, "Error: " + e.getMessage()));
        }
    }

    // ========================
    // 3. List Embeddings
    // ========================
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<FaceEmbeddingDTO>> listEmbeddings(@PathVariable Integer userId) {
        List<FaceEmbedding> embeddings = embeddingRepository.findByUserId(userId);

        List<FaceEmbeddingDTO> dtos = embeddings.stream()
                .map(e -> new FaceEmbeddingDTO(e.getId(), e.getUserId(), e.getPose(), e.getCreatedAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ========================
    // 4. Delete Embedding
    // ========================
    @DeleteMapping("/{embeddingId}")
    public ResponseEntity<Map<String, String>> deleteEmbedding(@PathVariable Long embeddingId) {
        var embedding = embeddingRepository.findById(embeddingId);
        if (embedding.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Integer userId = embedding.get().getUserId();

        // Evict cache before delete
        cacheService.evict(userId);
        embeddingRepository.deleteById(embeddingId);

        // Check if user has any remaining embeddings
        long remainingCount = embeddingRepository.countByUserId(userId);
        if (remainingCount == 0) {
            // Reset isVerified to false when all faces are deleted
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setVerified(false);
                userRepository.save(user);
                log.info("User {} has no more face embeddings, isVerified reset to false", userId);
            }
        }

        return ResponseEntity.ok(Map.of("message", "Embedding deleted successfully"));
    }

    // ========================
    // 5. Add Embedding (alias)
    // ========================
    @PostMapping("/add")
    public ResponseEntity<FaceRegisterResponse> addFace(
            @RequestParam("image") MultipartFile image,
            @RequestParam("userId") Integer userId,
            @RequestParam("pose") String pose) {
        return registerFace(image, userId, pose);
    }

    // ========================
    // 6. Generate Embedding (utility)
    // ========================
    @PostMapping("/embedding")
    public ResponseEntity<?> generateEmbedding(@RequestParam("file") MultipartFile file) {
        try {
            validateUpload(file);
            Mat img = decodeAndValidateImage(file);
            float[] embedding = pipeline.process(img);
            img.release();
            return ResponseEntity.ok(embedding);
        } catch (Exception e) {
            log.error("Error generating embedding", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================
    // 7. Compare Two Images
    // ========================
    @PostMapping("/compare")
    public ResponseEntity<?> compareFaces(
            @RequestParam("img1") MultipartFile img1,
            @RequestParam("img2") MultipartFile img2) {
        try {
            validateUpload(img1);
            validateUpload(img2);

            Mat image1 = decodeAndValidateImage(img1);
            Mat image2 = decodeAndValidateImage(img2);

            float[] e1 = pipeline.process(image1);
            float[] e2 = pipeline.process(image2);

            image1.release();
            image2.release();

            double similarity = similarityService.cosineFast(e1, e2);
            boolean isMatch = similarityService.isMatch(similarity);

            return ResponseEntity.ok(Map.of(
                    "similarity", similarity,
                    "isMatch", isMatch,
                    "threshold", similarityService.getThreshold()));
        } catch (Exception e) {
            log.error("Error comparing faces", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================
    // Helpers
    // ========================

    /**
     * Validate upload: file size <= 2MB.
     */
    private void validateUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("File too large: %d bytes (max: %d bytes / 2MB)",
                            file.getSize(), MAX_FILE_SIZE));
        }
    }

    /**
     * Decode image and validate resolution <= 1920x1080.
     */
    private Mat decodeAndValidateImage(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);

        if (img.empty()) {
            throw new RuntimeException("Could not decode image file");
        }

        if (img.width() > MAX_IMAGE_WIDTH || img.height() > MAX_IMAGE_HEIGHT) {
            img.release();
            throw new IllegalArgumentException(
                    String.format("Image resolution too large: %dx%d (max: %dx%d)",
                            img.width(), img.height(), MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT));
        }

        return img;
    }
}
