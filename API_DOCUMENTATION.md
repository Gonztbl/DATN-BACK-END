# API Documentation - E-Wallet Backend System

**Last Updated**: 2026-01-15
**Version**: 4.0

---

## API Summary

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### User & Profile APIs  
- `GET /api/user/profile` - Lấy thông tin cá nhân
- `PUT /api/user/profile` - Cập nhật thông tin cá nhân
- `PUT /api/user/profile/avatar` - Cập nhật avatar
- `GET /api/me` - Lấy thông tin người dùng hiện tại

### Card APIs
- `GET /api/cards` - Danh sách thẻ của user hiện tại
- `GET /api/cards/{userId}/users` - Danh sách thẻ theo userId
- `POST /api/cards` - Thêm thẻ mới
- `POST /api/cards/deposit` - Nạp tiền từ thẻ
- `POST /api/cards/withdraw` - Rút tiền từ thẻ

### Wallet APIs
- `GET /api/wallet/balance` - Xem số dư ví
- `GET /api/wallet/me` - Lấy thông tin ví (QR)

### Wallet Transfer APIs
- `POST /api/wallet/transfers` - Chuyển tiền giữa các ví
- `GET /api/wallet/transfers/recent` - Lịch sử giao dịch gần đây
- `GET /api/wallet/transfers/lookup/{accountNumber}` - Tìm kiếm tài khoản

### E-Wallet Transfer APIs
- `GET /api/user/E-Wallet/transfers/wallet/{walletId}/history` - Lịch sử chuyển khoản theo ví
- `GET /api/user/E-Wallet/transfers/{transferId}` - Chi tiết chuyển khoản
- `GET /api/user/E-Wallet/transfers/wallets/search` - Tìm kiếm ví theo số điện thoại
- `POST /api/user/E-Wallet/transfers` - Chuyển tiền qua điện thoại

### Transaction APIs
- `GET /api/transactions` - Lịch sử giao dịch (phân trang)
- `POST /api/transactions/transfer` - Chuyển tiền
- `POST /api/transactions` - Nạp tiền (topup)

### Admin APIs
- `GET /api/admin/transactions` - Xem tất cả giao dịch
- `GET /api/admin/wallets` - Xem tất cả ví
- `PUT /api/admin/wallets/lock/{id}` - Khóa ví
- `PUT /api/admin/wallets/unlock/{id}` - Mở khóa ví

### User Manager APIs
- `GET /api/userManager/all` - Xem tất cả users
- `PUT /api/userManager/lock/{id}` - Khóa user
- `PUT /api/userManager/unlock/{id}` - Mở khóa user

### QR Code APIs
- `GET /api/qr/wallet` - Tạo QR Code cho ví
- `GET /api/qr/wallet/download` - Tải QR Image
- `POST /api/qr/wallet/with-amount` - Tạo QR với số tiền
- `POST /api/qr/resolve` - Giải mã QR Payload
- `POST /api/qr/read-image` - Đọc ảnh QR thành JSON

### Bank Account APIs
- `GET /api/bank-account` - Danh sách tài khoản ngân hàng

### Food Ordering APIs
- `GET /api/categories` - Lấy danh sách danh mục món ăn
- `GET /api/products` - Lấy danh sách sản phẩm (món ăn)
- `GET /api/products/{id}` - Lấy chi tiết một sản phẩm
- `GET /api/products/{id}/reviews` - Lấy danh sách đánh giá của sản phẩm
- `POST /api/orders` - Đặt hàng (checkout)

### Admin Category Management APIs
- `GET /api/admin/categories` - Lấy danh sách danh mục (có phân trang, tìm kiếm, sắp xếp)
- `GET /api/admin/categories/{id}` - Lấy chi tiết một danh mục
- `POST /api/admin/categories` - Thêm mới danh mục
- `PUT /api/admin/categories/{id}` - Cập nhật danh mục
- `DELETE /api/admin/categories/{id}` - Xóa danh mục
- `GET /api/admin/categories/export` - Xuất dữ liệu danh mục (Excel/CSV)
- `GET /api/admin/categories/check-name` - Kiểm tra tên danh mục đã tồn tại

---

## 1. Authentication APIs

### 1.1 Register
**Endpoint**: `POST /api/auth/register`

**Request Body**:
```json
{
  "userName": "newuser",
  "email": "newuser@example.com", 
  "phone": "0987654321",
  "fullName": "Nguyen Van New",
  "passwordHash": "123456"
}
```

**Response**:
```json
{
  "message": "User registered successfully",
  "userId": 123,
  "accountNumber": "0987654321"
}
```

### 1.2 Login
**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "userName": "testuser",
  "passwordHash": "123456"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 36000000,
  "userName": "testuser",
  "email": "testuser@example.com",
  "fullName": "Test User",
  "roles": ["USER"],
  "avatar": "base64-avatar-string",
  "membership": "STANDARD",
  "status": "ACTIVE",
  "isActive": true
}
```

---

## 2. Card APIs

### 2.1 Get Cards
**Endpoint**: `GET /api/cards`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
[
  {
    "id": 1,
    "cardNumber": "**** **** **** 2103",
    "holderName": "NGUYEN VAN A",
    "expiryDate": "05/25",
    "type": "DEBIT",
    "bankName": "Vietcombank",
    "balanceCard": 100000000000000000000.0,
    "status": "ACTIVE",
    "last4": "2103"
  }
]
```

