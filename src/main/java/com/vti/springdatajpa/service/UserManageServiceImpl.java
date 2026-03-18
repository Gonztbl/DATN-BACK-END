package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserManagerDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {
    private final UserManagerRepository userManagerRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransferDetailRepository transferDetailRepository;
    private final BankTransferRepository bankTransferRepository;
    private final BalanceChangeLogRepository balanceChangeLogRepository;
    private final QRCodeRepository qrCodeRepository;
    private final AddressRepository addressRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CardRepository cardRepository;
    private final CardDepositRepository cardDepositRepository;
    private final CardWithdrawRepository cardWithdrawRepository;
    private final ContactRepository contactRepository;
    private final FaceEmbeddingRepository faceEmbeddingRepository;
    private final FaceVerificationLogRepository faceVerificationLogRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationRepository notificationRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OtpRequestRepository otpRequestRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ShipperProfileRepository shipperProfileRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final SessionRepository sessionRepository;

    @Override
    public List<User> getAllUsers() {
        return userManagerRepository.findByRoleNot(Role.ADMIN);
    }

    @Override
    public void lockUser(Integer id) {
        userManagerRepository.lockUser(id);
    }

    @Override
    public void unlockUser(Integer id) {
        userManagerRepository.unlockUser(id);
    }

    @Override
    public UserManagerDTO updateUser(Integer id, UserManagerDTO userDto) {
        User user = userManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setFullName(userDto.getFullName());
        user.setActive(userDto.isActive());

        User savedUser = userManagerRepository.save(user);

        UserManagerDTO updatedDto = new UserManagerDTO();
        updatedDto.setId(savedUser.getId());
        updatedDto.setUserName(savedUser.getUserName());
        updatedDto.setEmail(savedUser.getEmail());
        updatedDto.setPhone(savedUser.getPhone());
        updatedDto.setFullName(savedUser.getFullName());
        updatedDto.setActive(savedUser.isActive());
        updatedDto.setCreatedAt(savedUser.getCreatedAt());

        return updatedDto;
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 1. Wallet and its related data (Transactions, Transfers, QR, BalanceChangeLogs)
        Wallet wallet = user.getWallet();
        if (wallet != null) {
            Integer walletId = wallet.getId();
            qrCodeRepository.deleteByWalletId(walletId);
            balanceChangeLogRepository.deleteByWalletId(walletId);

            List<Transaction> transactions = wallet.getTransactions();
            if (transactions != null) {
                for (Transaction tx : transactions) {
                    Integer txId = tx.getId();
                    transferDetailRepository.deleteByTransactionId(txId);
                    bankTransferRepository.deleteByTransactionId(txId);
                    balanceChangeLogRepository.deleteByTransactionId(txId);
                    transactionRepository.delete(tx);
                }
            }
            walletRepository.delete(wallet);
        }

        // 2. Support Tickets
        supportTicketRepository.deleteByUserId(id);

        // 3. Order Items (Must delete before Orders)
        orderItemRepository.deleteByUserId(id);

        // 4. Card Deposits & Withdraws
        cardDepositRepository.deleteByUserId(id);
        cardWithdrawRepository.deleteByUserId(id);

        // 5. Cards
        cardRepository.deleteByUserId(id);

        // 6. Bank Accounts
        bankAccountRepository.deleteByUserId(id);

        // 7. General User-related entities
        contactRepository.deleteByUserId(id);
        faceEmbeddingRepository.deleteByUserId(id);
        faceVerificationLogRepository.deleteByUserId(id);
        favoriteRepository.deleteByUserId(id);
        notificationRepository.deleteByUserId(id);
        otpRequestRepository.deleteByUserId(id);
        reviewRepository.deleteByUserId(id);
        sessionRepository.deleteByUserId(id);
        addressRepository.deleteByUserId(id);
        shipperProfileRepository.deleteByUserId(id);

        // 8. Orders (After items are gone)
        orderRepository.deleteByUserId(id);
        orderRepository.deleteByShipperId(id);

        // 9. Restaurant (Nullify owner instead of deleting)
        restaurantRepository.setOwnerIdNull(id);

        // 10. Finally, delete the User
        userManagerRepository.delete(user);
    }
}
