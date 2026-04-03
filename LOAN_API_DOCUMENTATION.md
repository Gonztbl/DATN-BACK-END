# 🏦 Loan Management API Documentation

## 📋 API Tổng Quan

Hệ thống cho phép người dùng nộp đơn xin vay với **AI Credit Scoring**. API xử lý toàn bộ quy trình: validasi dữ liệu, trích xuất features, chấm điểm AI, và quản lý trạng thái.

---

## 1️⃣ **POST /api/v1/loans/apply** - Nộp Đơn Vay

### 📌 Mô Tả
Khách hàng nộp đơn xin vay. API sẽ:
1. Lưu yêu cầu vay với trạng thái `PENDING_AI`
2. Trích xuất 16 features từ lịch sử ví (90 ngày)
3. Chạy AI scoring model (ONNX)
4. Cập nhật trạng thái dựa trên quyết định AI:
   - ✅ **PASSED_AI** → trạng thái chuyển thành `PENDING_ADMIN` (chờ admin duyệt)
   - ❌ **REJECTED_BY_AI** → trạng thái chuyển thành `REJECTED` (AI từ chối)

### 📡 Request
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/v1/loans/apply`  
**Content-Type:** `application/json`  
**Authentication:** Yêu cầu JWT token (user phải đã đăng nhập)

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body (LoanRequestApplyDTO)
```json
{
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô để kinh doanh vận tải",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER"
}
```

#### Validation Rules
| Field | Type | Min | Max | Validation |
|-------|------|-----|-----|-----------|
| **amount** | BigDecimal (VND) | 1,000,000 | 1,000,000,000 | Bắt buộc, > 0 |
| **term** | Integer (tháng) | 3 | 60 | Bắt buộc |
| **purpose** | String | 5 ký tự | 255 ký tự | Bắt buộc, không được trống |
| **declaredIncome** | BigDecimal (VND) | 0 | ∞ | Bắt buộc, ≥ 0 |
| **jobSegmentNum** | String | - | - | Bắt buộc (BUSINESS_OWNER, SALARIED, SELF_EMPLOYED, etc.) |

#### Response Status Code

| Code | Meaning | Description |
|------|---------|-------------|
| **200** | OK | Đơn vay được xử lý thành công (AI đã chấm điểm) |
| **400** | Bad Request | Dữ liệu không hợp lệ (validate fails) |
| **401** | Unauthorized | Không có JWT token / token hết hạn |
| **404** | Not Found | User không tồn tại |
| **500** | Server Error | Lỗi server khi xử lý AI hoặc database |

### 📤 Response - Success (200 OK)

#### Trường Hợp 1: AI PASSED (Đơn vay chờ admin duyệt)
```json
{
  "id": 1,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô để kinh doanh vận tải",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",
  "adminNote": null,
  "finalStatus": "PENDING_ADMIN",
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T10:15:35",
  "statusDisplay": "Chờ phê duyệt từ Admin",
  "descriptionText": "Hồ sơ của bạn đang chờ Admin phê duyệt!"
}
```

**Giải thích:**
- `aiScore`: 0.82 (82% khả năng trả nợ) - xác suất cao
- `aiDecision`: "PASSED_AI" - AI chấp nhận
- `finalStatus`: "PENDING_ADMIN" - đợi admin review
- **FE hành động:** Hiển thị popup ✅ màu xanh + điều hướng sang "Lịch sử đơn vay"

---

#### Trường Hợp 2: AI REJECTED (Đơn vay bị từ chối)
```json
{
  "id": 2,
  "amount": 500000000,
  "term": 24,
  "purpose": "Đầu tư bất động sản",
  "declaredIncome": 5000000,
  "jobSegmentNum": "SALARIED",
  "aiScore": 0.15,
  "aiDecision": "REJECTED_BY_AI",
  "adminNote": "Rejected by AI scoring: high default risk",
  "finalStatus": "REJECTED",
  "createdAt": "2026-04-03T10:20:00",
  "updatedAt": "2026-04-03T10:20:05",
  "statusDisplay": "Đơn vay bị từ chối",
  "descriptionText": "Rất tiếc hồ sơ chưa đạt chuẩn. Vui lòng cải thiện hồ sơ hoặc liên hệ admin."
}
```

**Giải thích:**
- `aiScore`: 0.15 (15% khả năng trả nợ) - xác suất thấp
- `aiDecision`: "REJECTED_BY_AI" - AI từ chối do rủi ro cao
- `finalStatus`: "REJECTED" - từ chối cuối cùng
- **FE hành động:** Hiển thị popup ❌ màu đỏ/cam + điều hướng sang "Lịch sử đơn vay"

---

### 📋 Response - Error (400/401/404/500)

#### Case 1: Validation Error (400)
```json
{
  "timestamp": "2026-04-03T10:25:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    {
      "field": "amount",
      "message": "Loan amount is required"
    },
    {
      "field": "amount",
      "message": "Minimum loan amount is 1,000,000 VND"
    },
    {
      "field": "term",
      "message": "Loan term is required"
    }
  ]
}
```

**FE hành động:**
- Hiển thị lỗi validation
- Không gọi API nếu FE validation đã fail
- Nếu API từ chối, hiển thị lỗi chi tiết cho user

---

#### Case 2: Unauthorized (401)
```json
{
  "timestamp": "2026-04-03T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or expired"
}
```

**FE hành động:**
- Redirect to login page
- Xóa token khỏi localStorage/sessionStorage
- Yêu cầu user đăng nhập lại

---

#### Case 3: User Not Found (404)
```json
{
  "timestamp": "2026-04-03T10:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 123"
}
```

**FE hành động:**
- Hiển thị popup lỗi: "Không tìm thấy thông tin người dùng"

---

#### Case 4: Server Error (500)
```json
{
  "timestamp": "2026-04-03T10:40:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "AI scoring failed or database error"
}
```

**FE hành động:**
- Hiển thị popup lỗi: "Lỗi server, vui lòng thử lại sau"
- Log error ID để support team debug

---

## 2️⃣ **GET /api/v1/loans/my-loans** - Xem Danh Sách Đơn Vay Của User

### 📌 Mô Tả
Lấy danh sách tất cả đơn vay của user hiện tại (phân trang + sắp xếp theo ngày tạo giảm dần).

### 📡 Request
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/v1/loans/my-loans`  
**Authentication:** Yêu cầu JWT token