### 2.2 Add Card
**Endpoint**: `POST /api/cards`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "cardNumber": "5702676235112103",
  "holderName": "NGUYEN VAN A",
  "expiryDate": "05/25",
  "cvv": "477",
  "type": "DEBIT",
  "bankName": "Vietcombank",
  "balanceCard": 500000.0
}
```

**Response**:
```json
{
  "id": 123,
  "cardNumber": "**** **** **** 2103",
  "holderName": "NGUYEN VAN A",
  "expiryDate": "05/25",
  "type": "DEBIT",
  "bankName": "Vietcombank",
  "balanceCard": 500000.0,
  "status": "ACTIVE",
  "last4": "2103"
}
```

---

## 3. Wallet APIs

### 3.1 Get Balance
**Endpoint**: `GET /api/wallet/balance`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "balance": 5000000.0,
  "monthlyChangePercent": 2.5
}
```

---

## 4. Wallet Transfer APIs

### 4.1 Transfer Money
**Endpoint**: `POST /api/wallet/transfers`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "toAccountNumber": "0987654321",
  "amount": 1000.00,
  "description": "Thanh toán hóa đơn"
}
```

**Response**:
```json
{
  "transactionId": 12345,
  "fromAccountNumber": "0123456789",
  "toAccountNumber": "0987654321",
  "toAccountName": "Nguyen Van B",
  "amount": 1000.00,
  "previousBalance": 5000.00,
  "newBalance": 4000.00,
  "description": "Thanh toán hóa đơn",
  "timestamp": "2026-01-14T01:00:00",
  "status": "SUCCESS",
  "message": "Transfer successful",
  "transactionType": "WALLET_TRANSFER"
}
```

### 4.2 Get Recent Transactions
**Endpoint**: `GET /api/wallet/transfers/recent?limit=10`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `limit`: Số lượng record (default: 10, max: 100)

**Response**:
```json
[
  {
    "transactionId": 12345,
    "transactionType": "TRANSFER_OUT",
    "direction": "OUT",
    "amount": 1000.00,
    "balanceBefore": 5000.00,
    "balanceAfter": 4000.00,
    "partnerAccountNumber": "0987654321",
    "partnerAccountName": "Nguyen Van B",
    "description": "Thanh toán hóa đơn",
    "timestamp": "2026-01-14T01:00:00",
    "status": "COMPLETED",
    "referenceId": "0987654321"
  }
]
```

### 4.3 Lookup Account
**Endpoint**: `GET /api/wallet/transfers/lookup/{accountNumber}`

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `accountNumber`: Số tài khoản cần tìm

**Response**:
```json
{
  "accountNumber": "0987654321",
  "accountName": "Nguyen Van B",
  "accountType": "WALLET",
  "active": true,
  "found": true,
  "message": "Account found"
}
```

---

## 5. Transaction APIs

### 5.1 Get Transactions
**Endpoint**: `GET /api/transactions?page=0&size=10`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `page`: Số trang (default: 0)
- `size`: Số lượng record (default: 10)

**Response**:
```json
{
  "content": [
    {
      "id": 123,
      "type": "TRANSFER",
      "amount": 1000.00,
      "date": "2026-01-14T01:00:00",
      "status": "COMPLETED",
      "category": "General",
      "direction": "OUT"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 5.2 Transfer Money
**Endpoint**: `POST /api/transactions/transfer`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "toWalletId": 456,
  "amount": 1000.00,
  "description": "Chuyển tiền"
}
```

**Response**:
```json
{
  "transactionId": 12345,
  "fromWalletId": 123,
  "toWalletId": 456,
  "amount": 1000.00,
  "description": "Chuyển tiền",
  "timestamp": "2026-01-14T01:00:00",
  "status": "SUCCESS"
}
```

---

## 6. Admin APIs

### 6.1 Get All Transactions
**Endpoint**: `GET /api/admin/transactions`

**Query Parameters**:
- `page`: Số trang (default: 0)
- `size`: Số lượng record (default: 50)

**Response**:
```json
[
  {
    "transactionId": "TXN-882941",
    "walletId": "WAL-00923",
    "amount": 1200,
    "status": "COMPLETE",
    "type": "DEPOSIT",
    "createdAt": "2023-11-20T14:35:00"
  }
]
```

---

### 6.2 Get All Wallets
**Endpoint**: `GET /api/admin/wallets`

**Query Parameters**:
- `page`: Số trang (default: 0)
- `size`: Số lượng record (default: 50)

**Response**:
```json
[
  {
    "id": 1,
    "accountNumber": "0987654321",
    "availableBalance": 1500000.0,
    "createdAt": "2023-11-20T14:35:00",
    "status": "ACTIVE",
    "userId": 2
  }
]
```

---

### 6.3 Lock Wallet
**Endpoint**: `PUT /api/admin/wallets/lock/{id}`

**Path Parameters**:
- `id`: Wallet ID

**Response**:
```json
"Wallet locked successfully"
```

---

### 6.4 Unlock Wallet
**Endpoint**: `PUT /api/admin/wallets/unlock/{id}`

**Path Parameters**:
- `id`: Wallet ID

**Response**:
```json
"Wallet unlocked successfully"
```

---

## 7. QR Code APIs

### 7.1 Generate QR Code
**Endpoint**: `GET /api/qr/wallet`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "walletCode": "WALLET123",
  "accountNumber": "0987654321"
}
```

### 7.2 Generate QR with Amount
**Endpoint**: `POST /api/qr/wallet/with-amount`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "amount": 100000.00
}
```

**Response**:
```json
{
  "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "walletCode": "WALLET123",
  "accountNumber": "0987654321",
  "amount": 100000.00
}
```

### 7.3 Resolve QR
**Endpoint**: `POST /api/qr/resolve`

**Request Body**:
```json
{
  "qrData": "WALLET123"
}
```

**Response**:
```json
{
  "walletCode": "WALLET123",
  "accountNumber": "0987654321",
  "amount": null
}
```

---

## 10. Food Ordering APIs

### 10.1 Get Categories
**Endpoint**: `GET /api/categories`

**Description**: Lấy danh sách danh mục món ăn để hiển thị các nút lọc ở đầu trang

**Response**:
```json
[
  { "id": 1, "name": "Tất cả", "icon": "restaurant" },
  { "id": 2, "name": "Bánh Mì", "icon": "lunch_dining" },
  { "id": 3, "name": "Phở & Bún", "icon": "ramen_dining" },
  { "id": 4, "name": "Trà Sữa", "icon": "bubbles" },
  { "id": 5, "name": "Cà phê", "icon": "local_cafe" }
]
```

**Database Table**: `categories`
- `id` (INT AUTO_INCREMENT, Primary Key)
- `name` (VARCHAR(100), Not Null)
- `icon` (VARCHAR(50))
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

---

### 10.2 Get Products
**Endpoint**: `GET /api/products`

**Description**: Lấy danh sách sản phẩm (món ăn) để hiển thị grid sản phẩm. Có thể lọc theo danh mục hoặc tìm kiếm theo từ khóa.

**Query Parameters** (Optional):
- `category_id` – Lọc theo danh mục (nếu không gửi thì lấy tất cả)
- `search` – Từ khóa tìm kiếm (tên món, mô tả)
- `page`, `limit` – Phân trang (nếu cần)

**Response**:
```json
[
  {
    "id": 1,
    "name": "Bánh Mì Đặc Biệt",
    "category": { "id": 2, "name": "Bánh Mì" },
    "price": 35000.00,
    "rating": 4.8,
    "image_url": "https://example.com/banhmi.jpg",
    "restaurant_id": 2,
    "restaurant_name": "Bánh Mì Hồng Hoa"
  },
  {
    "id": 2,
    "name": "Phở Bò Gia Truyền",
    "category": { "id": 3, "name": "Phở & Bún" },
    "price": 65000.00,
    "rating": 4.9,
    "image_url": "https://example.com/pho.jpg",
    "restaurant_id": 1,
    "restaurant_name": "Phở Thìn Lò Đúc"
  }
]
```

**Database Tables**:
- `products`
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `name` (VARCHAR(255), Not Null)
  - `description` (TEXT)
  - `price` (DECIMAL(10,2), Not Null)
  - `image_url` (VARCHAR(500))
  - `category_id` (INT, Foreign Key to categories)
  - `restaurant_id` (INT, Foreign Key to restaurants)
  - `rating_avg` (DECIMAL(3,2), Default 0.00)
  - `rating_count` (INT, Default 0)
  - `created_at` (DATETIME)
  - `updated_at` (DATETIME)

- `categories` (xem ở API 10.1)
- `restaurants`
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `name` (VARCHAR(255), Not Null)
  - `address` (TEXT)
  - `logo_url` (VARCHAR(500))
  - `is_open` (BOOLEAN, Default TRUE)
  - `created_at` (DATETIME)
  - `updated_at` (DATETIME)

---

### 10.3 Get Product Detail
**Endpoint**: `GET /api/products/{id}`

**Description**: Lấy chi tiết một sản phẩm khi người dùng click vào sản phẩm, modal hiển thị chi tiết

**Path Parameters**:
- `id` – Product ID

**Response**:
```json
{
  "id": 2,
  "name": "Phở Bò Gia Truyền",
  "description": "Phở bò truyền thống với nước dùng hầm xương trong 12 giờ...",
  "price": 65000.00,
  "image_url": "https://example.com/pho-detail.jpg",
  "category": { "id": 3, "name": "Phở & Bún" },
  "restaurant": {
    "id": 1,
    "name": "Phở Thìn Lò Đúc",
    "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
    "is_open": true,
    "logo_url": "https://example.com/pho-thin-logo.jpg"
  },
  "rating": 4.9,
  "rating_count": 1200
}
```

**Database Tables**: `products`, `restaurants`, `categories` (xem ở API 10.2)

---

### 10.4 Get Product Reviews
**Endpoint**: `GET /api/products/{id}/reviews`

**Description**: Lấy danh sách đánh giá của sản phẩm để hiển thị các nhận xét bên dưới modal

**Path Parameters**:
- `id` – Product ID

**Query Parameters**:
- `page`, `limit` – Phân trang

**Response**:
```json
{
  "total": 1200,
  "reviews": [
    {
      "id": 1,
      "user": { 
        "id": 1, 
        "name": "Nguyễn Văn A", 
        "avatar_url": "https://example.com/avatar1.jpg" 
      },
      "rating": 5,
      "comment": "Nước dùng rất ngon và thanh. Sẽ quay lại ủng hộ!",
      "created_at": "2025-03-02T10:30:00Z"
    },
    {
      "id": 2,
      "user": { 
        "id": 2, 
        "name": "Trần Thị B", 
        "avatar_url": "https://example.com/avatar2.jpg" 
      },
      "rating": 4,
      "comment": "Phở ngon, giá hợp lý, phục vụ nhanh.",
      "created_at": "2025-03-01T15:45:00Z"
    }
  ]
}
```

**Database Tables**:
- `reviews`
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `user_id` (INT, Foreign Key to users)
  - `product_id` (INT, Foreign Key to products)
  - `rating` (INT, Not Null, Check 1-5)
  - `comment` (TEXT)
  - `created_at` (DATETIME)

- `users` (table existing)
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `name` (VARCHAR(100))
  - `avatar_url` (VARCHAR(500))

---

### 10.5 Create Order (Checkout)
**Endpoint**: `POST /api/orders`

**Description**: Đặt hàng khi người dùng điền thông tin giao hàng và bấm "Đặt hàng ngay với SmartPay"

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "user_id": 123,
  "items": [
    { 
      "product_id": 2, 
      "quantity": 1, 
      "price": 65000.00 
    },
    { 
      "product_id": 4, 
      "quantity": 2, 
      "price": 45000.00 
    }
  ],
  "delivery_address": "Số 123, đường ABC, quận Hai Bà Trưng, Hà Nội",
  "recipient_name": "Nguyễn Văn A",
  "recipient_phone": "0987654321",
  "note": "Không hành, nhiều ớt",
  "payment_method": "SmartPay"
}
```

**Response**:
```json
{
  "order_id": 2025,
  "total_amount": 155000.00,
  "status": "pending",
  "created_at": "2025-03-04T15:23:00Z"
}
```

**Database Tables**:
- `orders`
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `user_id` (INT, Foreign Key to users)
  - `total_amount` (DECIMAL(10,2), Not Null)
  - `status` (ENUM: 'pending', 'confirmed', 'preparing', 'delivering', 'completed', 'cancelled')
  - `delivery_address` (TEXT, Not Null)
  - `recipient_name` (VARCHAR(100), Not Null)
  - `recipient_phone` (VARCHAR(20), Not Null)
  - `note` (TEXT)
  - `payment_method` (VARCHAR(50))
  - `created_at` (DATETIME)
  - `updated_at` (DATETIME)

- `order_items`
  - `id` (INT AUTO_INCREMENT, Primary Key)
  - `order_id` (INT, Foreign Key to orders)
  - `product_id` (INT, Foreign Key to products)
  - `quantity` (INT, Not Null, Check > 0)
  - `price_at_time` (DECIMAL(10,2), Not Null)

---

## 12. Admin Category Management APIs

### 12.1 Get Categories (Admin)
**Endpoint**: `GET /api/admin/categories`

**Description**: Hiển thị bảng danh sách danh mục, hỗ trợ tìm kiếm theo tên/ID, phân trang, sắp xếp.

**Query Parameters**:
- `page` – Số trang (1-based, default: 1)
- `limit` – Số lượng record (default: 10)
- `search` – Từ khóa tìm kiếm (tên hoặc ID)
- `sort_by` – Trường sắp xếp (default: id)
- `sort_dir` – Hướng sắp xếp: asc/desc (default: asc)

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Tất cả",
      "icon": "restaurant",
      "orderIndex": 1,
      "createdAt": "2025-03-04T10:00:00Z",
      "updatedAt": "2025-03-04T10:00:00Z"
    },
    {
      "id": 2,
      "name": "Bánh Mì",
      "icon": "lunch_dining",
      "orderIndex": 2,
      "createdAt": "2025-03-04T10:00:00Z",
      "updatedAt": "2025-03-04T10:00:00Z"
    }
  ],
  "pageNumber": 1,
  "pageSize": 10,
  "totalElements": 5,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

