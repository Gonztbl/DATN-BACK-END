# Báo cáo Cấu trúc Thuộc tính Các Entity Database

Dưới đây là chi tiết các bảng thuộc tính của hệ thống dựa trên các class `@Entity` trong source code:

## Danh sách Entities

### 1. AdminAction
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue` |
| `adminId` | Integer | |
| `actionType` | String | |
| `targetType` | String | |
| `targetId` | Integer | |
| `reason` | String | |
| `metadata` | String | `@Column(columnDefinition = "TEXT")` |
| `createdAt` | LocalDateTime | |

### 2. BalanceChangeLog
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue` |
| `wallet` | Wallet | `@ManyToOne` |
| `transaction` | Transaction | `@ManyToOne` |
| `delta` | Double | |
| `balanceBefore` | Double | |
| `balanceAfter` | Double | |
| `createdAt` | LocalDateTime | |

### 3. BankAccount
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `code` | String | |
| `user` | User | `@ManyToOne`, `@JoinColumn(name = "user_id")` |
| `bankCode` | String | |
| `bankName` | String | |
| `accountNumber` | String | |
| `accountName` | String | |
| `status` | BankAccountStatus | `@Enumerated(EnumType.STRING)` |
| `createdAt` | LocalDateTime | |

### 4. BankTransfer
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `transaction` | Transaction | `@OneToOne` |
| `bankAccount` | BankAccount | `@ManyToOne` |
| `bankReference` | String | |
| `provider` | String | |
| `processedAt` | LocalDateTime | |
| `status` | BankTransferStatus | `@Enumerated(EnumType.STRING)` |

### 5. Card
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `cardNumber` | String | `@Column(nullable = false, unique = true)` |
| `cardHolderName`| String | |
| `expiryDate` | String | Format MM/yy |
| `cvv` | String | |
| `bankName` | String | |
| `type` | String | CREDIT, DEBIT |
| `status` | CardStatus | `@Enumerated(EnumType.STRING)` |
| `balanceCard` | Double | Default = `1000000000.0` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)` |

### 6. CardDeposit
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `card` | Card | `@ManyToOne(fetch = FetchType.LAZY)` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)` |
| `amount` | Double | `@Column(nullable = false)` |
| `description` | String | |
| `timestamp` | LocalDateTime | `@Column(nullable = false)` |
| `status` | DepositStatus | `@Enumerated(EnumType.STRING)` |

### 7. CardWithdraw
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)` |
| `card` | Card | `@ManyToOne(fetch = FetchType.LAZY)` |
| `amount` | Double | `@Column(nullable = false)` |
| `description` | String | `@Column(length = 500)` |
| `status` | CardWithdrawStatus | `@Enumerated(EnumType.STRING)` |
| `createdAt` | LocalDateTime | `@Column(nullable = false)` |
| `updatedAt` | LocalDateTime | |

### 8. Category
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `name` | String | `@Column(nullable = false, length = 100)` |
| `icon` | String | `@Column(length = 50)` |
| `orderIndex` | Integer | Default = `0` |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `products` | List<Product>| `@OneToMany(mappedBy = "category")` |

### 9. Contact
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne`, `@JoinColumn(nullable = false)` |
| `name` | String | |
| `avatarUrl` | String | |
| `accountNumber` | String | |

### 10. FaceEmbedding
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Long | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `userId` | Integer | `@Column(nullable = false)` |
| `embedding` | String | `@Column(columnDefinition = "TEXT")` - JSON array string |
| `pose` | String | `@Column(length = 20)` - front / left / right |
| `modelVersion` | String | `@Column(length = 50)` - e.g., "arcface_r100_v1" |
| `qualityScore` | Double | blur score from quality check |
| `faceAngle` | Double | rotation angle of detected face |
| `createdAt` | LocalDateTime | `@CreationTimestamp` |

### 11. FaceVerificationLog
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Long | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `userId` | Integer | `@Column(nullable = false)` |
| `similarity` | Double | |
| `result` | String | `@Column(length = 10)` - PASS / FAIL |
| `ip` | String | `@Column(length = 50)` |
| `deviceId` | String | `@Column(length = 100)` |
| `createdAt` | LocalDateTime | `@CreationTimestamp` |

