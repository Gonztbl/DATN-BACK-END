package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerifyResponse {
    private double similarity;
    private String result; // PASS / FAIL
    private String matchedPose;
    private double threshold;
    private String message;
}
