package com.vti.springdatajpa.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'SHIPPER', 'USER')")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new UploadResponse(null, null, "File is empty"));
            }

            // Check file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(new UploadResponse(null, null, "File size exceeds 5MB limit"));
            }

            // Check file type (only images)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(new UploadResponse(null, null, "Only image files are allowed"));
            }

            // Convert to base64
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUrl = "data:" + contentType + ";base64," + base64;

            // In production, you would save to cloud storage and return URL
            // String fileUrl = saveToCloudStorage(file);
            
            UploadResponse response = new UploadResponse(dataUrl, base64, null);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new UploadResponse(null, null, "Failed to process file: " + e.getMessage()));
        }
    }

    @Data
    public static class UploadResponse {
        private String url;
        private String base64;
        private String error;

        public UploadResponse(String url, String base64, String error) {
            this.url = url;
            this.base64 = base64;
            this.error = error;
        }
    }
}
