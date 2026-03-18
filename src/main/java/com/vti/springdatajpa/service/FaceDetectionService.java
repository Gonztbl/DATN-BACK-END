package com.vti.springdatajpa.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.*;

@Service
@Slf4j
public class FaceDetectionService {

    @Autowired
    private OnnxModelService modelService;

    private static final float CONFIDENCE_THRESHOLD = 0.73f;
    private static final int MIN_FACE_SIZE = 80;

    public static class DetectionResult {
        public Rect bbox;
        public float confidence;
        public Point[] landmarks; // [5] points
        public double faceAngle;

        public DetectionResult(Rect bbox, float confidence, Point[] landmarks) {
            this.bbox = bbox;
            this.confidence = confidence;
            this.landmarks = landmarks;
            this.faceAngle = 0;

            if (landmarks != null && landmarks.length >= 2) {
                double dx = landmarks[1].x - landmarks[0].x;
                double dy = landmarks[1].y - landmarks[0].y;
                this.faceAngle = Math.toDegrees(Math.atan2(dy, dx));
            }
        }
    }

    public DetectionResult detectFace(Mat image) throws Exception {
        OrtSession session = modelService.getRetinaSession();
        OrtEnvironment env = modelService.getEnvironment();

        int origWidth = image.width();
        int origHeight = image.height();

        Mat resized = new Mat();
        Imgproc.resize(image, resized, new Size(640, 640));

        int height = 640;
        int width = 640;

        float[] inputData = new float[height * width * 3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] pixel = resized.get(y, x);
                int idx = (y * width + x) * 3;
                // BGR to RGB and normalize to [-1, 1]
                inputData[idx] = (float) ((pixel[2] - 127.5f) / 128.0f); // R
                inputData[idx + 1] = (float) ((pixel[1] - 127.5f) / 128.0f); // G
                inputData[idx + 2] = (float) ((pixel[0] - 127.5f) / 128.0f); // B
            }
        }

        OnnxTensor tensor = OnnxTensor.createTensor(env,
                FloatBuffer.wrap(inputData),
                new long[]{1, 640, 640, 3});
        String inputName = session.getInputNames().iterator().next();

        // Debug input/output shapes
        session.getInputInfo().forEach((k, v) ->
                log.debug("INPUT: {} -> {}", k, v.getInfo()));
        OrtSession.Result result = session.run(Collections.singletonMap(inputName, tensor));
        for (Map.Entry<String, ai.onnxruntime.OnnxValue> entry : result) {
            log.debug("OUTPUT: {} -> {}", entry.getKey(), entry.getValue().getInfo());
        }

        List<DetectionResult> detections = parseAllDetections(result, origWidth, origHeight);

        if (!detections.isEmpty()) {
            float maxConf = detections.stream().map(d -> d.confidence).max(Float::compare).orElse(0f);
            log.info("Max raw confidence found: {}", maxConf);
        }

        tensor.close();
        result.close();
        resized.release();

        if (detections.isEmpty()) {
            throw new RuntimeException("No face detected");
        }

        List<DetectionResult> highConfidence = new ArrayList<>();
        for (DetectionResult det : detections) {
            if (det.confidence >= CONFIDENCE_THRESHOLD) {
                highConfidence.add(det);
            }
        }

        if (highConfidence.isEmpty()) {
            throw new RuntimeException("No face detected with confidence >= " + CONFIDENCE_THRESHOLD);
        }

        DetectionResult best;
        if (highConfidence.size() > 1) {
            highConfidence.sort((a, b) -> Float.compare(b.confidence, a.confidence));
            best = highConfidence.get(0);
            log.warn("Multiple high-confidence faces ({}) detected, selected highest score: {}",
                    highConfidence.size(), best.confidence);
        } else {
            best = highConfidence.get(0);
        }

        if (best.bbox.width < MIN_FACE_SIZE || best.bbox.height < MIN_FACE_SIZE) {
            throw new RuntimeException(String.format("Face too small: %dx%d px (min: %dx%d px)",
                    best.bbox.width, best.bbox.height, MIN_FACE_SIZE, MIN_FACE_SIZE));
        }

        if (Math.abs(best.faceAngle) > 30.0) {
            throw new RuntimeException(String.format("Face angle too large: %.1f° (max ±30°)", best.faceAngle));
        }

        log.info("Face detected bbox=({}, {}, {}, {}) conf={} angle={}°",
                best.bbox.x, best.bbox.y, best.bbox.width, best.bbox.height,
                best.confidence, best.faceAngle);

        return best;
    }

    // ==================== RETINAFACE ANCHOR-BASED PARSER ====================

    private static final int[] STRIDES = {32, 16, 8};
    private static final int[][] ANCHOR_SIZES = {
            {512, 256}, // stride 32
            {128, 64},  // stride 16
            {32, 16}    // stride 8
    };
    private static final float PRE_NMS_THRESHOLD = 0.3f;
    private static final float NMS_THRESHOLD = 0.55f;

    // --- Tên output cần khớp với log thực tế ---
    private static final String[] CLS_NAMES = {
            "tf.compat.v1.transpose_1",  // stride 32, shape [1,20,20,4]
            "tf.compat.v1.transpose_3",  // stride 16, shape [1,40,40,4]
            "tf.compat.v1.transpose_5"   // stride 8,  shape [1,80,80,4]
    };
    private static final String[] BBOX_NAMES = {
            "face_rpn_bbox_pred_stride32", // shape [1,20,20,8]
            "face_rpn_bbox_pred_stride16", // shape [1,40,40,8]
            "face_rpn_bbox_pred_stride8"   // shape [1,80,80,8]
    };
    private static final String[] LANDMARK_NAMES = {
            "face_rpn_landmark_pred_stride32", // shape [1,20,20,20]
            "face_rpn_landmark_pred_stride16", // shape [1,40,40,20]
            "face_rpn_landmark_pred_stride8"   // shape [1,80,80,20]
    };

    private List<DetectionResult> parseAllDetections(OrtSession.Result result, int origWidth, int origHeight) {
        List<DetectionResult> allDetections = new ArrayList<>();
        float scaleX = (float) origWidth / 640.0f;
        float scaleY = (float) origHeight / 640.0f;

        try {
            Map<String, float[][][][]> outputMap = new HashMap<>();
            for (Map.Entry<String, ai.onnxruntime.OnnxValue> entry : result) {
                String name = entry.getKey();
                Object value = entry.getValue().getValue();
                if (value instanceof float[][][][]) {
                    outputMap.put(name, (float[][][][]) value);
                }
            }

            float maxScoreOverall = 0f;

            for (int s = 0; s < STRIDES.length; s++) {
                int stride = STRIDES[s];
                int fmSize = 640 / stride;

                float[][][][] clsTensor = outputMap.get(CLS_NAMES[s]);
                float[][][][] bboxTensor = outputMap.get(BBOX_NAMES[s]);
                float[][][][] lmTensor = outputMap.get(LANDMARK_NAMES[s]);

                if (clsTensor == null || bboxTensor == null) {
                    log.warn("Missing tensor for stride {}", stride);
                    continue;
                }

                int numAnchors = ANCHOR_SIZES[s].length; // 2
                int[] anchorSizes = ANCHOR_SIZES[s];

                for (int fy = 0; fy < fmSize; fy++) {
                    for (int fx = 0; fx < fmSize; fx++) {
                        for (int a = 0; a < numAnchors; a++) {
                            // Mỗi anchor có 2 channels (bg, face)
                            int base = a * 2;

                            if (clsTensor[0][fy][fx].length < base + 2) {
                                log.error("Unexpected classification channels: expected at least {}, got {}",
                                        base + 2, clsTensor[0][fy][fx].length);
                                continue;
                            }

                            float logit_bg = clsTensor[0][fy][fx][base];
                            float logit_face = clsTensor[0][fy][fx][base + 1];

                            // Softmax 2-class
                            float score_sigmoid = (float) (1.0 / (1.0 + Math.exp(-logit_face)));
                            float score = score_sigmoid;

                            // Debug high scores
                            if (score > 0.6f) {
                                log.info("Stride={} anchor={} grid({},{}) | raw_cls=[{:.3f}, {:.3f}] | score={:.4f}",
                                        stride, a, fx, fy, logit_bg, logit_face, score);
                            }

                            if (score > maxScoreOverall) {
                                maxScoreOverall = score;
                            }

                            if (score < PRE_NMS_THRESHOLD) continue;

                            // --- Anchor center and size ---
                            float anchorCx = (fx + 0.5f) * stride;
                            float anchorCy = (fy + 0.5f) * stride;
                            float anchorW = anchorSizes[a];
                            float anchorH = anchorSizes[a];

                            // --- Decode bbox (dx, dy, dw, dh) with variance [0.1, 0.1, 0.2, 0.2] ---
                            int bboxBase = a * 4;
                            float dx = bboxTensor[0][fy][fx][bboxBase];
                            float dy = bboxTensor[0][fy][fx][bboxBase + 1];
                            float dw = bboxTensor[0][fy][fx][bboxBase + 2];
                            float dh = bboxTensor[0][fy][fx][bboxBase + 3];

                            float cx = anchorCx + dx * 0.1f * anchorW;
                            float cy = anchorCy + dy * 0.1f * anchorH;
                            float w = anchorW * (float) Math.exp(dw * 0.2f);
                            float h = anchorH * (float) Math.exp(dh * 0.2f);

                            // Loại bỏ box quá nhỏ hoặc ở biên
                            if (w < 30 || h < 30) continue;
                            float area = w * h;
                            if (area < 900) continue;
                            if (cx < 150 || cx > 490 || cy < 150 || cy > 490) {
                                if (score > 0.65f) {
                                    log.debug("Strong border ignored: score={} center=({}, {})", score, cx, cy);
                                }
                                continue;
                            }
                            float aspect = Math.max(w, h) / Math.min(w, h);
                            if (aspect > 2.5f || aspect < 0.4f) continue;

                            float x1 = (cx - w / 2f) * scaleX;
                            float y1 = (cy - h / 2f) * scaleY;
                            float x2 = (cx + w / 2f) * scaleX;
                            float y2 = (cy + h / 2f) * scaleY;

                            int bx1 = Math.max(0, (int) x1);
                            int by1 = Math.max(0, (int) y1);
                            int bx2 = Math.min(origWidth, (int) x2);
                            int by2 = Math.min(origHeight, (int) y2);

                            int bw = bx2 - bx1;
                            int bh = by2 - by1;
                            if (bw < 10 || bh < 10) continue;

                            Rect bbox = new Rect(bx1, by1, bw, bh);

                            // --- Decode landmarks ---
                            Point[] landmarks = null;
                            if (lmTensor != null) {
                                landmarks = new Point[5];
                                for (int lp = 0; lp < 5; lp++) {
                                    int lmBase = a * 10 + lp * 2;
                                    float lx = lmTensor[0][fy][fx][lmBase];
                                    float ly = lmTensor[0][fy][fx][lmBase + 1];
                                    landmarks[lp] = new Point(
                                            (anchorCx + lx * 0.1f * anchorW) * scaleX,
                                            (anchorCy + ly * 0.1f * anchorH) * scaleY);
                                }
                            }

                            allDetections.add(new DetectionResult(bbox, score, landmarks));
                        }
                    }
                }
            }

            log.info("Max raw score across all anchors: {}", maxScoreOverall);

            // Sort by confidence descending
            allDetections.sort((a, b) -> Float.compare(b.confidence, a.confidence));

            // Apply NMS
            allDetections = nms(allDetections, NMS_THRESHOLD);

            // Additional filtering after NMS
            allDetections = allDetections.stream()
                    .filter(d -> d.bbox.width >= 40 && d.bbox.height >= 40)
                    .collect(java.util.stream.Collectors.toList());

            log.info("RetinaFace decoded {} detections (final)", allDetections.size());

        } catch (Exception e) {
            log.error("Error parsing RetinaFace output: {}", e.getMessage(), e);
        }

        return allDetections;
    }

    private List<DetectionResult> nms(List<DetectionResult> detections, float nmsThreshold) {
        List<DetectionResult> result = new ArrayList<>();
        boolean[] suppressed = new boolean[detections.size()];

        for (int i = 0; i < detections.size(); i++) {
            if (suppressed[i]) continue;
            result.add(detections.get(i));

            for (int j = i + 1; j < detections.size(); j++) {
                if (suppressed[j]) continue;
                float iou = computeIoU(detections.get(i).bbox, detections.get(j).bbox);
                if (iou > nmsThreshold) {
                    suppressed[j] = true;
                }
            }
        }
        return result;
    }

    private float computeIoU(Rect a, Rect b) {
        int x1 = Math.max(a.x, b.x);
        int y1 = Math.max(a.y, b.y);
        int x2 = Math.min(a.x + a.width, b.x + b.width);
        int y2 = Math.min(a.y + a.height, b.y + b.height);

        int interW = Math.max(0, x2 - x1);
        int interH = Math.max(0, y2 - y1);
        float inter = interW * interH;

        float areaA = a.width * a.height;
        float areaB = b.width * b.height;

        return inter / (areaA + areaB - inter + 1e-6f);
    }
}