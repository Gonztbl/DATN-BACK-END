package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminExportService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public byte[] exportUsersToCsv() {
        List<User> users = (List<User>) userRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.println("ID,UserName,FullName,Email,Phone,Role,Status,CreatedAt");
            for (User user : users) {
                pw.printf("%d,%s,\"%s\",%s,%s,%s,%b,%s%n",
                        user.getId(),
                        user.getUserName(),
                        user.getFullName() != null ? user.getFullName() : "",
                        user.getEmail(),
                        user.getPhone() != null ? user.getPhone() : "",
                        user.getRole(),
                        user.isActive(),
                        user.getCreatedAt());
            }
        }
        return out.toByteArray();
    }

    public byte[] exportTransactionsToCsv() {
        List<Transaction> transactions = transactionRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.println("ID,WalletID,Type,Amount,Status,BalanceAfter,CreatedAt");
            for (Transaction tx : transactions) {
                Integer walletId = tx.getWallet() != null ? tx.getWallet().getId() : null;
                pw.printf("%d,%d,%s,%s,%s,%s,%s%n",
                        tx.getId(),
                        walletId,
                        tx.getType(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getBalanceAfter(),
                        tx.getCreatedAt());
            }
        }
        return out.toByteArray();
    }

    public byte[] exportOrdersToCsv() {
        List<Order> orders = orderRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.println("ID,UserID,RestaurantID,ShipperID,TotalAmount,Status,PaymentMethod,CreatedAt");
            for (Order o : orders) {
                pw.printf("%d,%d,%d,%d,%s,%s,%s,%s%n",
                        o.getId(),
                        o.getUserId(),
                        o.getRestaurantId(),
                        o.getShipperId(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getPaymentMethod(),
                        o.getCreatedAt());
            }
        }
        return out.toByteArray();
    }
}
