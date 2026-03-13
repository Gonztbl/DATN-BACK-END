package com.vti.springdatajpa.service;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OnnxModelService {

    @Value("${face.model.retina-path}")
    private String retinaModelPath;

    @Value("${face.model.arcface-path}")
    private String arcfaceModelPath;

    private OrtEnvironment env;
    private OrtSession retinaSession;
    private OrtSession arcfaceSession;

    @PostConstruct
    public void init() throws Exception {
        log.info("Loading ONNX models...");

        env = OrtEnvironment.getEnvironment();

        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);

        retinaSession = env.createSession(retinaModelPath, options);
        log.info("RetinaFace model loaded: {}", retinaModelPath);

        arcfaceSession = env.createSession(arcfaceModelPath, options);
        log.info("ArcFace model loaded: {}", arcfaceModelPath);
    }

    @PreDestroy
    public void cleanup() throws Exception {
        if (retinaSession != null)
            retinaSession.close();
        if (arcfaceSession != null)
            arcfaceSession.close();
        log.info("ONNX sessions closed.");
    }

    public OrtEnvironment getEnvironment() {
        return env;
    }

    public OrtSession getRetinaSession() {
        return retinaSession;
    }

    public OrtSession getArcfaceSession() {
        return arcfaceSession;
    }
}