### 12.2 Get Category Detail (Admin)
**Endpoint**: `GET /api/admin/categories/{id}`

**Description**: Dùng để đổ dữ liệu lên form sửa danh mục.

**Path Parameters**:
- `id` – Category ID

**Response**:
```json
{
  "id": 2,
  "name": "Bánh Mì",
  "icon": "lunch_dining",
  "orderIndex": 2,
  "createdAt": "2025-03-04T10:00:00Z",
  "updatedAt": "2025-03-04T10:00:00Z"
}
```

---

### 12.3 Create Category (Admin)
**Endpoint**: `POST /api/admin/categories`

**Description**: Thêm mới danh mục.

**Request Body**:
```json
{
  "name": "Món Lẩu",
  "icon": "soup_kitchen",
  "orderIndex": 6
}
```

**Response**:
```json
{
  "id": 6,
  "name": "Món Lẩu",
  "icon": "soup_kitchen",
  "orderIndex": 6,
  "createdAt": "2025-03-04T15:30:00Z",
  "updatedAt": "2025-03-04T15:30:00Z"
}
```

---

### 12.4 Update Category (Admin)
**Endpoint**: `PUT /api/admin/categories/{id}`

**Description**: Cập nhật danh mục (có thể gửi một phần dữ liệu).

**Path Parameters**:
- `id` – Category ID

