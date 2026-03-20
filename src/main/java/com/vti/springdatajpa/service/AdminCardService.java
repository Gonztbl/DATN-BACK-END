package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.AdminCardDTO;
import com.vti.springdatajpa.entity.BankAccount;
import com.vti.springdatajpa.entity.enums.BankAccountStatus;
import com.vti.springdatajpa.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCardService {

    private final BankAccountRepository bankAccountRepository;

    public List<AdminCardDTO> getUserCards(Integer userId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AdminCardDTO getCardDetail(Integer id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        return mapToDTO(account);
    }

    @Transactional
    public AdminCardDTO updateCardStatus(Integer id, BankAccountStatus status) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        
        account.setStatus(status);
        bankAccountRepository.save(account);

        return mapToDTO(account);
    }

    private AdminCardDTO mapToDTO(BankAccount account) {
        String fullName = null;
        Integer uId = null;
        if (account.getUser() != null) {
            uId = account.getUser().getId();
            fullName = account.getUser().getFullName() != null ? account.getUser().getFullName() : account.getUser().getUserName();
        }

        return AdminCardDTO.builder()
                .id(account.getId())
                .userId(uId)
                .userFullName(fullName)
                .bankCode(account.getBankCode())
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
