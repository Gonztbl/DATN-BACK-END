# Báo cáo `database_use`

## Phạm vi

Báo cáo này chỉ dựa trên **danh sách API đang được FE sử dụng** mà bạn cung cấp, sau đó đối chiếu với code hiện tại (`controller`, `service`, `repository`, `entity`) để xác định:

- **bảng nào đang được dùng**
- **thuộc tính/cột nào đang thực sự được đọc hoặc ghi**
- **bảng nào chỉ dùng gián tiếp hoặc chưa nằm trong scope FE hiện tại**

> Ghi chú: tên thuộc tính được giữ theo **field trong entity Java** để dễ đối chiếu với source code.

---

## 1) Các bảng được dùng trực tiếp bởi API FE

| Table | Nhóm API FE | Thuộc tính đang dùng (có giá trị) |
|---|---|---|
| `users` | Auth / Profile / Admin / Shipper | `id`, `userName`, `email`, `phone`, `fullName`, `avatar`, `dateOfBirth`, `address`, `passwordHash`, `pinHash`, `isActive`, `isVerified`, `role`, `avatarUrl`, `membership`, `createdAt`, `updatedAt` |
| `wallets` | Wallet / QR / Transfer / Admin / Merchant / Shipper | `id`, `code`, `user`, `currency`, `balance`, `availableBalance`, `status`, `accountNumber`, `createdAt`, `updatedAt` |
| `transactions` | Wallet / Deposit / Transfer / Withdraw / Admin | `id`, `wallet`, `type`, `direction`, `amount`, `fee`, `balanceBefore`, `balanceAfter`, `status`, `referenceId`, `idempotencyKey`, `metadata`, `createdAt`, `updatedAt` |
| `products` | Shopping / Admin / Restaurant Owner | `id`, `name`, `description`, `price`, `imageBase64`, `categoryId`, `restaurantId`, `ratingAvg`, `ratingCount`, `status`, `createdAt`, `updatedAt`, `deletedAt` |
| `categories` | Shopping / Admin | `id`, `name`, `icon`, `orderIndex`, `createdAt`, `updatedAt` |
| `restaurants` | Shopping / Admin / Restaurant Owner | `id`, `name`, `phone`, `email`, `address`, `logoBase64`, `status`, `productCount`, `ownerId`, `description`, `categoryId`, `schedule`, `createdAt`, `updatedAt`, `deletedAt` |
| `orders` | Shopping / Admin / Restaurant Owner / Shipper | `id`, `userId`, `totalAmount`, `status`, `deliveryAddress`, `recipientName`, `recipientPhone`, `note`, `paymentMethod`, `restaurantId`, `shipperId`, `rejectedReason`, `deliveryFailedReason`, `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt`, `createdAt`, `updatedAt` |
| `order_items` | Shopping / Order / Reorder | `id`, `orderId`, `productId`, `quantity`, `priceAtTime`, `note` |
| `reviews` | Product Review | `id`, `userId`, `productId`, `rating`, `comment`, `createdAt` |
| `addresses` | Order / Delivery Address | `id`, `user`, `recipientName`, `phone`, `address`, `isDefault`, `createdAt`, `updatedAt` |
| `cards` | Card / Deposit | `id`, `cardNumber`, `cardHolderName`, `expiryDate`, `cvv`, `bankName`, `type`, `status`, `balanceCard`, `user` |
| `bank_accounts` | Wallet / Bank Account | `id`, `user`, `bankCode`, `bankName`, `accountNumber`, `accountName`, `status`, `createdAt` |
| `contacts` | Frequent Contacts | `id`, `user`, `name`, `avatarUrl`, `accountNumber` |
| `qr_codes` | QR Wallet / QR Resolve / QR Read Image | `id`, `wallet`, `codeValue`, `type`, `expiresAt`, `createdAt` |
| `face_embeddings` | Face Register / Verify / Add / Compare / List / Delete | `id`, `userId`, `embedding`, `pose`, `modelVersion`, `qualityScore`, `faceAngle`, `createdAt` |
| `shipper_profiles` | Shipper Status / Vehicle | `id`, `userId`, `vehicleType`, `vehiclePlate`, `isOnline`, `createdAt`, `updatedAt` |

---

## 2) Các bảng được dùng gián tiếp từ API FE

Đây là các bảng không phải lúc nào FE cũng gọi trực tiếp bằng endpoint riêng, nhưng vẫn được tạo/ghi dữ liệu trong quá trình xử lý các API đang dùng.