### 12. Notification
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne` |
| `type` | String | |
| `title` | String | |
| `content` | String | `@Column(columnDefinition = "TEXT")` |
| `isRead` | boolean | |
| `createdAt` | LocalDateTime | |

### 13. Order
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `userId` | Integer | `@Column(nullable = false)` |
| `totalAmount` | BigDecimal | `@Column(precision = 10, scale = 2, nullable = false)` |
| `status` | OrderStatus | Enum, Default = `PENDING` |
| `deliveryAddress`| String | `@Column(columnDefinition = "TEXT", nullable = false)` |
| `recipientName`| String | `@Column(nullable = false)` |
| `recipientPhone`| String | `@Column(nullable = false)` |
| `note` | String | |
| `paymentMethod`| String | |
| `restaurantId` | String | `@Column(name = "restaurant_id")` |
| `shipperId` | Integer | `@Column(name = "shipper_id")` - ID của shipper giao hàng |
| `rejectedReason` | String | `@Column(name = "rejected_reason", columnDefinition = "TEXT")` - Lý do từ chối đơn |
| `deliveryFailedReason` | String | `@Column(name = "delivery_failed_reason", columnDefinition = "TEXT")` - Lý do giao thất bại |
| `confirmedAt` | LocalDateTime | `@Column(name = "confirmed_at")` - Thời gian xác nhận đơn |
| `readyAt` | LocalDateTime | `@Column(name = "ready_at")` - Thời gian sẵn sàng để giao |
| `pickedUpAt` | LocalDateTime | `@Column(name = "picked_up_at")` - Thời gian shipper lấy hàng |
| `deliveredAt` | LocalDateTime | `@Column(name = "delivered_at")` - Thời gian giao hàng thành công |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `user` | User | `@ManyToOne` |
| `restaurant` | Restaurant | `@ManyToOne(fetch = FetchType.LAZY)` |
| `orderItems` | List<OrderItem>| `@OneToMany` |

### 14. OrderItem
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `orderId` | Integer | `@Column(nullable = false)` |
| `productId` | Integer | `@Column(nullable = false)` |
| `quantity` | Integer | `@Column(nullable = false)` |
| `priceAtTime` | BigDecimal | `@Column(precision = 10, scale = 2, nullable = false)` |
| `note` | String | `@Column(columnDefinition = "TEXT")` - Ghi chú cho món |
| `order` | Order | `@ManyToOne(fetch = FetchType.LAZY)` |
| `product` | Product | `@ManyToOne(fetch = FetchType.LAZY)` |

### 15. OTPRequest
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne` |
| `purpose` | String | |
| `otpCode` | String | |
| `isUsed` | boolean | |
| `expiresAt` | LocalDateTime | |
| `createdAt` | LocalDateTime | |

### 16. Product
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `name` | String | `@Column(nullable = false)` |
| `description` | String | `@Column(columnDefinition = "TEXT")` |
| `price` | BigDecimal | `@Column(precision = 10, scale = 2, nullable = false)` |
| `imageBase64` | String | `@Column(columnDefinition = "LONGTEXT")` |
| `categoryId` | Integer | `@Column(nullable = false)` |
| `restaurantId` | String | `@Column(nullable = false)` |
| `ratingAvg` | BigDecimal | Default = `0` |
| `ratingCount` | Integer | Default = `0` |
| `status` | String | Default = `"available"` |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `deletedAt` | LocalDateTime | |
| `category` | Category | `@ManyToOne(fetch = FetchType.LAZY)` |
| `restaurant` | Restaurant | `@ManyToOne(fetch = FetchType.LAZY)` |
| `reviews` | List<Review> | `@OneToMany` |

### 17. QRCode
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `wallet` | Wallet | `@ManyToOne` |
| `codeValue` | String | `@Column(columnDefinition = "TEXT")` |
| `type` | QRType | `@Enumerated(EnumType.STRING)` |
| `expiresAt` | LocalDateTime | |
| `createdAt` | LocalDateTime | |

### 18. Restaurant
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | String | `@Id`, `@Column(length = 20)` |
| `name` | String | `@Column(nullable = false)` |
| `phone` | String | |
| `email` | String | |
| `address` | String | `@Column(columnDefinition = "TEXT", nullable = false)` |
| `logoBase64` | String | `@Column(columnDefinition = "LONGTEXT")` |
| `status` | Boolean | Default = `true` |
| `productCount` | Integer | Default = `0` |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `deletedAt` | LocalDateTime | |
| `ownerId` | Integer | `@Column(name = "owner_id")` - ID của chủ nhà hàng |
| `products` | List<Product>| `@OneToMany` |

