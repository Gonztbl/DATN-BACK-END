package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserAIAnalysisDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.FeatureExtractionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin User APIs", description = "Admin user analysis and management")
public class AdminUserController {

    private final UserRepository userRepository;
    private final FeatureExtractionService featureExtractionService;

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get user AI analysis",
            description = "Get complete AI analysis for a user including wallet metrics and transaction patterns (Admin only)"
    )
    @GetMapping("/{userId}/ai-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAIAnalysisDTO> getUserAIAnalysis(
            @PathVariable("userId") Integer userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        
        UserAIAnalysisDTO analysis = featureExtractionService.getUserAIAnalysis(user);
        return ResponseEntity.ok(analysis);
    }
}
