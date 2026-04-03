# 📱 Loan API - Quick Reference Card

## 🔗 Endpoints Summary

| Method | Endpoint | Description | Auth | Status |
|--------|----------|-------------|------|--------|
| **POST** | `/api/v1/loans/apply` | Nộp đơn vay | ✅ Required | 200 / 400 / 401 / 500 |
| **GET** | `/api/v1/loans/my-loans` | Danh sách đơn vay (phân trang) | ✅ Required | 200 / 401 |
| **GET** | `/api/v1/loans/{id}` | Chi tiết đơn vay cụ thể | ✅ Required | 200 / 401 / 404 |
| **GET** | `/api/v1/admin/loans` | Danh sách chờ duyệt (Admin) | ✅ Required | 200 / 401 |
| **GET** | `/api/v1/admin/loans/{id}` | Chi tiết (Admin) | ✅ Required | 200 / 401 / 404 |
| **POST** | `/api/v1/admin/loans/{id}/approve` | Duyệt đơn vay (Admin) | ✅ Required | 200 / 401 / 404 |
| **POST** | `/api/v1/admin/loans/{id}/reject` | Từ chối đơn vay (Admin) | ✅ Required | 200 / 401 / 404 |

---

## 🎯 Main Endpoint: POST /api/v1/loans/apply

### 📥 Request
```javascript
{
  "amount": 50000000,           // Min: 1,000,000 | Max: 1,000,000,000
  "term": 12,                   // Min: 3 | Max: 60 (months)
  "purpose": "Mua ô tô",        // Min: 5 | Max: 255 chars
  "declaredIncome": 20000000,   // >= 0
  "jobSegmentNum": "BUSINESS_OWNER"
}
```

### 📤 Response (200 OK)

#### ✅ Case 1: AI PASSED (finalStatus = PENDING_ADMIN)
```javascript
{
  "id": 1,
  "amount": 50000000,
  "term": 12,
  "purpose": "Mua ô tô",
  "declaredIncome": 20000000,
  "jobSegmentNum": "BUSINESS_OWNER",
  "aiScore": 0.82,
  "aiDecision": "PASSED_AI",        // ← AI chấp nhận
  "adminNote": null,
  "finalStatus": "PENDING_ADMIN",   // ← Chờ admin duyệt
  "createdAt": "2026-04-03T10:15:30",
  "updatedAt": "2026-04-03T10:15:35",
  "statusDisplay": "Chờ phê duyệt",
  "descriptionText": "Hồ sơ của bạn đang chờ Admin phê duyệt!"
}

// FE Action: Show ✅ Green Popup → Redirect to /loan-history
```

#### ❌ Case 2: AI REJECTED (finalStatus = REJECTED)
```javascript
{
  "id": 2,
  "amount": 500000000,
  "term": 24,
  "purpose": "Đầu tư bất động sản",
  "declaredIncome": 5000000,
  "jobSegmentNum": "SALARIED",
  "aiScore": 0.15,
  "aiDecision": "REJECTED_BY_AI",    // ← AI từ chối
  "adminNote": "Rejected by AI scoring: high default risk",
  "finalStatus": "REJECTED",         // ← Bị từ chối
  "createdAt": "2026-04-03T10:20:00",
  "updatedAt": "2026-04-03T10:20:05",
  "statusDisplay": "Đơn vay bị từ chối",
  "descriptionText": "Rất tiếc hồ sơ chưa đạt chuẩn..."
}

// FE Action: Show ❌ Red Popup → Redirect to /loan-history
```

### ❌ Error Responses

#### 400 Bad Request - Validation Error
```javascript
{
  "status": 400,
  "message": "Validation failed",
  "details": [
    { "field": "amount", "message": "Minimum loan amount is 1,000,000 VND" },
    { "field": "purpose", "message": "Purpose must be between 5 and 255 characters" }
  ]
}

// FE Action: Display field errors on form
```

#### 401 Unauthorized - Missing/Expired JWT
```javascript
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or expired"
}

// FE Action: Clear token → Redirect to /login
```

#### 404 Not Found - User not found
```javascript
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 123"
}

// FE Action: Show error popup → Redirect to /login
```

#### 500 Internal Server Error
```javascript
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "AI scoring failed or database error"
}

// FE Action: Show error popup → Let user retry
```

---

## 🎨 UI Behavior

### Form Submission Flow:
```
User fills form
    ↓
FE validates (client-side)
    ├─ If invalid → Show field errors
    └─ If valid → Continue
    ↓
Show loading spinner (disable button)
    ↓
POST /api/v1/loans/apply
    ↓
    ├── 200 OK (AI PASSED) 
    │   └─ Show ✅ Green Popup: "Hồ sơ chờ Admin duyệt"
    │   └─ Redirect to /loan-history
    │
    ├── 200 OK (AI REJECTED)
    │   └─ Show ❌ Red Popup: "Hồ sơ chưa đạt"
    │   └─ Redirect to /loan-history
    │
    ├── 400 Bad Request
    │   └─ Show field errors on form
    │   └─ User can retry
    │
    ├── 401 Unauthorized
    │   └─ Clear JWT token
    │   └─ Show warning popup
    │   └─ Redirect to /login
    │
    └── Other errors
        └─ Show error popup
        └─ Let user retry
```

---

