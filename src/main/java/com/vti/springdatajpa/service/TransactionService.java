package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
        private final UserRepository userRepository;


        public Page<TransactionDTO> getTransactions(String username, Pageable pageable) {
                User user = userRepository.findByUserName(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Wallet wallet = walletRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Wallet not found"));

                return transactionRepository.findByWalletId(wallet.getId(), pageable)
                                .map(tx -> {
                                        TransactionDTO dto = new TransactionDTO();
                                        dto.setId(tx.getId());
                                        dto.setType(tx.getType().name());
                                        dto.setAmount(tx.getAmount());
                                        dto.setDate(tx.getCreatedAt());
                                        dto.setStatus(tx.getStatus().name());
                                        // Simplified category/direction
                                        dto.setCategory("General");
                                        dto.setDirection(tx.getDirection() != null ? tx.getDirection().name() : "IN");
                                        return dto;
                                });
        }

        @Transactional
        public void createTransfer(String username, TransferRequest request) {
                User sender = userRepository.findByUserName(username)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));
                Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

                if (senderWallet.getAvailableBalance() < request.getAmount()) {
                        throw new RuntimeException("Insufficient available balance");
                }

                User receiver = userRepository.findById(request.getToUserId())
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));
                Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

                senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
                senderWallet.setAvailableBalance(senderWallet.getAvailableBalance() - request.getAmount());
                walletRepository.save(senderWallet);

                receiverWallet.setBalance(receiverWallet.getBalance() + request.getAmount());
                receiverWallet.setAvailableBalance(receiverWallet.getAvailableBalance() + request.getAmount());
                walletRepository.save(receiverWallet);

                Transaction txOut = new Transaction();
                txOut.setWallet(senderWallet);
                txOut.setAmount(request.getAmount());
                txOut.setType(TransactionType.TRANSFER_OUT);
                txOut.setStatus(TransactionStatus.COMPLETED);
                txOut.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(txOut);

                Transaction txIn = new Transaction();
                txIn.setWallet(receiverWallet);
                txIn.setAmount(request.getAmount());
                txIn.setType(TransactionType.TRANSFER_IN);
                txIn.setStatus(TransactionStatus.COMPLETED);
                txIn.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(txIn);
        }

        @Transactional
        public void createTransaction(String username, TransactionRequest request) {
                if ("topup".equalsIgnoreCase(request.getType())) {
                        User user = userRepository.findByUserName(username)
                                        .orElseThrow(() -> new RuntimeException("User not found"));
                        Wallet wallet = walletRepository.findByUserId(user.getId())
                                        .orElseThrow(() -> new RuntimeException("Wallet not found"));

                        wallet.setBalance(wallet.getBalance() + request.getAmount());
                        wallet.setAvailableBalance(wallet.getAvailableBalance() + request.getAmount());
                        walletRepository.save(wallet);

                        Transaction tx = new Transaction();
                        tx.setWallet(wallet);
                        tx.setAmount(request.getAmount());
                        tx.setType(TransactionType.DEPOSIT);
                        tx.setStatus(TransactionStatus.COMPLETED);
                        tx.setCreatedAt(LocalDateTime.now());
                        transactionRepository.save(tx);
                } else {
                        throw new RuntimeException("Invalid transaction type");
                }
        }

        public List<Transaction> getAllTransactionsForAdmin(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Transaction> transactions = transactionRepository.findAll(pageable);
                return transactions.getContent();
        }

        public List<Transaction> getAllTransactionsForAdmin() {
                return transactionRepository.findAll();
        }

        public AdminWalletTopupResponse adminTopupWallet(AdminWalletTopupRequest request) {
                try {
                        // Validate wallet exists and belongs to user
                        Wallet wallet = walletRepository.findById(request.getWalletId())
                                        .orElseThrow(() -> new RuntimeException("Wallet not found"));
                        
                        if (!wallet.getUser().getId().equals(request.getUserId())) {
                                throw new RuntimeException("Wallet does not belong to specified user");
                        }
                        
                        if (!wallet.getAccountNumber().equals(request.getAccountNumber())) {
                                throw new RuntimeException("Account number does not match");
                        }
                        
                        if (wallet.getStatus() != WalletStatus.ACTIVE) {
                                throw new RuntimeException("Wallet is not active");
                        }
                        
                        // Store previous balance
                        Double previousBalance = wallet.getAvailableBalance();
                        
                        // Update wallet balance
                        wallet.setBalance(wallet.getBalance() + request.getAmountAdd());
                        wallet.setAvailableBalance(wallet.getAvailableBalance() + request.getAmountAdd());
                        walletRepository.save(wallet);
                        
                        // Create transaction record
                        Transaction transaction = new Transaction();
                        transaction.setWallet(wallet);
                        transaction.setAmount(request.getAmountAdd());
                        transaction.setType(TransactionType.DEPOSIT);
                        transaction.setDirection(TransactionDirection.IN);
                        transaction.setStatus(TransactionStatus.COMPLETED);
                        transaction.setCreatedAt(java.time.LocalDateTime.now());
                        transaction.setMetadata("Admin topup - Wallet ID: " + request.getWalletId());
                        transactionRepository.save(transaction);
                        
                        // Build response
                        AdminWalletTopupResponse response = new AdminWalletTopupResponse();
                        response.setTransactionId(transaction.getId());
                        response.setWalletId(request.getWalletId());
                        response.setUserId(request.getUserId());
                        response.setAccountNumber(request.getAccountNumber());
                        response.setAmountAdded(request.getAmountAdd());
                        response.setPreviousBalance(previousBalance);
                        response.setNewBalance(wallet.getAvailableBalance());
                        response.setStatus("SUCCESS");
                        response.setMessage("Wallet topped up successfully");
                        response.setTimestamp(java.time.LocalDateTime.now());
                        
                        return response;
                        
                } catch (Exception e) {
                        AdminWalletTopupResponse response = new AdminWalletTopupResponse();
                        response.setWalletId(request.getWalletId());
                        response.setUserId(request.getUserId());
                        response.setAccountNumber(request.getAccountNumber());
                        response.setAmountAdded(request.getAmountAdd());
                        response.setStatus("FAILED");
                        response.setMessage("Topup failed: " + e.getMessage());
                        response.setTimestamp(java.time.LocalDateTime.now());
                        return response;
                }
        }

        public WalletTransferResponse transferToWallet(String username, WalletTransferRequest request) {
                try {
                        User sender = userRepository.findByUserName(username)
                                        .orElseThrow(() -> new RuntimeException("Sender not found"));
                        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                                        .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

                        if (senderWallet.getAvailableBalance() < request.getAmount()) {
                                throw new RuntimeException("Insufficient balance");
                        }

                        // Mock receiver lookup (in real app, would lookup by account number)
                        User receiver = userRepository.findByUserName("user") // Mock receiver
                                        .orElseThrow(() -> new RuntimeException("Receiver not found"));
                        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                                        .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

                        // Perform transfer
                        senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
                        senderWallet.setAvailableBalance(senderWallet.getAvailableBalance() - request.getAmount());
                        walletRepository.save(senderWallet);

                        receiverWallet.setBalance(receiverWallet.getBalance() + request.getAmount());
                        receiverWallet.setAvailableBalance(receiverWallet.getAvailableBalance() + request.getAmount());
                        walletRepository.save(receiverWallet);

                        // Create transactions
                        Transaction txOut = new Transaction();
                        txOut.setWallet(senderWallet);
                        txOut.setAmount(request.getAmount());
                        txOut.setType(TransactionType.TRANSFER_OUT);
                        txOut.setDirection(TransactionDirection.OUT);
                        txOut.setStatus(TransactionStatus.COMPLETED);
                        txOut.setCreatedAt(LocalDateTime.now());
                        txOut.setMetadata("Transfer to " + request.getToAccountNumber());
                        transactionRepository.save(txOut);

                        Transaction txIn = new Transaction();
                        txIn.setWallet(receiverWallet);
                        txIn.setAmount(request.getAmount());
                        txIn.setType(TransactionType.TRANSFER_IN);
                        txIn.setDirection(TransactionDirection.IN);
                        txIn.setStatus(TransactionStatus.COMPLETED);
                        txIn.setCreatedAt(LocalDateTime.now());
                        txIn.setMetadata("Transfer from " + username);
                        transactionRepository.save(txIn);

                        WalletTransferResponse response = new WalletTransferResponse();
                        response.setSuccess(true);
                        response.setMessage("Transfer successful");
                        response.setTransactionId(txOut.getId());
                        return response;
                } catch (Exception e) {
                        WalletTransferResponse response = new WalletTransferResponse();
                        response.setSuccess(false);
                        response.setMessage("Transfer failed: " + e.getMessage());
                        return response;
                }
        }

        public AccountLookupResponse lookupAccount(String accountNumber) {
                try {
                        // Mock lookup - in real app would query by account number
                        User user = userRepository.findByUserName("user") // Mock user
                                        .orElse(null);
                        
                        if (user == null) {
                                AccountLookupResponse response = new AccountLookupResponse();
                                response.setFound(false);
                                return response;
                        }

                        Wallet wallet = walletRepository.findByUserId(user.getId())
                                        .orElse(null);

                        AccountLookupResponse response = new AccountLookupResponse();
                        response.setFound(true);
                        response.setAccountNumber(accountNumber);
                        response.setAccountHolderName(user.getFullName() != null ? user.getFullName() : user.getUserName());
                        response.setAvatarUrl("https://i.pravatar.cc/150?u=" + user.getUserName());
                        return response;
                } catch (Exception e) {
                        AccountLookupResponse response = new AccountLookupResponse();
                        response.setFound(false);
                        return response;
                }
        }
}
