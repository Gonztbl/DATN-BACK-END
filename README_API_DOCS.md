# 📚 Loan API Documentation Index

## 🎯 Choose Your Documentation

### 👨‍💼 If you're a **Frontend Developer** starting integration:
1. **Start here:** [API_QUICK_REFERENCE.md](./API_QUICK_REFERENCE.md) - 5 min read
   - Quick endpoints table
   - Request/response examples
   - Popup templates

2. **Then read:** [FE_INTEGRATION_GUIDE.md](./FE_INTEGRATION_GUIDE.md) - 20 min read
   - Validation logic
   - Complete code examples (React)
   - Error handling
   - Sample components

3. **If needed:** [LOAN_API_DOCUMENTATION.md](./LOAN_API_DOCUMENTATION.md) - Full reference
   - Detailed API specs
   - All endpoints
   - Error scenarios

### 🔧 If you're a **Backend Developer** or **DevOps**:
- [LOAN_API_DOCUMENTATION.md](./LOAN_API_DOCUMENTATION.md) - Complete technical specs

### 🧪 If you want to **test manually**:
- Use **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- Use **Postman** collections (coming soon)
- See curl examples in [API_QUICK_REFERENCE.md](./API_QUICK_REFERENCE.md)

---

## 📄 Documents Overview

### 1. 📋 [API_QUICK_REFERENCE.md](./API_QUICK_REFERENCE.md)
**Best for:** Quick lookup, API endpoints table, request/response examples  
**Length:** ~5 minutes  
**Contains:**
- ✅ Endpoints summary table
- ✅ Main endpoint (POST /api/v1/loans/apply) with examples
- ✅ Error response samples
- ✅ UI behavior flow diagram
- ✅ Status values reference
- ✅ curl command examples
- ✅ Popup message templates
- ✅ Troubleshooting guide

---

### 2. 🎯 [FE_INTEGRATION_GUIDE.md](./FE_INTEGRATION_GUIDE.md)
**Best for:** Implementing the API in React/JavaScript  
**Length:** ~20 minutes  
**Contains:**
- ✅ Form validation logic
- ✅ API call examples (Axios)
- ✅ Success response handling
- ✅ Error handling (400, 401, 404, 500)
- ✅ Popup/notification implementations
- ✅ Loading spinner component
- ✅ Loan history display logic
- ✅ Request/response interceptors
- ✅ Complete React component example
- ✅ Testing tips

---

### 3. 📖 [LOAN_API_DOCUMENTATION.md](./LOAN_API_DOCUMENTATION.md)
**Best for:** Comprehensive technical reference  
**Length:** ~30 minutes  
**Contains:**
- ✅ Complete system overview
- ✅ All 7 API endpoints documented
- ✅ Request/response specifications
- ✅ Status codes & error handling
- ✅ Loan status enum values
- ✅ AI decision values
- ✅ AI score interpretation
- ✅ Frontend integration flow diagram
- ✅ curl command examples
- ✅ Swagger/OpenAPI info
- ✅ Production-ready notes

---

## 🚀 Quick Start (5 minutes)

### For FE Developers:

1. **Read** [API_QUICK_REFERENCE.md](./API_QUICK_REFERENCE.md) endpoints table
2. **Copy** main endpoint example:
   ```javascript
   POST /api/v1/loans/apply
   ```
3. **Handle** two response scenarios:
   - ✅ `finalStatus: "PENDING_ADMIN"` → Show green popup
   - ❌ `finalStatus: "REJECTED"` → Show red popup
4. **Test** with Swagger: `http://localhost:8080/swagger-ui.html`
5. **Read** [FE_INTEGRATION_GUIDE.md](./FE_INTEGRATION_GUIDE.md) for code examples

---

## 🔄 Main API Flow

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend Flow                            │
└─────────────────────────────────────────────────────────────┘

1. User fills Loan Form
   ├─ Amount: 50,000,000 VND
   ├─ Term: 12 months
   ├─ Purpose: "Mua ô tô để kinh doanh"
   ├─ Income: 20,000,000 VND
   └─ Job Segment: "BUSINESS_OWNER"
   
