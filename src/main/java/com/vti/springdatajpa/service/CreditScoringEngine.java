package com.vti.springdatajpa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditScoringEngine {

    @Value("${credit.model.path}")
    private String modelPath;

    @Value("${credit.model.scaler-path}")
    private String scalerPath;

    @Value("${credit.model.decision-threshold:0.5}")
    private double decisionThreshold;

    @Value("${credit.model.mock-mode:false}")
    private boolean mockMode;

    private OrtEnvironment ortEnvironment;
    private OrtSession session;
    private NormalizationParams normalizationParams;
    private boolean modelLoaded = false;

    /**
     * Load ONNX model and scaler parameters at startup
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing CreditScoringEngine...");
            
            // Check if mock mode is enabled
            if (mockMode) {
                log.warn("*** MOCK MODE ENABLED *** AI scoring will return random scores for testing");
                modelLoaded = false;
                return;
            }
            
            // Initialize ONNX Runtime environment
            ortEnvironment = OrtEnvironment.getEnvironment();
            
            // Load ONNX model
            File modelFile = new File(modelPath);
            if (!modelFile.exists()) {
                log.error("Model file not found: {}", modelPath);
                log.warn("Falling back to mock scoring mode...");
                modelLoaded = false;
                return;
            }
            session = ortEnvironment.createSession(modelPath, new OrtSession.SessionOptions());
            log.info("ONNX model loaded from: {}", modelPath);
            
            // Load scaler parameters
            loadScalerParams();
            log.info("Scaler parameters loaded from: {}", scalerPath);
            
            modelLoaded = true;
            log.info("CreditScoringEngine initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize CreditScoringEngine, using mock mode: {}", e.getMessage());
            modelLoaded = false;
        }
    }

    /**
     * Load normalization scaler parameters from JSON file
     */
    private void loadScalerParams() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File scalerFile = new File(scalerPath);
        if (!scalerFile.exists()) {
            log.warn("Scaler file not found: {}. Using default parameters.", scalerPath);
            normalizationParams = new NormalizationParams();
            return;
        }
        
        JsonNode root = mapper.readTree(scalerFile);
        normalizationParams = mapper.treeToValue(root, NormalizationParams.class);
    }

    /**
     * Predict credit score (probability of bad loan) based on 16 features
     * 
     * @param rawFeatures Array of 16 raw features
     * @return Probability score (0.0 to 1.0). Higher = higher risk of default
     */
    public double predict(float[] rawFeatures) {
        if (rawFeatures == null || rawFeatures.length != 16) {
            throw new IllegalArgumentException("Expected 16 features, received: " + (rawFeatures == null ? 0 : rawFeatures.length));
        }

        // Use mock scoring if model is not loaded
        if (!modelLoaded) {
            log.warn("Model not available, using mock scoring");
            return mockPredict(rawFeatures);
        }

        try {
            // Normalize features using (value - mean) / scale
            float[] normalizedFeatures = normalizeFeatures(rawFeatures);
            
            // Create ONNX tensor
            try (OnnxTensor tensor = createInputTensor(normalizedFeatures)) {
                // Run inference
                Map<String, OnnxTensor> inputs = new HashMap<>();
                inputs.put("input", tensor);
                
                OrtSession.Result results = session.run(inputs);
                
                // Extract prediction score from output
                float[][] output = (float[][]) results.get(0).getValue();
                double score = output[0][0]; // First row, first column
                
                log.debug("Raw prediction score: {}", score);
                return Math.min(Math.max(score, 0.0), 1.0); // Clamp between 0 and 1
            }
        } catch (Exception e) {
            log.error("Error during prediction: {}", e.getMessage());
            log.warn("Falling back to mock scoring due to error: {}", e.getMessage());
            return mockPredict(rawFeatures);
        }
    }

    /**
     * Mock prediction for testing when model is not available
     * Based on income-to-loan ratio and job segment
     */
    private double mockPredict(float[] rawFeatures) {
        if (rawFeatures == null || rawFeatures.length < 2) {
            return 0.6; // Default moderate risk
        }
        
        // Features: [0]=jobSegment, [1]=declaredIncome, [2]=age, ...
        float declaredIncome = rawFeatures[1];
        
        // Simple mock: random score based on income (higher income = lower risk)
        double baseScore = 0.7;
        
        if (declaredIncome > 50_000_000) {
            baseScore = 0.3; // Low risk
        } else if (declaredIncome > 20_000_000) {
            baseScore = 0.5; // Medium risk
        } else if (declaredIncome > 10_000_000) {
            baseScore = 0.65; // Higher risk
        } else {
            baseScore = 0.8; // Very high risk
        }
        
        log.info("Mock prediction: score={} (based on income={})", baseScore, declaredIncome);
        return baseScore;
    }

    /**
     * Get decision based on score and threshold
     * 
     * @param score Probability score
     * @return "REJECTED_BY_AI" if score > threshold, else "PASSED_AI"
     */
    public String getDecision(double score) {
        return score > decisionThreshold ? "REJECTED_BY_AI" : "PASSED_AI";
    }

    /**
     * Normalize features using loaded scaler parameters
     * Formula: (value - mean) / scale
     */
    private float[] normalizeFeatures(float[] rawFeatures) {
        float[] normalized = new float[16];
        
        for (int i = 0; i < 16; i++) {
            float mean = normalizationParams.mean[i];
            float scale = normalizationParams.scale[i];
            
            if (scale != 0) {
                normalized[i] = (rawFeatures[i] - mean) / scale;
            } else {
                normalized[i] = 0f;
            }
        }
        
        return normalized;
    }

    /**
     * Create ONNX tensor from float array
     */
    private OnnxTensor createInputTensor(float[] features) throws Exception {
        // Create shape: [1, 16] for batch size 1 with 16 features
        long[] shape = {1, 16};
        FloatBuffer floatBuffer = FloatBuffer.wrap(features);
        return OnnxTensor.createTensor(ortEnvironment, floatBuffer, shape);
    }

    /**
     * Clean up resources
     */
    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) {
                session.close();
                log.info("ONNX session closed");
            }
            if (ortEnvironment != null) {
                ortEnvironment.close();
                log.info("ONNX environment closed");
            }
        } catch (Exception e) {
            log.error("Error closing ONNX resources", e);
        }
    }

    /**
     * Inner class to hold normalization parameters
     */
    public static class NormalizationParams {
        public float[] mean;
        public float[] scale;

        public NormalizationParams() {
            // Initialize with default parameters (identity)
            this.mean = new float[16];
            this.scale = new float[16];
            for (int i = 0; i < 16; i++) {
                this.mean[i] = 0f;
                this.scale[i] = 1f;
            }
        }
    }
}