**Request Body**:
```json
{
  "name": "Bánh Mì & Bánh Ngọt",
  "icon": "bakery_dining",
  "orderIndex": 2
}
```

**Response**:
```json
{
  "id": 2,
  "name": "Bánh Mì & Bánh Ngọt",
  "icon": "bakery_dining",
  "orderIndex": 2,
  "createdAt": "2025-03-04T10:00:00Z",
  "updatedAt": "2025-03-04T15:45:00Z"
}
```

---

### 12.5 Delete Category (Admin)
**Endpoint**: `DELETE /api/admin/categories/{id}`

**Description**: Xóa danh mục (kiểm tra ràng buộc nếu có sản phẩm liên kết).

**Path Parameters**:
- `id` – Category ID

**Response**: `204 No Content`

**Error Response** (nếu có sản phẩm liên kết):
```json
{
  "status": "FAILED",
  "message": "Cannot delete category because it has associated products",
  "timestamp": 1641748400000
}
```

---

### 12.6 Export Categories (Admin)
**Endpoint**: `GET /api/admin/categories/export`

**Description**: Xuất dữ liệu danh mục ra file Excel/CSV.

**Response**: File download với header `Content-Disposition: attachment; filename=categories.xlsx`

---

### 12.7 Check Category Name (Admin)
**Endpoint**: `GET /api/admin/categories/check-name`