#### Query Parameters
```
?page=0&size=10&sort=createdAt,desc
```

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| **page** | Integer | 0 | Trang (0-indexed) |
| **size** | Integer | 10 | Số bản ghi trên trang |
| **sort** | String | createdAt,desc | Sắp xếp (createdAt,desc hoặc createdAt,asc) |

### 📤 Response - Success (200 OK)
```json
{
  "content": [
    {
      "id": 1,
      "amount": 50000000,
      "term": 12,
      "purpose": "Mua ô tô",
      "declaredIncome": 20000000,
      "jobSegmentNum": "BUSINESS_OWNER",
      "aiScore": 0.82,
      "aiDecision": "PASSED_AI",
      "adminNote": null,
      "finalStatus": "PENDING_ADMIN",
      "createdAt": "2026-04-03T10:15:30",
      "updatedAt": "2026-04-03T10:15:35",
      "statusDisplay": "Chờ phê duyệt",
      "descriptionText": "Hồ sơ đang chờ Admin phê duyệt"
    },
    {
      "id": 2,
      "amount": 20000000,
      "term": 6,
      "purpose": "Kinh doanh",
      "declaredIncome": 10000000,
      "jobSegmentNum": "SALARIED",
      "aiScore": 0.90,
      "aiDecision": "PASSED_AI",
      "adminNote": "Approved - Funds disbursed",
      "finalStatus": "APPROVED",
      "createdAt": "2026-04-02T14:20:00",
      "updatedAt": "2026-04-03T08:00:00",
      "statusDisplay": "Đã được duyệt",
      "descriptionText": "Khoản vay đã được duyệt và tiền được chuyển vào ví"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10,
  "hasNext": false
}
```

---

## 3️⃣ **GET /api/v1/loans/{id}** - Xem Chi Tiết Đơn Vay

### 📌 Mô Tả
Lấy thông tin chi tiết của một đơn vay (user chỉ có thể xem đơn vay của chính mình).

### 📡 Request
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/v1/loans/1`  
**Authentication:** Yêu cầu JWT token

#### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| **id** | Long | ID của đơn vay |

### 📤 Response - Success (200 OK)
```json
{
  "id": 1,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô để kinh doanh vận tải",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",
  "adminNote": null,
  "finalStatus": "PENDING_ADMIN",
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T10:15:35",
  "statusDisplay": "Chờ phê duyệt",
  "descriptionText": "Hồ sơ của bạn đang chờ Admin phê duyệt!"
}
```

---

## 🔐 **Admin APIs**

### 4️⃣ **GET /api/v1/admin/loans** - Danh Sách Đơn Vay Chờ Duyệt

### 📌 Mô Tả
Lấy danh sách tất cả đơn vay có trạng thái `PENDING_ADMIN`.

### 📡 Request
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/v1/admin/loans`  
**Authentication:** Yêu cầu JWT token + Admin role

