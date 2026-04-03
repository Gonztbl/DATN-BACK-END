package com.vti.springdatajpa.config;

import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.WalletDailySnapshot;
import com.vti.springdatajpa.repository.WalletDailySnapshotRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyBalanceSnapshotJob {

    private final WalletRepository walletRepository;
    private final WalletDailySnapshotRepository walletDailySnapshotRepository;

    /**
     * Runs every day at 23:59:59 to snapshot all wallet balances
     * Cron: second minute hour day month day-of-week
     * 59 59 23 * * * = 23:59:59 every day
     */
    @Scheduled(cron = "59 59 23 * * *", zone = "Asia/Ho_Chi_Minh")
    public void snapshotDailyBalances() {
        log.info("Starting daily balance snapshot job...");
        try {
            LocalDate today = LocalDate.now();
            List<Wallet> allWallets = walletRepository.findAll();

            for (Wallet wallet : allWallets) {
                // Get current available balance
                Double currentBalance = wallet.getAvailableBalance() != null ? 
                    wallet.getAvailableBalance() : wallet.getBalance();

                // Create snapshot
                WalletDailySnapshot snapshot = new WalletDailySnapshot();
                snapshot.setWallet(wallet);
                snapshot.setRecordDate(today);
                snapshot.setEndOfDayBalance(BigDecimal.valueOf(currentBalance != null ? currentBalance : 0.0));

                walletDailySnapshotRepository.save(snapshot);
            }

            log.info("Daily balance snapshot job completed. Processed {} wallets", allWallets.size());
        } catch (Exception e) {
            log.error("Error in daily balance snapshot job", e);
        }
    }
}
