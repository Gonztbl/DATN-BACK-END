# 📋 **API Loan History Documentation**

## 🔗 **Endpoint chính: GET `/api/v1/loans/my-loans`**

**Full URL:** `http://localhost:8080/api/v1/loans/my-loans`

---

## 🔗 **Endpoint mới: GET `/api/v1/loans/summary`**

**Full URL:** `http://localhost:8080/api/v1/loans/summary`

### Mô tả:
Lấy dữ liệu tóm tắt tất cả khoản vay và điểm tín dụng của người dùng hiện tại (dùng Dashboard / History).

### 📥 Request
Method: `GET`
Headers:
```javascript
{
  "Authorization": "Bearer YOUR_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

### 📤 Response
HTTP Status: `200 OK`

Response Structure:
```javascript
{
  "totalLoans": 4,
  "totalLoanAmount": 220000000,
  "approvedLoans": 2,
  "pendingAdminLoans": 1,
  "rejectedLoans": 1,
  "averageAiScore": 0.45,
  "creditRating": "Good",
  "statusDisplay": "Loan dashboard summary",
  "descriptionText": "This summary is based on all your loan requests and AI scoring."
}
```

---

## 📥 **Request**

### **Method:** `GET`

### **Headers:**
```javascript
{
  "Authorization": "Bearer YOUR_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

### **Query Parameters (Pagination):**
```javascript
{
  "page": 0,        // Page number (0-based)
  "size": 10,       // Items per page (default: 20)
  "sort": "createdAt,desc"  // Sort by createdAt descending
}
```

**Example URL:**
```
GET /api/v1/loans/my-loans?page=0&size=10&sort=createdAt,desc
```

---

## 📤 **Response**

### **HTTP Status:** `200 OK`

### **Response Structure:**
```javascript
{
  "content": [           // Array of loan requests
    {
      "id": 12,
      "amount": 50000000,
      "term": 12,
      "purpose": "Mua xe ô tô",
      "declaredIncome": 100000000,
      "jobSegmentNum": "BUSINESS_OWNER",
      "aiScore": 0.3,
      "aiDecision": "PASSED_AI",
      "adminNote": null,
      "finalStatus": "PENDING_ADMIN",
      "createdAt": "2026-04-03T16:25:56.332094",
      "updatedAt": "2026-04-03T16:25:56.333091",
      "statusDisplay": "Đang chờ admin xét duyệt",
      "descriptionText": "AI đã hoàn thành phân tích, chờ quản lý xem xét"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

---

## 📊 **Field Descriptions**

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | `Long` | Loan request ID | `12` |
| `amount` | `BigDecimal` | Loan amount (VND) | `50000000` |
| `term` | `Integer` | Term in months | `12` |
| `purpose` | `String` | Loan purpose | `"Mua xe ô tô"` |
| `declaredIncome` | `BigDecimal` | Declared monthly income | `100000000` |
| `jobSegmentNum` | `String` | Job segment | `"BUSINESS_OWNER"` |
| `aiScore` | `Double` | AI risk score (0.0-1.0) | `0.3` |
| `aiDecision` | `String` | AI decision | `"PASSED_AI"` |
| `adminNote` | `String` | Admin notes (may contain fraud alerts) | `null` |
| `finalStatus` | `String` | Current status | `"PENDING_ADMIN"` |
| `createdAt` | `String` | Creation timestamp | `"2026-04-03T16:25:56"` |
| `updatedAt` | `String` | Last update timestamp | `"2026-04-03T16:25:56"` |
| `statusDisplay` | `String` | User-friendly status | `"Đang chờ admin xét duyệt"` |
| `descriptionText` | `String` | Status description | `"AI đã hoàn thành phân tích..."` |

---

## 🎨 **Status Mapping**

| `finalStatus` | `statusDisplay` | `descriptionText` | Color |
|---------------|-----------------|-------------------|-------|
| `PENDING_AI` | "Đang chờ AI chấm điểm" | "Hồ sơ đang được hệ thống AI phân tích" | 🟡 Orange |
| `PENDING_ADMIN` | "Đang chờ admin xét duyệt" | "AI đã hoàn thành phân tích, chờ quản lý xem xét" | 🟡 Orange |
| `APPROVED` | "Đã duyệt" | "Hồ sơ đã được duyệt, tiền sẽ được cộng vào tài khoản" | 🟢 Green |
| `REJECTED` | "Đã từ chối" | "Hồ sơ không được duyệt" | 🔴 Red |

---

## 🔍 **Fraud Detection Alert**

Nếu phát hiện khai khống thu nhập, `adminNote` sẽ chứa:
```javascript
{
  "adminNote": "⚠️ FRAUD ALERT: Declared income VND 100,000,000 but actual average monthly inflow only VND 20,000,000 (80% mismatch). Requires manual verification."
}
```

**FE Action:** Hiển thị cảnh báo màu đỏ cho admin review.

---

## 📱 **Frontend Integration Guide**

### **1. API Call Function:**
```javascript
async function fetchLoanHistory(page = 0, size = 10) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(
    `http://localhost:8080/api/v1/loans/my-loans?page=${page}&size=${size}&sort=createdAt,desc`,
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );

  if (!response.ok) {
    throw new Error('Failed to fetch loan history');
  }

  return await response.json();
}
```

### **2. Display Logic:**
```javascript
function renderLoanHistory(data) {
  const loans = data.content;

  loans.forEach(loan => {
    const statusColor = getStatusColor(loan.finalStatus);
    const fraudAlert = loan.adminNote?.includes('FRAUD ALERT');

    // Render loan card with status color and fraud warning
  });
}

