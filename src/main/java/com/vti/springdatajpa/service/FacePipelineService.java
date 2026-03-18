package com.vti.springdatajpa.service;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Full face processing pipeline (production-grade):
 *
 * image
 * ↓
 * quality check (blur, brightness, size)
 * ↓
 * face detection (RetinaFace) → bbox + conf + landmarks
 * ↓ [validates: conf≥0.9, size≥80px, angle≤30°, single face]
 * anti-spoofing (texture variance)
 * ↓
 * face alignment (5-point landmarks → affine transform)
 * ↓
 * embedding (ArcFace) → float[512] L2-normalized
 *
 * Returns PipelineResult with embedding + metadata for DB storage.
 */
@Service
@Slf4j
public class FacePipelineService {

    @Autowired
    private FaceDetectionService detectionService;

    @Autowired
    private FaceAlignmentService alignmentService;

    @Autowired
    private FaceEmbeddingService embeddingService;

    @Autowired
    private FaceImageQualityService qualityService;

    /**
     * Pipeline result containing embedding + metadata.
     */
    public record PipelineResult(
            float[] embedding,
            double qualityScore, // blur score
            double brightness,
            double faceAngle, // face rotation angle
            float confidence // detection confidence
    ) {
    }

    /**
     * Full pipeline with all production validations.
     */
    public PipelineResult processWithMetadata(Mat image) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Pipeline START: image {}x{}", image.width(), image.height());

        // Step 0: Image quality check
        long t0 = System.currentTimeMillis();
        FaceImageQualityService.QualityResult quality = qualityService.checkQuality(image);
        if (!quality.passed()) {
            throw new RuntimeException("Image quality check failed: " + quality.reason());
        }
        log.info("  [quality] OK blur={} brightness={} ({}ms)",
                String.format("%.1f", quality.blurScore()),
                String.format("%.1f", quality.brightness()),
                System.currentTimeMillis() - t0);

        // Step 1: Detect face
        long t1 = System.currentTimeMillis();
        FaceDetectionService.DetectionResult detection = detectionService.detectFace(image);
        log.info("  [detect] conf={} angle={} bbox={}x{} landmarks={} ({}ms)",
                String.format("%.4f", detection.confidence),
                String.format("%.1f°", detection.faceAngle),
                detection.bbox.width, detection.bbox.height,
                detection.landmarks != null ? "yes" : "no",
                System.currentTimeMillis() - t1);

        // Step 2: Anti-spoofing check (texture variance)
        long t2 = System.currentTimeMillis();
        checkAntiSpoofing(image, detection.bbox);
        log.info("  [anti-spoof] passed ({}ms)", System.currentTimeMillis() - t2);

        // Step 3: Align face using landmarks
        long t3 = System.currentTimeMillis();
        Mat alignedFace = null;

        if (detection.landmarks != null && detection.landmarks.length >= 2) {
    // Sử dụng alignFaceByEyes để xoay ảnh theo mắt, sau đó crop theo bbox và resize
    alignedFace = alignmentService.alignFaceByEyes(
            image,
            detection.landmarks[0],
            detection.landmarks[1],
            detection.bbox
    );
}

        if (alignedFace == null) {
            log.warn("  [align] fallback to simple crop (lower accuracy)");
            alignedFace = simpleCropWithPadding(image, detection.bbox);
        }
        log.info("  [align] done ({}ms)", System.currentTimeMillis() - t3);

        // Step 4: Generate embedding
        long t4 = System.currentTimeMillis();
        float[] embedding = embeddingService.generateEmbedding(alignedFace);
        alignedFace.release();
        log.info("  [embed] {} dimensions ({}ms)", embedding.length, System.currentTimeMillis() - t4);

        long totalMs = System.currentTimeMillis() - startTime;
        log.info("Pipeline END: total {}ms", totalMs);

        return new PipelineResult(
                embedding,
                quality.blurScore(),
                quality.brightness(),
                detection.faceAngle,
                detection.confidence);
    }

    /**
     * Simple pipeline: returns just the embedding (backward compatible).
     */
    public float[] process(Mat image) throws Exception {
        return processWithMetadata(image).embedding();
    }

    /**
     * Basic anti-spoofing: check texture variance of the face region.
     * Flat/printed images have low texture variance.
     */
    private void checkAntiSpoofing(Mat image, Rect faceBox) {
        try {
            // Crop face region
            int x = Math.max(0, faceBox.x);
            int y = Math.max(0, faceBox.y);
            int w = Math.min(image.width() - x, faceBox.width);
            int h = Math.min(image.height() - y, faceBox.height);
            Mat face = new Mat(image, new Rect(x, y, w, h));

            // Convert to grayscale
            Mat gray = new Mat();
            if (face.channels() == 3) {
                Imgproc.cvtColor(face, gray, Imgproc.COLOR_BGR2GRAY);
            } else {
                gray = face.clone();
            }

            // Calculate texture variance using Laplacian
            Mat laplacian = new Mat();
            Imgproc.Laplacian(gray, laplacian, org.opencv.core.CvType.CV_64F);

            org.opencv.core.MatOfDouble mean = new org.opencv.core.MatOfDouble();
            org.opencv.core.MatOfDouble stddev = new org.opencv.core.MatOfDouble();
            org.opencv.core.Core.meanStdDev(laplacian, mean, stddev);

            double textureVariance = stddev.get(0, 0)[0];
            textureVariance = textureVariance * textureVariance;

            face.release();
            gray.release();
            laplacian.release();
            mean.release();
            stddev.release();

            // Flat image threshold (printed photo, screen capture)
            if (textureVariance < 50.0) {
                throw new RuntimeException(
                        String.format("Possible spoofing detected: low face texture variance (%.1f). "
                                + "Please use a live camera image.", textureVariance));
            }

            log.debug("Anti-spoofing texture variance: {}", String.format("%.1f", textureVariance));

        } catch (RuntimeException e) {
            throw e; // Re-throw spoofing detection
        } catch (Exception e) {
            log.warn("Anti-spoofing check skipped: {}", e.getMessage());
            // Don't block on anti-spoofing failures
        }
    }

    /**
     * Simple crop with padding (0.2) as fallback when no landmarks.
     * Padding ensures forehead and chin are included.
     */
    private Mat simpleCropWithPadding(Mat image, Rect faceBox) {
        double padding = 0.2;
        int padW = (int) (faceBox.width * padding);
        int padH = (int) (faceBox.height * padding);

        int x = Math.max(0, faceBox.x - padW);
        int y = Math.max(0, faceBox.y - padH);
        int w = Math.min(image.width() - x, faceBox.width + 2 * padW);
        int h = Math.min(image.height() - y, faceBox.height + 2 * padH);

        Mat cropped = new Mat(image, new Rect(x, y, w, h));
        Mat resized = new Mat();
        Imgproc.resize(cropped, resized, new Size(112, 112));
        cropped.release();
        return resized;
    }
}
