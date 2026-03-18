package com.vti.springdatajpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Face similarity service with optimized cosine calculation
 * and similarity margin check for production use.
 */
@Service
@Slf4j
public class FaceSimilarityService {

    @Value("${face.verify.threshold}")
    private double threshold;

    // Minimum gap between best and second-best similarity to confirm match
    private static final double SIMILARITY_MARGIN = 0.07;

    /**
     * Cosine similarity for L2-normalized vectors.
     * Since vectors are already normalized (|A|=|B|=1), cosine = dot product.
     * This is ~3x faster than full cosine computation.
     */
    public double cosineFast(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dot = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
        }
        return dot;
    }

    /**
     * Full cosine similarity (for non-normalized vectors).
     * similarity(A,B) = (A·B) / (|A| × |B|)
     */
    public double cosine(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dot = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0)
            return 0;

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Check if similarity score passes the verification threshold.
     */
    public boolean isMatch(double similarity) {
        return similarity >= threshold;
    }

    /**
     * Check if the match is confident enough (best - secondBest > margin).
     * Helps avoid confusion when multiple embeddings have similar scores.
     */
    public boolean isConfidentMatch(double bestSimilarity, double secondBestSimilarity) {
        if (!isMatch(bestSimilarity))
            return false;
        return (bestSimilarity - secondBestSimilarity) >= SIMILARITY_MARGIN;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getMargin() {
        return SIMILARITY_MARGIN;
    }
}
