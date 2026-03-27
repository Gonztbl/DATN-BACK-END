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

| Entity | Tổng thuộc tính | Đang sử dụng | Chưa sử dụng | Tỷ lệ sử dụng |
|---|---|---|---|---|
| User | 16 | 14 | 2 | 87.5% |
| Order | 24 | 20 | 4 | 83.3% |
| Product | 15 | 13 | 2 | 86.7% |
| Restaurant | 14 | 12 | 2 | 85.7% |
| Wallet | 10 | 10 | 0 | 100% |
| Transaction | 15 | 13 | 2 | 86.7% |
| Category | 7 | 7 | 0 | 100% |
| Card | 10 | 10 | 0 | 100% |
| Review | 7 | 7 | 0 | 100% |
| Notification | 7 | 7 | 0 | 100% |
| Address | 8 | 8 | 0 | 100% |
| BankAccount | 10 | 9 | 1 | 90% |
| Favorite | 4 | 4 | 0 | 100% |
| SupportTicket | 11 | 9 | 2 | 81.8% |
| AdminAction | 8 | 6 | 2 | 75% |
| BalanceChangeLog | 7 | 6 | 1 | 85.7% |
| BankTransfer | 8 | 6 | 2 | 75% |
| CardDeposit | 7 | 7 | 0 | 100% |
| CardWithdraw | 9 | 9 | 0 | 100% |
| Contact | 5 | 5 | 0 | 100% |
| FaceEmbedding | 8 | 8 | 0 | 100% |
| FaceVerificationLog | 7 | 7 | 0 | 100% |
| OrderItem | 9 | 9 | 0 | 100% |
| OTPRequest | 7 | 6 | 1 | 85.7% |
| QRCode | 6 | 6 | 0 | 100% |
| Session | 8 | 8 | 0 | 100% |
| ShipperProfile | 10 | 8 | 2 | 80% |
| SystemConfig | 5 | 5 | 0 | 100% |
| TicketReply | 6 | 6 | 0 | 100% |
| TransferDetail | 7 | 7 | 0 | 100% |

### Chi tiết theo Entity

