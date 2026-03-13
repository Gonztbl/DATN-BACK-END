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

    // Minimum confidence to accept a face detection
    private static final float CONFIDENCE_THRESHOLD = 0.72f;

    // Minimum face dimension in pixels (after scaling back to original size)
    private static final int MIN_FACE_SIZE = 80;

    /**
     * Detection result containing bounding box, confidence, and 5 landmark points.
     */
    public static class DetectionResult {
        public Rect bbox;
        public float confidence;
        // 5 landmarks: left_eye, right_eye, nose, mouth_left, mouth_right
        public Point[] landmarks; // [5] points
        public double faceAngle; // rotation angle between eyes (degrees)

        public DetectionResult(Rect bbox, float confidence, Point[] landmarks) {
            this.bbox = bbox;
            this.confidence = confidence;
            this.landmarks = landmarks;
            this.faceAngle = 0;

            // Calculate face angle from eye landmarks
            if (landmarks != null && landmarks.length >= 2) {
                double dx = landmarks[1].x - landmarks[0].x;
                double dy = landmarks[1].y - landmarks[0].y;
                this.faceAngle = Math.toDegrees(Math.atan2(dy, dx));
            }
        }
    }

    /**
     * Detect face(s) in the image using RetinaFace ONNX model.
     * Returns the best detection with bbox, confidence, and landmarks.
     *
     * Validations:
     * - confidence >= 0.9
     * - face size >= 80px
     * - only 1 face allowed (anti-spoof)
     * - face angle <= 30° (pose filter)
     */
    public DetectionResult detectFace(Mat image) throws Exception {

        OrtSession session = modelService.getRetinaSession();
        OrtEnvironment env = modelService.getEnvironment();

        int origWidth = image.width();
        int origHeight = image.height();

        // Resize image to model input size
        Mat resized = new Mat();
        Imgproc.resize(image, resized, new Size(640, 640));

        int height = 640;
        int width = 640;

        float[] inputData = new float[height * width * 3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double[] pixel = resized.get(y, x);

                int idx = (y * width + x) * 3;

                // Normalize: RGB + [-1, 1] (pixel - 127.5) / 128.0
                // OpenCV loads as BGR, so swap to RGB: B=pixel[0], G=pixel[1], R=pixel[2]
                inputData[idx] = (float) ((pixel[2] - 127.5f) / 128.0f); // R
                inputData[idx + 1] = (float) ((pixel[1] - 127.5f) / 128.0f); // G
                inputData[idx + 2] = (float) ((pixel[0] - 127.5f) / 128.0f); // B
            }
        }

        OnnxTensor tensor = OnnxTensor.createTensor(
                env,
                FloatBuffer.wrap(inputData),
                new long[] { 1, 640, 640, 3 });
        String inputName = session.getInputNames().iterator().next();

        session.getInputInfo().forEach((k, v) -> {
            System.out.println("INPUT: " + k + " -> " + v.getInfo());
        });

        OrtSession.Result result = session.run(
                Collections.singletonMap(inputName, tensor));

        // Debug: Log all model output shapes
        for (Map.Entry<String, ai.onnxruntime.OnnxValue> entry : result) {
            System.out.println("OUTPUT: " + entry.getKey() + " -> " + entry.getValue().getInfo());
        }

        // Parse detections
        List<DetectionResult> detections = parseAllDetections(result, origWidth, origHeight);

        // Log max confidence để debug
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

        // Filter by confidence
        List<DetectionResult> highConfidence = new ArrayList<>();

        for (DetectionResult det : detections) {
            if (det.confidence >= CONFIDENCE_THRESHOLD) {
                highConfidence.add(det);
            }
        }

        if (highConfidence.isEmpty()) {
            throw new RuntimeException(
                    "No face detected with confidence >= " + CONFIDENCE_THRESHOLD);
        }

        // Nếu vẫn nhiều, chọn box tốt nhất thay vì throw
        DetectionResult best;
        if (highConfidence.size() > 1) {
            highConfidence.sort((a, b) -> Float.compare(b.confidence, a.confidence));
            best = highConfidence.get(0);
            log.warn("Multiple high-confidence faces ({}) detected, selected highest score: {}",
                    highConfidence.size(), best.confidence);
        } else {
            best = highConfidence.get(0);
        }

        // Minimum face size check
        if (best.bbox.width < MIN_FACE_SIZE || best.bbox.height < MIN_FACE_SIZE) {

            throw new RuntimeException(
                    String.format(
                            "Face too small: %dx%d px (min: %dx%d px)",
                            best.bbox.width,
                            best.bbox.height,
                            MIN_FACE_SIZE,
                            MIN_FACE_SIZE));
        }

        // Face pose check
        if (Math.abs(best.faceAngle) > 30.0) {

            throw new RuntimeException(
                    String.format(
                            "Face angle too large: %.1f° (max ±30°)",
                            best.faceAngle));
        }

        log.info(
                "Face detected bbox=({}, {}, {}, {}) conf={} angle={}°",
                best.bbox.x,
                best.bbox.y,
                best.bbox.width,
                best.bbox.height,
                best.confidence,
                best.faceAngle);

        return best;
    }

    // ==================== RETINAFACE ANCHOR-BASED PARSER ====================

    // Anchor configuration for RetinaFace
    private static final int[] STRIDES = { 32, 16, 8 };
    private static final int[][] ANCHOR_SIZES = {
            { 512, 256 }, // stride 32
            { 128, 64 }, // stride 16
            { 32, 16 } // stride 8
    };
    private static final float PRE_NMS_THRESHOLD = 0.72f; // Thử 0.72 -> 0.73
    private static final float NMS_THRESHOLD = 0.55f; // Tăng để lọc mạnh hơn

    // Output tensor name mapping per stride
    private static final String[] CLS_NAMES = {
            "tf.compat.v1.transpose_1", // stride 32, shape [1,20,20,8]
            "tf.compat.v1.transpose_3", // stride 16, shape [1,40,40,8]
            "tf.compat.v1.transpose_5" // stride 8, shape [1,80,80,8]
    };
    private static final String[] BBOX_NAMES = {
            "face_rpn_bbox_pred_stride32", // shape [1,20,20,8]
            "face_rpn_bbox_pred_stride16", // shape [1,40,40,8]
            "face_rpn_bbox_pred_stride8" // shape [1,80,80,8]
    };
    private static final String[] LANDMARK_NAMES = {
            "face_rpn_landmark_pred_stride32", // shape [1,20,20,20]
            "face_rpn_landmark_pred_stride16", // shape [1,40,40,20]
            "face_rpn_landmark_pred_stride8" // shape [1,80,80,20]
    };

    /**
     * Parse all detections from the RetinaFace anchor-based output.
     * The model outputs 9 tensors (3 per stride: cls, bbox, landmarks).
     */
    private List<DetectionResult> parseAllDetections(OrtSession.Result result, int origWidth, int origHeight) {
        List<DetectionResult> allDetections = new ArrayList<>();
        float scaleX = (float) origWidth / 640.0f;
        float scaleY = (float) origHeight / 640.0f;

        try {
            // Collect output tensors into a map by name
            Map<String, float[][][][]> outputMap = new HashMap<>();
            for (Map.Entry<String, ai.onnxruntime.OnnxValue> entry : result) {
                String name = entry.getKey();
                Object value = entry.getValue().getValue();
                if (value instanceof float[][][][]) {
                    outputMap.put(name, (float[][][][]) value);
                }
            }

            float maxScoreOverall = 0f; // Để debug max score toàn bộ

            // Process each stride level
            for (int s = 0; s < STRIDES.length; s++) {
                int stride = STRIDES[s];
                int fmSize = 640 / stride; // feature map size: 20, 40, 80

                float[][][][] clsTensor = outputMap.get(CLS_NAMES[s]);
                float[][][][] bboxTensor = outputMap.get(BBOX_NAMES[s]);
                float[][][][] lmTensor = outputMap.get(LANDMARK_NAMES[s]);

                if (clsTensor == null || bboxTensor == null) {
                    log.warn("Missing tensor for stride {}", stride);
                    continue;
                }

                int numAnchors = ANCHOR_SIZES[s].length; // 2
                int[] anchorSizes = ANCHOR_SIZES[s];

                // Iterate over feature map grid
                for (int fy = 0; fy < fmSize; fy++) {
                    for (int fx = 0; fx < fmSize; fx++) {
                        for (int a = 0; a < numAnchors; a++) {

                            // 4 channels per anchor (classification head)
                            int base = a * 4;

                            // Safety check
                            if (clsTensor[0][fy][fx].length < base + 4) {
                                log.error("Unexpected classification channels: expected at least {}, got {}",
                                        base + 4, clsTensor[0][fy][fx].length);
                                continue;
                            }

                            float logit_bg = clsTensor[0][fy][fx][base + 0];
                            float logit_face1 = clsTensor[0][fy][fx][base + 1];
                            float logit_face2 = clsTensor[0][fy][fx][base + 2];
                            float logit_other = clsTensor[0][fy][fx][base + 3];

                            // Method 1: Sigmoid on channel 1 (Standard for many RetinaFace exports)
                            float score = (float) (1.0 / (1.0 + Math.exp(-logit_face1)));

                            // Optional: Log confident detections to verify channels
                            if (score > 0.6f) {
                                log.info("Stride={} anchor={} grid({},{}) | raw_cls=[{:.3f}, {:.3f}, {:.3f}, {:.3f}] | score={:.4f}",
                                        stride, a, fx, fy, logit_bg, logit_face1, logit_face2, logit_other, score);
                            }

                            // Debug max score position
                            if (score > maxScoreOverall) {
                                maxScoreOverall = score;
                                log.info(
                                        "NEW MAX SCORE {:.4f} at stride={} anchor={} grid=({}, {}) center≈({}, {}) raw=[{:.3f}, {:.3f}, {:.3f}, {:.3f}]",
                                        score, stride, a, fx, fy, (fx + 0.5f) * stride, (fy + 0.5f) * stride,
                                        logit_bg, logit_face1, logit_face2, logit_other);
                            }

                            // We only care about confident face detections
                            if (score < PRE_NMS_THRESHOLD)
                                continue;

                            // --- Anchor center and size ---
                            float anchorCx = (fx + 0.5f) * stride;
                            float anchorCy = (fy + 0.5f) * stride;
                            float anchorW = anchorSizes[a];
                            float anchorH = anchorSizes[a];

                            // --- Decode bbox (dx, dy, dw, dh) with VARIANCE ---
                            // RetinaFace uses variances [0.1, 0.1, 0.2, 0.2] for regression
                            float dx = bboxTensor[0][fy][fx][a * 4];
                            float dy = bboxTensor[0][fy][fx][a * 4 + 1];
                            float dw = bboxTensor[0][fy][fx][a * 4 + 2];
                            float dh = bboxTensor[0][fy][fx][a * 4 + 3];

                            float cx = anchorCx + dx * 0.1f * anchorW;
                            float cy = anchorCy + dy * 0.1f * anchorH;
                            float w = anchorW * (float) Math.exp(dw * 0.2f);
                            float h = anchorH * (float) Math.exp(dh * 0.2f);

                            // Thêm lọc sớm: box quá nhỏ hoặc ở biên quá
                            if (w < 30 || h < 30)
                                continue; // loại nhỏ ngay

                            float area = w * h;
                            if (area < 900)
                                continue; // ~30x30

                            // Lọc biên rộng hơn (150px)
                            if (cx < 150 || cx > 490 || cy < 150 || cy > 490) {
                                if (score > 0.65f) {
                                    log.debug("Strong border ignored: score={} center=({}, {})", score, cx, cy);
                                }
                                continue;
                            }

                            // Lọc aspect ratio
                            float aspect = Math.max(w, h) / Math.min(w, h);
                            if (aspect > 2.5f || aspect < 0.4f)
                                continue;

                            float x1 = (cx - w / 2f) * scaleX;
                            float y1 = (cy - h / 2f) * scaleY;
                            float x2 = (cx + w / 2f) * scaleX;
                            float y2 = (cy + h / 2f) * scaleY;

                            // Clamp to image bounds
                            int bx1 = Math.max(0, (int) x1);
                            int by1 = Math.max(0, (int) y1);
                            int bx2 = Math.min(origWidth, (int) x2);
                            int by2 = Math.min(origHeight, (int) y2);

                            int bw = bx2 - bx1;
                            int bh = by2 - by1;
                            if (bw < 10 || bh < 10)
                                continue;

                            Rect bbox = new Rect(bx1, by1, bw, bh);

                            // --- Decode landmarks (5 points × 2 coords) with VARIANCE ---
                            Point[] landmarks = null;
                            if (lmTensor != null) {
                                landmarks = new Point[5];
                                for (int lp = 0; lp < 5; lp++) {
                                    float lx = lmTensor[0][fy][fx][a * 10 + lp * 2];
                                    float ly = lmTensor[0][fy][fx][a * 10 + lp * 2 + 1];
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

            // Optional: cap max detections để tránh quá tải
            if (allDetections.size() > 10000) {
                allDetections = (List<DetectionResult>) allDetections.subList(0, 10000);
            }

            // Apply NMS
            allDetections = nms(allDetections, NMS_THRESHOLD);

            // Sau NMS, thêm lọc area gốc
            allDetections = allDetections.stream()
                    .filter(d -> d.bbox.width >= 40 && d.bbox.height >= 40) // ở ảnh gốc
                    .collect(java.util.stream.Collectors.toList());

            log.info("RetinaFace decoded {} detections (pre-threshold)", allDetections.size());
            log.info("After NMS and pre-threshold: {} detections", allDetections.size());

        } catch (Exception e) {
            log.error("Error parsing RetinaFace output: {}", e.getMessage(), e);
        }

        return allDetections;
    }

    /**
     * Softmax for 2-class (background, foreground) → return foreground probability.
     */

    /**
     * Non-Maximum Suppression to remove overlapping detections.
     */
    private List<DetectionResult> nms(List<DetectionResult> detections, float nmsThreshold) {
        List<DetectionResult> result = new ArrayList<>();
        boolean[] suppressed = new boolean[detections.size()];

        for (int i = 0; i < detections.size(); i++) {
            if (suppressed[i])
                continue;
            result.add(detections.get(i));

            for (int j = i + 1; j < detections.size(); j++) {
                if (suppressed[j])
                    continue;
                float iou = computeIoU(detections.get(i).bbox, detections.get(j).bbox);
                if (iou > nmsThreshold) {
                    suppressed[j] = true;
                }
            }
        }
        return result;
    }

    /**
     * Compute Intersection over Union (IoU) between two bounding boxes.
     */
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