# 📋 **Admin Loan Management API Documentation**

## 🔗 **Base URL:** `/api/v1/admin/loans`

**Full Base URL:** `http://localhost:8080/api/v1/admin/loans`

---

## 📋 **API Endpoints Overview**

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/admin/loans` | Danh sách khoản vay chờ duyệt (paginated + search/filter) | ✅ Active |
| `GET` | `/api/v1/admin/loans/all` | Danh sách tất cả khoản vay (paginated + search/filter) | ✅ Active (Mới) |
| `GET` | `/api/v1/admin/loans/{id}` | Chi tiết khoản vay + thông tin user + AI features | ✅ Active |
| `GET` | `/api/v1/admin/loans/stats` | Thống kê tổng hợp dashboard | ✅ Active |
| `POST` | `/api/v1/admin/loans/{id}/approve` | Duyệt khoản vay + chuyển tiền | ✅ Active |
| `POST` | `/api/v1/admin/loans/{id}/reject` | Từ chối khoản vay | ✅ Active |

---

## 🔐 **Authentication & Authorization**

**Required:** Admin role JWT token
```javascript
{
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

**Note:** Chỉ admin mới có quyền truy cập các API này.

---

## 📥 **1. GET `/api/v1/admin/loans` - List Pending Loans (Cải Tiến)**

### **Description:**
Lấy danh sách tất cả khoản vay đang chờ admin duyệt (status = PENDING_ADMIN) với thông tin user, hỗ trợ tìm kiếm và lọc.

### **Request:**
```javascript
Method: GET
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
Query Params: {
  "page": 0,        // Page number (0-based)
  "size": 10,       // Items per page
  "sort": "createdAt,asc",  // Sort by creation time ascending
  "keyword": "Nguyen",     // Search by user fullName or phone (optional)
  "minAiScore": 0.2,       // Filter min AI score (optional)
  "maxAiScore": 0.8        // Filter max AI score (optional)
}
```

### **Response (200 OK):**
```javascript
{
  "content": [
    {
      "loanId": 12,
      "amount": 50000000,
      "aiScore": 0.3,
      "finalStatus": "PENDING_ADMIN",
      "userId": 5,
      "fullName": "Nguyễn Văn A",
      "createdAt": "2026-04-03T16:25:56"
    },
    {
      "loanId": 13,
      "amount": 30000000,
      "aiScore": 0.7,
      "finalStatus": "PENDING_ADMIN",
      "userId": 8,
      "fullName": "Trần Thị B",
      "createdAt": "2026-04-03T17:10:22"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 5,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 5,
  "empty": false
}
```

---

## 📥 **2. GET `/api/v1/admin/loans/{id}` - Loan Details**

### **Description:**
Lấy thông tin chi tiết của một khoản vay bao gồm:
- Thông tin khoản vay
- Thông tin user (KYC, wallet)
- AI features và scoring
- Fraud detection alerts

### **Request:**
```javascript
Method: GET
URL: /api/v1/admin/loans/12
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

### **Response (200 OK):**
```javascript
{
  // Loan Request Info
  "loanId": 12,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua xe ô tô",
  "declaredIncome": 100000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.3,
  "aiDecision": "PASSED_AI",
  "finalStatus": "PENDING_ADMIN",
  "adminNote": "⚠️ FRAUD ALERT: Declared income VND 100,000,000 but actual average monthly inflow only VND 20,000,000 (80% mismatch). Requires manual verification.",
  "loanCreatedAt": "2026-04-03T16:25:56",

  // User Info
  "userId": 5,
  "userName": "nguyenvana",
  "email": "nguyenvana@example.com",
  "phone": "0987654321",
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "1990-01-15",
  "address": "123 Đường ABC, Quận 1, TP.HCM",
  "kycLevel": 2,
  "jobSegment": "BUSINESS_OWNER",

  // Wallet Info
  "walletId": 5,
  "walletBalance": 1500000.00,
  "availableBalance": 1500000.00,

  // AI Features (for admin review)
  "age": 36.0,
  "accountAgeDays": 365.0,
  "avgBalance": 2000000.0,
  "balanceVolatility": 500000.0,
  "lowBalanceDaysRatio": 0.1,
  "monthlyInflowMean": 15000000.0,
  "monthlyOutflowMean": 12000000.0,
  "largestInflow": 50000000.0,
  "transactionCount": 45.0,
  "rejectedTransactionRatio": 0.02,
  "peerTransferRatio": 0.8,
  "uniqueReceivers": 12.0,

  // Computed
  "spendIncomeRatio": 0.8
}
```

---

## 📥 **3. POST `/api/v1/admin/loans/{id}/approve` - Approve Loan**

### **Description:**
Admin duyệt khoản vay và tự động chuyển tiền vào ví user

### **Request:**
```javascript
Method: POST
URL: /api/v1/admin/loans/12/approve
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
Body: {
  "adminNote": "Duyệt khoản vay - Hồ sơ hợp lệ"  // Optional
}
```

### **Response (200 OK):**
```javascript
{
  "id": 12,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua xe ô tô",
  "declaredIncome": 100000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.3,
  "aiDecision": "PASSED_AI",
  "adminNote": "Duyệt khoản vay - Hồ sơ hợp lệ",
  "finalStatus": "APPROVED",
  "createdAt": "2026-04-03T16:25:56",
  "updatedAt": "2026-04-03T16:26:00",
  "statusDisplay": "Đã duyệt",
  "descriptionText": "Hồ sơ đã được duyệt, tiền sẽ được cộng vào tài khoản"
}
```

### **Business Logic:**
1. ✅ Kiểm tra loan status = PENDING_ADMIN
2. ✅ Cập nhật status → APPROVED
3. ✅ Thêm tiền vào wallet balance + availableBalance
4. ✅ Tạo transaction record (LOAN_DISBURSEMENT, IN)
5. ✅ Gửi notification cho user

---

## 📥 **4. POST `/api/v1/admin/loans/{id}/reject` - Reject Loan**

### **Description:**
Admin từ chối khoản vay với lý do cụ thể

### **Request:**
```javascript
Method: POST
URL: /api/v1/admin/loans/12/reject
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
Body: {
  "adminNote": "Hồ sơ không đủ điều kiện - Thu nhập không ổn định"  // Required
}
```

### **Response (200 OK):**
```javascript
{
  "id": 12,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua xe ô tô",
  "declaredIncome": 100000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.3,
  "aiDecision": "PASSED_AI",
  "adminNote": "Hồ sơ không đủ điều kiện - Thu nhập không ổn định",
  "finalStatus": "REJECTED",
  "createdAt": "2026-04-03T16:25:56",
  "updatedAt": "2026-04-03T16:26:00",
  "statusDisplay": "Đã từ chối",
  "descriptionText": "Hồ sơ không được duyệt"
}
```

### **Business Logic:**
1. ✅ Kiểm tra loan status = PENDING_ADMIN
2. ✅ Cập nhật status → REJECTED
3. ✅ Lưu admin note
4. ✅ Gửi notification cho user

---

## 📥 **5. GET `/api/v1/admin/loans/all` - List All Loans (Mới)**

### **Description:**
Lấy danh sách tất cả các khoản vay trong hệ thống (không phân biệt trạng thái), hỗ trợ tìm kiếm và lọc nâng cao. 

### **Request:**
```javascript
Method: GET
URL: /api/v1/admin/loans/all
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
Query Params: {
  "page": 0,
  "size": 10,
  "keyword": "Nguyen",
  "minAiScore": 0.0,
  "maxAiScore": 1.0
}
```

### **Response (200 OK):**
Trả về cấu trúc `Page` tương tự như API danh sách chờ duyệt, nhưng chứa tất cả các bản ghi.

---

## 📥 **6. GET `/api/v1/admin/loans/stats` - Dashboard Stats (Mới Thêm)**

### **Description:**
Lấy thống kê tổng hợp về các khoản vay đang chờ duyệt cho admin dashboard.

### **Request:**
```javascript
Method: GET
URL: /api/v1/admin/loans/stats
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

### **Response (200 OK):**
```javascript
{
  "totalPending": 15,
  "averageAmount": 45000000.0,
  "highRiskCount": 3,
  "moderateRiskCount": 7,
  "lowRiskCount": 5
}
```

#### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| **totalPending** | Long | Tổng số đơn vay chờ duyệt |
| **averageAmount** | Double | Số tiền vay trung bình |
| **highRiskCount** | Long | Số đơn high risk (aiScore >= 0.3) |
| **moderateRiskCount** | Long | Số đơn moderate risk (0.1 <= aiScore < 0.3) |
| **lowRiskCount** | Long | Số đơn low risk (< 0.1) |

### **Business Logic:**
- Tính toán từ tất cả loan với status = PENDING_ADMIN
- Risk classification dựa trên aiScore

---
## ⚠️ **Error Handling**

### **400 Bad Request:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Loan is not pending admin review: APPROVED"
}
```

### **401 Unauthorized:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or expired"
}
```

