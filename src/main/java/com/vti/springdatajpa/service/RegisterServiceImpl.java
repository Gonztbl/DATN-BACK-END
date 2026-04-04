package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.repository.RegisterRepository;
import com.vti.springdatajpa.repository.ShipperProfileRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final RegisterRepository registerRepository;
    private final WalletRepository walletRepository;
    private final ShipperProfileRepository shipperProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(RegisterRepository registerRepository,
                               WalletRepository walletRepository,
                               ShipperProfileRepository shipperProfileRepository,
                               PasswordEncoder passwordEncoder) {
        this.registerRepository = registerRepository;
        this.walletRepository = walletRepository;
        this.shipperProfileRepository = shipperProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createAccount(User user) {

        // check trùng username/email/phone
        if (registerRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }
        if (registerRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        if (registerRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("PHONE_EXISTS");
        }

        // mã hoá password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        int pin = 100000 + new Random().nextInt(900000);
        user.setPinHash(String.valueOf(pin));
        user.setActive(true);
        user.setRole(Role.USER);
        user.setKycLevel(1); // Default KYC level 1 on registration
        user.setVerified(false); // Not verified until face registration
        user.setCreatedAt(java.time.LocalDateTime.now());


        // Lưu user trước
        User savedUser = registerRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setCode("WALLET" + savedUser.getId()); // mã wallet
        wallet.setCurrency("VND");
        wallet.setBalance(0.0);
        wallet.setAvailableBalance(0.0);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setAccountNumber(user.getPhone());
        wallet.setCreatedAt(java.time.LocalDateTime.now());

        walletRepository.save(wallet);

        return savedUser;
    }

    @Override
    public User createAccountWithRole(User user) {
        // check trùng username/email/phone
        if (registerRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }
        if (registerRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        if (registerRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("PHONE_EXISTS");
        }

        // mã hoá password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        int pin = 100000 + new Random().nextInt(900000);
        user.setPinHash(String.valueOf(pin));
        user.setActive(true);
        user.setKycLevel(1); // Default KYC level 1 on registration
        user.setVerified(false); // Not verified until face registration
        // Role is already set by the caller
        user.setCreatedAt(java.time.LocalDateTime.now());

        // Lưu user trước
        User savedUser = registerRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setCode("WALLET" + savedUser.getId()); // mã wallet
        wallet.setCurrency("VND");
        wallet.setBalance(0.0);
        wallet.setAvailableBalance(0.0);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setAccountNumber(user.getPhone());
        wallet.setCreatedAt(java.time.LocalDateTime.now());

        walletRepository.save(wallet);

        // Create ShipperProfile if role is SHIPPER
        if (savedUser.getRole() == Role.SHIPPER) {
            com.vti.springdatajpa.entity.ShipperProfile profile = new com.vti.springdatajpa.entity.ShipperProfile();
            profile.setUserId(savedUser.getId());
            profile.setIsOnline(false);
            profile.setCreatedAt(java.time.LocalDateTime.now());
            profile.setUpdatedAt(java.time.LocalDateTime.now());
            shipperProfileRepository.save(profile);
        }

        return savedUser;
    }
}