| Table | Liên quan tới API nào | Thuộc tính đang có giá trị |
|---|---|---|
| `face_verification_logs` | `/api/face/verify`, `/api/face/compare` | `id`, `userId`, `similarity`, `result`, `ip`, `deviceId`, `createdAt` |
| `card_deposits` | `/api/cards/deposit`, `/api/cards/deposit/history` | `id`, `card`, `user`, `amount`, `description`, `timestamp`, `status` |
| `bank_transfers` | `/api/wallets/{walletId}/withdraw` | `id`, `transaction`, `bankAccount`, `status` *(một số field như `bankReference`, `provider`, `processedAt` đang dùng nhẹ)* |
| `transfer_details` | `/api/transactions/transfer`, `/api/user/E-Wallet/transfers` | `id`, `transaction`, `counterpartyWalletId`, `counterpartyUserId`, `note`, `method`, `createdAt` |
| `balance_change_logs` | Deposit / Withdraw / Transfer nội bộ | `id`, `wallet`, `transaction`, `delta`, `balanceBefore`, `balanceAfter`, `createdAt` |

---

## 3) Gom theo nhóm API FE

### Auth / Profile / Face
Dùng chủ yếu các bảng:
- `users`
- `wallets`
- `face_embeddings`
- `face_verification_logs`

Các field nổi bật có giá trị:
- `userName`, `email`, `passwordHash`, `fullName`, `phone`, `role`
- `avatar`, `avatarUrl`, `dateOfBirth`, `address`
- `isVerified`, `membership`
- `embedding`, `pose`, `qualityScore`, `faceAngle`

### Shopping / Product / Order
Dùng chủ yếu các bảng:
- `categories`
- `products`
- `restaurants`
- `orders`
- `order_items`
- `reviews`
- `addresses`

Các field nổi bật có giá trị:
- `name`, `description`, `price`, `imageBase64`, `categoryId`, `restaurantId`
- `totalAmount`, `status`, `deliveryAddress`, `recipientName`, `recipientPhone`
- `rejectedReason`, `deliveryFailedReason`
- `confirmedAt`, `readyAt`, `pickedUpAt`, `deliveredAt`

### Wallet / QR / Deposit / Transfer
Dùng chủ yếu các bảng:
- `wallets`
- `transactions`
- `cards`
- `bank_accounts`
- `contacts`
- `qr_codes`
- `bank_transfers`
- `transfer_details`

Các field nổi bật có giá trị:
- `balance`, `availableBalance`, `status`, `accountNumber`, `code`
- `type`, `direction`, `amount`, `fee`, `referenceId`, `idempotencyKey`, `metadata`
- `cardNumber`, `cardHolderName`, `bankName`, `balanceCard`
- `codeValue`, `expiresAt`

### Admin
Dùng chủ yếu các bảng:
- `users`
- `wallets`
- `transactions`
- `orders`
- `products`
- `categories`
- `restaurants`
- `shipper_profiles`

Các field admin thay đổi nhiều:
- `isActive`
- `status`
- `deletedAt`
- `ownerId`
- `categoryId`
- `productCount`

### Merchant / Restaurant Owner
Dùng chủ yếu các bảng:
- `restaurants`
- `products`
- `orders`
- `wallets`

Các field nổi bật có giá trị:
- `restaurant.status`, `restaurant.schedule`, `restaurant.description`
- `product.name`, `product.price`, `product.description`, `product.status`
- `order.confirmedAt`, `order.readyAt`, `order.rejectedReason`

### Shipper
Dùng chủ yếu các bảng:
- `orders`
- `shipper_profiles`
- `wallets`

Các field nổi bật có giá trị:
- `shipperId`
- `pickedUpAt`
- `deliveredAt`
- `deliveryFailedReason`
- `isOnline`, `vehicleType`, `vehiclePlate`

---

## 4) Các bảng hiện không nằm trong scope FE API đã liệt kê hoặc dùng rất ít

| Table | Nhận định |
|---|---|
| `admin_actions` | Chủ yếu phục vụ audit nội bộ, không thấy nằm trong danh sách API FE bạn đưa |
| `notifications` | Có thể tồn tại trong hệ thống nhưng không xuất hiện trong tập API FE hiện tại |
| `support_tickets` | Có trong codebase nhưng không nằm trong danh sách API FE ở yêu cầu này |
| `ticket_replies` | Đi kèm support ticket, ngoài scope danh sách FE hiện tại |
| `favorites` | Có entity trong hệ thống nhưng không nằm trong danh sách API FE bạn vừa gửi |
| `otp_requests` | Không nằm trong tập endpoint FE được liệt kê lần này |
| `sessions` | Quản lý phiên đăng nhập/hệ thống, không có endpoint FE trực tiếp trong danh sách này |
| `system_configs` | Chủ yếu phục vụ cấu hình quản trị nội bộ |
| `card_withdraws` | Không thấy được FE gọi trực tiếp trong tập API hiện tại |

---

## 5) Kết luận ngắn

Các bảng được FE sử dụng **nhiều nhất** hiện tại là:

- `users`
- `wallets`
- `transactions`
- `products`
- `restaurants`
- `orders`
- `order_items`

Đây là nhóm bảng lõi của hệ thống và cũng là nơi có nhiều thuộc tính được đọc/ghi nhất qua FE.

---

*File này được tạo từ đối chiếu danh sách API FE với codebase hiện tại của dự án.*