### **403 Forbidden:**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied - Admin role required"
}
```

### **404 Not Found:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Loan not found: 999"
}
```

---

## 🧪 **Test với Postman**

### **1. Get Pending Loans (with search/filter):**
```
GET http://localhost:8080/api/v1/admin/loans?keyword=Nguyen&minAiScore=0.2&maxAiScore=0.8&page=0&size=10&sort=createdAt,asc
Authorization: Bearer ADMIN_JWT_TOKEN
```

### **2. Get Loan Details:**
```
GET http://localhost:8080/api/v1/admin/loans/12
Authorization: Bearer ADMIN_JWT_TOKEN
```

### **3. Get Dashboard Stats:**
```
GET http://localhost:8080/api/v1/admin/loans/stats
Authorization: Bearer ADMIN_JWT_TOKEN
```

### **4. Approve Loan:**
```
POST http://localhost:8080/api/v1/admin/loans/12/approve
Authorization: Bearer ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "adminNote": "Duyệt - Hồ sơ hợp lệ"
}
```

### **5. Reject Loan:**
```
POST http://localhost:8080/api/v1/admin/loans/12/reject
Authorization: Bearer ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "adminNote": "Từ chối - Thu nhập không ổn định"
}
```

---

## 🔍 **Fraud Detection Features**