**Description**: Kiểm tra tên danh mục đã tồn tại (dùng cho validation form).

**Query Parameters**:
- `name` – Tên danh mục cần kiểm tra
- `exclude_id` – ID loại trừ (khi sửa) (optional)

**Response**:
```json
{
  "exists": false
}
```

---

## 13. Security & Testing Notes

### 13.1 Authentication
- **Production**: Tất cả Admin APIs yêu cầu JWT token với `ADMIN` role
- **Testing**: Tạm thời disable security cho `/api/admin/categories/**` để development

### 13.2 Testing Examples
```bash
# 1. Get categories with pagination (1-based page numbering)
GET http://localhost:8080/api/admin/categories?page=1&limit=10

# 2. Search categories
GET http://localhost:8080/api/admin/categories?search=Bánh&page=1&limit=5

# 3. Sort categories
GET http://localhost:8080/api/admin/categories?sortBy=name&sortDir=desc&page=1&limit=10

# 4. Get category detail
GET http://localhost:8080/api/admin/categories/1

# 5. Create new category
POST http://localhost:8080/api/admin/categories
{
  "name": "Món Lẩu",
  "icon": "soup_kitchen",
  "orderIndex": 6
}

# 6. Update category (partial update)
PUT http://localhost:8080/api/admin/categories/1
{
  "name": "Bánh Mì & Bánh Ngọt"
}

# 7. Delete category (with constraint check)
DELETE http://localhost:8080/api/admin/categories/1

# 8. Check category name availability
GET http://localhost:8080/api/admin/categories/check-name?name=Test%20Category
GET http://localhost:8080/api/admin/categories/check-name?name=Bánh%20Mì&excludeId=2

# 9. Export categories (CSV format)
GET http://localhost:8080/api/admin/categories/export
```

### 13.3 Database Schema
```sql
-- Categories table (updated)
CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(50),
  order_index INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 13.5 Important Notes
- **Page numbering**: Sử dụng 1-based (page=1, 2, 3...) thay vì 0-based
- **Partial update**: PUT endpoint cho phép cập nhật một hoặc nhiều fields
- **Constraint check**: Không thể xóa category có sản phẩm liên kết
- **Export format**: Hiện tại là CSV, có thể nâng cấp lên Excel với Apache POI
- **Search**: Hỗ trợ tìm kiếm theo tên và ID của category
- **Soft Delete**: Restaurants sử dụng soft delete với deleted_at timestamp
- **Image Storage**: Logo được lưu dưới dạng base64 trong LONGTEXT field
- **ID Format**: Restaurant ID sử dụng format RS-XXXX (auto-generated)
- **Product Count**: Được cache trong product_count field để tối ưu performance

---

## 15. Admin Restaurant Management APIs

### 15.1 Get Restaurants (Admin)
**Endpoint**: `GET /api/admin/restaurants`

**Description**: Hiển thị bảng danh sách nhà hàng, hỗ trợ tìm kiếm, lọc trạng thái, phân trang, sắp xếp.

**Query Parameters**:
- `page` – Số trang (1-based, default: 1)
- `limit` – Số lượng record (default: 10)
- `search` – Từ khóa tìm kiếm (tên, địa chỉ, SĐT, email, ID)
- `status` – Lọc theo trạng thái (true=open, false=closed)
- `sort_by` – Trường sắp xếp (name, created_at, product_count)
- `sort_dir` – Hướng sắp xếp: asc/desc (default: asc)

**Response**:
```json
{
  "data": [
    {
      "id": "RS-1234",
      "name": "Phở Thìn",
      "phone": "02439712738",
      "email": "contact@phothin.com",
      "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
      "logo_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
      "status": true,
      "product_count": 45,
      "created_at": "2025-01-15T08:30:00Z",
      "updated_at": "2025-01-15T08:30:00Z"
    }
  ],
  "pagination": {
    "total": 32,
    "page": 1,
    "limit": 10,
    "total_pages": 4
  }
}
```

---

### 15.2 Get Restaurant Detail (Admin)
**Endpoint**: `GET /api/admin/restaurants/{id}`

**Description**: Lấy chi tiết một nhà hàng.

**Path Parameters**:
- `id` – Restaurant ID

**Response**:
```json
{
  "id": "RS-1234",
  "name": "Phở Thìn",
  "phone": "02439712738",
  "email": "contact@phothin.com",
  "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
  "logo_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "status": true,
  "product_count": 45,
  "created_at": "2025-01-15T08:30:00Z",
  "updated_at": "2025-01-15T08:30:00Z"
}
```

---

### 15.3 Create Restaurant (Admin)
**Endpoint**: `POST /api/admin/restaurants`

**Description**: Thêm mới nhà hàng.

**Request Body**:
```json
{
  "name": "Phở Thìn",
  "phone": "02439712738",
  "email": "contact@phothin.com",
  "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
  "status": true,
  "logo_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
}
```

**Response**:
```json
{
  "id": "RS-1235",
  "name": "Phở Thìn",
  "phone": "02439712738",
  "email": "contact@phothin.com",
  "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
  "logo_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "status": true,
  "product_count": 0,
  "created_at": "2025-03-05T15:30:00Z",
  "updated_at": "2025-03-05T15:30:00Z"
}
```

---

### 15.4 Update Restaurant (Admin)
**Endpoint**: `PUT /api/admin/restaurants/{id}`

**Description**: Cập nhật nhà hàng.

**Request Body**:
```json
{
  "name": "Phở Thìn Lò Đúc",
  "status": false,
  "logo_base64": "data:image/png;base64,new_base64_string..."
}
```

**Response**: Object nhà hàng sau cập nhật (tương tự response của GET detail).

---

### 15.5 Delete Restaurant (Admin)
**Endpoint**: `DELETE /api/admin/restaurants/{id}`

**Description**: Xóa nhà hàng (soft delete, chỉ khi không có sản phẩm liên kết).

**Response**: 204 No Content nếu thành công.

**Error Response**:
```json
{
  "status": "FAILED",
  "message": "Cannot delete restaurant because it has associated products",
  "timestamp": 1641748400000
}
```

---

### 15.6 Check Restaurant Name (Admin)
**Endpoint**: `GET /api/admin/restaurants/check-name`

**Description**: Kiểm tra tên nhà hàng đã tồn tại.

**Query Parameters**:
- `name` – Tên nhà hàng cần kiểm tra
- `exclude_id` – ID hiện tại khi sửa (để loại trừ chính nó)

**Response**:
```json
{
  "exists": false
}
```

---

### 15.7 Export Restaurants (Admin)
**Endpoint**: `GET /api/admin/restaurants/export`

**Description**: Xuất dữ liệu nhà hàng ra file CSV.

**Query Parameters**:
- `search` – Từ khóa tìm kiếm (tùy chọn)
- `status` – Lọc theo trạng thái (tùy chọn)

**Response**: File CSV với các cột: ID, Tên, SĐT, Email, Địa chỉ, Trạng thái, Số món, Ngày tạo.

### 15.8 Testing Examples
```bash
# 1. Get restaurants with pagination (1-based page numbering)
GET http://localhost:8080/api/admin/restaurants?page=1&limit=10