#### Query Parameters
```
?page=0&size=20
```

### 📤 Response - Success (200 OK)
```json
{
  "content": [
    {
      "id": 1,
      "amount": 50000000,
      "term": 12,
      "purpose": "Mua ô tô",
      "declaredIncome": 20000000,
      "jobSegmentNum": "BUSINESS_OWNER",
      "aiScore": 0.82,
      "aiDecision": "PASSED_AI",
      "adminNote": null,
      "finalStatus": "PENDING_ADMIN",
      "createdAt": "2026-04-03T10:15:30",
      "updatedAt": "2026-04-03T10:15:35"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20,
  "hasNext": false
}
```

---

### 5️⃣ **GET /api/v1/admin/loans/{id}** - Chi Tiết Đơn Vay (Admin)

### 📌 Mô Tả
Lấy chi tiết đầy đủ của một đơn vay (bao gồm thông tin user và AI scores).

### 📡 Request
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/v1/admin/loans/1`  
**Authentication:** Yêu cầu JWT token + Admin role

### 📤 Response - Success (200 OK)
```json
{
  "loanId": 1,
  "loanAmount": 50000000,
  "loanTerm": 12,
  "loanPurpose": "Mua ô tô",
  "loanStatus": "PENDING_ADMIN",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",
  "userId": 5,
  "username": "john_doe",
  "userEmail": "john@example.com",
  "userFullName": "John Doe",
  "userPhoneNumber": "0901234567",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "currentWalletBalance": 15000000,
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T10:15:35"
}
```

---

### 6️⃣ **POST /api/v1/admin/loans/{id}/approve** - Duyệt Đơn Vay

### 📌 Mô Tả
Admin duyệt đơn vay. Hệ thống sẽ:
1. Cập nhật trạng thái thành `APPROVED`
2. Chuyển tiền vào ví user (balance + availableBalance)
3. Tạo transaction record với type `DEPOSIT`, direction `IN`

### 📡 Request
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/v1/admin/loans/1/approve`  
**Authentication:** Yêu cầu JWT token + Admin role

#### Request Body
```json
{
  "adminNote": "Duyệt khoản vay - Hồ sơ đầy đủ"
}
```

**adminNote** không bắt buộc (default: "Approved by admin")

### 📤 Response - Success (200 OK)
```json
{
  "id": 1,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",
  "adminNote": "Duyệt khoản vay - Hồ sơ đầy đủ",
  "finalStatus": "APPROVED",
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T11:00:00",
  "statusDisplay": "Đã được duyệt",
  "descriptionText": "Khoản vay đã được duyệt. Tiền sẽ được chuyển vào ví của bạn."
}
```

**Post-approval side effects:**
- User's wallet balance += 50,000,000 VND
- Transaction record được tạo với ID referenceId `LOAN_1`
- User có thể thấy tiền trong ví ngay

---

### 7️⃣ **POST /api/v1/admin/loans/{id}/reject** - Từ Chối Đơn Vay

### 📌 Mô Tả
Admin từ chối đơn vay. Trạng thái chuyển thành `REJECTED`.