### **Income Verification:**
- So sánh declared income vs actual wallet inflow
- Flag nếu mismatch > 50%
- Alert hiển thị trong `adminNote`

### **AI Features for Review:**
- 16 AI features được expose cho admin review
- Balance volatility, transaction patterns
- Risk scoring insights

### **KYC Integration:**
- User KYC level (1-3)
- Personal information verification
- Address and contact details

---

## 📋 **Changelog & Updates**

### 📅 **03/04/2026 - Latest Updates**
- ✅ **Cải tiến**: API `/loans` thêm search/filter (keyword, minAiScore, maxAiScore) và trả về DTO với user info.
- ✅ **Thêm mới**: API `/loans/stats` cho dashboard stats.
- ✅ **Fix kỹ thuật**: Sửa kiểu dữ liệu DTO để compile thành công.
- ✅ **Tăng cường**: Fraud detection với income verification.

---

---

## 📊 **Admin Dashboard Integration**

### **Pending Loans Count:**
```javascript
// Get total pending loans
const response = await fetch('/api/v1/admin/loans?page=0&size=1');
const totalPending = response.data.totalElements;
```

### **Loan Approval Workflow:**
```javascript
async function approveLoan(loanId, adminNote) {
  const response = await fetch(`/api/v1/admin/loans/${loanId}/approve`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${adminToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ adminNote })
  });

  if (response.ok) {
    // Update UI - remove from pending list
    // Show success notification
  }
}
```

---

## 🔒 **Security Features**

- ✅ **Role-based Access**: Chỉ admin
- ✅ **Audit Trail**: Tất cả actions được log
- ✅ **Transaction Atomicity**: Approve = money transfer + status update
- ✅ **Input Validation**: Required admin notes
- ✅ **Status Validation**: Chỉ approve/reject PENDING_ADMIN loans

---

## 📋 **Implementation Details**

### **Controller:**
```java
@RestController
@RequestMapping("/api/v1/admin/loans")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin Loan APIs", description = "Admin loan management endpoints")
public class AdminLoanController {
    // Implementation as shown above
}
```

### **Service Methods:**
```java
// Approve with money transfer
@Transactional
public LoanRequestResponseDTO approveLoan(Long loanId, String adminNote)

// Reject with admin note
@Transactional
public LoanRequestResponseDTO rejectLoan(Long loanId, String adminNote)

// Get detailed info for review
public AdminLoanDetailDTO getAdminLoanDetail(Long loanId)
```

---

## 🎯 **Key Features**

- ✅ **Complete Loan Lifecycle Management**
- ✅ **Fraud Detection & Alerts**
- ✅ **Automatic Money Transfer on Approval**
- ✅ **Comprehensive User & AI Data**
- ✅ **Audit Trail & Admin Notes**
- ✅ **Real-time Status Updates**
- ✅ **Pagination Support**
- ✅ **Role-based Security**

---

**Version:** 1.0
**Last Updated:** April 3, 2026
**Status:** ✅ Production Ready