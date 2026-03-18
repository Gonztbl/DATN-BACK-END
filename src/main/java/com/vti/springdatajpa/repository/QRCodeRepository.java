package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Integer> {
    void deleteByWalletId(Integer walletId);
}