# 2. Search restaurants by name, address, phone, email, or ID
GET http://localhost:8080/api/admin/restaurants?search=Phở&page=1&limit=5

# 3. Filter by status (open/closed)
GET http://localhost:8080/api/admin/restaurants?status=true&page=1&limit=10

# 4. Sort restaurants
GET http://localhost:8080/api/admin/restaurants?sortBy=name&sortDir=desc&page=1&limit=10
GET http://localhost:8080/api/admin/restaurants?sortBy=product_count&sortDir=asc&page=1&limit=10

# 5. Combined search and filter
GET http://localhost:8080/api/admin/restaurants?search=Hà%20Nội&status=true&sortBy=created_at&sortDir=desc&page=1&limit=10

# 6. Get restaurant detail
GET http://localhost:8080/api/admin/restaurants/RS-1234

# 7. Create new restaurant
POST http://localhost:8080/api/admin/restaurants
{
  "name": "Phở Thìn",
  "phone": "02439712738",
  "email": "contact@phothin.com",
  "address": "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
  "status": true,
  "logo_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
}

# 8. Update restaurant (partial update)
PUT http://localhost:8080/api/admin/restaurants/RS-1234
{
  "name": "Phở Thìn Lò Đúc",
  "status": false,
  "logo_base64": "data:image/png;base64,new_base64_string..."
}

# 9. Delete restaurant (with constraint check)
DELETE http://localhost:8080/api/admin/restaurants/RS-1234

# 10. Check restaurant name availability
GET http://localhost:8080/api/admin/restaurants/check-name?name=Test%20Restaurant
GET http://localhost:8080/api/admin/restaurants/check-name?name=Phở%20Thìn&excludeId=RS-1234

# 11. Export restaurants (CSV format)
GET http://localhost:8080/api/admin/restaurants/export
GET http://localhost:8080/api/admin/restaurants/export?search=Phở&status=true
```

### 15.9 Database Schema
```sql
-- Restaurants table (updated for admin management)
CREATE TABLE restaurants (
  id VARCHAR(20) PRIMARY KEY,               -- RS-XXXX format
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  email VARCHAR(255),
  address TEXT NOT NULL,
  logo_base64 LONGTEXT,                      -- Base64 image storage
  status BOOLEAN DEFAULT TRUE,                -- true = open, false = closed
  product_count INT DEFAULT 0,                 -- Cached product count
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL                    -- Soft delete support
);
```

### 15.10 Important Implementation Notes
- **ID Format**: Restaurant ID tự động sinh theo format RS-XXXX
- **Soft Delete**: Xóa mềm sử dụng deleted_at timestamp
- **Image Storage**: Logo lưu dạng base64 trong LONGTEXT field
- **Search**: Multi-field search (name, address, phone, email, ID)
- **Constraint Check**: Không xóa restaurant có sản phẩm liên kết
- **Partial Update**: PUT endpoint cho phép cập nhật một hoặc nhiều fields
- **Export Format**: CSV với UTF-8 encoding và Vietnamese headers
- **Performance**: product_count được cache để tối ưu query performance

---

## 17. Admin Product Management APIs

### 17.1 Get Products (Admin)
**Endpoint**: `GET /api/admin/products`

**Description**: Hiển thị bảng danh sách sản phẩm, hỗ trợ tìm kiếm, lọc, phân trang, sắp xếp.

**Query Parameters**:
- `page` – Số trang (1-based, default: 1)
- `limit` – Số lượng record (default: 10)
- `search` – Từ khóa tìm kiếm (tên, mô tả, ID)
- `category_id` – Lọc theo danh mục
- `restaurant_id` – Lọc theo nhà hàng
- `status` – Lọc theo trạng thái (available, unavailable)
- `sort_by` – Trường sắp xếp (name, price, created_at, rating_avg)
- `sort_dir` – Hướng sắp xếp: asc/desc (default: asc)

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Salad Ức Gà",
      "description": "Ức gà áp chảo ăn kèm với rau...",
      "price": 85000,
      "category": { "id": 1, "name": "Món chính", "icon": "restaurant" },
      "restaurant": { "id": "RS-9021", "name": "SmartPay Quận 1", "logo_base64": "..." },
      "image_base64": "data:image/png;base64,...",
      "status": "available",
      "rating_avg": 4.8,
      "rating_count": 124,
      "created_at": "2025-02-10T09:30:00Z",
      "updated_at": "2025-02-10T09:30:00Z"
    }
  ],
  "pagination": {
    "total": 24,
    "page": 1,
    "limit": 10,
    "total_pages": 3
  }
}
```

