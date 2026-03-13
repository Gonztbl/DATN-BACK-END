package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "face_embeddings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaceEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "embedding", columnDefinition = "TEXT", nullable = false)
    private String embedding; // JSON array string: "[0.21, -0.33, ...]"

    @Column(name = "pose", length = 20, nullable = false)
    private String pose; // front / left / right

    // ---- Metadata for debugging and model upgrades ----

    @Column(name = "model_version", length = 50)
    private String modelVersion; // e.g., "arcface_r100_v1"

    @Column(name = "quality_score")
    private Double qualityScore; // blur score from quality check

    @Column(name = "face_angle")
    private Double faceAngle; // rotation angle of detected face

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Utility: convert float[] to JSON string
    public static String toJson(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    // Utility: convert JSON string to float[]
    public float[] toFloatArray() {
        String clean = embedding.replace("[", "").replace("]", "").trim();
        if (clean.isEmpty())
            return new float[0];
        String[] parts = clean.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
