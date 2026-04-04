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
            log.error("CRITICAL: Failed to initialize CreditScoringEngine: {}", e.getMessage(), e);
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
            String errorMsg = "Credit scoring model is not initialized. Check server logs for startup errors.";
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        try {
            // Log raw features for debugging
            log.info("AI Prediction - Raw Features: {}", java.util.Arrays.toString(rawFeatures));

            // Normalize features using (value - mean) / scale
            float[] normalizedFeatures = normalizeFeatures(rawFeatures);
            log.info("AI Prediction - Normalized Features: {}", java.util.Arrays.toString(normalizedFeatures));
            
            // Create ONNX tensor
            try (OnnxTensor tensor = createInputTensor(normalizedFeatures)) {
                // Run inference
                Map<String, OnnxTensor> inputs = new HashMap<>();
                inputs.put("float_input", tensor);
                
                try (OrtSession.Result results = session.run(inputs)) {
                    // Log all available outputs for debugging
                    log.info("Model Inference completed. Number of outputs: {}", results.size());
                    
                    double score = 0.5;
                    Object firstOutput = null;
                    Object secondOutput = null;
                    
                    int idx = 0;
                    for (var entry : results) {
                        Object val = entry.getValue().getValue();
                        log.info("Output[{}] - Name: {}, Type: {}", idx, entry.getKey(), val.getClass().getName());
                        if (idx == 0) firstOutput = val;
                        if (idx == 1) secondOutput = val;
                        idx++;
                    }

                    // Patterns for ONNX classifiers (Skl2Onnx / ONNXMLTools)
                    // Index 0: Labels (long[] - [J])
                    // Index 1: Probabilities (List<Map<Long, Float>> or float[][])
                    if (results.size() >= 2 && secondOutput != null) {
                        if (secondOutput instanceof float[][]) {
                            float[][] probs = (float[][]) secondOutput;
                            score = probs[0][1]; // Probabilities for class 1 (Target)
                        } else if (secondOutput instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<Map<Long, Float>> probList = (java.util.List<Map<Long, Float>>) secondOutput;
                            if (!probList.isEmpty()) {
                                Map<Long, Float> probMap = probList.get(0);
                                score = probMap.getOrDefault(1L, 0.5f);
                            }
                        }
                        log.info("AI Prediction - Extracted Probability (Class 1): {}", score);
                    } else if (firstOutput != null) {
                        // Possible Regressor or single-output model
                        if (firstOutput instanceof float[][]) {
                            float[][] output = (float[][]) firstOutput;
                            score = output[0][0];
                        }
                        log.info("AI Prediction - Extracted Single Score: {}", score);
                    }
                    
                    return Math.min(Math.max(score, 0.0), 1.0); // Clamp between 0 and 1
                }
            }
        } catch (Exception e) {
            log.error("Error during prediction: {}", e.getMessage(), e);
            throw new RuntimeException("AI Prediction failed: " + e.getMessage());
        }
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
