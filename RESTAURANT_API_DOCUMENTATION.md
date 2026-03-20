# Restaurant Owner (Merchant) API Documentation

Dưới đây là mô tả chi tiết cho tất cả các API liên quan đến phía Restaurant (Merchant / Chủ quán) hiện có trong hệ thống.

---

## 1. GET /api/restaurant-owner/orders

### 1. Endpoint
`GET /api/restaurant-owner/orders`

### 2. Request Body / Parameters
- `page` (query, integer) – Trang hiện tại (default: 0)
- `size` (query, integer) – Số lượng mỗi trang (default: 10)
- `status` (query, string, optional) – Lọc theo trạng thái đơn hàng (PENDING, CONFIRMED, READY_FOR_PICKUP, DELIVERING, COMPLETED, CANCELLED...)

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "size": 10,
  "content": [
    {
      "id": 0,
      "userId": 0,
      "totalAmount": 0,
      "status": "PENDING",
      "deliveryAddress": "string",
      "recipientName": "string",
      "recipientPhone": "string",
      "note": "string",
      "paymentMethod": "SmartPay",
      "restaurantId": "string",
      "shipperId": 0,
      "createdAt": "2023-10-10T10:10:10.000Z",
      "confirmedAt": "2023-10-10T10:10:10.000Z",
      "readyAt": "2023-10-10T10:10:10.000Z",
      "pickedUpAt": "2023-10-10T10:10:10.000Z",
      "deliveredAt": "2023-10-10T10:10:10.000Z",
      "orderItems": [
        {
          "productId": 0,
          "productName": "string",
          "quantity": 0,
          "priceAtTime": 0
        }
      ]
    }
  ],
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": true,
    "unsorted": true
  },
  "numberOfElements": 0,
  "pageable": {
    "offset": 0,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "paged": true,
    "pageSize": 10,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## 2. GET /api/restaurant-owner/orders/{id}

### 1. Endpoint
`GET /api/restaurant-owner/orders/{id}`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "id": 0,
  "userId": 0,
  "totalAmount": 0,
  "status": "PENDING",
  "deliveryAddress": "string",
  "recipientName": "string",
  "recipientPhone": "string",
  "note": "string",
  "paymentMethod": "SmartPay",
  "restaurantId": "string",
  "shipperId": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "confirmedAt": "2023-10-10T10:10:10.000Z",
  "readyAt": "2023-10-10T10:10:10.000Z",
  "pickedUpAt": "2023-10-10T10:10:10.000Z",
  "deliveredAt": "2023-10-10T10:10:10.000Z",
  "orderItems": [
    {
      "productId": 0,
      "productName": "string",
      "quantity": 0,
      "priceAtTime": 0
    }
  ],
  "statusHistory": [
    {
      "status": "PENDING",
      "timestamp": "2023-10-10T10:10:10.000Z"
    }
  ]
}
```

---

## 3. PUT /api/restaurant-owner/orders/{id}/confirm

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/confirm`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- Request body: Không bắt buộc (có thể để trống)

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "id": 0,
  "status": "CONFIRMED",
  "message": "Đơn hàng đã được xác nhận"
}
```

---

## 4. PUT /api/restaurant-owner/orders/{id}/ready

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/ready`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- Request body: Không bắt buộc

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "id": 0,
  "status": "READY_FOR_PICKUP",
  "message": "Đơn hàng đã sẵn sàng để shipper lấy"
}
```

---

## 5. PUT /api/restaurant-owner/orders/{id}/reject

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/reject`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- Request body (optional):
```json
{
  "reason": "string"
}
```

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "id": 0,
  "status": "CANCELLED",
  "message": "Đơn hàng đã bị từ chối",
  "rejectedReason": "string"
}
```

---

## 6. GET /api/restaurant-owner/products

### 1. Endpoint
`GET /api/restaurant-owner/products`

### 2. Request Body / Parameters
- `page` (query, integer) – default 0
- `size` (query, integer) – default 10
- `status` (query, string, optional) – available/unavailable

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "totalElements": 0,
  "totalPages": 0,
  "content": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "price": 0,
      "imageBase64": "string",
      "status": "AVAILABLE",
      "categoryId": 0,
      "restaurantId": "string"
    }
  ]
}
```

---

## 7. POST /api/restaurant-owner/products

### 1. Endpoint
`POST /api/restaurant-owner/products`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "categoryId": 0,
  "status": "AVAILABLE"
}
```

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 201**: Created
```json
{
  "id": 0,
  "name": "string",
  "price": 0,
  "imageBase64": "string",
  "status": "AVAILABLE"
}
```

---

## 8. PUT /api/restaurant-owner/products/{id}

### 1. Endpoint
`PUT /api/restaurant-owner/products/{id}`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*

```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "status": "AVAILABLE"
}
```

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "id": 0,
  "name": "string",
  "price": 0,
  "imageBase64": "string",
  "status": "AVAILABLE"
}
```

---

## 9. DELETE /api/restaurant-owner/products/{id}

### 1. Endpoint
`DELETE /api/restaurant-owner/products/{id}`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "message": "Sản phẩm đã được xóa"
}
```

---

## 10. PUT /api/restaurant-owner/restaurant/status

### 1. Endpoint
`PUT /api/restaurant-owner/restaurant/status`

### 2. Request Body / Parameters
```json
{
  "status": "OPEN" | "CLOSED" | "BUSY"
}
```

### 3. Response
**Status 400**: Bad Request
```json
{
  "status": 0,
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

**Status 200**: OK
```json
{
  "restaurantId": "string",
  "status": "OPEN",
  "message": "Trạng thái quán đã được cập nhật"
}
```
