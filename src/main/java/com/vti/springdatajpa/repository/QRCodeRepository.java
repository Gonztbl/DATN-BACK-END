package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Integer> {
    List<QRCode> findByWalletId(Integer walletId);
    void deleteByWalletId(Integer walletId);
}
