package com.vti.springdatajpa.controller;

import com.google.zxing.WriterException;
import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.service.QRService;
import com.vti.springdatajpa.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QrController {

    private final QRService qrService;
    private final WalletService walletService;

    @GetMapping("/wallet/me")
    public ResponseEntity<WalletInfoDTO> getWalletInfo() {
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("QR Wallet Info - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        
        try {
            WalletInfoDTO walletInfo = walletService.getWalletInfo(identity);
            System.out.println("Wallet info retrieved successfully: " + walletInfo);
            System.out.println("WalletId: " + walletInfo.getWalletId());
            System.out.println("AccountName: " + walletInfo.getAccountName());
            System.out.println("AccountNumber: " + walletInfo.getAccountNumber());
            System.out.println("Balance: " + walletInfo.getBalance());
            return ResponseEntity.ok(walletInfo);
        } catch (Exception e) {
            System.err.println("Error getting wallet info: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/qr/wallet")
    public ResponseEntity<QrResponseDTO> generateQrForWallet() throws WriterException, IOException {
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("QR Generate - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        QrResponseDTO response = qrService.generateQrForWallet(identity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qr/wallet/download")
    public ResponseEntity<ByteArrayResource> downloadQrForWallet() throws WriterException, IOException {
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("QR Download - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        byte[] qrImage = qrService.generateQrForDownload(identity);
        
        ByteArrayResource resource = new ByteArrayResource(qrImage);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"wallet-qr.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(qrImage.length)
                .body(resource);
    }

    @PostMapping("/qr/wallet/with-amount")
    public ResponseEntity<QrResponseDTO> generateQrWithAmount(@Valid @RequestBody QrWithAmountRequest request) 
            throws WriterException, IOException {
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("QR With Amount - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        
        // Get wallet info first
        var walletInfo = walletService.getWalletInfo(identity);
        String qrBase64 = qrService.generateQrWithAmount(identity, request);
        
        // Create complete response DTO
        QrResponseDTO response = new QrResponseDTO();
        response.setUserId(walletInfo.getUserId());
        response.setWalletId(walletInfo.getWalletId());
        response.setReceiverName(walletInfo.getAccountName());
        response.setAccountNumber(walletInfo.getAccountNumber());
        response.setAmount(request.getAmount());
        response.setCurrency("VND");
        response.setValid(true);
        response.setTransferReady(true); // Ready for transfer since amount is provided
        response.setQrBase64(qrBase64);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr/resolve")
    public ResponseEntity<ResolveQrResponse> resolveQrPayload(@Valid @RequestBody ResolveQrRequest request) {
        ResolveQrResponse response = qrService.resolveQrPayload(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr/read-image")
    public ResponseEntity<QrImageReadResponse> readQrImage(@RequestParam("file") MultipartFile file) 
            throws IOException {
        QrImageReadResponse response = qrService.readQrImage(file);
        return ResponseEntity.ok(response);
    }
}