2. FE validates (client-side)
   ├─ Amount: 1M - 1B range ✓
   ├─ Term: 3 - 60 months ✓
   ├─ Purpose: 5-255 chars ✓
   ├─ Income: >= 0 ✓
   └─ Job: provided ✓
   
3. Show Loading Spinner
   
4. POST /api/v1/loans/apply
   Header: Authorization: Bearer JWT_TOKEN
   
        ┌─────────────────────────────────────┐
        │     Backend Processing              │
        ├─────────────────────────────────────┤
        │ 1. Save loan with PENDING_AI status │
        │ 2. Extract 16 features from wallet  │
        │ 3. Run ONNX AI model                │
        │ 4. Get AI score & decision          │
        │ 5. Update loan status based on AI   │
        │    - PASSED_AI → PENDING_ADMIN      │
        │    - REJECTED_BY_AI → REJECTED      │
        │ 6. Return response                  │
        └─────────────────────────────────────┘
   
5. Handle Response
   
   ┌─────────────────┐        ┌──────────────────┐
   │ AI PASSED       │        │ AI REJECTED      │
   │ (score >= 0.5)  │        │ (score < 0.5)    │
   │                 │        │                  │
   │ finalStatus:    │        │ finalStatus:     │
   │ PENDING_ADMIN   │        │ REJECTED         │
   └────────┬────────┘        └────────┬─────────┘
            │                         │
            ▼                         ▼
   ┌──────────────────────┐  ┌──────────────────────┐
   │ ✅ Green Popup:      │  │ ❌ Red Popup:        │
   │ "Hồ sơ chờ duyệt"   │  │ "Hồ sơ chưa đạt"     │
   │                      │  │                      │
   │ + 2sec auto-close   │  │ + 3sec auto-close    │
   └────────┬─────────────┘  └─────────┬────────────┘
            │                         │
            ▼                         ▼
   ┌──────────────────────┐  ┌──────────────────────┐
   │ Redirect to          │  │ Redirect to          │
   │ /loan-history        │  │ /loan-history        │
   │                      │  │                      │
   │ User sees:           │  │ User sees:           │
   │ - Status: Chờ duyệt  │  │ - Status: Bị từ chối │
   │ - AI Score: 0.82     │  │ - AI Score: 0.15     │
   │ - Waiting for admin  │  │ - Reason: high risk  │
   └──────────────────────┘  └──────────────────────┘
   
6. Future: Admin Approves/Rejects
   - Admin sees pending loans via GET /api/v1/admin/loans
   - Admin duyệt: POST /api/v1/admin/loans/1/approve
     → Money transferred to user wallet
   - Admin từ chối: POST /api/v1/admin/loans/1/reject
     → Status updated to REJECTED
```

---

## 📊 Endpoints Cheat Sheet

```javascript
// User APIs
POST   /api/v1/loans/apply              // Nộp đơn vay ★ MAIN
GET    /api/v1/loans/my-loans           // Danh sách của user
GET    /api/v1/loans/{id}               // Chi tiết 1 đơn vay

// Admin APIs
GET    /api/v1/admin/loans              // Danh sách chờ review
GET    /api/v1/admin/loans/{id}         // Chi tiết (full info)
POST   /api/v1/admin/loans/{id}/approve // Duyệt khoản vay
POST   /api/v1/admin/loans/{id}/reject  // Từ chối khoản vay
```

---

## 📝 Response Status Codes

| Code | Meaning | When | Action |
|------|---------|------|--------|
| **200** | OK - Processing successful | AI scored, saved result | Parse response |
| **400** | Validation error | Bad input data | Show field errors |
| **401** | Unauthorized | Missing/expired JWT | Redirect to login |
| **404** | Not found | User doesn't exist | Show error popup |
| **500** | Server error | AI/DB error | Show error, retry later |

---

## 🧩 FE Integration Checklist

### Phase 1: Setup
- [ ] Create `.env` file with API base URL
- [ ] Setup Axios instance with JWT interceptors
- [ ] Create API service file for loan endpoints

### Phase 2: Form Page
- [ ] Create form component with 5 fields
- [ ] Implement client-side validation
- [ ] Show loading spinner on submit
- [ ] Disable button while loading

### Phase 3: Response Handling
- [ ] Show green popup for `finalStatus = PENDING_ADMIN`
- [ ] Show red popup for `finalStatus = REJECTED`
- [ ] Auto-redirect to history page after popup
- [ ] Handle all error cases (400, 401, 404, 500)

### Phase 4: History Page
- [ ] Display loan list with status colors
- [ ] Show AI score with interpretation
- [ ] Map status to display text (PENDING_ADMIN → "Chờ duyệt")
- [ ] Implement pagination

### Phase 5: Testing
- [ ] Test with valid data
- [ ] Test with invalid data (empty, min/max)
- [ ] Test with expired JWT
- [ ] Test error handling

---

## 🎨 Status Color Reference

```css
/* Use these colors for loan status */