### 📡 Request
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/v1/admin/loans/1/reject`  
**Authentication:** Yêu cầu JWT token + Admin role

#### Request Body
```json
{
  "adminNote": "Từ chối - Chứng minh tài chính không rõ ràng"
}
```

**adminNote** không bắt buộc (default: "Rejected by admin")

### 📤 Response - Success (200 OK)
```json
{
  "id": 1,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",
  "adminNote": "Từ chối - Chứng minh tài chính không rõ ràng",
  "finalStatus": "REJECTED",
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T11:05:00",
  "statusDisplay": "Bị từ chối",
  "descriptionText": "Đơn vay của bạn đã bị từ chối. Admin ghi chú: Từ chối - Chứng minh tài chính không rõ ràng"
}
```

---

## 📊 **Loan Status & AI Decision Values**

### Loan Status Enum
```
PENDING_AI      → Đang chờ AI chấm điểm
PENDING_ADMIN   → AI passed, chờ admin duyệt
APPROVED        → Admin đã duyệt, tiền được chuyển
REJECTED        → Bị từ chối (AI hoặc Admin)
```

### AI Decision Values
```
PASSED_AI       → AI chấp nhận (score >= threshold, default 0.5)
REJECTED_BY_AI  → AI từ chối (score < threshold)
```

### AI Score Range
- **0.0 - 1.0**: Xác suất trả nợ đầy đủ
  - 0.9 - 1.0: **Rất tốt** (very safe)
  - 0.7 - 0.9: **Tốt** (safe)
  - 0.5 - 0.7: **Bình thường** (moderate risk)
  - 0.3 - 0.5: **Rủi ro cao** (high risk)
  - 0.0 - 0.3: **Rất rủi ro** (very high risk) → AI từ chối

---

## 🎯 **Frontend Integration Flow**

### 1. User Nhập Form & Submit

```
┌─────────────────────────────────────┐
│  FE Form: Nộp Đơn Vay               │
│  - Số tiền (amount)                 │
│  - Kỳ hạn (term)                    │
│  - Mục đích (purpose)               │
│  - Thu nhập (declaredIncome)        │
│  - Ngành nghề (jobSegmentNum)       │
└──────────────┬──────────────────────┘
               │
               ▼
        ┌─────────────┐
        │ Validate    │
        │ (FE side)   │ ← Kiểm tra empty, min/max
        └──────┬──────┘
               │
               ▼  (Valid)
        ┌──────────────────────────┐
        │ Show Loading Spinner      │
        │ (Disable Submit Button)   │
        └──────────┬───────────────┘
                   │
                   ▼
        ┌──────────────────────────┐
        │ POST /api/v1/loans/apply │
        │ + JWT Token Header       │
        └──────────┬───────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼ (AI PASSED)        ▼ (AI REJECTED)
    ┌──────────────┐    ┌──────────────┐
    │ finalStatus: │    │ finalStatus: │
    │ PENDING_ADMIN│    │ REJECTED     │
    └──────┬───────┘    └───────┬──────┘
           │                    │
           ▼                    ▼
    ┌─────────────────┐  ┌─────────────────┐
    │ Popup ✅ Xanh:  │  │ Popup ❌ Đỏ:    │
    │ "Hồ sơ chờ      │  │ "Rất tiếc hồ    │
    │  Admin duyệt"   │  │  sơ chưa đạt"   │
    └────────┬────────┘  └────────┬────────┘
             │                   │
             ▼                   ▼
        Redirect to          Redirect to
      "Lịch Sử Đơn Vay"    "Lịch Sử Đơn Vay"
```

### 2. FE Error Handling

```
┌─────────────────────┐
│ API Response        │
├─────────────────────┤
│ 200 (Success)       │ → Process response
│ 400 (Validation)    │ → Show field errors
│ 401 (Unauthorized)  │ → Redirect to login
│ 404 (Not Found)     │ → Show "Người dùng không tìm thấy"
│ 500 (Server Error)  │ → Show "Lỗi server, thử lại"
└─────────────────────┘
```

---

## 🔗 **Swagger/OpenAPI Testing**

Truy cập **Swagger UI** để test tất cả API:
```
http://localhost:8080/swagger-ui.html
```

Hoặc lấy OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

---

## 📝 **Sample cURL Commands**

### Apply for Loan
```bash
curl -X POST http://localhost:8080/api/v1/loans/apply \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000000,
    "term": 12,
    "purpose": "Mua ô tô để kinh doanh vận tải",
    "declaredIncome": 20000000,
    "jobSegmentNum": "BUSINESS_OWNER"
  }'
```

### Get My Loans
```bash
curl -X GET "http://localhost:8080/api/v1/loans/my-loans?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Loan by ID
```bash
curl -X GET http://localhost:8080/api/v1/loans/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Admin Approve Loan
```bash
curl -X POST http://localhost:8080/api/v1/admin/loans/1/approve \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "adminNote": "Duyệt khoản vay - Hồ sơ đầy đủ"
  }'
```

---

## 📞 **Support & Questions**

Nếu FE gặp vấn đề:
1. Kiểm tra JWT token có còn hạn không
2. Kiểm tra dữ liệu input có khớp validation rules không
3. Check server logs: `tail -f target/app.log`
4. Test API trực tiếp trên Swagger UI trước

---

**Version:** 1.0  
**Last Updated:** April 3, 2026  
**Status:** ✅ Ready for Production