### 19. Review
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `userId` | Integer | `@Column(nullable = false)` |
| `productId` | Integer | `@Column(nullable = false)` |
| `rating` | Integer | `@Column(nullable = false)` |
| `comment` | String | `@Column(columnDefinition = "TEXT")` |
| `createdAt` | LocalDateTime | |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)` |
| `product` | Product | `@ManyToOne(fetch = FetchType.LAZY)` |

### 20. Session
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne` |
| `deviceInfo` | String | |
| `ipAddress` | String | |
| `refreshTokenHash`| String| |
| `revoked` | boolean | |
| `createdAt` | LocalDateTime | |
| `lastActiveAt` | LocalDateTime | |

### 21. Transaction
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `wallet` | Wallet | `@ManyToOne` |
| `type` | TransactionType| `@Enumerated(EnumType.STRING)` |
| `direction` | TransactionDirection| `@Enumerated(EnumType.STRING)` |
| `amount` | Double | |
| `fee` | Double | |
| `balanceBefore` | Double | |
| `balanceAfter` | Double | |
| `status` | TransactionStatus| `@Enumerated(EnumType.STRING)` |
| `referenceId` | String | |
| `idempotencyKey`| String | |
| `relatedTxId` | Integer | |
| `metadata` | String | `@Column(columnDefinition = "TEXT")` |
| `createdAt` | LocalDateTime | `@CreationTimestamp` |
| `updatedAt` | LocalDateTime | `@UpdateTimestamp` |

### 22. TransferDetail
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `transaction` | Transaction | `@OneToOne` |
| `counterpartyWalletId`| Integer| |
| `counterpartyUserId`| Integer| |
| `note` | String | |
| `method` | TransferMethod | `@Enumerated(EnumType.STRING)` |
| `createdAt` | LocalDateTime | |

### 23. User
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `userName` | String | `@Column(nullable = false, unique = true)` |
| `email` | String | `@Column(nullable = false, unique = true)` |
| `phone` | String | |
| `fullName` | String | |
| `avatar` | String | `@Column(columnDefinition = "MEDIUMTEXT")` |
| `dateOfBirth` | LocalDate | |
| `address` | String | `@Column(length = 255)` |
| `passwordHash` | String | `@Column(nullable = false)` |
| `pinHash` | String | |
| `isActive` | boolean | |
| `isVerified` | boolean | |
| `role` | Role | `@Enumerated(EnumType.STRING)` |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `wallet` | Wallet | `@OneToOne(mappedBy = "user")` |
| `avatarUrl` | String | |
| `membership` | String | Silver, Gold, Platinum |

### 24. Wallet
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `code` | String | |
| `user` | User | `@OneToOne` |
| `currency` | String | |
| `balance` | Double | |
| `availableBalance`| Double | |
| `status` | WalletStatus | `@Enumerated(EnumType.STRING)` |
| `accountNumber` | String | Số tài khoản = số điện thoại |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |

### 25. Address
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)`, `@JoinColumn(name = "user_id", nullable = false)` |
| `recipientName` | String | `@Column(name = "recipient_name", nullable = false)` |
| `phone` | String | `@Column(nullable = false)` |
| `address` | String | `@Column(nullable = false, columnDefinition = "TEXT")` |
| `isDefault` | Boolean | `@Column(name = "is_default")`, Default = `false` |
| `createdAt` | LocalDateTime | `@Column(name = "created_at")` |
| `updatedAt` | LocalDateTime | `@Column(name = "updated_at")` |

### 26. Favorite
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)`, `@JoinColumn(name = "user_id", nullable = false)` |
| `restaurant` | Restaurant | `@ManyToOne(fetch = FetchType.LAZY)`, `@JoinColumn(name = "restaurant_id", nullable = false)` |
| `createdAt` | LocalDateTime | `@Column(name = "created_at")` |

