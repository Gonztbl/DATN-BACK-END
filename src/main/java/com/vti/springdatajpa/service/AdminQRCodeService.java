package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.AdminQRCodeDTO;
import com.vti.springdatajpa.entity.QRCode;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.repository.QRCodeRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminQRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final WalletRepository walletRepository;

    public List<AdminQRCodeDTO> getUserQRCodes(Integer userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for this user"));

        List<QRCode> qrCodes = qrCodeRepository.findByWalletId(wallet.getId());

        return qrCodes.stream().map(qr -> {
            String fullName = null;
            if (wallet.getUser() != null) {
                fullName = wallet.getUser().getFullName() != null ? wallet.getUser().getFullName() : wallet.getUser().getUserName();
            }

            return AdminQRCodeDTO.builder()
                    .id(qr.getId())
                    .userId(userId)
                    .userFullName(fullName)
                    .walletId(wallet.getId())
                    .codeValue(qr.getCodeValue())
                    .type(qr.getType())
                    .expiresAt(qr.getExpiresAt())
                    .createdAt(qr.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    public void deleteQRCode(Integer id) {
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QR Code not found"));
        qrCodeRepository.delete(qrCode);
    }
}