.status-pending-ai {
  color: #FFA500;  /* Orange */
  label: "⏳ Đang xử lý"
}

.status-pending-admin {
  color: #FFA500;  /* Orange */
  label: "⏳ Chờ duyệt"
}

.status-approved {
  color: #28A745;  /* Green */
  label: "✅ Đã duyệt"
}

.status-rejected {
  color: #DC3545;  /* Red */
  label: "❌ Bị từ chối"
}
```

---

## 🔐 Security Notes

⚠️ **Important:**
1. **JWT Token:** Always send in `Authorization: Bearer TOKEN` header
2. **HTTPS:** Use HTTPS in production (not HTTP)
3. **Token Storage:** Store JWT only in localStorage (NOT in cookies to avoid CSRF)
4. **Token Expiry:** Handle 401 responses by redirecting to login
5. **Validation:** Always validate on both FE and BE
6. **CORS:** Ensure CORS is properly configured on server

---

## 💡 Pro Tips

✅ **Do:**
- Validate on frontend first (faster UX)
- Show loading spinner while API is processing
- Auto-retry on network failures
- Log errors for debugging
- Test with Swagger before FE integration
- Store JWT securely

❌ **Don't:**
- Trust only client-side validation (always server-side too)
- Leave requests hanging without timeout
- Hardcode API URLs (use .env instead)
- Expose sensitive data in logs
- Forget to handle 401 responses

---

## 🆘 Troubleshooting

### "401 Unauthorized"
```
Cause: JWT token missing or expired
Fix: 
1. Check localStorage for 'jwtToken'
2. Re-login if token expired
3. Add Authorization header to request
```

### "400 Validation Error"
```
Cause: Input data doesn't match requirements
Fix:
1. Check field values in browser console
2. Verify amounts in correct range
3. Check string lengths
```

### "Cannot POST /api/v1/loans/apply"
```
Cause: API server not running or wrong URL
Fix: 
1. Start backend: .\mvnw.cmd spring-boot:run
2. Check API URL matches localhost:8080
3. Try Swagger: http://localhost:8080/swagger-ui.html
```

### CORS Error in Browser
```
Cause: API doesn't allow cross-origin requests
Fix: Backend must have CORS configured
```

---

## 📞 Contact & Support

- **Backend Issue?** → Check server logs
- **API Down?** → Restart server
- **JWT Issue?** → Re-login
- **Data Invalid?** → Check validation rules

---

## 📅 API Status

- **Version:** 1.0
- **Last Updated:** April 3, 2026
- **Status:** ✅ Production Ready
- **Production Date:** Ready to deploy
- **Testing:** Passed all unit tests
- **Performance:** Optimized for sub-second AI scoring

---

## 🎓 Learning Resources

1. **JWT Tokens:** https://jwt.io/
2. **Axios Documentation:** https://axios-http.com/
3. **OpenAPI/Swagger:** https://swagger.io/
4. **REST API Best Practices:** https://restfulapi.net/

---

## 📥 Files in This Documentation

```
├── LOAN_API_DOCUMENTATION.md      (30 min - Complete reference)
├── FE_INTEGRATION_GUIDE.md         (20 min - Implementation guide)
├── API_QUICK_REFERENCE.md          (5 min - Quick lookup)
└── README_API_DOCS.md              (This file - Navigation)
```

---

**Start reading now! Choose based on your role above.** ⬆️
