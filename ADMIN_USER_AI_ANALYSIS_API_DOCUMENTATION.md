# 📊 **Admin User AI Analysis API Documentation**

## 🔗 **Base URL:** `/api/v1/admin/users`

**Full Base URL:** `http://localhost:8080/api/v1/admin/users`

---

## 📋 **API Endpoints Overview**

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/admin/users/{userId}/ai-analysis` | Phân tích AI + metrics ví cho 1 user | ✅ Active (Mới) |

---

## 🔐 **Authentication & Authorization**

**Required:** Admin role JWT token
```javascript
{
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

**Note:** Chỉ admin mới có quyền truy cập.

---

## 📥 **1. GET `/api/v1/admin/users/{userId}/ai-analysis` - User AI Analysis (Mới)**

### **Description:**
Lấy thông tin phân tích AI và metrics ví của 1 user cho admin review. Bao gồm:
- Thông tin cơ bản user
- Số dư và balance có sẵn
- Dòng tiền vào/ra (thu nhập)
- Các AI features liên quan đến tín dụng

### **Request:**
```javascript
Method: GET
URL: /api/v1/admin/users/5/ai-analysis
Headers: {
  "Authorization": "Bearer ADMIN_JWT_TOKEN",
  "Content-Type": "application/json"
}
```

### **Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| **userId** | Integer | ID của user cần phân tích |

### **Response (200 OK):**
```javascript
{
  // User Basic Info
  "userId": 5,
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phone": "0987654321",
  "userName": "nguyenvana",
  
  // Wallet Info
  "walletBalance": 1500000.0,          // Số dư TB (VNĐ)
  "availableBalance": 1500000.0,       // Số dư khả dụng (VNĐ)
  
  // AI Analysis - Main Metrics
  "monthlyInflowMean": 15000000.0,     // Dòng tiền vào (VNĐ/tháng)
  "monthlyOutflowMean": 12000000.0,    // Dòng tiền ra (VNĐ/tháng)
  "transactionCount": 45.0,             // Số giao dịch (90 ngày)
  "accountAgeDays": 365.0,              // Tuổi tài khoản (ngày)
  "spendIncomeRatio": 80.0,             // Tỷ lệ chi/thu (%)
  "balanceVolatility": 500000.0,        // Độ biến động số dư (VNĐ)
  "rejectedTransactionRatio": 2.0,      // Giao dịch bị từ chối (%)
  
  // Additional AI Features
  "age": 36.0,                          // Tuổi
  "avgBalance": 2000000.0,              // Số dư trung bình (VNĐ)
  "lowBalanceDaysRatio": 10.0,          // % ngày có số dư < 50k
  "largestInflow": 50000000.0,          // Giao dịch vào lớn nhất (VNĐ)
  "peerTransferRatio": 80.0,            // % giao dịch peer-to-peer
  "uniqueReceivers": 12.0               // Số người nhận khác nhau
}
```

### **Response Fields Explanation:**

#### **User Basic Info**
| Field | Type | Description |
|-------|------|-------------|
| **userId** | Integer | ID độc nhất |
| **fullName** | String | Tên đầy đủ |
| **email** | String | Email |
| **phone** | String | Số điện thoại |
| **userName** | String | Tên đăng nhập |

#### **Wallet Info**
| Field | Type | Description |
|-------|------|-------------|
| **walletBalance** | Double | Tổng số dư ví (VNĐ) |
| **availableBalance** | Double | Số dư khả dụng (VNĐ) |

#### **AI Analysis - Key Metrics**
| Field | Type | Description | Range |
|-------|------|-------------|-------|
| **monthlyInflowMean** | Double | Dòng tiền trung bình vào/tháng (VNĐ) | >0 |
| **monthlyOutflowMean** | Double | Dòng tiền trung bình ra/tháng (VNĐ) | >0 |
| **transactionCount** | Double | Tổng số giao dịch trong 90 ngày | >=0 |
| **accountAgeDays** | Double | Số ngày từ khi mở tài khoản | >0 |
| **spendIncomeRatio** | Double | Tỷ lệ chi so với thu nhập (%) | 0-100% |
| **balanceVolatility** | Double | Độ biến động của số dư (VNĐ) | >0 |
| **rejectedTransactionRatio** | Double | % giao dịch bị từ chối | 0-100% |

#### **Additional AI Features (for deeper analysis)**
| Field | Type | Description |
|-------|------|-------------|
| **age** | Double | Tuổi của user (năm) |
| **avgBalance** | Double | Số dư trung bình (VNĐ) |
| **lowBalanceDaysRatio** | Double | % ngày có số dư < 50,000 VNĐ |
| **largestInflow** | Double | Giao dịch vào lớn nhất (VNĐ) |
| **peerTransferRatio** | Double | % giao dịch peer-to-peer |
| **uniqueReceivers** | Double | Số người nhận khác nhau |

### **📤 Response - Error Cases**

#### Case 1: User Not Found (404)
```json
{
  "timestamp": "2026-04-03T23:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 999"
}
```

#### Case 2: Unauthorized (401)
```json
{
  "timestamp": "2026-04-03T23:35:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or expired"
}
```

#### Case 3: Forbidden (403)
```json
{
  "timestamp": "2026-04-03T23:40:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied - Admin role required"
}
```

---

## 🧪 **Test với Postman/Curl**

### **Get User AI Analysis:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/users/5/ai-analysis" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### **Response Example:**
```bash
{
  "userId": 5,
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phone": "0987654321",
  "userName": "nguyenvana",
  "walletBalance": 1500000.0,
  "availableBalance": 1500000.0,
  "monthlyInflowMean": 15000000.0,
  "monthlyOutflowMean": 12000000.0,
  "transactionCount": 45.0,
  "accountAgeDays": 365.0,
  "spendIncomeRatio": 80.0,
  "balanceVolatility": 500000.0,
  "rejectedTransactionRatio": 2.0,
  "age": 36.0,
  "avgBalance": 2000000.0,
  "lowBalanceDaysRatio": 10.0,
  "largestInflow": 50000000.0,
  "peerTransferRatio": 80.0,
  "uniqueReceivers": 12.0
}
```

---

## 📊 **Use Cases - Admin Dashboard Integration**

### **1. User Risk Assessment Table**
```javascript
// Display all metrics in a table for quick review
const userAnalysis = await fetch(`/api/v1/admin/users/${userId}/ai-analysis`, {
  headers: { 'Authorization': `Bearer ${adminToken}` }
});
const data = await userAnalysis.json();

// Show metrics in dashboard
console.log(`User: ${data.fullName}`);
console.log(`Balance: ${data.walletBalance}₫`);
console.log(`Monthly Income: ${data.monthlyInflowMean}₫`);
console.log(`Spend Ratio: ${data.spendIncomeRatio}%`);
console.log(`Rejected TXs: ${data.rejectedTransactionRatio}%`);
```

### **2. Credit Risk Classification**
```javascript
// Classify user into risk categories
function classifyRisk(analysis) {
  const score = analysis.spendIncomeRatio + analysis.rejectedTransactionRatio;
  
  if (analysis.monthlyInflowMean === 0) return "NO_DATA";
  if (score > 100) return "HIGH_RISK";
  if (score > 50) return "MEDIUM_RISK";
  return "LOW_RISK";
}
```

### **3. Income Verification**
```javascript
// Verify user's declared income vs actual inflow
const declaredIncome = userLoanApplication.declaredIncome;
const actualIncome = analysis.monthlyInflowMean;
const mismatch = Math.abs(declaredIncome - actualIncome) / declaredIncome * 100;

if (mismatch > 50) {
  console.warn(`⚠️ FRAUD ALERT: Income mismatch ${mismatch}%`);
}
```

### **4. Account Maturity Check**
```javascript
// Ensure account has sufficient history
if (analysis.accountAgeDays < 90) {
  console.warn(`⚠️ Account too new (${analysis.accountAgeDays} days)`);
}
```

---

## 🔒 **Security & Privacy**

- ✅ **Role-based Access**: Admin role required
- ✅ **Data Protection**: Sensitive user data exposed only to authorized admins
- ✅ **Audit Ready**: All requests logged for compliance
- ✅ **90-day Window**: Analysis always uses last 90 days of data
- ✅ **Error Handling**: Proper error messages without exposing system details

---

## 📋 **Implementation Details**

### **Controller:**
```java
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin User APIs")
public class AdminUserController {
    
    @GetMapping("/{userId}/ai-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAIAnalysisDTO> getUserAIAnalysis(
            @PathVariable("userId") Integer userId
    )
}
```

### **DTO:**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAIAnalysisDTO {
    private Integer userId;
    private String fullName;
    // ... fields as shown above
}
```

---

## 📈 **Data Collection Method**

- **Time Window**: Sliding 90-day lookback from today
- **Data Sources**: 
  - `wallet_daily_snapshots` - for balance metrics
  - `transactions` - for inflow/outflow/rejected counts
  - `transfer_details` - for peer transfer analysis
  - `users` table - for basic info
- **Calculation**: All metrics aggregated from transaction history

---

## 🎯 **Key Features**

- ✅ **Complete User Profile** - Basic info + wallet status
- ✅ **Income Analysis** - Monthly inflow/outflow calculation
- ✅ **Behavior Patterns** - Transaction frequency + volatility
- ✅ **Risk Indicators** - Rejected transactions, low balance ratio
- ✅ **Transaction Network** - Unique receivers, peer transfer ratio
- ✅ **Real-time Data** - Fresh calculations on every request
- ✅ **Fraud Detection Ready** - All data needed for income verification

---

**Version:** 1.0
**Last Updated:** April 3, 2026
**Status:** ✅ Production Ready
