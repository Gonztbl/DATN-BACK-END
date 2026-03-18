package com.vti.springdatajpa.service;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

/**
 * Image quality checks before feeding into the face AI pipeline.
 * Rejects blurry or dark images that would produce unreliable embeddings.
 */
@Service
@Slf4j
public class FaceImageQualityService {

    // Laplacian variance threshold — below this, image is considered blurry
    private static final double BLUR_THRESHOLD = 35.0;

    // Minimum average brightness (0-255) — below this, image is too dark
    private static final double MIN_BRIGHTNESS = 40.0;

    // Maximum average brightness — above this, image is overexposed
    private static final double MAX_BRIGHTNESS = 240.0;

    // Minimum image dimension in pixels
    private static final int MIN_DIMENSION = 150;

    /**
     * Quality check result.
     */
    public record QualityResult(boolean passed, String reason, double blurScore, double brightness) {
    }

    /**
     * Check image quality: blur, brightness, and size.
     */
    public QualityResult checkQuality(Mat image) {
        // Check minimum size
        if (image.width() < MIN_DIMENSION || image.height() < MIN_DIMENSION) {
            return new QualityResult(false,
                    String.format("Image too small: %dx%d (min: %dx%d)",
                            image.width(), image.height(), MIN_DIMENSION, MIN_DIMENSION),
                    0, 0);
        }

        // Convert to grayscale for quality checks
        Mat gray = new Mat();
        if (image.channels() == 3) {
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = image.clone();
        }

        // Check blur using Laplacian variance
        double blurScore = calculateBlurScore(gray);

        // Check brightness
        double brightness = calculateBrightness(gray);

        gray.release();

        // Evaluate
        if (blurScore < BLUR_THRESHOLD) {
            log.warn("Image rejected: too blurry (score={:.2f}, threshold={})", blurScore, BLUR_THRESHOLD);
            return new QualityResult(false,
                    String.format("Image is too blurry (score: %.1f, min: %.1f)", blurScore, BLUR_THRESHOLD),
                    blurScore, brightness);
        }

        if (brightness < MIN_BRIGHTNESS) {
            log.warn("Image rejected: too dark (brightness={:.2f})", brightness);
            return new QualityResult(false,
                    String.format("Image is too dark (brightness: %.1f, min: %.1f)", brightness, MIN_BRIGHTNESS),
                    blurScore, brightness);
        }

        if (brightness > MAX_BRIGHTNESS) {
            log.warn("Image rejected: overexposed (brightness={:.2f})", brightness);
            return new QualityResult(false,
                    String.format("Image is overexposed (brightness: %.1f, max: %.1f)", brightness, MAX_BRIGHTNESS),
                    blurScore, brightness);
        }

        log.debug("Image quality OK: blur={:.2f}, brightness={:.2f}", blurScore, brightness);
        return new QualityResult(true, "OK", blurScore, brightness);
    }

    /**
     * Calculate blur score using Laplacian variance.
     * Higher value = sharper image. Lower value = more blur.
     */
    private double calculateBlurScore(Mat gray) {
        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, org.opencv.core.CvType.CV_64F);

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        org.opencv.core.Core.meanStdDev(laplacian, mean, stddev);

        double variance = stddev.get(0, 0)[0];
        variance = variance * variance; // stddev² = variance

        laplacian.release();
        mean.release();
        stddev.release();

        return variance;
    }

    /**
     * Calculate average brightness of the image.
     */
    private double calculateBrightness(Mat gray) {
        return org.opencv.core.Core.mean(gray).val[0];
    }
}
