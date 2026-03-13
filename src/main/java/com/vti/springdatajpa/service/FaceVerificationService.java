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

    /**
     * Verify a face against all registered embeddings of a user.
     * Uses cached embeddings, optimized dot-product similarity, and margin check.
     */
    @Transactional
    public VerificationResult verify(Mat image, Integer userId, String ip, String deviceId) throws Exception {
        log.info("Verifying face for user {}", userId);

        // Step 1: Get embedding from input image
        float[] inputEmbedding = pipeline.process(image);

        // Step 2: Load embeddings from cache (fallback to DB)
        List<FaceEmbedding> userEmbeddings = cacheService.getEmbeddings(userId);

        if (userEmbeddings.isEmpty()) {
            throw new RuntimeException("No registered face found for user " + userId);
        }

        // Step 3: Compare with ALL embeddings, find best AND second-best match
        double bestSimilarity = -1;
        double secondBestSimilarity = -1;
        String bestPose = null;

        for (FaceEmbedding stored : userEmbeddings) {
            float[] storedVector = stored.toFloatArray();
            // Use cosineFast (dot product) since vectors are L2-normalized
            double similarity = similarityService.cosineFast(inputEmbedding, storedVector);

            log.debug("Similarity with pose '{}': {}", stored.getPose(), String.format("%.4f", similarity));

            if (similarity > bestSimilarity) {
                secondBestSimilarity = bestSimilarity;
                bestSimilarity = similarity;
                bestPose = stored.getPose();
            } else if (similarity > secondBestSimilarity) {
                secondBestSimilarity = similarity;
            }
        }

        // Step 4: Determine result with margin check
        boolean isMatch = similarityService.isMatch(bestSimilarity);
        boolean isConfident = userEmbeddings.size() == 1
                || similarityService.isConfidentMatch(bestSimilarity, secondBestSimilarity);

        String result = isMatch ? "PASS" : "FAIL";

        log.info("Verification: similarity={}, secondBest={}, result={}, confident={}, pose={}",
                String.format("%.4f", bestSimilarity),
                String.format("%.4f", secondBestSimilarity),
                result, isConfident, bestPose);

        // Step 5: Log verification attempt
        FaceVerificationLog verifyLog = new FaceVerificationLog();
        verifyLog.setUserId(userId);
        verifyLog.setSimilarity(bestSimilarity);
        verifyLog.setResult(result);
        verifyLog.setIp(ip);
        verifyLog.setDeviceId(deviceId);
        logRepository.save(verifyLog);

        return new VerificationResult(bestSimilarity, result, bestPose,
                similarityService.getThreshold(), isConfident);
    }

    /**
     * Verification result with confidence indicator.
     */
    public record VerificationResult(
            double similarity,
            String result,
            String matchedPose,
            double threshold,
            boolean confident) {
    }
}