---

### 17.2 Get Product Detail (Admin)
**Endpoint**: `GET /api/admin/products/{id}`

**Description**: Lấy chi tiết một sản phẩm.

**Path Parameters**:
- `id` – Product ID

**Response**: Object sản phẩm chi tiết (tương tự response của GET list).

---

### 17.3 Create Product (Admin)
**Endpoint**: `POST /api/admin/products`

**Description**: Thêm mới sản phẩm.

**Request Body**:
```json
{
  "name": "Salad Ức Gà",
  "description": "Ức gà áp chảo ăn kèm với rau...",
  "price": 85000,
  "category_id": 1,
  "restaurant_id": "RS-9021",
  "status": "available",
  "image_base64": "data:image/png;base64,..."
}
```

**Response**: Object sản phẩm vừa tạo (tương tự response của GET detail).

---

### 17.4 Update Product (Admin)
**Endpoint**: `PUT /api/admin/products/{id}`

**Description**: Cập nhật sản phẩm.

**Request Body**:
```json
{
  "name": "Salad Ức Gà Mới",
  "price": 90000,
  "status": "unavailable",
  "image_base64": "data:image/png;base64,new_base64_string..."
}
```

**Response**: Object sản phẩm sau cập nhật (tương tự response của GET detail).

---

### 17.5 Delete Product (Admin)
**Endpoint**: `DELETE /api/admin/products/{id}`

**Description**: Xóa sản phẩm (soft delete).

**Response**: 204 No Content nếu thành công.

---

### 17.6 Testing Examples
```bash
# 1. Get products with pagination (1-based page numbering)
GET http://localhost:8080/api/admin/products?page=1&limit=10

# 2. Search products by name or description
GET http://localhost:8080/api/admin/products?search=Salad&page=1&limit=5

# 3. Filter by category
GET http://localhost:8080/api/admin/products?category_id=1&page=1&limit=10

# 4. Filter by restaurant
GET http://localhost:8080/api/admin/products?restaurant_id=RS-1234&page=1&limit=10

# 5. Filter by status
GET http://localhost:8080/api/admin/products?status=available&page=1&limit=10

# 6. Sort products
GET http://localhost:8080/api/admin/products?sortBy=price&sortDir=desc&page=1&limit=10
GET http://localhost:8080/api/admin/products?sortBy=rating_avg&sortDir=asc&page=1&limit=10

# 7. Combined filters
GET http://localhost:8080/api/admin/products?search=Gà&category_id=1&status=available&sortBy=price&sortDir=asc&page=1&limit=10

# 8. Get product detail
GET http://localhost:8080/api/admin/products/1

# 9. Create new product
POST http://localhost:8080/api/admin/products
{
  "name": "Salad Ức Gà",
  "description": "Ức gà áp chảo ăn kèm với rau...",
  "price": 85000,
  "category_id": 1,
  "restaurant_id": "RS-9021",
  "status": "available",
  "image_base64": "data:image/png;base64,..."
}

# 10. Update product (partial update)
PUT http://localhost:8080/api/admin/products/1
{
  "name": "Salad Ức Gà Mới",
  "price": 90000,
  "status": "unavailable"
}

# 11. Delete product (soft delete)
DELETE http://localhost:8080/api/admin/products/1
```

### 17.7 Database Schema
```sql
-- Products table (updated for admin management)
CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  image_base64 LONGTEXT,                    -- Changed from image_url
  category_id INT NOT NULL,
  restaurant_id VARCHAR(20) NOT NULL,
  rating_avg DECIMAL(3,2) DEFAULT 0.00,
  rating_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'available',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT fk_product_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);
```

### 17.8 Important Implementation Notes
- **Soft Delete**: Sản phẩm sử dụng soft delete với deleted_at timestamp
- **Image Storage**: Ảnh lưu dạng base64 trong image_base64 LONGTEXT field (thay đổi từ image_url)
- **Status Values**: "available" hoặc "unavailable"
- **Search**: Multi-field search (name, description, ID)
- **Validation**: Validate category và restaurant existence
- **Partial Update**: PUT endpoint cho phép cập nhật một hoặc nhiều fields
- **Unique Check**: Không cho phép trùng tên sản phẩm (excluding current product khi update)
- **Price Handling**: Convert giữa BigDecimal và Double trong DTO conversion
- **Column Migration**: Từ image_url VARCHAR(500) sang image_base64 LONGTEXT để lưu large base64 strings

---

## 18. Error Responses

### Common Error Format
```json
{
  "status": "FAILED",
  "message": "Error description",
  "timestamp": 1641748400000
}
```

### HTTP Status Codes
- `200 OK`: Request thành công
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Resource không tồn tại
- `500 Internal Server Error`: Lỗi server

---

## 9. Testing Examples

### Add Card
```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "5702676235112103",
    "holderName": "NGUYEN VAN A",
    "expiryDate": "05/25",
    "cvv": "477",
    "type": "DEBIT",
    "bankName": "Vietcombank"
  }'
```

