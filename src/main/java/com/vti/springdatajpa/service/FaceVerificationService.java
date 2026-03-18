package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.FaceEmbedding;
import com.vti.springdatajpa.entity.FaceVerificationLog;
import com.vti.springdatajpa.repository.FaceVerificationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class FaceVerificationService {

    @Autowired
    private FacePipelineService pipeline;

    @Autowired
    private FaceSimilarityService similarityService;

    @Autowired
    private FaceEmbeddingCacheService cacheService;

    @Autowired
    private FaceVerificationLogRepository logRepository;

    @Transactional
    public VerificationResult verify(Mat image, Integer userId, String ip, String deviceId) throws Exception {
        log.info("Verifying face for user {}", userId);

        float[] inputEmbedding = pipeline.process(image);
        List<FaceEmbedding> userEmbeddings = cacheService.getEmbeddings(userId);

        if (userEmbeddings.isEmpty()) {
            throw new RuntimeException("No registered face found for user " + userId);
        }

        double bestSimilarity = -1;
        String bestPose = null;

        for (FaceEmbedding stored : userEmbeddings) {
            float[] storedVector = stored.toFloatArray();
            double similarity = similarityService.cosineFast(inputEmbedding, storedVector);
            log.debug("Similarity with pose '{}': {}", stored.getPose(), String.format("%.4f", similarity));

            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestPose = stored.getPose();
            }
        }

        boolean isMatch = similarityService.isMatch(bestSimilarity);
        String result = isMatch ? "PASS" : "FAIL";

        log.info("Verification: similarity={}, result={}, pose={}",
                String.format("%.4f", bestSimilarity), result, bestPose);

        FaceVerificationLog verifyLog = new FaceVerificationLog();
        verifyLog.setUserId(userId);
        verifyLog.setSimilarity(bestSimilarity);
        verifyLog.setResult(result);
        verifyLog.setIp(ip);
        verifyLog.setDeviceId(deviceId);
        logRepository.save(verifyLog);

        return new VerificationResult(bestSimilarity, result, bestPose,
                similarityService.getThreshold(), true);
    }

    public record VerificationResult(
            double similarity,
            String result,
            String matchedPose,
            double threshold,
            boolean confident) {
    }
}