### 27. SupportTicket
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne(fetch = FetchType.LAZY)`, `@JoinColumn(name = "user_id", nullable = false)` |
| `subject` | String | `@Column(nullable = false)` |
| `message` | String | `@Column(nullable = false, columnDefinition = "TEXT")` |
| `orderId` | Integer | `@Column(name = "order_id")` |
| `attachments` | String | `@Column(columnDefinition = "TEXT")` - JSON array of base64 strings |
| `status` | TicketStatus | `@Enumerated(EnumType.STRING)`, Default = `OPEN` |
| `createdAt` | LocalDateTime | `@Column(name = "created_at")` |
| `updatedAt` | LocalDateTime | `@Column(name = "updated_at")` |

---

## Danh sách Enums

### BankAccountStatus
- `ACTIVE`
- `PENDING`
- `REVOKED`

### BankTransferStatus
- `PENDING`
- `SUCCESS`
- `FAILED`

### CardStatus
- `ACTIVE`
- `INACTIVE`
- `LOCKED`

### CardWithdrawStatus
- `PENDING`
- `SUCCESS`
- `FAILED`

### Order.OrderStatus
- `PENDING`
- `CONFIRMED`
- `PREPARING`
- `READY_FOR_PICKUP`
- `DELIVERING`
- `COMPLETED`
- `CANCELLED`
- `DELIVERY_FAILED`

### CardDeposit.DepositStatus
- `PENDING`
- `SUCCESS`
- `FAILED`

### QRType
- `STATIC`
- `DYNAMIC`

### Role
- `USER`
- `ADMIN`
- `SUPPORT`
- `RESTAURANT_OWNER`
- `SHIPPER`

### TransactionDirection
- `IN`
- `OUT`

### TransactionStatus
- `PENDING`
- `COMPLETED`
- `FAILED`

### TransactionType
- `DEPOSIT`
- `WITHDRAW`
- `TRANSFER_IN`
- `TRANSFER_OUT`

### TransferMethod
- `IN_APP`
- `QR`
- `BANK`

### WalletStatus
- `ACTIVE`
- `FROZEN`
- `CLOSED`

### SupportTicket.TicketStatus
- `OPEN`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

---

## Báo cáo Phân tích Sử dụng Thuộc tính (API Usage Report)

Báo cáo này được tạo sau khi phân tích toàn bộ codebase (controllers, services, repositories) để xác định thuộc tính nào đang được sử dụng thực tế trong các API.

### Tổng quan

> Cập nhật lại theo code hiện tại: một số dòng trong báo cáo cũ đã lỗi thời. Các field như `User.isVerified`, `User.membership`, `User.avatarUrl`, `Order.rejectedReason`, `Order.deliveryFailedReason`, các timestamp trạng thái của `Order`, `Restaurant.schedule`, `Transaction.idempotencyKey` và `SupportTicket.assignedTo` đều đã có reference trong service/controller hiện tại.

| Nhóm Entity | Mức độ sử dụng | Ghi chú |
|---|---|---|
| `users`, `wallets`, `transactions` | Rất cao | Nhóm lõi xác thực, ví và dòng tiền |
| `orders`, `order_items`, `products`, `restaurants`, `categories`, `reviews`, `addresses` | Rất cao | Nhóm food ordering chính |
| `cards`, `bank_accounts`, `card_deposits`, `card_withdraws`, `favorites`, `notifications`, `support_tickets` | Trung bình - cao | Tính năng thanh toán và hỗ trợ |
| `otp_requests`, `sessions`, `qr_codes`, `face_embeddings`, `face_verification_logs`, `shipper_profiles`, `system_configs` | Trung bình | Tính năng bổ trợ / bảo mật / vận hành |
| `admin_actions`, `balance_change_logs`, `bank_transfers`, `transfer_details` | Thấp - trung bình | Audit, cleanup và một số flow chuyên biệt |

### Chi tiết theo Entity

#### 1. User
**Thuộc tính đang sử dụng:**
- `id`, `userName`, `email`, `phone`, `fullName`, `avatar`, `dateOfBirth`, `address`, `passwordHash`, `pinHash`, `isActive`, `isVerified`, `role`, `createdAt`, `updatedAt`, `avatarUrl`, `membership`

**Ghi chú cập nhật:**
- `isVerified` đang được cập nhật trong luồng đăng ký/xóa dữ liệu khuôn mặt
- `membership` và `avatarUrl` đang được map ra DTO/profile, tuy chưa có luồng quản trị riêng biệt

#### 2. Order
**Thuộc tính đang sử dụng:**
- `id`, `userId`, `totalAmount`, `status`, `deliveryAddress`, `recipientName`, `recipientPhone`, `note`, `paymentMethod`, `restaurantId`, `shipperId`, `rejectedReason`, `deliveryFailedReason`, `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt`, `createdAt`, `updatedAt`, `orderItems`, `restaurant`

**Ghi chú cập nhật:**
- `rejectedReason` và `deliveryFailedReason` đã được set trong các flow từ chối/giao thất bại
- `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt` đã được cập nhật trong luồng nhà hàng và shipper

#### 3. Product
**Thuộc tính đang sử dụng:**
- `id`, `name`, `description`, `price`, `imageBase64`, `categoryId`, `restaurantId`, `ratingAvg`, `ratingCount`, `status`, `createdAt`, `updatedAt`, `deletedAt`, `category`, `restaurant`

**Thuộc tính dùng gián tiếp / ít dùng trực tiếp:**
- `reviews` - Quan hệ ORM có tồn tại, nhưng API hiện chủ yếu query review qua `ReviewRepository` thay vì truy cập trực tiếp từ `Product`

#### 4. Restaurant
**Thuộc tính đang sử dụng:**
- `id`, `name`, `phone`, `email`, `address`, `logoBase64`, `status`, `productCount`, `createdAt`, `updatedAt`, `deletedAt`, `ownerId`, `description`, `categoryId`, `schedule`, `products`

**Ghi chú cập nhật:**
- `schedule` đang được đọc/ghi trong luồng cập nhật thông tin nhà hàng, không còn là field bỏ trống

#### 5. Wallet
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `code`, `user`, `currency`, `balance`, `availableBalance`, `status`, `accountNumber`, `createdAt`, `updatedAt`, `transactions`

#### 6. Transaction
**Thuộc tính đang sử dụng:**
- `id`, `wallet`, `type`, `direction`, `amount`, `fee`, `balanceBefore`, `balanceAfter`, `status`, `referenceId`, `idempotencyKey`, `metadata`, `createdAt`, `updatedAt`

**Thuộc tính dùng nhẹ / chưa thấy dùng rõ:**
- `relatedTxId` - Chưa thấy được khai thác rõ trong các flow refund/reversal hiện tại

#### 7. Category
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `name`, `icon`, `orderIndex`, `createdAt`, `updatedAt`, `products`

#### 8. Card
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `cardNumber`, `cardHolderName`, `expiryDate`, `cvv`, `bankName`, `type`, `status`, `balanceCard`, `user`

#### 9. Review
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `userId`, `productId`, `rating`, `comment`, `createdAt`, `user`, `product`

#### 10. Notification
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `type`, `title`, `content`, `isRead`, `createdAt`

#### 11. Address
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `recipientName`, `phone`, `address`, `isDefault`, `createdAt`, `updatedAt`

#### 12. BankAccount
**Thuộc tính đang sử dụng:**
- `id`, `user`, `bankCode`, `bankName`, `accountNumber`, `accountName`, `status`, `createdAt`

**Thuộc tính dùng nhẹ / chưa thấy dùng rõ:**
- `code` - Được tạo trong model nhưng hiện chưa thấy được khai thác rõ ở API nghiệp vụ

#### 13. Favorite
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `restaurant`, `createdAt`

#### 14. SupportTicket
**Thuộc tính đang sử dụng:**
- `id`, `user`, `subject`, `message`, `orderId`, `attachments`, `status`, `createdAt`, `updatedAt`, `assignedTo`, `replies`

**Ghi chú cập nhật:**
- `assignedTo` đã được dùng trong luồng admin nhận/xử lý ticket, không còn là field bỏ trống

#### 15. AdminAction
**Thuộc tính đang sử dụng:**
- `id`, `adminId`, `actionType`, `targetType`, `targetId`, `createdAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `reason` - Chưa có API cập nhật lý do
- `metadata` - Chưa có API cập nhật metadata