function getStatusColor(status) {
  const colors = {
    'PENDING_AI': '#ffa500',    // Orange
    'PENDING_ADMIN': '#ffa500', // Orange
    'APPROVED': '#28a745',      // Green
    'REJECTED': '#dc3545'       // Red
  };
  return colors[status] || '#6c757d';
}
```

### **3. Pagination:**
```javascript
// Handle pagination
const { totalPages, number: currentPage } = data;

// Show pagination controls
for (let i = 0; i < totalPages; i++) {
  // Create page buttons
}
```

---

## 🧪 **Test với Postman**

### **Request:**
```
GET http://localhost:8080/api/v1/loans/my-loans?page=0&size=10&sort=createdAt,desc
Authorization: Bearer YOUR_JWT_TOKEN
```

### **Expected Response:**
```json
{
  "content": [...],
  "totalElements": 2,
  "totalPages": 1,
  "pageable": {...},
  "last": true,
  "first": true
}
```

---

## ⚠️ **Error Handling**

### **401 Unauthorized:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or expired"
}
// FE Action: Redirect to login
```

### **404 Not Found:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 123"
}
// FE Action: Show error, redirect to login
```

---

## 🔗 **Related APIs**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `POST` | `/api/v1/loans/apply` | Nộp đơn vay mới |
| `GET` | `/api/v1/loans/{id}` | Chi tiết đơn vay cụ thể |
| `GET` | `/api/v1/admin/loans` | Admin: Danh sách chờ duyệt |

---

## 📋 **Implementation Details**

### **Controller:**
```java
@io.swagger.v3.oas.annotations.Operation(summary = "Get my loans", description = "Retrieve logged-in user's loan requests (paginated)")
@GetMapping("/my-loans")
public ResponseEntity<Page<LoanRequestResponseDTO>> getMyLoans(Pageable pageable) {
    Integer userId = loanService.getAuthenticatedUserId();
    Page<LoanRequestResponseDTO> page = loanService.getMyLoans(userId, pageable);
    return ResponseEntity.ok(page);
}
```

### **Service Method:**
```java
public Page<LoanRequestResponseDTO> getMyLoans(Integer userId, Pageable pageable) {
    Page<LoanRequest> loans = loanRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    return loans.map(this::convertToResponseDTO);
}
```

### **Repository:**
```java
Page<LoanRequest> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
```

---

## 🎯 **Key Features**

- ✅ **Pagination Support**: Spring Data Pageable
- ✅ **Sorting**: Default by `createdAt DESC`
- ✅ **Security**: JWT Authentication required
- ✅ **Fraud Detection**: Income verification alerts
- ✅ **Status Tracking**: Real-time loan status updates
- ✅ **User-friendly Display**: Vietnamese status messages
- ✅ **Admin Notes**: Fraud alerts and review comments

---

**Version:** 1.0
**Last Updated:** April 3, 2026
**Status:** ✅ Production Ready