# API Documentation

## PUT /api/userManager/update/{id}

### 1. Endpoint
`PUT /api/userManager/update/{id}`

### 2. Request Body / Parameters
```json
{
  "id": 0,
  "userName": "string",
  "email": "string",
  "phone": "string",
  "fullName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "role": "string",
  "active": true
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
  "userName": "string",
  "email": "string",
  "phone": "string",
  "fullName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "role": "string",
  "active": true
}
```

---

## PUT /api/userManager/unlock/{id}

### 1. Endpoint
`PUT /api/userManager/unlock/{id}`

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

`No JSON response body`

---

## PUT /api/userManager/lock/{id}

### 1. Endpoint
`PUT /api/userManager/lock/{id}`

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

`No JSON response body`

---

## GET /api/user/profile

### 1. Endpoint
`GET /api/user/profile`

### 2. Request Body / Parameters
`None`

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
  "userName": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "avatar": "string",
  "avatarUrl": "string",
  "verified": true,
  "membership": "string",
  "phone": "string",
  "dateOfBirth": "string",
  "address": "string"
}
```

---

## PUT /api/user/profile

### 1. Endpoint
`PUT /api/user/profile`

### 2. Request Body / Parameters
```json
{
  "firstName": "string",
  "lastName": "string",
  "phone": "string",
  "dateOfBirth": "string",
  "address": "string"
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
{}
```

---

## PUT /api/user/profile/avatar

### 1. Endpoint
`PUT /api/user/profile/avatar`

### 2. Request Body / Parameters
```json
{
  "avatar": "string"
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
{}
```

---

## PUT /api/shipper/orders/{id}/picked-up

### 1. Endpoint
`PUT /api/shipper/orders/{id}/picked-up`

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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/shipper/orders/{id}/failed

### 1. Endpoint
`PUT /api/shipper/orders/{id}/failed`

### 2. Request Body / Parameters
```json
{
  "reason": "string",
  "photoBase64": "string"
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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/shipper/orders/{id}/delivered

### 1. Endpoint
`PUT /api/shipper/orders/{id}/delivered`

### 2. Request Body / Parameters
```json
{
  "photoBase64": "string"
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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/shipper/orders/{id}/accept

### 1. Endpoint
`PUT /api/shipper/orders/{id}/accept`

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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/reviews/{id}

### 1. Endpoint
`PUT /api/reviews/{id}`

### 2. Request Body / Parameters
```json
{
  "rating": 0,
  "comment": "string"
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
  "productId": 0,
  "userId": 0,
  "rating": 0,
  "comment": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "productName": "string"
}
```

---

## DELETE /api/reviews/{id}

### 1. Endpoint
`DELETE /api/reviews/{id}`

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

`No JSON response body`

---

## PUT /api/restaurant-owner/restaurant/status

### 1. Endpoint
`PUT /api/restaurant-owner/restaurant/status`

### 2. Request Body / Parameters
```json
{
  "isOpen": true
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
  "isOpen": true,
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/restaurant-owner/products/{id}

### 1. Endpoint
`GET /api/restaurant-owner/products/{id}`

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
  "name": "string",
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "categoryId": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/restaurant-owner/products/{id}

### 1. Endpoint
`PUT /api/restaurant-owner/products/{id}`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "categoryId": 0,
  "status": "string"
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
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "categoryId": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/restaurant-owner/products/{id}

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

`No JSON response body`

---

## PUT /api/restaurant-owner/orders/{id}/reject

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/reject`

### 2. Request Body / Parameters
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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/restaurant-owner/orders/{id}/ready

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/ready`

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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/restaurant-owner/orders/{id}/confirm

### 1. Endpoint
`PUT /api/restaurant-owner/orders/{id}/confirm`

### 2. Request Body / Parameters
```json
{
  "estimatedReadyTime": "2023-10-10T10:10:10.000Z"
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
  "status": "string",
  "message": "string"
}
```

---

## PUT /api/orders/{id}/cancel

### 1. Endpoint
`PUT /api/orders/{id}/cancel`

### 2. Request Body / Parameters
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
  "status": "string",
  "refundTransactionId": 0,
  "message": "string"
}
```

---

## PUT /api/notifications/{id}/read

### 1. Endpoint
`PUT /api/notifications/{id}/read`

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
  "type": "string",
  "title": "string",
  "content": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "read": true
}
```

---

## PUT /api/notifications/read-all

### 1. Endpoint
`PUT /api/notifications/read-all`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## PUT /api/admin/wallets/unlock/{id}

### 1. Endpoint
`PUT /api/admin/wallets/unlock/{id}`

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
"string"
```

---

## PUT /api/admin/wallets/lock/{id}

### 1. Endpoint
`PUT /api/admin/wallets/lock/{id}`

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
"string"
```

---

## PUT /api/admin/support-tickets/{id}/status

### 1. Endpoint
`PUT /api/admin/support-tickets/{id}/status`

### 2. Request Body / Parameters
```json
{
  "replyMessage": "string",
  "status": "string",
  "assigneeId": 0
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
  "userId": 0,
  "userName": "string",
  "subject": "string",
  "message": "string",
  "orderId": 0,
  "attachments": "string",
  "status": "string",
  "assignedTo": 0,
  "assignedToName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "replies": [
    {
      "id": 0,
      "ticketId": 0,
      "adminId": 0,
      "adminName": "string",
      "message": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ]
}
```

---

## PUT /api/admin/support-tickets/{id}/assign

### 1. Endpoint
`PUT /api/admin/support-tickets/{id}/assign`

### 2. Request Body / Parameters
```json
{
  "replyMessage": "string",
  "status": "string",
  "assigneeId": 0
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
  "userId": 0,
  "userName": "string",
  "subject": "string",
  "message": "string",
  "orderId": 0,
  "attachments": "string",
  "status": "string",
  "assignedTo": 0,
  "assignedToName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "replies": [
    {
      "id": 0,
      "ticketId": 0,
      "adminId": 0,
      "adminName": "string",
      "message": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ]
}
```

---

## PUT /api/admin/shippers/{id}/unlock

### 1. Endpoint
`PUT /api/admin/shippers/{id}/unlock`

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

`No JSON response body`

---

## PUT /api/admin/shippers/{id}/lock

### 1. Endpoint
`PUT /api/admin/shippers/{id}/lock`

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

`No JSON response body`

---

## GET /api/admin/settings

### 1. Endpoint
`GET /api/admin/settings`

### 2. Request Body / Parameters
`None`

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
[
  {
    "configKey": "string",
    "configValue": "string",
    "description": "string"
  }
]
```

---

## PUT /api/admin/settings

### 1. Endpoint
`PUT /api/admin/settings`

### 2. Request Body / Parameters
```json
[
  {
    "configKey": "string",
    "configValue": "string",
    "description": "string"
  }
]
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
[
  {
    "configKey": "string",
    "configValue": "string",
    "description": "string"
  }
]
```

---

## GET /api/admin/restaurants/{id}

**Summary:** Get restaurant by ID

### 1. Endpoint
`GET /api/admin/restaurants/{id}`

### 2. Request Body / Parameters
- `id` (path, string) *(required)*

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
  "id": "string",
  "name": "string",
  "phone": "string",
  "email": "string",
  "address": "string",
  "logoBase64": "string",
  "status": true,
  "productCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/admin/restaurants/{id}

**Summary:** Update restaurant

### 1. Endpoint
`PUT /api/admin/restaurants/{id}`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "phone": "string",
  "email": "string",
  "address": "string",
  "logoBase64": "string",
  "status": true
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
  "id": "string",
  "name": "string",
  "phone": "string",
  "email": "string",
  "address": "string",
  "logoBase64": "string",
  "status": true,
  "productCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/admin/restaurants/{id}

**Summary:** Delete restaurant

### 1. Endpoint
`DELETE /api/admin/restaurants/{id}`

### 2. Request Body / Parameters
- `id` (path, string) *(required)*

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

`No JSON response body`

---

## PUT /api/admin/restaurant-owners/{id}/unlock

### 1. Endpoint
`PUT /api/admin/restaurant-owners/{id}/unlock`

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

`No JSON response body`

---

## PUT /api/admin/restaurant-owners/{id}/lock

### 1. Endpoint
`PUT /api/admin/restaurant-owners/{id}/lock`

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

`No JSON response body`

---

## GET /api/admin/products/{id}

**Summary:** Get product by ID

### 1. Endpoint
`GET /api/admin/products/{id}`

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
  "name": "string",
  "description": "string",
  "price": 0,
  "category": {
    "id": 0,
    "name": "string",
    "icon": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "imageBase64": "string",
  "status": "string",
  "ratingAvg": 0,
  "ratingCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/admin/products/{id}

**Summary:** Update product

### 1. Endpoint
`PUT /api/admin/products/{id}`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "categoryId": 0,
  "restaurantId": "string",
  "status": "string",
  "imageBase64": "string"
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
  "description": "string",
  "price": 0,
  "category": {
    "id": 0,
    "name": "string",
    "icon": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "imageBase64": "string",
  "status": "string",
  "ratingAvg": 0,
  "ratingCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/admin/products/{id}

**Summary:** Delete product

### 1. Endpoint
`DELETE /api/admin/products/{id}`

### 2. Request Body / Parameters
- `id` (path, string) *(required)*

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

`No JSON response body`

---

## PUT /api/admin/products/{id}/status

**Summary:** Update product status

### 1. Endpoint
`PUT /api/admin/products/{id}/status`

### 2. Request Body / Parameters
```json
{}
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
  "description": "string",
  "price": 0,
  "category": {
    "id": 0,
    "name": "string",
    "icon": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "imageBase64": "string",
  "status": "string",
  "ratingAvg": 0,
  "ratingCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/admin/orders/{id}/status

### 1. Endpoint
`PUT /api/admin/orders/{id}/status`

### 2. Request Body / Parameters
```json
{
  "status": "string",
  "note": "string"
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
  "status": "string",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "message": "string"
}
```

---

## PUT /api/admin/orders/{id}/cancel

### 1. Endpoint
`PUT /api/admin/orders/{id}/cancel`

### 2. Request Body / Parameters
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
  "status": "string",
  "refundTransactionId": 0,
  "message": "string"
}
```

---

## GET /api/admin/categories/{id}

**Summary:** Get category by ID

### 1. Endpoint
`GET /api/admin/categories/{id}`

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
  "name": "string",
  "icon": "string",
  "orderIndex": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/admin/categories/{id}

**Summary:** Update category

### 1. Endpoint
`PUT /api/admin/categories/{id}`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "icon": "string",
  "orderIndex": 0
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
  "icon": "string",
  "orderIndex": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/admin/categories/{id}

**Summary:** Delete category

### 1. Endpoint
`DELETE /api/admin/categories/{id}`

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

`No JSON response body`

---

## PUT /api/admin/cards/{id}/unlock

### 1. Endpoint
`PUT /api/admin/cards/{id}/unlock`

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
  "userFullName": "string",
  "bankCode": "string",
  "bankName": "string",
  "accountNumber": "string",
  "accountName": "string",
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/admin/cards/{id}/lock

### 1. Endpoint
`PUT /api/admin/cards/{id}/lock`

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
  "userFullName": "string",
  "bankCode": "string",
  "bankName": "string",
  "accountNumber": "string",
  "accountName": "string",
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## PUT /api/addresses/{id}

### 1. Endpoint
`PUT /api/addresses/{id}`

### 2. Request Body / Parameters
```json
{
  "recipientName": "string",
  "phone": "string",
  "address": "string",
  "isDefault": true
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
  "recipientName": "string",
  "phone": "string",
  "address": "string",
  "isDefault": true,
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/addresses/{id}

### 1. Endpoint
`DELETE /api/addresses/{id}`

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

`No JSON response body`

---

## PUT /api/addresses/{id}/default

### 1. Endpoint
`PUT /api/addresses/{id}/default`

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
  "recipientName": "string",
  "phone": "string",
  "address": "string",
  "isDefault": true,
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/wallets/{walletId}/withdraw

### 1. Endpoint
`POST /api/wallets/{walletId}/withdraw`

### 2. Request Body / Parameters
```json
{
  "bankAccountId": 0,
  "amount": 0,
  "note": "string"
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
  "transactionId": 0,
  "status": "string",
  "amount": 0,
  "fee": 0,
  "totalDebit": 0,
  "balanceBefore": 0,
  "availableBalanceBefore": 0,
  "availableBalanceAfter": 0,
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/wallet/transfers

### 1. Endpoint
`POST /api/wallet/transfers`

### 2. Request Body / Parameters
```json
{
  "toAccountNumber": "string",
  "amount": 0,
  "description": "string"
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
  "transactionId": 0,
  "fromAccountNumber": "string",
  "toAccountNumber": "string",
  "toAccountName": "string",
  "amount": 0,
  "previousBalance": 0,
  "newBalance": 0,
  "description": "string",
  "timestamp": "2023-10-10T10:10:10.000Z",
  "status": "string",
  "message": "string",
  "transactionType": "string",
  "success": true
}
```

---

## POST /api/user/E-Wallet/transfers

### 1. Endpoint
`POST /api/user/E-Wallet/transfers`

### 2. Request Body / Parameters
```json
{
  "toUserId": 0,
  "amount": 0,
  "note": "string"
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
  "partnerName": "string",
  "direction": "string",
  "amount": 0,
  "status": "string",
  "note": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "type": "string",
  "referenceId": "string",
  "success": true
}
```

---

## POST /api/upload

### 1. Endpoint
`POST /api/upload`

### 2. Request Body / Parameters
```json
{
  "file": "string"
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
  "url": "string",
  "base64": "string",
  "error": "string"
}
```

---

## GET /api/transactions

### 1. Endpoint
`GET /api/transactions`

### 2. Request Body / Parameters
- `pageable` (query, string) *(required)*

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "type": "string",
      "description": "string",
      "category": "string",
      "amount": 0,
      "date": "2023-10-10T10:10:10.000Z",
      "status": "string",
      "direction": "string"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## POST /api/transactions

### 1. Endpoint
`POST /api/transactions`

### 2. Request Body / Parameters
```json
{
  "type": "string",
  "amount": 0,
  "note": "string",
  "toUserName": "string",
  "sourceCardId": "string"
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

`No JSON response body`

---

## POST /api/transactions/transfer

### 1. Endpoint
`POST /api/transactions/transfer`

### 2. Request Body / Parameters
```json
{
  "toUserId": 0,
  "amount": 0,
  "note": "string"
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

`No JSON response body`

---

## POST /api/support/tickets

### 1. Endpoint
`POST /api/support/tickets`

### 2. Request Body / Parameters
```json
{
  "subject": "string",
  "message": "string",
  "orderId": 0,
  "attachments": "string"
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
  "ticketId": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "message": "string"
}
```

---

## GET /api/restaurant-owner/products

### 1. Endpoint
`GET /api/restaurant-owner/products`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `search` (query, string) 
- `status` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "price": 0,
      "imageBase64": "string",
      "categoryId": 0,
      "status": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## POST /api/restaurant-owner/products

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
  "status": "string"
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
  "description": "string",
  "price": 0,
  "imageBase64": "string",
  "categoryId": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/qr/wallet/with-amount

### 1. Endpoint
`POST /api/qr/wallet/with-amount`

### 2. Request Body / Parameters
```json
{
  "amount": 0
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
  "userId": "string",
  "walletId": "string",
  "receiverName": "string",
  "accountNumber": "string",
  "amount": 0,
  "currency": "string",
  "valid": true,
  "transferReady": true,
  "qrBase64": "string"
}
```

---

## POST /api/qr/resolve

### 1. Endpoint
`POST /api/qr/resolve`

### 2. Request Body / Parameters
```json
{
  "qrPayload": "string"
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
  "userId": "string",
  "walletId": "string",
  "receiverName": "string",
  "accountNumber": "string",
  "amount": 0,
  "currency": "string",
  "valid": true
}
```

---

## POST /api/qr/read-image

### 1. Endpoint
`POST /api/qr/read-image`

### 2. Request Body / Parameters
```json
{
  "file": "string"
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
  "userId": "string",
  "walletId": "string",
  "receiverName": "string",
  "accountNumber": "string",
  "amount": 0,
  "currency": "string",
  "valid": true,
  "transferReady": true
}
```

---

## GET /api/products/{id}/reviews

### 1. Endpoint
`GET /api/products/{id}/reviews`

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
[
  {
    "id": 0,
    "productId": 0,
    "userId": 0,
    "rating": 0,
    "comment": "string",
    "createdAt": "2023-10-10T10:10:10.000Z",
    "productName": "string"
  }
]
```

---

## POST /api/products/{id}/reviews

### 1. Endpoint
`POST /api/products/{id}/reviews`

### 2. Request Body / Parameters
```json
{
  "rating": 0,
  "comment": "string"
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
  "productId": 0,
  "userId": 0,
  "rating": 0,
  "comment": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "productName": "string"
}
```

---

## GET /api/orders

### 1. Endpoint
`GET /api/orders`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 
- `status` (query, string) 
- `sort` (query, string) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "totalAmount": 0,
      "status": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z",
      "recipientName": "string",
      "recipientPhone": "string",
      "deliveryAddress": "string",
      "note": "string",
      "paymentMethod": "string",
      "itemCount": 0,
      "items": [
        {
          "productId": 0,
          "productName": "string",
          "productImage": "string",
          "quantity": 0,
          "priceAtTime": 0,
          "subtotal": 0
        }
      ],
      "paymentHistory": [
        {
          "transactionId": 0,
          "amount": 0,
          "status": "string",
          "paymentMethod": "string",
          "timestamp": "2023-10-10T10:10:10.000Z",
          "referenceId": "string"
        }
      ],
      "statusHistory": [
        {
          "status": "string",
          "timestamp": "2023-10-10T10:10:10.000Z",
          "note": "string"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## POST /api/orders

### 1. Endpoint
`POST /api/orders`

### 2. Request Body / Parameters
```json
{
  "items": [
    {
      "quantity": 0,
      "product_id": "string",
      "price_at_time": 0
    }
  ],
  "note": "string",
  "user_id": "string",
  "delivery_address": "string",
  "recipient_name": "string",
  "recipient_phone": "string",
  "payment_method": "string",
  "restaurant_id": "string",
  "total_amount": "string"
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
  "totalAmount": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "recipientName": "string",
  "recipientPhone": "string",
  "deliveryAddress": "string",
  "note": "string",
  "paymentMethod": "string",
  "itemCount": 0,
  "items": [
    {
      "productId": 0,
      "productName": "string",
      "productImage": "string",
      "quantity": 0,
      "priceAtTime": 0,
      "subtotal": 0
    }
  ],
  "paymentHistory": [
    {
      "transactionId": 0,
      "amount": 0,
      "status": "string",
      "paymentMethod": "string",
      "timestamp": "2023-10-10T10:10:10.000Z",
      "referenceId": "string"
    }
  ],
  "statusHistory": [
    {
      "status": "string",
      "timestamp": "2023-10-10T10:10:10.000Z",
      "note": "string"
    }
  ]
}
```

---

## POST /api/orders/{id}/reorder

### 1. Endpoint
`POST /api/orders/{id}/reorder`

### 2. Request Body / Parameters
```json
{
  "deliveryAddress": "string",
  "note": "string"
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
  "orderId": 0,
  "totalAmount": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/favorites/restaurants/{id}

### 1. Endpoint
`POST /api/favorites/restaurants/{id}`

### 2. Request Body / Parameters
- `id` (path, string) *(required)*

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
  "restaurantId": "string",
  "restaurantName": "string",
  "logoBase64": "string",
  "address": "string",
  "favoritedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## DELETE /api/favorites/restaurants/{id}

### 1. Endpoint
`DELETE /api/favorites/restaurants/{id}`

### 2. Request Body / Parameters
- `id` (path, string) *(required)*

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

`No JSON response body`

---

## POST /api/face/verify

### 1. Endpoint
`POST /api/face/verify`

### 2. Request Body / Parameters
```json
{
  "image": "string"
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
  "similarity": 0,
  "result": "string",
  "matchedPose": "string",
  "threshold": 0,
  "message": "string"
}
```

---

## POST /api/face/register

### 1. Endpoint
`POST /api/face/register`

### 2. Request Body / Parameters
```json
{
  "image": "string"
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
  "embeddingId": 0,
  "userId": 0,
  "pose": "string",
  "message": "string"
}
```

---

## POST /api/face/embedding

### 1. Endpoint
`POST /api/face/embedding`

### 2. Request Body / Parameters
```json
{
  "file": "string"
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
{}
```

---

## POST /api/face/compare

### 1. Endpoint
`POST /api/face/compare`

### 2. Request Body / Parameters
```json
{
  "img1": "string",
  "img2": "string"
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
{}
```

---

## POST /api/face/add

### 1. Endpoint
`POST /api/face/add`

### 2. Request Body / Parameters
```json
{
  "image": "string"
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
  "embeddingId": 0,
  "userId": 0,
  "pose": "string",
  "message": "string"
}
```

---

## GET /api/cards

### 1. Endpoint
`GET /api/cards`

### 2. Request Body / Parameters
`None`

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
[
  {
    "id": 0,
    "cardNumber": "string",
    "holderName": "string",
    "expiryDate": "string",
    "cvv": "string",
    "type": "string",
    "bankName": "string",
    "status": "string",
    "last4": "string",
    "balanceCard": 0
  }
]
```

---

## POST /api/cards

### 1. Endpoint
`POST /api/cards`

### 2. Request Body / Parameters
```json
{
  "id": 0,
  "cardNumber": "string",
  "holderName": "string",
  "expiryDate": "string",
  "cvv": "string",
  "type": "string",
  "bankName": "string",
  "status": "string",
  "last4": "string",
  "balanceCard": 0
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
  "cardNumber": "string",
  "holderName": "string",
  "expiryDate": "string",
  "cvv": "string",
  "type": "string",
  "bankName": "string",
  "status": "string",
  "last4": "string",
  "balanceCard": 0
}
```

---

## POST /api/cards/{id}/default

### 1. Endpoint
`POST /api/cards/{id}/default`

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
  "cardNumber": "string",
  "message": "string",
  "default": true
}
```

---

## POST /api/cards/deposit

### 1. Endpoint
`POST /api/cards/deposit`

### 2. Request Body / Parameters
```json
{
  "cardId": 0,
  "amount": 0,
  "description": "string"
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
  "transactionId": 0,
  "cardId": 0,
  "cardNumber": "string",
  "amount": 0,
  "previousCardBalance": 0,
  "newCardBalance": 0,
  "previousWalletBalance": 0,
  "newWalletBalance": 0,
  "description": "string",
  "timestamp": "2023-10-10T10:10:10.000Z",
  "status": "string",
  "message": "string"
}
```

---

## POST /api/auth/verify-otp

### 1. Endpoint
`POST /api/auth/verify-otp`

### 2. Request Body / Parameters
```json
{
  "email": "string",
  "otpCode": "string",
  "purpose": "string"
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
{}
```

---

## POST /api/auth/register

### 1. Endpoint
`POST /api/auth/register`

### 2. Request Body / Parameters
```json
{
  "userName": "string",
  "email": "string",
  "phone": "string",
  "fullName": "string",
  "passwordHash": "string"
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
  "message": "string",
  "userId": 0,
  "accountNumber": "string",
  "walletId": "string"
}
```

---

## POST /api/auth/login

### 1. Endpoint
`POST /api/auth/login`

### 2. Request Body / Parameters
```json
{
  "userName": "string",
  "password": "string"
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
  "token": "string",
  "type": "string",
  "expiresIn": 0,
  "userName": "string",
  "email": "string",
  "fullName": "string",
  "roles": [
    "string"
  ],
  "status": "string",
  "active": true
}
```

---

## POST /api/auth/forgot-password

### 1. Endpoint
`POST /api/auth/forgot-password`

### 2. Request Body / Parameters
```json
{
  "email": "string"
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
{}
```

---

## POST /api/auth/admin/register

### 1. Endpoint
`POST /api/auth/admin/register`

### 2. Request Body / Parameters
```json
{
  "userName": "string",
  "email": "string",
  "phone": "string",
  "fullName": "string",
  "passwordHash": "string",
  "role": "string"
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
  "message": "string",
  "userId": 0,
  "accountNumber": "string",
  "walletId": "string"
}
```

---

## POST /api/admin/wallets/topup

### 1. Endpoint
`POST /api/admin/wallets/topup`

### 2. Request Body / Parameters
```json
{
  "walletId": 0,
  "userId": 0,
  "accountNumber": "string",
  "amountAdd": 0
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
  "transactionId": 0,
  "walletId": 0,
  "userId": 0,
  "accountNumber": "string",
  "amountAdded": 0,
  "previousBalance": 0,
  "newBalance": 0,
  "status": "string",
  "message": "string",
  "timestamp": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/admin/users

### 1. Endpoint
`POST /api/admin/users`

### 2. Request Body / Parameters
```json
{
  "userName": "string",
  "email": "string",
  "phone": "string",
  "fullName": "string",
  "passwordHash": "string",
  "role": "string"
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
  "message": "string",
  "userId": 0,
  "accountNumber": "string",
  "walletId": "string"
}
```

---

## POST /api/admin/support-tickets/{id}/reply

### 1. Endpoint
`POST /api/admin/support-tickets/{id}/reply`

### 2. Request Body / Parameters
```json
{
  "replyMessage": "string",
  "status": "string",
  "assigneeId": 0
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
  "userId": 0,
  "userName": "string",
  "subject": "string",
  "message": "string",
  "orderId": 0,
  "attachments": "string",
  "status": "string",
  "assignedTo": 0,
  "assignedToName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "replies": [
    {
      "id": 0,
      "ticketId": 0,
      "adminId": 0,
      "adminName": "string",
      "message": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ]
}
```

---

## GET /api/admin/restaurants

**Summary:** Get restaurants with pagination, search, and filters

### 1. Endpoint
`GET /api/admin/restaurants`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `search` (query, string) 
- `status` (query, boolean) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": "string",
      "name": "string",
      "phone": "string",
      "email": "string",
      "address": "string",
      "logoBase64": "string",
      "status": true,
      "productCount": 0,
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## POST /api/admin/restaurants

**Summary:** Create new restaurant

### 1. Endpoint
`POST /api/admin/restaurants`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "phone": "string",
  "email": "string",
  "address": "string",
  "logoBase64": "string",
  "status": true
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
  "id": "string",
  "name": "string",
  "phone": "string",
  "email": "string",
  "address": "string",
  "logoBase64": "string",
  "status": true,
  "productCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/admin/products

**Summary:** Get products with pagination, search, and filters

### 1. Endpoint
`GET /api/admin/products`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `search` (query, string) 
- `categoryId` (query, integer) 
- `restaurantId` (query, string) 
- `status` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "price": 0,
      "category": {
        "id": 0,
        "name": "string",
        "icon": "string"
      },
      "restaurant": {
        "id": "string",
        "name": "string",
        "logoBase64": "string"
      },
      "imageBase64": "string",
      "status": "string",
      "ratingAvg": 0,
      "ratingCount": 0,
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## POST /api/admin/products

**Summary:** Create new product

### 1. Endpoint
`POST /api/admin/products`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "categoryId": 0,
  "restaurantId": "string",
  "status": "string",
  "imageBase64": "string"
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
  "description": "string",
  "price": 0,
  "category": {
    "id": 0,
    "name": "string",
    "icon": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "imageBase64": "string",
  "status": "string",
  "ratingAvg": 0,
  "ratingCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/admin/categories

**Summary:** Get categories with pagination, search, and sort

### 1. Endpoint
`GET /api/admin/categories`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `search` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "content": [
    {
      "id": 0,
      "name": "string",
      "icon": "string",
      "orderIndex": 0,
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

---

## POST /api/admin/categories

**Summary:** Create new category

### 1. Endpoint
`POST /api/admin/categories`

### 2. Request Body / Parameters
```json
{
  "name": "string",
  "icon": "string",
  "orderIndex": 0
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
  "icon": "string",
  "orderIndex": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/addresses

### 1. Endpoint
`GET /api/addresses`

### 2. Request Body / Parameters
`None`

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
[
  {
    "id": 0,
    "recipientName": "string",
    "phone": "string",
    "address": "string",
    "isDefault": true,
    "createdAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## POST /api/addresses

### 1. Endpoint
`POST /api/addresses`

### 2. Request Body / Parameters
```json
{
  "recipientName": "string",
  "phone": "string",
  "address": "string",
  "isDefault": true
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
  "recipientName": "string",
  "phone": "string",
  "address": "string",
  "isDefault": true,
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## POST /api/E-Wallet/deposits

### 1. Endpoint
`POST /api/E-Wallet/deposits`

### 2. Request Body / Parameters
```json
{
  "walletId": 0,
  "amount": 0
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
  "message": "string",
  "newBalance": 0
}
```

---

## GET /api/wallet/transfers/lookup/{accountNumber}

### 1. Endpoint
`GET /api/wallet/transfers/lookup/{accountNumber}`

### 2. Request Body / Parameters
- `accountNumber` (path, string) *(required)*

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
  "accountNumber": "string",
  "accountName": "string",
  "accountHolderName": "string",
  "avatarUrl": "string",
  "accountType": "string",
  "active": true,
  "message": "string",
  "found": true
}
```

---

## GET /api/wallet/summary

### 1. Endpoint
`GET /api/wallet/summary`

### 2. Request Body / Parameters
- `period` (query, string) 

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
  "income": 0,
  "expense": 0,
  "period": "string"
}
```

---

## GET /api/wallet/me

### 1. Endpoint
`GET /api/wallet/me`

### 2. Request Body / Parameters
`None`

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
  "userId": "string",
  "walletId": "string",
  "accountName": "string",
  "accountNumber": "string",
  "currency": "string",
  "balance": 0
}
```

---

## GET /api/wallet/balance

### 1. Endpoint
`GET /api/wallet/balance`

### 2. Request Body / Parameters
`None`

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
  "balance": 0,
  "monthlyChangePercent": 0
}
```

---

## GET /api/wallet/available-balance

### 1. Endpoint
`GET /api/wallet/available-balance`

### 2. Request Body / Parameters
`None`

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
  "totalBalance": 0,
  "availableBalance": 0,
  "heldBalance": 0,
  "currency": "string"
}
```

---

## GET /api/userManager/all

### 1. Endpoint
`GET /api/userManager/all`

### 2. Request Body / Parameters
`None`

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
[
  {
    "id": 0,
    "userName": "string",
    "email": "string",
    "phone": "string",
    "fullName": "string",
    "createdAt": "2023-10-10T10:10:10.000Z",
    "role": "string",
    "active": true
  }
]
```

---

## GET /api/user/E-Wallet/transfers/{transferId}

### 1. Endpoint
`GET /api/user/E-Wallet/transfers/{transferId}`

### 2. Request Body / Parameters
- `transferId` (path, integer) *(required)*

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
  "direction": "string",
  "amount": 0,
  "status": "string",
  "type": "string",
  "partnerName": "string",
  "note": "string",
  "referenceId": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/user/E-Wallet/transfers/wallets/search

### 1. Endpoint
`GET /api/user/E-Wallet/transfers/wallets/search`

### 2. Request Body / Parameters
- `params` (query, object) *(required)*

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
[
  {
    "walletId": 0,
    "userId": 0,
    "fullName": "string",
    "accountNumber": "string"
  }
]
```

---

## GET /api/user/E-Wallet/transfers/wallet/{walletId}/history

### 1. Endpoint
`GET /api/user/E-Wallet/transfers/wallet/{walletId}/history`

### 2. Request Body / Parameters
- `walletId` (path, integer) *(required)*
- `direction` (query, string) 
- `filter` (query, string) 
- `page` (query, integer) 
- `size` (query, integer) 

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
{}
```

---

## GET /api/transactions/incoming

### 1. Endpoint
`GET /api/transactions/incoming`

### 2. Request Body / Parameters
- `limit` (query, integer) 

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
[
  {
    "id": 0,
    "type": "string",
    "amount": 0,
    "date": "2023-10-10T10:10:10.000Z",
    "status": "string",
    "description": "string"
  }
]
```

---

## GET /api/test/categories-list

### 1. Endpoint
`GET /api/test/categories-list`

### 2. Request Body / Parameters
`None`

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
[
  {
    "id": 0,
    "name": "string",
    "icon": "string",
    "orderIndex": 0,
    "createdAt": "2023-10-10T10:10:10.000Z",
    "updatedAt": "2023-10-10T10:10:10.000Z",
    "products": [
      {
        "id": 0,
        "name": "string",
        "description": "string",
        "price": 0,
        "imageBase64": "string",
        "categoryId": 0,
        "restaurantId": "string",
        "ratingAvg": 0,
        "ratingCount": 0,
        "status": "string",
        "createdAt": "2023-10-10T10:10:10.000Z",
        "updatedAt": "2023-10-10T10:10:10.000Z",
        "deletedAt": "2023-10-10T10:10:10.000Z",
        "category": "Circular reference: Category",
        "restaurant": {
          "id": "string",
          "name": "string",
          "phone": "string",
          "email": "string",
          "address": "string",
          "logoBase64": "string",
          "status": true,
          "productCount": 0,
          "createdAt": "2023-10-10T10:10:10.000Z",
          "updatedAt": "2023-10-10T10:10:10.000Z",
          "deletedAt": "2023-10-10T10:10:10.000Z",
          "ownerId": 0,
          "products": [
            "Circular reference: Product"
          ]
        },
        "reviews": [
          {
            "id": 0,
            "userId": 0,
            "productId": 0,
            "rating": 0,
            "comment": "string",
            "createdAt": "2023-10-10T10:10:10.000Z",
            "user": {
              "id": 0,
              "userName": "string",
              "email": "string",
              "phone": "string",
              "fullName": "string",
              "avatar": "string",
              "dateOfBirth": "string",
              "address": "string",
              "passwordHash": "string",
              "pinHash": "string",
              "role": "string",
              "createdAt": "2023-10-10T10:10:10.000Z",
              "updatedAt": "2023-10-10T10:10:10.000Z",
              "wallet": {
                "id": 0,
                "code": "string",
                "currency": "string",
                "balance": 0,
                "availableBalance": 0,
                "status": "string",
                "accountNumber": "string",
                "createdAt": "2023-10-10T10:10:10.000Z",
                "updatedAt": "2023-10-10T10:10:10.000Z",
                "transactions": [
                  {
                    "id": 0,
                    "wallet": "Circular reference: Wallet",
                    "type": "string",
                    "direction": "string",
                    "amount": 0,
                    "fee": 0,
                    "balanceBefore": 0,
                    "balanceAfter": 0,
                    "status": "string",
                    "referenceId": "string",
                    "idempotencyKey": "string",
                    "relatedTxId": 0,
                    "metadata": "string",
                    "createdAt": "2023-10-10T10:10:10.000Z",
                    "updatedAt": "2023-10-10T10:10:10.000Z"
                  }
                ]
              },
              "avatarUrl": "string",
              "membership": "string",
              "active": true,
              "verified": true
            },
            "product": "Circular reference: Product"
          }
        ]
      }
    ]
  }
]
```

---

## GET /api/test/categories-count

### 1. Endpoint
`GET /api/test/categories-count`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/shipper/orders

### 1. Endpoint
`GET /api/shipper/orders`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `status` (query, string) 
- `assigned` (query, boolean) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": 0,
      "orderCode": "string",
      "restaurantName": "string",
      "restaurantAddress": "string",
      "customerName": "string",
      "customerPhone": "string",
      "deliveryAddress": "string",
      "totalAmount": 0,
      "status": "string",
      "paymentMethod": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## GET /api/shipper/orders/{id}

### 1. Endpoint
`GET /api/shipper/orders/{id}`

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
  "orderCode": "string",
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "customer": {
    "name": "string",
    "phone": "string",
    "address": "string",
    "latitude": 0,
    "longitude": 0
  },
  "items": [
    {
      "productName": "string",
      "quantity": 0,
      "note": "string"
    }
  ],
  "totalAmount": 0,
  "paymentMethod": "string",
  "paymentStatus": "string",
  "note": "string",
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/restaurant-owner/orders

### 1. Endpoint
`GET /api/restaurant-owner/orders`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `status` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": 0,
      "orderCode": "string",
      "userId": 0,
      "customerName": "string",
      "customerPhone": "string",
      "totalAmount": 0,
      "status": "string",
      "paymentMethod": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "items": [
        {
          "productId": 0,
          "productName": "string",
          "productImage": "string",
          "quantity": 0,
          "priceAtTime": 0,
          "subtotal": 0
        }
      ]
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## GET /api/restaurant-owner/orders/{id}

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
  "orderCode": "string",
  "userId": 0,
  "customerName": "string",
  "customerPhone": "string",
  "deliveryAddress": "string",
  "note": "string",
  "totalAmount": 0,
  "status": "string",
  "paymentMethod": "string",
  "paymentStatus": "string",
  "transactionId": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "items": [
    {
      "productId": 0,
      "productName": "string",
      "quantity": 0,
      "price": 0,
      "note": "string"
    }
  ]
}
```

---

## GET /api/qr/wallet

### 1. Endpoint
`GET /api/qr/wallet`

### 2. Request Body / Parameters
`None`

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
  "userId": "string",
  "walletId": "string",
  "receiverName": "string",
  "accountNumber": "string",
  "amount": 0,
  "currency": "string",
  "valid": true,
  "transferReady": true,
  "qrBase64": "string"
}
```

---

## GET /api/qr/wallet/download

### 1. Endpoint
`GET /api/qr/wallet/download`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/products

### 1. Endpoint
`GET /api/products`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `limit` (query, integer) 
- `search` (query, string) 
- `categoryId` (query, integer) 
- `restaurantId` (query, string) 
- `status` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "price": 0,
      "category": {
        "id": 0,
        "name": "string",
        "icon": "string"
      },
      "restaurant": {
        "id": "string",
        "name": "string",
        "logoBase64": "string"
      },
      "imageBase64": "string",
      "status": "string",
      "ratingAvg": 0,
      "ratingCount": 0,
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z"
    }
  ],
  "pagination": {
    "total": 0,
    "page": 0,
    "limit": 0,
    "totalPages": 0
  }
}
```

---

## GET /api/products/{id}

### 1. Endpoint
`GET /api/products/{id}`

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
  "name": "string",
  "description": "string",
  "price": 0,
  "category": {
    "id": 0,
    "name": "string",
    "icon": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "logoBase64": "string"
  },
  "imageBase64": "string",
  "status": "string",
  "ratingAvg": 0,
  "ratingCount": 0,
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/orders/{id}

### 1. Endpoint
`GET /api/orders/{id}`

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
  "totalAmount": 0,
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "recipientName": "string",
  "recipientPhone": "string",
  "deliveryAddress": "string",
  "note": "string",
  "paymentMethod": "string",
  "itemCount": 0,
  "items": [
    {
      "productId": 0,
      "productName": "string",
      "productImage": "string",
      "quantity": 0,
      "priceAtTime": 0,
      "subtotal": 0
    }
  ],
  "paymentHistory": [
    {
      "transactionId": 0,
      "amount": 0,
      "status": "string",
      "paymentMethod": "string",
      "timestamp": "2023-10-10T10:10:10.000Z",
      "referenceId": "string"
    }
  ],
  "statusHistory": [
    {
      "status": "string",
      "timestamp": "2023-10-10T10:10:10.000Z",
      "note": "string"
    }
  ]
}
```

---

## GET /api/orders/tracking/{id}

### 1. Endpoint
`GET /api/orders/tracking/{id}`

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
  "orderId": 0,
  "currentStatus": "string",
  "estimatedDeliveryTime": "2023-10-10T10:10:10.000Z",
  "statusHistory": [
    {
      "status": "string",
      "timestamp": "2023-10-10T10:10:10.000Z"
    }
  ],
  "deliveryLocation": {
    "lat": 0,
    "lng": 0
  }
}
```

---

## GET /api/notifications

### 1. Endpoint
`GET /api/notifications`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 
- `unreadOnly` (query, boolean) 

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
  "content": [
    {
      "id": 0,
      "type": "string",
      "title": "string",
      "content": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "read": true
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}
```

---

## GET /api/me

### 1. Endpoint
`GET /api/me`

### 2. Request Body / Parameters
`None`

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
{}
```

---

## GET /api/favorites/restaurants

### 1. Endpoint
`GET /api/favorites/restaurants`

### 2. Request Body / Parameters
`None`

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
[
  {
    "id": 0,
    "restaurantId": "string",
    "restaurantName": "string",
    "logoBase64": "string",
    "address": "string",
    "favoritedAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## GET /api/face/list/{userId}

### 1. Endpoint
`GET /api/face/list/{userId}`

### 2. Request Body / Parameters
- `userId` (path, integer) *(required)*

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
[
  {
    "id": 0,
    "userId": 0,
    "pose": "string",
    "createdAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## GET /api/contacts/frequent

### 1. Endpoint
`GET /api/contacts/frequent`

### 2. Request Body / Parameters
- `limit` (query, integer) 

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
[
  {
    "id": 0,
    "name": "string",
    "avatarUrl": "string"
  }
]
```

---

## GET /api/cards/{userId}/users

### 1. Endpoint
`GET /api/cards/{userId}/users`

### 2. Request Body / Parameters
- `userId` (path, integer) *(required)*

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
[
  {
    "id": 0,
    "cardNumber": "string",
    "holderName": "string",
    "expiryDate": "string",
    "cvv": "string",
    "type": "string",
    "bankName": "string",
    "status": "string",
    "last4": "string",
    "balanceCard": 0
  }
]
```

---

## GET /api/cards/deposit/history

### 1. Endpoint
`GET /api/cards/deposit/history`

### 2. Request Body / Parameters
`None`

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
[
  {
    "transactionId": 0,
    "cardId": 0,
    "cardNumber": "string",
    "bankName": "string",
    "amount": 0,
    "description": "string",
    "timestamp": "2023-10-10T10:10:10.000Z",
    "status": "string"
  }
]
```

---

## GET /api/bank-account

### 1. Endpoint
`GET /api/bank-account`

### 2. Request Body / Parameters
- `userId` (query, integer) *(required)*

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
{}
```

---

## GET /api/analytics/spending

### 1. Endpoint
`GET /api/analytics/spending`

### 2. Request Body / Parameters
- `range` (query, string) 

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
[
  {
    "label": "string",
    "value": 0
  }
]
```

---

## GET /api/admin/wallets

### 1. Endpoint
`GET /api/admin/wallets`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 

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
[
  {
    "id": 0,
    "accountNumber": "string",
    "availableBalance": 0,
    "createdAt": "2023-10-10T10:10:10.000Z",
    "status": "string",
    "userId": 0,
    "role": "string"
  }
]
```

---

## GET /api/admin/users/{userId}/qrcodes

### 1. Endpoint
`GET /api/admin/users/{userId}/qrcodes`

### 2. Request Body / Parameters
- `userId` (path, integer) *(required)*

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
[
  {
    "id": 0,
    "userId": 0,
    "userFullName": "string",
    "walletId": 0,
    "codeValue": "string",
    "type": "string",
    "expiresAt": "2023-10-10T10:10:10.000Z",
    "createdAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## GET /api/admin/users/{userId}/cards

### 1. Endpoint
`GET /api/admin/users/{userId}/cards`

### 2. Request Body / Parameters
- `userId` (path, integer) *(required)*

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
[
  {
    "id": 0,
    "userId": 0,
    "userFullName": "string",
    "bankCode": "string",
    "bankName": "string",
    "accountNumber": "string",
    "accountName": "string",
    "status": "string",
    "createdAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## GET /api/admin/transactions

### 1. Endpoint
`GET /api/admin/transactions`

### 2. Request Body / Parameters
`None`

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
[
  {
    "transactionId": "string",
    "walletId": "string",
    "partnerName": "string",
    "direction": "string",
    "amount": 0,
    "status": "string",
    "note": "string",
    "createdAt": "2023-10-10T10:10:10.000Z",
    "type": "string",
    "referenceId": "string",
    "success": true,
    "userId": 0
  }
]
```

---

## GET /api/admin/support-tickets

### 1. Endpoint
`GET /api/admin/support-tickets`

### 2. Request Body / Parameters
- `status` (query, string) 
- `page` (query, integer) 
- `size` (query, integer) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "userId": 0,
      "userName": "string",
      "subject": "string",
      "message": "string",
      "orderId": 0,
      "attachments": "string",
      "status": "string",
      "assignedTo": 0,
      "assignedToName": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z",
      "replies": [
        {
          "id": 0,
          "ticketId": 0,
          "adminId": 0,
          "adminName": "string",
          "message": "string",
          "createdAt": "2023-10-10T10:10:10.000Z"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/support-tickets/{id}

### 1. Endpoint
`GET /api/admin/support-tickets/{id}`

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
  "userName": "string",
  "subject": "string",
  "message": "string",
  "orderId": 0,
  "attachments": "string",
  "status": "string",
  "assignedTo": 0,
  "assignedToName": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z",
  "replies": [
    {
      "id": 0,
      "ticketId": 0,
      "adminId": 0,
      "adminName": "string",
      "message": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
    }
  ]
}
```

---

## GET /api/admin/statistics/top-products

### 1. Endpoint
`GET /api/admin/statistics/top-products`

### 2. Request Body / Parameters
- `limit` (query, integer) 
- `sortBy` (query, string) 
- `fromDate` (query, string) 
- `toDate` (query, string) 

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
[
  {
    "productId": 0,
    "productName": "string",
    "quantitySold": 0,
    "revenue": 0
  }
]
```

---

## GET /api/admin/statistics/revenue

### 1. Endpoint
`GET /api/admin/statistics/revenue`

### 2. Request Body / Parameters
- `groupBy` (query, string) 
- `fromDate` (query, string) 
- `toDate` (query, string) 

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
  "labels": [
    "string"
  ],
  "revenue": [
    0
  ],
  "orderCount": [
    0
  ]
}
```

---

## GET /api/admin/statistics/overview

### 1. Endpoint
`GET /api/admin/statistics/overview`

### 2. Request Body / Parameters
- `fromDate` (query, string) 
- `toDate` (query, string) 

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
  "totalOrders": 0,
  "totalRevenue": 0,
  "averageOrderValue": 0,
  "ordersByStatus": {},
  "revenueToday": 0,
  "ordersToday": 0,
  "newUsersToday": 0,
  "topRestaurants": [
    {
      "restaurantId": "string",
      "restaurantName": "string",
      "orderCount": 0,
      "revenue": 0
    }
  ]
}
```

---

## GET /api/admin/shippers

### 1. Endpoint
`GET /api/admin/shippers`

### 2. Request Body / Parameters
- `search` (query, string) 
- `isOnline` (query, boolean) 
- `page` (query, integer) 
- `size` (query, integer) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "userName": "string",
      "fullName": "string",
      "email": "string",
      "phone": "string",
      "avatarUrl": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "statistics": {
        "totalOrders": 0,
        "completedOrders": 0,
        "failedOrders": 0,
        "totalEarnings": 0,
        "avgRating": 0,
        "dailyStats": [
          {
            "date": "string",
            "orders": 0,
            "earnings": 0
          }
        ]
      },
      "active": true
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/shippers/{id}

### 1. Endpoint
`GET /api/admin/shippers/{id}`

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
  "userName": "string",
  "fullName": "string",
  "email": "string",
  "phone": "string",
  "avatarUrl": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "statistics": {
    "totalOrders": 0,
    "completedOrders": 0,
    "failedOrders": 0,
    "totalEarnings": 0,
    "avgRating": 0,
    "dailyStats": [
      {
        "date": "string",
        "orders": 0,
        "earnings": 0
      }
    ]
  },
  "active": true
}
```

---

## GET /api/admin/shippers/{id}/statistics

### 1. Endpoint
`GET /api/admin/shippers/{id}/statistics`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- `fromDate` (query, string) 
- `toDate` (query, string) 

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
  "totalOrders": 0,
  "completedOrders": 0,
  "failedOrders": 0,
  "totalEarnings": 0,
  "avgRating": 0,
  "dailyStats": [
    {
      "date": "string",
      "orders": 0,
      "earnings": 0
    }
  ]
}
```

---

## GET /api/admin/shippers/{id}/orders

### 1. Endpoint
`GET /api/admin/shippers/{id}/orders`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- `status` (query, string) 
- `fromDate` (query, string) 
- `toDate` (query, string) 
- `page` (query, integer) 
- `size` (query, integer) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "userId": 0,
      "totalAmount": 0,
      "status": "string",
      "deliveryAddress": "string",
      "recipientName": "string",
      "recipientPhone": "string",
      "note": "string",
      "paymentMethod": "string",
      "restaurantId": "string",
      "restaurant": {
        "id": "string",
        "name": "string",
        "phone": "string",
        "email": "string",
        "address": "string",
        "logoBase64": "string",
        "status": true,
        "productCount": 0,
        "createdAt": "2023-10-10T10:10:10.000Z",
        "updatedAt": "2023-10-10T10:10:10.000Z",
        "deletedAt": "2023-10-10T10:10:10.000Z",
        "ownerId": 0,
        "products": [
          {
            "id": 0,
            "name": "string",
            "description": "string",
            "price": 0,
            "imageBase64": "string",
            "categoryId": 0,
            "restaurantId": "string",
            "ratingAvg": 0,
            "ratingCount": 0,
            "status": "string",
            "createdAt": "2023-10-10T10:10:10.000Z",
            "updatedAt": "2023-10-10T10:10:10.000Z",
            "deletedAt": "2023-10-10T10:10:10.000Z",
            "category": {
              "id": 0,
              "name": "string",
              "icon": "string",
              "orderIndex": 0,
              "createdAt": "2023-10-10T10:10:10.000Z",
              "updatedAt": "2023-10-10T10:10:10.000Z",
              "products": [
                "Circular reference: Product"
              ]
            },
            "restaurant": "Circular reference: Restaurant",
            "reviews": [
              {
                "id": 0,
                "userId": 0,
                "productId": 0,
                "rating": 0,
                "comment": "string",
                "createdAt": "2023-10-10T10:10:10.000Z",
                "user": {
                  "id": 0,
                  "userName": "string",
                  "email": "string",
                  "phone": "string",
                  "fullName": "string",
                  "avatar": "string",
                  "dateOfBirth": "string",
                  "address": "string",
                  "passwordHash": "string",
                  "pinHash": "string",
                  "role": "string",
                  "createdAt": "2023-10-10T10:10:10.000Z",
                  "updatedAt": "2023-10-10T10:10:10.000Z",
                  "wallet": {
                    "id": 0,
                    "code": "string",
                    "currency": "string",
                    "balance": 0,
                    "availableBalance": 0,
                    "status": "string",
                    "accountNumber": "string",
                    "createdAt": "2023-10-10T10:10:10.000Z",
                    "updatedAt": "2023-10-10T10:10:10.000Z",
                    "transactions": [
                      {
                        "id": 0,
                        "wallet": "Circular reference: Wallet",
                        "type": "string",
                        "direction": "string",
                        "amount": 0,
                        "fee": 0,
                        "balanceBefore": 0,
                        "balanceAfter": 0,
                        "status": "string",
                        "referenceId": "string",
                        "idempotencyKey": "string",
                        "relatedTxId": 0,
                        "metadata": "string",
                        "createdAt": "2023-10-10T10:10:10.000Z",
                        "updatedAt": "2023-10-10T10:10:10.000Z"
                      }
                    ]
                  },
                  "avatarUrl": "string",
                  "membership": "string",
                  "active": true,
                  "verified": true
                },
                "product": "Circular reference: Product"
              }
            ]
          }
        ]
      },
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z",
      "shipperId": 0,
      "rejectedReason": "string",
      "deliveryFailedReason": "string",
      "confirmedAt": "2023-10-10T10:10:10.000Z",
      "readyAt": "2023-10-10T10:10:10.000Z",
      "pickedUpAt": "2023-10-10T10:10:10.000Z",
      "deliveredAt": "2023-10-10T10:10:10.000Z",
      "user": {
        "id": 0,
        "userName": "string",
        "email": "string",
        "phone": "string",
        "fullName": "string",
        "avatar": "string",
        "dateOfBirth": "string",
        "address": "string",
        "passwordHash": "string",
        "pinHash": "string",
        "role": "string",
        "createdAt": "2023-10-10T10:10:10.000Z",
        "updatedAt": "2023-10-10T10:10:10.000Z",
        "wallet": {
          "id": 0,
          "code": "string",
          "currency": "string",
          "balance": 0,
          "availableBalance": 0,
          "status": "string",
          "accountNumber": "string",
          "createdAt": "2023-10-10T10:10:10.000Z",
          "updatedAt": "2023-10-10T10:10:10.000Z",
          "transactions": [
            {
              "id": 0,
              "wallet": "Circular reference: Wallet",
              "type": "string",
              "direction": "string",
              "amount": 0,
              "fee": 0,
              "balanceBefore": 0,
              "balanceAfter": 0,
              "status": "string",
              "referenceId": "string",
              "idempotencyKey": "string",
              "relatedTxId": 0,
              "metadata": "string",
              "createdAt": "2023-10-10T10:10:10.000Z",
              "updatedAt": "2023-10-10T10:10:10.000Z"
            }
          ]
        },
        "avatarUrl": "string",
        "membership": "string",
        "active": true,
        "verified": true
      },
      "orderItems": [
        {
          "id": 0,
          "orderId": 0,
          "productId": 0,
          "quantity": 0,
          "priceAtTime": 0,
          "note": "string",
          "order": "Circular reference: Order",
          "product": {
            "id": 0,
            "name": "string",
            "description": "string",
            "price": 0,
            "imageBase64": "string",
            "categoryId": 0,
            "restaurantId": "string",
            "ratingAvg": 0,
            "ratingCount": 0,
            "status": "string",
            "createdAt": "2023-10-10T10:10:10.000Z",
            "updatedAt": "2023-10-10T10:10:10.000Z",
            "deletedAt": "2023-10-10T10:10:10.000Z",
            "category": {
              "id": 0,
              "name": "string",
              "icon": "string",
              "orderIndex": 0,
              "createdAt": "2023-10-10T10:10:10.000Z",
              "updatedAt": "2023-10-10T10:10:10.000Z",
              "products": [
                "Circular reference: Product"
              ]
            },
            "restaurant": {
              "id": "string",
              "name": "string",
              "phone": "string",
              "email": "string",
              "address": "string",
              "logoBase64": "string",
              "status": true,
              "productCount": 0,
              "createdAt": "2023-10-10T10:10:10.000Z",
              "updatedAt": "2023-10-10T10:10:10.000Z",
              "deletedAt": "2023-10-10T10:10:10.000Z",
              "ownerId": 0,
              "products": [
                "Circular reference: Product"
              ]
            },
            "reviews": [
              {
                "id": 0,
                "userId": 0,
                "productId": 0,
                "rating": 0,
                "comment": "string",
                "createdAt": "2023-10-10T10:10:10.000Z",
                "user": {
                  "id": 0,
                  "userName": "string",
                  "email": "string",
                  "phone": "string",
                  "fullName": "string",
                  "avatar": "string",
                  "dateOfBirth": "string",
                  "address": "string",
                  "passwordHash": "string",
                  "pinHash": "string",
                  "role": "string",
                  "createdAt": "2023-10-10T10:10:10.000Z",
                  "updatedAt": "2023-10-10T10:10:10.000Z",
                  "wallet": {
                    "id": 0,
                    "code": "string",
                    "currency": "string",
                    "balance": 0,
                    "availableBalance": 0,
                    "status": "string",
                    "accountNumber": "string",
                    "createdAt": "2023-10-10T10:10:10.000Z",
                    "updatedAt": "2023-10-10T10:10:10.000Z",
                    "transactions": [
                      {
                        "id": 0,
                        "wallet": "Circular reference: Wallet",
                        "type": "string",
                        "direction": "string",
                        "amount": 0,
                        "fee": 0,
                        "balanceBefore": 0,
                        "balanceAfter": 0,
                        "status": "string",
                        "referenceId": "string",
                        "idempotencyKey": "string",
                        "relatedTxId": 0,
                        "metadata": "string",
                        "createdAt": "2023-10-10T10:10:10.000Z",
                        "updatedAt": "2023-10-10T10:10:10.000Z"
                      }
                    ]
                  },
                  "avatarUrl": "string",
                  "membership": "string",
                  "active": true,
                  "verified": true
                },
                "product": "Circular reference: Product"
              }
            ]
          }
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/reviews

### 1. Endpoint
`GET /api/admin/reviews`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 
- `productId` (query, integer) 
- `userId` (query, integer) 
- `rating` (query, integer) 
- `fromDate` (query, string) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "user": {
        "id": 0,
        "fullName": "string",
        "avatarUrl": "string"
      },
      "product": {
        "id": 0,
        "name": "string"
      },
      "rating": 0,
      "comment": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/restaurants/export

**Summary:** Export restaurants to CSV

### 1. Endpoint
`GET /api/admin/restaurants/export`

### 2. Request Body / Parameters
- `search` (query, string) 
- `status` (query, boolean) 

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
"string"
```

---

## GET /api/admin/restaurants/check-name

**Summary:** Check if restaurant name exists

### 1. Endpoint
`GET /api/admin/restaurants/check-name`

### 2. Request Body / Parameters
- `name` (query, string) *(required)*
- `excludeId` (query, string) 

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
  "exists": true
}
```

---

## GET /api/admin/restaurant-owners

### 1. Endpoint
`GET /api/admin/restaurant-owners`

### 2. Request Body / Parameters
- `search` (query, string) 
- `page` (query, integer) 
- `size` (query, integer) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "userName": "string",
      "fullName": "string",
      "email": "string",
      "phone": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "restaurants": [
        {
          "id": "string",
          "name": "string",
          "status": true,
          "productCount": 0
        }
      ],
      "active": true
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/restaurant-owners/{id}

### 1. Endpoint
`GET /api/admin/restaurant-owners/{id}`

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
  "userName": "string",
  "fullName": "string",
  "email": "string",
  "phone": "string",
  "createdAt": "2023-10-10T10:10:10.000Z",
  "restaurants": [
    {
      "id": "string",
      "name": "string",
      "status": true,
      "productCount": 0
    }
  ],
  "active": true
}
```

---

## GET /api/admin/restaurant-owners/{id}/statistics

### 1. Endpoint
`GET /api/admin/restaurant-owners/{id}/statistics`

### 2. Request Body / Parameters
- `id` (path, integer) *(required)*
- `fromDate` (query, string) 
- `toDate` (query, string) 

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
  "totalRevenue": 0,
  "totalOrders": 0,
  "avgOrderValue": 0,
  "restaurants": [
    {
      "restaurantId": "string",
      "name": "string",
      "revenue": 0,
      "orders": 0
    }
  ]
}
```

---

## GET /api/admin/restaurant-owners/{id}/restaurants

### 1. Endpoint
`GET /api/admin/restaurant-owners/{id}/restaurants`

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
[
  {
    "id": "string",
    "name": "string",
    "status": true,
    "productCount": 0
  }
]
```

---

## GET /api/admin/orders

### 1. Endpoint
`GET /api/admin/orders`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 
- `status` (query, string) 
- `userId` (query, integer) 
- `restaurantId` (query, string) 
- `fromDate` (query, string) 
- `toDate` (query, string) 
- `search` (query, string) 
- `sortBy` (query, string) 
- `sortDir` (query, string) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "userId": 0,
      "userName": "string",
      "fullName": "string",
      "totalAmount": 0,
      "status": "string",
      "paymentMethod": "string",
      "recipientName": "string",
      "recipientPhone": "string",
      "deliveryAddress": "string",
      "note": "string",
      "createdAt": "2023-10-10T10:10:10.000Z",
      "updatedAt": "2023-10-10T10:10:10.000Z"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/admin/orders/{id}

### 1. Endpoint
`GET /api/admin/orders/{id}`

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
  "user": {
    "id": 0,
    "userName": "string",
    "fullName": "string",
    "phone": "string",
    "email": "string"
  },
  "restaurant": {
    "id": "string",
    "name": "string",
    "phone": "string",
    "address": "string"
  },
  "items": [
    {
      "productId": 0,
      "productName": "string",
      "quantity": 0,
      "priceAtTime": 0,
      "subtotal": 0,
      "image": "string"
    }
  ],
  "totalAmount": 0,
  "status": "string",
  "statusHistory": [
    {
      "status": "string",
      "updatedAt": "2023-10-10T10:10:10.000Z",
      "updatedBy": "string"
    }
  ],
  "payment": {
    "method": "string",
    "transactionId": 0,
    "amount": 0,
    "status": "string",
    "paidAt": "2023-10-10T10:10:10.000Z"
  },
  "deliveryInfo": {
    "recipientName": "string",
    "recipientPhone": "string",
    "deliveryAddress": "string",
    "note": "string"
  },
  "createdAt": "2023-10-10T10:10:10.000Z",
  "updatedAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/admin/export/users

### 1. Endpoint
`GET /api/admin/export/users`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/admin/export/transactions

### 1. Endpoint
`GET /api/admin/export/transactions`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/admin/export/orders

### 1. Endpoint
`GET /api/admin/export/orders`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/admin/dashboard/stats

### 1. Endpoint
`GET /api/admin/dashboard/stats`

### 2. Request Body / Parameters
`None`

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
  "totalUsers": 0,
  "newUsersToday": 0,
  "totalWallets": 0,
  "activeWallets": 0,
  "totalTransactions": 0,
  "transactionsToday": 0,
  "totalRevenue": 0,
  "revenueToday": 0,
  "totalOrders": 0,
  "ordersToday": 0
}
```

---

## GET /api/admin/dashboard/charts

### 1. Endpoint
`GET /api/admin/dashboard/charts`

### 2. Request Body / Parameters
- `period` (query, string) 
- `startDate` (query, string) 
- `endDate` (query, string) 

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
  "revenue": [
    {
      "date": "string",
      "value": 0
    }
  ],
  "transactions": [
    {
      "date": "string",
      "count": 0
    }
  ],
  "newUsers": [
    {
      "date": "string",
      "count": 0
    }
  ]
}
```

---

## GET /api/admin/categories/export

**Summary:** Export categories to Excel

### 1. Endpoint
`GET /api/admin/categories/export`

### 2. Request Body / Parameters
`None`

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
"string"
```

---

## GET /api/admin/categories/check-name

**Summary:** Check if category name exists

### 1. Endpoint
`GET /api/admin/categories/check-name`

### 2. Request Body / Parameters
- `name` (query, string) *(required)*
- `excludeId` (query, integer) 

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
  "exists": true
}
```

---

## GET /api/admin/cards/{id}

### 1. Endpoint
`GET /api/admin/cards/{id}`

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
  "userFullName": "string",
  "bankCode": "string",
  "bankName": "string",
  "accountNumber": "string",
  "accountName": "string",
  "status": "string",
  "createdAt": "2023-10-10T10:10:10.000Z"
}
```

---

## GET /api/admin/audit-logs

### 1. Endpoint
`GET /api/admin/audit-logs`

### 2. Request Body / Parameters
- `page` (query, integer) 
- `size` (query, integer) 

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
  "size": 0,
  "content": [
    {
      "id": 0,
      "adminId": 0,
      "adminName": "string",
      "actionType": "string",
      "targetType": "string",
      "targetId": 0,
      "reason": "string",
      "metadata": "string",
      "createdAt": "2023-10-10T10:10:10.000Z"
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
    "pageSize": 0,
    "pageNumber": 0,
    "unpaged": true
  },
  "empty": true
}
```

---

## GET /api/E-Wallet/deposits/wallet/{id}

### 1. Endpoint
`GET /api/E-Wallet/deposits/wallet/{id}`

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
  "balance": 0,
  "availableBalance": 0
}
```

---

## GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits

### 1. Endpoint
`GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits`

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
[
  {
    "id": 0,
    "amount": 0,
    "referenceId": "string",
    "status": "string",
    "createdAt": "2023-10-10T10:10:10.000Z"
  }
]
```

---

## GET /api/E-Wallet/deposits/wallet-by-username/{userName}

### 1. Endpoint
`GET /api/E-Wallet/deposits/wallet-by-username/{userName}`

### 2. Request Body / Parameters
- `userName` (path, string) *(required)*

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
  "walletId": 0,
  "balance": 0
}
```

---

## DELETE /api/face/{embeddingId}

### 1. Endpoint
`DELETE /api/face/{embeddingId}`

### 2. Request Body / Parameters
- `embeddingId` (path, integer) *(required)*

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
{}
```

---

## DELETE /api/cards/{id}

### 1. Endpoint
`DELETE /api/cards/{id}`

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

`No JSON response body`

---

## DELETE /api/admin/users/{id}

### 1. Endpoint
`DELETE /api/admin/users/{id}`

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
  "message": "string"
}
```

---

## DELETE /api/admin/reviews/{id}

### 1. Endpoint
`DELETE /api/admin/reviews/{id}`

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

`No JSON response body`

---

## DELETE /api/admin/qrcodes/{id}

### 1. Endpoint
`DELETE /api/admin/qrcodes/{id}`

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

`No JSON response body`

---

