package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "face_verification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "similarity")
    private Double similarity;

    @Column(name = "result", length = 10)
    private String result; // PASS / FAIL

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