### Wallet Transfer
```bash
curl -X POST http://localhost:8080/api/wallet/transfers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "toAccountNumber": "0987654321",
    "amount": 100000,
    "description": "Chuyển tiền test"
  }'
```

### Admin Get All Transactions
```bash
curl -X GET "http://localhost:8080/api/admin/transactions?page=0&size=50" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

---

**API Documentation Updated: 2026-03-06**
**Version: 8.1**
**Total APIs**: 69 endpoints (45 E-Wallet APIs + 5 Food Ordering APIs + 7 Admin Category Management APIs + 7 Admin Restaurant Management APIs + 5 Admin Product Management APIs)

**Recent Updates**:
- ✅ Added Admin Category Management APIs (7 endpoints)
- ✅ Added Admin Restaurant Management APIs (7 endpoints)
- ✅ Added Admin Product Management APIs (5 endpoints)
- ✅ Fixed DELETE API 400 Bad Request error with String parameter parsing
- ✅ Fixed soft delete queries to include deleted_at IS NULL filter
- ✅ Updated image storage from image_url to image_base64 LONGTEXT column
- ✅ Added comprehensive database migration scripts
- ✅ Fixed pagination to use 1-based page numbering
- ✅ Updated database schema with order_index, restaurant, and product status fields
- ✅ Added comprehensive testing examples
- ✅ Temporarily disabled security for development testing

## Notes

1. **Authentication**: Tất cả APIs (trừ register, login và food ordering public APIs) cần JWT token trong header `Authorization: Bearer <token>`
2. **Pagination**: Các API trả về danh sách hỗ trợ phân trang với `page` và `size`
3. **Validation**: Request body được validate, lỗi 400 sẽ trả về chi tiết lỗi
4. **Wallet Status**: Chỉ wallet `ACTIVE` mới có thể thực hiện giao dịch
5. **Transaction Status**: Giao dịch có thể thành công (`success: true`) hoặc thất bại (`success: false`)
6. **Currency**: Mặc định là VND
7. **Timestamp Format**: ISO 8601 (yyyy-MM-ddTHH:mm:ss)
8. **Error Handling**: Các lỗi phổ biến: 400 (Bad Request), 401 (Unauthorized), 403 (Forbidden), 404 (Not Found), 500 (Internal Server Error)
9. **Food Ordering**: APIs mới cho hệ thống đặt đồ ăn trực tuyến, tích hợp với hệ thống thanh toán SmartPay hiện có
10. **Database Schema**: Schema cho food ordering được tạo trong database `bank_db` hiện có để tích hợp với hệ thống user/wallet

---

## 13. Face AI APIs

### 13.1 Register Face Embedding
**Endpoint**: `POST /api/face/register`

**Description**: Đăng ký embedding khuôn mặt mới cho user.

**Headers**: `Authorization: Bearer <token>`

**Request Body** (multipart/form-data):
- `image` (file): Ảnh khuôn mặt (max 2MB, 1920x1080)
- `userId` (integer): ID của user
- `pose` (string): Góc chụp khuôn mặt (`front` / `left` / `right`)

**Response**: 200 OK
```json
{
  "id": 1,
  "userId": 10,
  "pose": "front",
  "message": "Face registered successfully"
}
```

---

### 13.2 Verify Face
**Endpoint**: `POST /api/face/verify`

**Description**: Xác thực khuôn mặt bằng cách so sánh với các embedding đã đăng ký của user.

**Headers**: `Authorization: Bearer <token>`

**Request Body** (multipart/form-data):
- `image` (file): Ảnh khuôn mặt cần xác thực
- `userId` (integer): User ID cần xác thực
- `deviceId` (string, optional): Thông tin thiết bị để audit log

**Response**: 200 OK
```json
{
  "similarity": 0.85,
  "result": "PASS",
  "matchedPose": "front",
  "threshold": 0.55,
  "message": "Face verification passed"
}
```

---

### 13.3 List Embeddings
**Endpoint**: `GET /api/face/list/{userId}`

**Description**: Lấy danh sách metadata của các embedding đã đăng ký của một user.

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `userId`: ID của user

**Response**: 200 OK
```json
[
  {
    "id": 1,
    "userId": 10,
    "pose": "front",
    "createdAt": "2024-03-11T16:00:00"
  }
]
```

---

### 13.4 Delete Embedding
**Endpoint**: `DELETE /api/face/{embeddingId}`

**Description**: Xóa một embedding đã đăng ký.

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `embeddingId`: ID của embedding cần xóa

**Response**: 200 OK
```json
{
  "message": "Embedding deleted successfully"
}
```

---

### 13.5 Generate Embedding (Utility)
**Endpoint**: `POST /api/face/embedding`

**Description**: Tạo vector embedding thô từ ảnh (không lưu vào DB). Dành cho mục đích test.

**Headers**: `Authorization: Bearer <token>`

**Request Body** (multipart/form-data):
- `file` (file): Ảnh khuôn mặt

**Response**: 200 OK
```json
[0.0123, -0.0456, 0.0789, ...] // Array of 512 floats
```

---

### 13.6 Compare Two Faces (Utility)
**Endpoint**: `POST /api/face/compare`

**Description**: So sánh trực tiếp 2 ảnh khuôn mặt và trả về điểm tương đồng.

**Headers**: `Authorization: Bearer <token>`

**Request Body** (multipart/form-data):
- `img1` (file): Ảnh khuôn mặt 1
- `img2` (file): Ảnh khuôn mặt 2

**Response**: 200 OK
```json
{
  "similarity": 0.85,
  "isMatch": true,
  "threshold": 0.55
}
```