## 🏦 Loan Status Values

| Status | Display | Color | Meaning |
|--------|---------|-------|---------|
| `PENDING_AI` | ⏳ Đang xử lý | Orange | AI đang chấm điểm |
| `PENDING_ADMIN` | ⏳ Chờ duyệt | Orange | Chờ admin review |
| `APPROVED` | ✅ Đã duyệt | Green | Tiền sẽ chuyển vào ví |
| `REJECTED` | ❌ Bị từ chối | Red | AI hoặc Admin từ chối |

---

## 🤖 AI Score Interpretation

| Score Range | Level | Meaning |
|-------------|-------|---------|
| 0.9 - 1.0 | ⭐⭐⭐ Rất tốt | Xác suất trả nợ cao |
| 0.7 - 0.89 | ⭐⭐ Tốt | Xác suất trả nợ bình thường |
| 0.5 - 0.69 | ⭐ Bình thường | Rủi ro trung bình |
| 0.3 - 0.49 | ⚠️ Rủi ro cao | Khó khăn trong trả nợ |
| 0.0 - 0.29 | ❌ Rất rủi ro | AI từ chối (threshold) |

**Note:** AI quyết định từ chối nếu `aiScore < 0.5` hoặc `aiDecision = "REJECTED_BY_AI"`

---

## 📋 Request Headers

```javascript
Headers: {
  "Authorization": "Bearer YOUR_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

**Important:** JWT token phải được lưu từ login API. Nếu token hết hạn, user phải đăng nhập lại.

---

## 🔍 Job Segment Values

```
"BUSINESS_OWNER"      // Chủ doanh nghiệp
"SALARIED"            // Nhân viên lương
"SELF_EMPLOYED"       // Tự kinh doanh
"FREELANCER"          // Freelancer
"RETIRED"             // Người hưu trí
"STUDENT"             // Sinh viên
"UNEMPLOYED"          // Chưa có việc làm
```

---

## ✅ Checklist for FE Integration

- [ ] Implement client-side validation for form fields
- [ ] Show loading spinner while waiting for API response
- [ ] Handle success response (finalStatus = PENDING_ADMIN or REJECTED)
- [ ] Handle error responses (400, 401, 404, 500)
- [ ] Display appropriate popups (green for success, red for error)
- [ ] Implement JWT token management (store/retrieve from localStorage)
- [ ] Add Authorization header to all requests
- [ ] Implement automatic redirect to login on 401
- [ ] Test with Postman/Swagger before final integration
- [ ] Display loan history with proper status colors
- [ ] Implement AI score interpretation display

---

## 🧪 Test with curl

```bash
# 1. Get JWT token by logging in (use your login endpoint)
# Store token in: TOKEN=your_jwt_token

# 2. Apply for loan
curl -X POST http://localhost:8080/api/v1/loans/apply \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000000,
    "term": 12,
    "purpose": "Mua xe ô tô",
    "declaredIncome": 20000000,
    "jobSegmentNum": "BUSINESS_OWNER"
  }'

# 3. View my loans
curl -X GET "http://localhost:8080/api/v1/loans/my-loans?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# 4. View loan details
curl -X GET http://localhost:8080/api/v1/loans/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 Popup Messages Template

### Success Popup (Green) - AI PASSED
```
Title: ✅ Nộp đơn vay thành công!
Message: Hồ sơ của bạn đang chờ Admin phê duyệt. 
         Bạn sẽ nhận được thông báo khi có kết quả.
Button: [OK] → Redirect to /loan-history
Duration: 3-5 seconds auto-close
```

### Error Popup (Red) - AI REJECTED
```
Title: ❌ Rất tiếc hồ sơ chưa đạt chuẩn
Message: Đơn vay của bạn không đạt tiêu chuẩn duyệt.
         Vui lòng cải thiện hồ sơ hoặc liên hệ Admin.
Button: [OK] → Redirect to /loan-history
Duration: 3-5 seconds auto-close
```

### Error Popup (Red) - Network/Server Error
```
Title: ❌ Lỗi server
Message: Có lỗi xảy ra. Vui lòng thử lại sau.
Button: [Thử lại] [Đóng]
Duration: Manual close
```

### Warning Popup (Orange) - Session Expired
```
Title: ⚠️ Phiên đăng nhập hết hạn
Message: Vui lòng đăng nhập lại để tiếp tục.
Button: [OK] → Redirect to /login
Duration: 2 seconds auto-close
```

---

## 📞 Troubleshooting

| Problem | Solution |
|---------|----------|
| "JWT token is missing or expired" (401) | Check token in localStorage, re-login if expired |
| "User not found" (404) | Check user ID is correct, re-login |
| "Validation failed" (400) | Check all form fields meet requirements |
| "AI scoring failed" (500) | Server error, contact admin/support |
| CORS error in browser | Check server CORS config, ensure API URL matches |
| No response from server | Check API server is running on localhost:8080 |

---

## 🔗 Related Resources

- Full API Documentation: [LOAN_API_DOCUMENTATION.md](./LOAN_API_DOCUMENTATION.md)
- FE Integration Guide: [FE_INTEGRATION_GUIDE.md](./FE_INTEGRATION_GUIDE.md)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

---

**Version:** 1.0  
**Last Updated:** April 3, 2026  
**Status:** ✅ Production Ready
