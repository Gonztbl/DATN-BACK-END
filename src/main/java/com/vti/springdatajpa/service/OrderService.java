package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.OrderResponseDTO;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.vti.springdatajpa.dto.OrderRequestDTO;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Get paginated orders for user with optional status filter
     */
    public Page<OrderResponseDTO> getOrdersForUser(Integer userId, Order.OrderStatus status, Pageable pageable) {
        Page<Order> ordersPage;
        if (status != null) {
            ordersPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            ordersPage = orderRepository.findByUserId(userId, pageable);
        }
        return ordersPage.map(this::mapToListDTO);
    }

    /**
     * Get order detail by ID - verify ownership
     */
    public OrderResponseDTO getOrderDetail(Integer orderId, Integer userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));
        return mapToDetailDTO(order, userId);
    }

    /**
     * Map Order to list view DTO (lighter, for listing)
     */
    private OrderResponseDTO mapToListDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setPaymentMethod(order.getPaymentMethod());

        // Map restaurant info
        if (order.getRestaurant() != null) {
            OrderResponseDTO.RestaurantInfoDTO restDTO = new OrderResponseDTO.RestaurantInfoDTO();
            restDTO.setId(order.getRestaurant().getId());
            restDTO.setName(order.getRestaurant().getName());
            restDTO.setLogoBase64(order.getRestaurant().getLogoBase64());
            dto.setRestaurant(restDTO);
        } else if (order.getRestaurantId() != null) {
            // Fallback if restaurant is not loaded but ID exists
            OrderResponseDTO.RestaurantInfoDTO restDTO = new OrderResponseDTO.RestaurantInfoDTO();
            restDTO.setId(order.getRestaurantId());
            dto.setRestaurant(restDTO);
        }

        // Map items with product info
        List<OrderResponseDTO.OrderItemDTO> itemDTOs = new ArrayList<>();
        int totalCount = 0;

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                OrderResponseDTO.OrderItemDTO itemDTO = new OrderResponseDTO.OrderItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPriceAtTime(item.getPriceAtTime());
                itemDTO.setSubtotal(item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())));

                // Get product info if available
                if (item.getProduct() != null) {
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setProductImage(item.getProduct().getImageBase64());
                }

                itemDTOs.add(itemDTO);
                totalCount += item.getQuantity();
            }
        }

        dto.setItems(itemDTOs);
        dto.setItemCount(totalCount);

        return dto;
    }

    /**
     * Map Order to detail DTO (full info including payment & status history)
     */
    private OrderResponseDTO mapToDetailDTO(Order order, Integer userId) {
        OrderResponseDTO dto = mapToListDTO(order);

        // Add note field
        dto.setNote(order.getNote());

        // Build payment history from transactions
        List<OrderResponseDTO.PaymentHistoryDTO> paymentHistory = buildPaymentHistory(order);
        dto.setPaymentHistory(paymentHistory);

        // Build status history (simplified - based on current status and timestamps)
        List<OrderResponseDTO.StatusHistoryDTO> statusHistory = buildStatusHistory(order);
        dto.setStatusHistory(statusHistory);

        return dto;
    }

    /**
     * Build payment history from transactions with reference to this order
     */
    private List<OrderResponseDTO.PaymentHistoryDTO> buildPaymentHistory(Order order) {
        List<OrderResponseDTO.PaymentHistoryDTO> history = new ArrayList<>();

        // Find transactions related to this order
        // Transactions should have referenceId = order ID and type = PAYMENT or similar
        List<Transaction> transactions = transactionRepository.findAll();

        for (Transaction tx : transactions) {
            // Check if transaction is related to this order
            if (isOrderRelatedTransaction(tx, order.getId())) {
                OrderResponseDTO.PaymentHistoryDTO paymentDTO = new OrderResponseDTO.PaymentHistoryDTO();
                paymentDTO.setTransactionId(tx.getId());
                paymentDTO.setAmount(BigDecimal.valueOf(tx.getAmount()));
                paymentDTO.setStatus(tx.getStatus().name());
                paymentDTO.setPaymentMethod(order.getPaymentMethod());
                paymentDTO.setTimestamp(tx.getCreatedAt());
                paymentDTO.setReferenceId(String.valueOf(order.getId()));
                history.add(paymentDTO);
            }
        }

        return history.isEmpty() ? Collections.emptyList() : history;
    }

    /**
     * Check if transaction is related to this order
     */
    private boolean isOrderRelatedTransaction(Transaction tx, Integer orderId) {
        if (tx.getReferenceId() != null && tx.getReferenceId().equals(String.valueOf(orderId))) {
            return true;
        }
        if (tx.getMetadata() != null && tx.getMetadata().contains("Order " + orderId)) {
            return true;
        }
        // Also check if it's a payment type transaction that might be related
        return tx.getType() == TransactionType.DEPOSIT || tx.getType() == TransactionType.WITHDRAW;
    }

    /**
     * Build simplified status history
     * Note: In production, you should have a separate order_status_history table
     */
    private List<OrderResponseDTO.StatusHistoryDTO> buildStatusHistory(Order order) {
        List<OrderResponseDTO.StatusHistoryDTO> history = new ArrayList<>();

        // Add order creation
        OrderResponseDTO.StatusHistoryDTO created = new OrderResponseDTO.StatusHistoryDTO();
        created.setStatus(Order.OrderStatus.PENDING.name());
        created.setTimestamp(order.getCreatedAt());
        created.setNote("Đơn hàng đã được tạo");
        history.add(created);

        // If order has progressed beyond pending, add other statuses
        if (order.getStatus() != Order.OrderStatus.PENDING && order.getUpdatedAt() != null
                && !order.getUpdatedAt().equals(order.getCreatedAt())) {
            OrderResponseDTO.StatusHistoryDTO updated = new OrderResponseDTO.StatusHistoryDTO();
            updated.setStatus(order.getStatus().name());
            updated.setTimestamp(order.getUpdatedAt());
            updated.setNote(getStatusNote(order.getStatus()));
            history.add(updated);
        }

        return history;
    }

    private String getStatusNote(Order.OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Nhà hàng đã xác nhận";
            case PREPARING -> "Đang chế biến";
            case DELIVERING -> "Đang giao hàng";
            case COMPLETED -> "Giao hàng thành công";
            case CANCELLED -> "Đơn hàng đã bị hủy";
            default -> "Trạng thái cập nhật";
        };
    }

    /**
     * Cancel order - only allowed for PENDING or CONFIRMED orders
     */
    public CancelOrderResponse cancelOrder(Integer orderId, Integer userId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));

        // Check if order can be cancelled
        if (order.getStatus() == Order.OrderStatus.DELIVERING ||
            order.getStatus() == Order.OrderStatus.COMPLETED ||
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order cannot be cancelled at this status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // TODO: Process refund if order was paid
        Integer refundTransactionId = null;

        CancelOrderResponse response = new CancelOrderResponse();
        response.setId(order.getId());
        response.setStatus("CANCELLED");
        response.setRefundTransactionId(refundTransactionId);
        response.setMessage("Đơn hàng đã được hủy thành công" + (refundTransactionId != null ? " và tiền đã được hoàn lại vào ví" : ""));
        return response;
    }

    /**
     * Reorder from existing order
     */
    public ReorderResponse reorder(Integer orderId, Integer userId, ReorderRequest request) {
        Order originalOrder = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));

        // Check if all products are still available
        if (originalOrder.getOrderItems() != null) {
            for (OrderItem item : originalOrder.getOrderItems()) {
                if (item.getProduct() != null && !"available".equals(item.getProduct().getStatus())) {
                    throw new RuntimeException("Product " + item.getProduct().getName() + " is no longer available");
                }
            }
        }

        // Create new order (simplified - in real scenario would use current prices)
        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setTotalAmount(originalOrder.getTotalAmount()); // Should recalculate with current prices
        newOrder.setStatus(Order.OrderStatus.PENDING);
        newOrder.setDeliveryAddress(request != null && request.getDeliveryAddress() != null
                ? request.getDeliveryAddress() : originalOrder.getDeliveryAddress());
        newOrder.setRecipientName(originalOrder.getRecipientName());
        newOrder.setRecipientPhone(originalOrder.getRecipientPhone());
        newOrder.setNote(request != null && request.getNote() != null
                ? request.getNote() : originalOrder.getNote());
        newOrder.setPaymentMethod(originalOrder.getPaymentMethod());
        newOrder.setRestaurantId(originalOrder.getRestaurantId());

        Order saved = orderRepository.save(newOrder);

        // Copy order items
        if (originalOrder.getOrderItems() != null) {
            List<OrderItem> newItems = new ArrayList<>();
            for (OrderItem item : originalOrder.getOrderItems()) {
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(saved.getId());
                newItem.setProductId(item.getProductId());
                newItem.setQuantity(item.getQuantity());
                // Use current price from product if available
                if (item.getProduct() != null) {
                    newItem.setPriceAtTime(item.getProduct().getPrice());
                } else {
                    newItem.setPriceAtTime(item.getPriceAtTime());
                }
                newItems.add(newItem);
            }
            // Save order items
        }

        ReorderResponse response = new ReorderResponse();
        response.setOrderId(saved.getId());
        response.setTotalAmount(saved.getTotalAmount());
        response.setStatus(saved.getStatus().name());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }

    /**
     * Track order status
     */
    public OrderTrackingResponse trackOrder(Integer orderId, Integer userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));

        OrderTrackingResponse response = new OrderTrackingResponse();
        response.setOrderId(order.getId());
        response.setCurrentStatus(order.getStatus().name());

        // Estimate delivery time based on status
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            response.setEstimatedDeliveryTime(order.getCreatedAt().plusHours(1));
        } else if (order.getStatus() == Order.OrderStatus.CONFIRMED) {
            response.setEstimatedDeliveryTime(order.getCreatedAt().plusMinutes(45));
        } else if (order.getStatus() == Order.OrderStatus.PREPARING) {
            response.setEstimatedDeliveryTime(order.getCreatedAt().plusMinutes(30));
        } else if (order.getStatus() == Order.OrderStatus.DELIVERING) {
            response.setEstimatedDeliveryTime(order.getCreatedAt().plusMinutes(15));
        }

        // Build status history
        List<StatusHistoryItem> history = new ArrayList<>();
        StatusHistoryItem pending = new StatusHistoryItem();
        pending.setStatus(Order.OrderStatus.PENDING.name());
        pending.setTimestamp(order.getCreatedAt());
        history.add(pending);

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getUpdatedAt() != null) {
            StatusHistoryItem current = new StatusHistoryItem();
            current.setStatus(order.getStatus().name());
            current.setTimestamp(order.getUpdatedAt());
            history.add(current);
        }
        response.setStatusHistory(history);

        // Mock delivery location
        DeliveryLocation location = new DeliveryLocation();
        location.setLat(10.8231);
        location.setLng(106.6297);
        response.setDeliveryLocation(location);

        return response;
    }

    /**
     * Create a new order
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, Integer userId) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }

        // Calculate total and validate products
        BigDecimal totalAmount = BigDecimal.ZERO;
        String restaurantId = null;
        
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequestDTO.OrderItemRequestDTO itemReq : request.getItems()) {
            Integer prodId = Integer.parseInt(itemReq.getProductId());
            Product product = productRepository.findByIdAndDeletedAtIsNull(prodId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + prodId));

            // 1. Check if product is available (status must be "available" - case insensitive)
            String productStatus = product.getStatus();
            boolean isProductAvailable = productStatus == null || 
                                       productStatus.isBlank() || 
                                       "available".equalsIgnoreCase(productStatus);
            
            if (!isProductAvailable) {
                throw new RuntimeException("Product is not available: " + product.getName());
            }

            // 2. Check if restaurant is active and not deleted
            Restaurant restaurant = product.getRestaurant();
            if (restaurant == null || (restaurant.getStatus() != null && !restaurant.getStatus()) || restaurant.getDeletedAt() != null) {
                throw new RuntimeException("Restaurant is currently unavailable: " + 
                    (restaurant != null ? restaurant.getName() : "Unknown"));
            }

            // Validate or set restaurantId
            if (restaurantId == null) {
                restaurantId = product.getRestaurantId();
                // If request provides restaurantId, validate it matches
                if (request.getRestaurantId() != null && !request.getRestaurantId().equals(restaurantId)) {
                    throw new RuntimeException("Product does not belong to the specified restaurant: " + product.getName());
                }
            } else if (!restaurantId.equals(product.getRestaurantId())) {
                throw new RuntimeException("All products in one order must be from the same restaurant");
            }

            BigDecimal itemPrice = product.getPrice();
            BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(prodId);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPriceAtTime(itemPrice); // Use DB price for security
            orderItem.setNote(request.getNote());
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setRestaurantId(restaurantId);

        Order savedOrder = orderRepository.save(order);

        // Associate items with the saved order and save them
        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }

        return getOrderDetail(savedOrder.getId(), userId);
    }

    // DTO classes for responses
    @lombok.Data
    public static class CancelOrderResponse {
        private Integer id;
        private String status;
        private Integer refundTransactionId;
        private String message;
    }

    @lombok.Data
    public static class ReorderRequest {
        private String deliveryAddress;
        private String note;
    }

    @lombok.Data
    public static class ReorderResponse {
        private Integer orderId;
        private BigDecimal totalAmount;
        private String status;
        private java.time.LocalDateTime createdAt;
    }

    @lombok.Data
    public static class OrderTrackingResponse {
        private Integer orderId;
        private String currentStatus;
        private java.time.LocalDateTime estimatedDeliveryTime;
        private List<StatusHistoryItem> statusHistory;
        private DeliveryLocation deliveryLocation;
    }

    @lombok.Data
    public static class StatusHistoryItem {
        private String status;
        private java.time.LocalDateTime timestamp;
    }

    @lombok.Data
    public static class DeliveryLocation {
        private Double lat;
        private Double lng;
    }
}
