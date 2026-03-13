package com.vti.springdatajpa.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.Collections;

@Service
@Slf4j
public class FaceEmbeddingService {

    @Autowired
    private OnnxModelService modelService;

    /**
     * Generate a 512-dimensional face embedding vector using ArcFace ONNX model.
     * Input: cropped face image (Mat).
     * Output: float[512] normalized embedding vector.
     */
    public float[] generateEmbedding(Mat face) throws Exception {
        OrtSession session = modelService.getArcfaceSession();
        OrtEnvironment env = modelService.getEnvironment();

        // Resize face to 112x112 (ArcFace input size)
        Mat resized = new Mat();
        Imgproc.resize(face, resized, new Size(112, 112));

        // Convert to CHW format: [1, 3, 112, 112]
        // Normalize: (pixel - 127.5) / 128.0
        float[] inputData = new float[1 * 3 * 112 * 112];
        int channelSize = 112 * 112;

        for (int y = 0; y < 112; y++) {
            for (int x = 0; x < 112; x++) {
                double[] pixel = resized.get(y, x);
                int idx = y * 112 + x;
                // BGR → RGB, normalize
                inputData[0 * channelSize + idx] = ((float) pixel[2] - 127.5f) / 128.0f; // R
                inputData[1 * channelSize + idx] = ((float) pixel[1] - 127.5f) / 128.0f; // G
                inputData[2 * channelSize + idx] = ((float) pixel[0] - 127.5f) / 128.0f; // B
            }
        }

        OnnxTensor inputTensor = OnnxTensor.createTensor(env,
                FloatBuffer.wrap(inputData),
                new long[] { 1, 3, 112, 112 });

        String inputName = session.getInputNames().iterator().next();

        session.getInputInfo().forEach((k,v)->{
            System.out.println("INPUT: "+k+" -> "+v.getInfo());
        });

        session.getOutputInfo().forEach((k,v)->{
            System.out.println("OUTPUT: "+k+" -> "+v.getInfo());
        });

        OrtSession.Result result = session.run(Collections.singletonMap(inputName, inputTensor));

        // ArcFace output: [1, 512]
        float[][] embedding = (float[][]) result.get(0).getValue();
        float[] vector = embedding[0];

        // L2 normalize the embedding
        vector = l2Normalize(vector);

        inputTensor.close();
        result.close();
        resized.release();

        log.info("Generated face embedding: {} dimensions", vector.length);
        return vector;
    }

    /**
     * L2 normalize a vector for better cosine similarity comparison.
     */
    private float[] l2Normalize(float[] vector) {
        float norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);

        if (norm > 0) {
            float[] normalized = new float[vector.length];
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = vector[i] / norm;
            }
            return normalized;
        }
        return vector;
    }
}
