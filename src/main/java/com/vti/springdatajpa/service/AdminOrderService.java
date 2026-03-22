package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.OrderItem;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.OrderItemRepository;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Get all orders with filters (admin)
     */
    public Page<AdminOrderResponseDTO> getAllOrders(
            Order.OrderStatus status,
            Integer userId,
            String restaurantId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String search,
            Pageable pageable) {

        // Get all orders
        List<Order> allOrders = orderRepository.findAll();

        // Apply filters
        List<Order> filteredOrders = allOrders.stream()
                .filter(o -> status == null || o.getStatus() == status)
                .filter(o -> userId == null || o.getUserId().equals(userId))
                .filter(o -> restaurantId == null || o.getRestaurantId().equals(restaurantId))
                .filter(o -> fromDate == null || !o.getCreatedAt().isBefore(fromDate))
                .filter(o -> toDate == null || !o.getCreatedAt().isAfter(toDate))
                .filter(o -> search == null || search.isEmpty() ||
                        (String.valueOf(o.getId()).contains(search)) ||
                        (o.getRecipientName() != null && o.getRecipientName().toLowerCase().contains(search.toLowerCase())) ||
                        (o.getRecipientPhone() != null && o.getRecipientPhone().contains(search)) ||
                        (o.getDeliveryAddress() != null && o.getDeliveryAddress().toLowerCase().contains(search.toLowerCase())) ||
                        (o.getUser() != null && o.getUser().getUserName() != null && o.getUser().getUserName().toLowerCase().contains(search.toLowerCase())) ||
                        (o.getUser() != null && o.getUser().getFullName() != null && o.getUser().getFullName().toLowerCase().contains(search.toLowerCase())))
                .sorted((o1, o2) -> {
                    // Default sort by createdAt desc
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                })
                .collect(Collectors.toList());

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredOrders.size());

        if (start > filteredOrders.size()) {
            start = 0;
            end = 0;
        }

        List<Order> pageContent = filteredOrders.subList(start, end);
        List<AdminOrderResponseDTO> dtoList = pageContent.stream()
                .map(this::mapToAdminOrderResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, filteredOrders.size());
    }

    /**
     * Get order detail (admin)
     */
    public AdminOrderDetailDTO getOrderDetail(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToAdminOrderDetailDTO(order);
    }

    /**
     * Update order status (admin)
     */
    @Transactional
    public AdminOrderUpdateResponseDTO updateOrderStatus(Integer orderId, String statusStr, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + statusStr);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        AdminOrderUpdateResponseDTO response = new AdminOrderUpdateResponseDTO();
        response.setId(order.getId());
        response.setStatus(newStatus.name());
        response.setUpdatedAt(LocalDateTime.now());
        response.setMessage("Order status updated successfully");
        return response;
    }

    /**
     * Cancel order (admin)
     */
    @Transactional
    public AdminOrderCancelResponseDTO cancelOrder(Integer orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // TODO: Process refund if order was paid
        Integer refundTransactionId = null;

        AdminOrderCancelResponseDTO response = new AdminOrderCancelResponseDTO();
        response.setId(order.getId());
        response.setStatus("CANCELLED");
        response.setRefundTransactionId(refundTransactionId);
        response.setMessage("Order cancelled successfully");
        return response;
    }

    /**
     * Delete order (admin)
     */
    @Transactional
    public void deleteOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Cascade deletion of OrderItems is handled by JPA (cascade = CascadeType.ALL)
        orderRepository.delete(order);
    }

    private AdminOrderResponseDTO mapToAdminOrderResponseDTO(Order order) {
        AdminOrderResponseDTO dto = new AdminOrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setShipperId(order.getShipperId());

        // Set user info if available
        if (order.getUser() != null) {
            dto.setUserName(order.getUser().getUserName());
            dto.setFullName(order.getUser().getFullName());
        }

        // Set restaurant info if available
        if (order.getRestaurant() != null) {
            dto.setRestaurantId(order.getRestaurant().getId());
            dto.setRestaurantName(order.getRestaurant().getName());
        } else if (order.getRestaurantId() != null) {
            dto.setRestaurantId(order.getRestaurantId());
        }

        return dto;
    }

    private AdminOrderDetailDTO mapToAdminOrderDetailDTO(Order order) {
        AdminOrderDetailDTO dto = new AdminOrderDetailDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setShipperId(order.getShipperId());

        // User info
        if (order.getUser() != null) {
            AdminOrderDetailDTO.AdminOrderUserDTO userDTO = new AdminOrderDetailDTO.AdminOrderUserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setUserName(order.getUser().getUserName());
            userDTO.setFullName(order.getUser().getFullName());
            userDTO.setPhone(order.getUser().getPhone());
            userDTO.setEmail(order.getUser().getEmail());
            dto.setUser(userDTO);
        }

        // Restaurant info
        if (order.getRestaurant() != null) {
            AdminOrderDetailDTO.AdminOrderRestaurantDTO restDTO = new AdminOrderDetailDTO.AdminOrderRestaurantDTO();
            restDTO.setId(order.getRestaurant().getId());
            restDTO.setName(order.getRestaurant().getName());
            restDTO.setPhone(order.getRestaurant().getPhone());
            restDTO.setAddress(order.getRestaurant().getAddress());
            dto.setRestaurant(restDTO);
        }

        // Items
        List<AdminOrderDetailDTO.AdminOrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                AdminOrderDetailDTO.AdminOrderItemDTO itemDTO = new AdminOrderDetailDTO.AdminOrderItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPriceAtTime(item.getPriceAtTime());
                itemDTO.setSubtotal(item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())));

                Product product = item.getProduct();
                if (product != null) {
                    itemDTO.setProductName(product.getName());
                    itemDTO.setImage(product.getImageBase64());
                }
                itemDTOs.add(itemDTO);
            }
        }
        dto.setItems(itemDTOs);

        // Status history
        List<AdminOrderDetailDTO.AdminOrderStatusHistoryDTO> statusHistory = new ArrayList<>();
        AdminOrderDetailDTO.AdminOrderStatusHistoryDTO history1 = new AdminOrderDetailDTO.AdminOrderStatusHistoryDTO();
        history1.setStatus(Order.OrderStatus.PENDING.name());
        history1.setUpdatedAt(order.getCreatedAt());
        history1.setUpdatedBy("system");
        statusHistory.add(history1);

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getUpdatedAt() != null) {
            AdminOrderDetailDTO.AdminOrderStatusHistoryDTO history2 = new AdminOrderDetailDTO.AdminOrderStatusHistoryDTO();
            history2.setStatus(order.getStatus().name());
            history2.setUpdatedAt(order.getUpdatedAt());
            history2.setUpdatedBy("admin");
            statusHistory.add(history2);
        }
        dto.setStatusHistory(statusHistory);

        // Payment info
        AdminOrderDetailDTO.AdminOrderPaymentDTO paymentDTO = new AdminOrderDetailDTO.AdminOrderPaymentDTO();
        paymentDTO.setMethod(order.getPaymentMethod());
        paymentDTO.setAmount(order.getTotalAmount());

        // Find related transaction
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction tx : transactions) {
            if (tx.getReferenceId() != null && tx.getReferenceId().equals(String.valueOf(order.getId()))) {
                paymentDTO.setTransactionId(tx.getId());
                paymentDTO.setStatus(tx.getStatus().name());
                paymentDTO.setPaidAt(tx.getCreatedAt());
                break;
            }
        }
        dto.setPayment(paymentDTO);

        // Delivery info
        AdminOrderDetailDTO.AdminOrderDeliveryDTO deliveryDTO = new AdminOrderDetailDTO.AdminOrderDeliveryDTO();
        deliveryDTO.setRecipientName(order.getRecipientName());
        deliveryDTO.setRecipientPhone(order.getRecipientPhone());
        deliveryDTO.setDeliveryAddress(order.getDeliveryAddress());
        deliveryDTO.setNote(order.getNote());
        dto.setDeliveryInfo(deliveryDTO);

        return dto;
    }

    @lombok.Data
    public static class AdminOrderUpdateResponseDTO {
        private Integer id;
        private String status;
        private LocalDateTime updatedAt;
        private String message;
    }

    @lombok.Data
    public static class AdminOrderCancelResponseDTO {
        private Integer id;
        private String status;
        private Integer refundTransactionId;
        private String message;
    }
}
