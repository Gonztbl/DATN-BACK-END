package com.vti.springdatajpa.service;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

/**
 * Face alignment service using 5 landmark points from RetinaFace.
 *
 * The 5 landmarks are:
 * [0] left_eye
 * [1] right_eye
 * [2] nose
 * [3] mouth_left
 * [4] mouth_right
 *
 * ArcFace was trained on aligned faces, so alignment is CRITICAL for accuracy.
 * Without alignment, a tilted face produces a distorted embedding.
 */
@Service
@Slf4j
public class FaceAlignmentService {

    // Standard ArcFace aligned face reference positions (for 112x112 output)
    // These are the "ideal" positions where eyes should be in the aligned image
    private static final double[] REF_LEFT_EYE = { 38.2946, 51.6963 };
    private static final double[] REF_RIGHT_EYE = { 73.5318, 51.5014 };
    private static final double[] REF_NOSE = { 56.0252, 71.7366 };
    private static final double[] REF_MOUTH_L = { 41.5493, 92.3655 };
    private static final double[] REF_MOUTH_R = { 70.7299, 92.2041 };

    /**
     * ALIGNMENT NHẸ - CHỈ XOAY + SCALE THEO MẮT (không ép 5 điểm)
     * Đây là cách tốt nhất cho ArcFace ResNet100 khi test người thật khác nhau
     */
    public Mat alignFace(Mat image, Point[] landmarks) {
        if (landmarks == null || landmarks.length < 2) {
            log.warn("Không đủ landmarks, trả về null để pipeline dùng fallback crop");
            return null;
        }

        Point leftEye = landmarks[0];
        Point rightEye = landmarks[1];

        // Tính góc và khoảng cách mắt
        double dx = rightEye.x - leftEye.x;
        double dy = rightEye.y - leftEye.y;
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        double eyeDist = Math.hypot(dx, dy);

        // Center giữa hai mắt
        Point center = new Point((leftEye.x + rightEye.x) / 2.0, (leftEye.y + rightEye.y) / 2.0);

        // Scale để khoảng cách mắt ~68-72px trên ảnh 112x112 (chuẩn ArcFace)
        double scale = 10.0 / eyeDist;

        Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, scale);

        Mat aligned = new Mat();
        Imgproc.warpAffine(image, aligned, rotMatrix, new Size(112, 112), Imgproc.INTER_LINEAR);

        rotMatrix.release();
        log.debug("Aligned bằng 2-point eye only, angle={:.1f}°, scale={:.2f}", angle, scale);
        return aligned;
    }

    /**
     * Align face using just eye coordinates (simpler 2-point alignment).
     * Used when only eye landmarks are available or as a simpler alternative.
     *
     * @param image    Original image
     * @param leftEye  Left eye position
     * @param rightEye Right eye position
     * @param faceBox  Face bounding box for context
     * @return Aligned face image 112x112
     */
    public Mat alignFaceByEyes(Mat image, Point leftEye, Point rightEye, Rect faceBox) {
        try {
            // Calculate angle between eyes
            double dx = rightEye.x - leftEye.x;
            double dy = rightEye.y - leftEye.y;
            double angle = Math.toDegrees(Math.atan2(dy, dx));

            // Eye center
            double centerX = (leftEye.x + rightEye.x) / 2.0;
            double centerY = (leftEye.y + rightEye.y) / 2.0;

            // Rotation matrix
            Mat rotMatrix = Imgproc.getRotationMatrix2D(
                    new Point(centerX, centerY), angle, 1.0);

            // Apply rotation to the full image
            Mat rotated = new Mat();
            Imgproc.warpAffine(image, rotated, rotMatrix, image.size());

            // Crop the face region with some margin
            int margin = (int) (Math.max(faceBox.width, faceBox.height) * 0.2);
            int x = Math.max(0, faceBox.x - margin);
            int y = Math.max(0, faceBox.y - margin);
            int w = Math.min(rotated.width() - x, faceBox.width + 2 * margin);
            int h = Math.min(rotated.height() - y, faceBox.height + 2 * margin);

            Mat cropped = new Mat(rotated, new Rect(x, y, w, h));

            // Resize to 112x112
            Mat aligned = new Mat();
            Imgproc.resize(cropped, aligned, new Size(112, 112));

            rotMatrix.release();
            rotated.release();
            cropped.release();

            log.debug("Face aligned by eyes to 112x112, rotation angle: {:.2f}°", angle);
            return aligned;

        } catch (Exception e) {
            log.error("Eye-based alignment failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Estimate similarity transform (rotation + uniform scale + translation)
     * from source points to destination points using least squares.
     *
     * Returns a 2x3 affine transformation matrix.
     */
    private Mat estimateSimilarityTransform(MatOfPoint2f src, MatOfPoint2f dst) {
        try {
            // Use OpenCV's estimateAffinePartial2D which computes
            // a similarity transform (4 DOF: rotation, scale, tx, ty)
            Mat inliers = new Mat();
            Mat transform = org.opencv.calib3d.Calib3d.estimateAffinePartial2D(src, dst, inliers);
            inliers.release();

            if (transform != null && !transform.empty()) {
                return transform;
            }

            // Fallback: compute manually from eye positions
            Point[] srcPts = src.toArray();
            Point[] dstPts = dst.toArray();

            if (srcPts.length >= 2 && dstPts.length >= 2) {
                return computeSimpleAffine(srcPts[0], srcPts[1], dstPts[0], dstPts[1]);
            }

        } catch (Exception e) {
            log.debug("estimateAffinePartial2D failed, using manual computation: {}", e.getMessage());

            // Manual fallback using first two points (eyes)
            Point[] srcPts = src.toArray();
            Point[] dstPts = dst.toArray();
            if (srcPts.length >= 2 && dstPts.length >= 2) {
                return computeSimpleAffine(srcPts[0], srcPts[1], dstPts[0], dstPts[1]);
            }
        }

        return null;
    }

    /**
     * Compute a simple affine transform from two point pairs (eyes).
     * This gives rotation + scale + translation.
     */
    private Mat computeSimpleAffine(Point srcP1, Point srcP2, Point dstP1, Point dstP2) {
        double srcDx = srcP2.x - srcP1.x;
        double srcDy = srcP2.y - srcP1.y;
        double dstDx = dstP2.x - dstP1.x;
        double dstDy = dstP2.y - dstP1.y;

        double srcDist = Math.sqrt(srcDx * srcDx + srcDy * srcDy);
        double dstDist = Math.sqrt(dstDx * dstDx + dstDy * dstDy);

        if (srcDist < 1e-6)
            return null;

        double scale = dstDist / srcDist;
        double srcAngle = Math.atan2(srcDy, srcDx);
        double dstAngle = Math.atan2(dstDy, dstDx);
        double angle = dstAngle - srcAngle;

        double cosA = scale * Math.cos(angle);
        double sinA = scale * Math.sin(angle);

        double tx = dstP1.x - (cosA * srcP1.x - sinA * srcP1.y);
        double ty = dstP1.y - (sinA * srcP1.x + cosA * srcP1.y);

        Mat transform = new Mat(2, 3, CvType.CV_64F);
        transform.put(0, 0, cosA, -sinA, tx);
        transform.put(1, 0, sinA, cosA, ty);

        return transform;
    }
}