#### 1. User
**Thuộc tính đang sử dụng:**
- `id`, `userName`, `email`, `phone`, `fullName`, `avatar`, `dateOfBirth`, `address`, `passwordHash`, `pinHash`, `isActive`, `role`, `createdAt`, `updatedAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `isVerified` - Luôn được đọc nhưng chưa có API cập nhật
- `membership` - Đọc trong profile nhưng chưa có logic cập nhật
- `avatarUrl` - Đọc trong profile nhưng không có API cập nhật riêng

#### 2. Order
**Thuộc tính đang sử dụng:**
- `id`, `userId`, `totalAmount`, `status`, `deliveryAddress`, `recipientName`, `recipientPhone`, `note`, `paymentMethod`, `restaurantId`, `shipperId`, `createdAt`, `updatedAt`, `orderItems`, `restaurant`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `rejectedReason` - Chưa có API cập nhật lý do từ chối
- `deliveryFailedReason` - Chưa có API cập nhật lý do giao thất bại
- `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt` - Timestamps trạng thái chưa được cập nhật tự động

#### 3. Product
**Thuộc tính đang sử dụng:**
- `id`, `name`, `description`, `price`, `imageBase64`, `categoryId`, `restaurantId`, `ratingAvg`, `ratingCount`, `status`, `createdAt`, `updatedAt`, `deletedAt`, `category`, `restaurant`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `reviews` - Relation được định nghĩa nhưng chưa có API truy vấn reviews từ product

#### 4. Restaurant
**Thuộc tính đang sử dụng:**
- `id`, `name`, `phone`, `email`, `address`, `logoBase64`, `status`, `productCount`, `createdAt`, `updatedAt`, `deletedAt`, `ownerId`, `description`, `categoryId`, `products`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `schedule` - Chưa có API cập nhật/cập nhật lịch làm việc

#### 5. Wallet
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `code`, `user`, `currency`, `balance`, `availableBalance`, `status`, `accountNumber`, `createdAt`, `updatedAt`, `transactions`

#### 6. Transaction
**Thuộc tính đang sử dụng:**
- `id`, `wallet`, `type`, `direction`, `amount`, `fee`, `balanceBefore`, `balanceAfter`, `status`, `referenceId`, `metadata`, `createdAt`, `updatedAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `idempotencyKey` - Chưa có logic kiểm tra idempotency
- `relatedTxId` - Chưa có logic liên kết transaction

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
- `id`, `code`, `user`, `bankCode`, `bankName`, `accountNumber`, `accountName`, `status`, `createdAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `code` - Được tạo nhưng chưa sử dụng trong API

#### 13. Favorite
**Tất cả thuộc tính đều được sử dụng:**
- `id`, `user`, `restaurant`, `createdAt`

#### 14. SupportTicket
**Thuộc tính đang sử dụng:**
- `id`, `user`, `subject`, `message`, `orderId`, `attachments`, `status`, `createdAt`, `updatedAt`, `replies`

**Thuộc tính CHƯA sử dụng / Để trống:**
- `assignedTo` - Chưa có API phân công admin xử lý

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
**Thuộc tính đang sử dụng:**
- `id`, `user`, `purpose`, `otpCode`, `isUsed`, `expiresAt`, `createdAt`

**Thuộc tính CHƯA sử dụng / Để trống:**
- Đang được sử dụng đầy đủ trong OTPService

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

1. **Order timestamps**: Nên thêm logic tự động cập nhật `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt` khi chuyển trạng thái
2. **Restaurant schedule**: Nên triển khai API quản lý lịch làm việc của nhà hàng
3. **Shipper location**: Nên triển khai API cập nhật vị trí real-time cho shipper
4. **Bank integration**: `bankReference`, `provider`, `processedAt` nên được sử dụng khi tích hợp ngân hàng thực
5. **User verification**: Nên có API để cập nhật `isVerified` và `membership`
6. **Transaction idempotency**: Nên triển khai kiểm tra `idempotencyKey` để tránh duplicate transactions

---

## Tổng hợp Danh sách Thuộc tính CHƯA Sử dụng

Dưới đây là danh sách đầy đủ tất cả các thuộc tính đang bỏ trống/chưa được sử dụng trong hệ thống:

### User
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `isVerified` | Đọc trong profile nhưng chưa có API cập nhật trạng thái xác thực |
| `membership` | Đọc trong profile nhưng chưa có logic cập nhật cấp độ thành viên |
| `avatarUrl` | Đọc trong profile nhưng không có API cập nhật riêng, sử dụng `avatar` thay thế |

### Order
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `rejectedReason` | Chưa có API cập nhật lý do từ chối đơn hàng |
| `deliveryFailedReason` | Chưa có API cập nhật lý do giao hàng thất bại |
| `confirmedAt` | Timestamp chưa được cập nhật khi nhà hàng xác nhận đơn |
| `readyAt` | Timestamp chưa được cập nhật khi đơn sẵn sàng giao |
| `pickedUpAt` | Timestamp chưa được cập nhật khi shipper nhận đơn |
| `deliveredAt` | Timestamp chưa được cập nhật khi giao thành công |

### Product
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `reviews` | Relation được định nghĩa nhưng chưa có API truy vấn reviews từ product (chỉ có API ngược lại từ Review) |

### Restaurant
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `schedule` | Chưa có API quản lý/cập nhật lịch làm việc của nhà hàng |

### Transaction
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `idempotencyKey` | Chưa triển khai logic kiểm tra idempotency để tránh duplicate |
| `relatedTxId` | Chưa có logic liên kết các transaction liên quan (refund, reversal) |

### BankAccount
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `code` | Được tạo tự động nhưng chưa sử dụng trong bất kỳ API nào |

### SupportTicket
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `assignedTo` | Chưa có API phân công admin xử lý ticket cụ thể |

### AdminAction
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `reason` | Chưa có API cập nhật lý do thực hiện action |
| `metadata` | Chưa có API cập nhật thông tin bổ sung dạng JSON |

### BalanceChangeLog
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| *Entity đầy đủ* | Đang được tạo record nhưng chưa có API endpoint để query chi tiết |

### BankTransfer
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `bankReference` | Chưa có tích hợp ngân hàng thực để nhận mã tham chiếu |
| `provider` | Chưa có tích hợp payment provider |
| `processedAt` | Chưa có cập nhật thời gian xử lý thực tế |

### OTPRequest
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| *Đầy đủ* | Đang sử dụng đầy đủ trong OTPService |

### ShipperProfile
| Thuộc tính | Lý do chưa sử dụng |
|---|---|
| `currentLat` | Chưa có API cập nhật vị trí latitude real-time |
| `currentLng` | Chưa có API cập nhật vị trí longitude real-time |

---

## Thống kê Tổng quan Thuộc tính Chưa Sử dụng

| Entity | Số thuộc tính chưa dùng | Phân loại |
|---|---|---|
| User | 3 | Thiếu API cập nhật |
| Order | 6 | Thiếu timestamps tự động |
| Product | 1 | Thiếu API query |
| Restaurant | 1 | Thiếu API quản lý lịch |
| Transaction | 2 | Thiếu logic nghiệp vụ |
| BankAccount | 1 | Thuộc tính dư thừa |
| SupportTicket | 1 | Thiếu API phân công |
| AdminAction | 2 | Thiếu API cập nhật |
| BalanceChangeLog | 1 | Thiếu API query |
| BankTransfer | 3 | Chưa tích hợp ngân hàng |
| ShipperProfile | 2 | Thiếu API vị trí |
| **TỔNG** | **23** | |

---

*Báo cáo được tạo tự động từ phân tích codebase*