#### 16. BalanceChangeLog
**Thuộc tính đang sử dụng:**
- `id`, `wallet`, `transaction`, `delta`, `balanceBefore`, `balanceAfter`, `createdAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- Đang được tạo nhưng chưa có API query chi tiết

#### 17. BankTransfer
**Thuộc tính đang sử dụng:**
- `id`, `transaction`, `bankAccount`, `status`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `bankReference` - Chưa có tích hợp ngân hàng thực
- `provider` - Chưa có tích hợp provider
- `processedAt` - Chưa có cập nhật thời gian xử lý

#### 18. CardDeposit
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `card`, `user`, `amount`, `description`, `timestamp`, `status`

#### 19. CardWithdraw
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `card`, `amount`, `description`, `status`, `createdAt`, `updatedAt`

#### 20. Contact
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `name`, `avatarUrl`, `accountNumber`

#### 21. FaceEmbedding
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `userId`, `embedding`, `pose`, `modelVersion`, `qualityScore`, `faceAngle`, `createdAt`

#### 22. FaceVerificationLog
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `userId`, `similarity`, `result`, `ip`, `deviceId`, `createdAt`

#### 23. OrderItem
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `orderId`, `productId`, `quantity`, `priceAtTime`, `note`, `order`, `product`

#### 24. OTPRequest
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `purpose`, `otpCode`, `isUsed`, `expiresAt`, `createdAt`

#### 25. QRCode
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `wallet`, `codeValue`, `type`, `expiresAt`, `createdAt`

#### 26. Session
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `deviceInfo`, `ipAddress`, `refreshTokenHash`, `revoked`, `createdAt`, `lastActiveAt`

#### 27. ShipperProfile
**Thuộc tính đang sử dụng:**
- `id`, `userId`, `vehicleType`, `vehiclePlate`, `isOnline`, `createdAt`, `updatedAt`, `user`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `currentLat`, `currentLng` - Chưa có API cập nhật vị trí real-time

#### 28. SystemConfig
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `configKey`, `configValue`, `description`, `updatedAt`

#### 29. TicketReply
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `ticket`, `adminId`, `message`, `createdAt`

#### 30. TransferDetail
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `transaction`, `counterpartyWalletId`, `counterpartyUserId`, `note`, `method`, `createdAt`

### Khuyến nghị

1. **Shipper location**: Nên bổ sung API cập nhật `currentLat`/`currentLng` nếu muốn theo dõi vị trí real-time.
2. **Bank integration**: `provider`, `processedAt` và cơ chế đồng bộ `bankReference` ở `BankTransfer` nên được hoàn thiện khi tích hợp ngân hàng thật.
3. **Transaction linking**: Có thể bổ sung use case rõ ràng cho `relatedTxId` nếu sau này hỗ trợ refund/reversal.
4. **Documentation hygiene**: Nên tiếp tục cập nhật `DATABASE.md` theo code hiện tại để tránh sai lệch giữa tài liệu và implementation.

---

## Tổng hợp Danh sách Thuộc tính Dùng Nhẹ / Chưa Thấy Dùng Rõ

Sau khi đối chiếu lại code hiện tại, nhiều mục từng được đánh dấu “chưa sử dụng” thực tế **đã có logic dùng**. Hiện tại các field cần xem là **dùng nhẹ hoặc chưa thấy khai thác rõ** chủ yếu gồm:

### Transaction
| Thuộc tính | Ghi chú |
|---|---|
| `relatedTxId` | Chưa thấy được dùng rõ trong flow hoàn tiền / đảo giao dịch |

### BankAccount
| Thuộc tính | Ghi chú |
|---|---|
| `code` | Có trong entity nhưng chưa thấy khai thác rõ ở API nghiệp vụ |

### Product
| Thuộc tính | Ghi chú |
|---|---|
| `reviews` | Quan hệ ORM có tồn tại, nhưng API hiện đang query review chủ yếu từ `ReviewRepository` |

### BankTransfer
| Thuộc tính | Ghi chú |
|---|---|
| `bankReference` | Có khái niệm trong luồng webhook, nhưng việc lưu/use trong `BankTransfer` còn mỏng |
| `provider` | Chưa thấy tích hợp provider hoàn chỉnh |
| `processedAt` | Chưa thấy cập nhật thời điểm xử lý một cách ổn định |

### ShipperProfile
| Thuộc tính | Ghi chú |
|---|---|
| `currentLat` | Chưa có API cập nhật vị trí latitude real-time |
| `currentLng` | Chưa có API cập nhật vị trí longitude real-time |

---

## Kết luận cập nhật

Các field sau đây **đang được dùng thật trong code hiện tại** và không nên tiếp tục xếp vào nhóm “chưa sử dụng”:

- `User.isVerified`, `User.membership`, `User.avatarUrl`
- `Order.rejectedReason`, `Order.deliveryFailedReason`
- `Order.confirmedAt`, `Order.readyAt`, `Order.pickedUpAt`, `Order.deliveredAt`
- `Restaurant.schedule`
- `Transaction.idempotencyKey`
- `SupportTicket.assignedTo`

---

*Báo cáo đã được cập nhật lại theo codebase hiện tại*
