# Báo cáo Cấu trúc Thuộc tính Các Entity Database

Dưới đây là chi tiết các bảng thuộc tính của hệ thống dựa trên các class `@Entity` trong source code:

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

### 10. Notification
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne` |
| `type` | String | |
| `title` | String | |
| `content` | String | `@Column(columnDefinition = "TEXT")` |
| `isRead` | boolean | |
| `createdAt` | LocalDateTime | |

### 11. Order
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
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
| `user` | User | `@ManyToOne` |
| `orderItems` | List<OrderItem>| `@OneToMany` |

### 12. OrderItem
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `orderId` | Integer | `@Column(nullable = false)` |
| `productId` | Integer | `@Column(nullable = false)` |
| `quantity` | Integer | `@Column(nullable = false)` |
| `priceAtTime` | BigDecimal | `@Column(precision = 10, scale = 2, nullable = false)` |
| `order` | Order | `@ManyToOne(fetch = FetchType.LAZY)` |
| `product` | Product | `@ManyToOne(fetch = FetchType.LAZY)` |

### 13. OTPRequest
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `user` | User | `@ManyToOne` |
| `purpose` | String | |
| `otpCode` | String | |
| `isUsed` | boolean | |
| `expiresAt` | LocalDateTime | |
| `createdAt` | LocalDateTime | |

### 14. Product
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

### 15. QRCode
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `wallet` | Wallet | `@ManyToOne` |
| `codeValue` | String | `@Column(columnDefinition = "TEXT")` |
| `type` | QRType | `@Enumerated(EnumType.STRING)` |
| `expiresAt` | LocalDateTime | |
| `createdAt` | LocalDateTime | |

### 16. Restaurant
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
| `products` | List<Product>| `@OneToMany` |

### 17. Review
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

### 18. Session
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

### 19. Transaction
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

### 20. TransferDetail
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `transaction` | Transaction | `@OneToOne` |
| `counterpartyWalletId`| Integer| |
| `counterpartyUserId`| Integer| |
| `note` | String | |
| `method` | TransferMethod | `@Enumerated(EnumType.STRING)` |
| `createdAt` | LocalDateTime | |

### 21. User
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
| `membership` | String | |

### 22. Wallet
| Thuộc tính | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `id` | Integer | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `code` | String | |
| `user` | User | `@OneToOne` |
| `currency` | String | |
| `balance` | Double | |
| `availableBalance`| Double | |
| `status` | WalletStatus | `@Enumerated(EnumType.STRING)` |
| `accountNumber` | String | |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |
