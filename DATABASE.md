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

## Tổng kết

- **Tổng số Entities:** 27
- **Tổng số Enums:** 14

**Entities mới thêm:**
- `FaceEmbedding` - Lưu trữ embedding vector khuôn mặt cho xác thực sinh trắc học
- `FaceVerificationLog` - Log lịch sử xác thực khuôn mặt
- `Address` - Địa chỉ giao hàng của người dùng
- `Favorite` - Nhà hàng yêu thích của người dùng
- `SupportTicket` - Yêu cầu hỗ trợ khách hàng
