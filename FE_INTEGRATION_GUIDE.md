# 🎯 Frontend Integration Guide - Loan API

## Quick Reference for FE Developers

---

## 1️⃣ **Form Validation (Client-side)**

### FE phải validate trước khi gọi API:

```javascript
// Validation Rules (JavaScript Example)
const validateLoanForm = (data) => {
  const errors = {};

  // Amount validation
  if (!data.amount) {
    errors.amount = "Số tiền là bắt buộc";
  } else if (data.amount < 1000000) {
    errors.amount = "Số tiền tối thiểu là 1,000,000 VND";
  } else if (data.amount > 1000000000) {
    errors.amount = "Số tiền tối đa là 1,000,000,000 VND";
  }

  // Term validation
  if (!data.term) {
    errors.term = "Kỳ hạn là bắt buộc";
  } else if (data.term < 3) {
    errors.term = "Kỳ hạn tối thiểu là 3 tháng";
  } else if (data.term > 60) {
    errors.term = "Kỳ hạn tối đa là 60 tháng";
  }

  // Purpose validation
  if (!data.purpose || data.purpose.trim().length === 0) {
    errors.purpose = "Mục đích vay là bắt buộc";
  } else if (data.purpose.length < 5) {
    errors.purpose = "Mục đích phải tối thiểu 5 ký tự";
  } else if (data.purpose.length > 255) {
    errors.purpose = "Mục đích không vượt quá 255 ký tự";
  }

  // Declared income validation
  if (!data.declaredIncome && data.declaredIncome !== 0) {
    errors.declaredIncome = "Thu nhập là bắt buộc";
  } else if (data.declaredIncome < 0) {
    errors.declaredIncome = "Thu nhập phải ≥ 0";
  }

  // Job segment validation
  if (!data.jobSegmentNum || data.jobSegmentNum.trim().length === 0) {
    errors.jobSegmentNum = "Ngành nghề là bắt buộc";
  }

  return { isValid: Object.keys(errors).length === 0, errors };
};

// Usage
const formData = {
  amount: 50000000,
  term: 12,
  purpose: "Mua ô tô để kinh doanh",
  declaredIncome: 20000000,
  jobSegmentNum: "BUSINESS_OWNER"
};

const { isValid, errors } = validateLoanForm(formData);
if (!isValid) {
  // Hiển thị lỗi trên form
  console.log("Validation errors:", errors);
} else {
  // Gọi API
  applyForLoan(formData);
}
```

---

## 2️⃣ **Calling the API**

### React + Axios Example:

```javascript
import axios from 'axios';

const applyForLoan = async (formData) => {
  try {
    // Show loading spinner
    setIsLoading(true);
    setError(null);

    const response = await axios.post(
      'http://localhost:8080/api/v1/loans/apply',
      {
        amount: formData.amount,
        term: formData.term,
        purpose: formData.purpose,
        declaredIncome: formData.declaredIncome,
        jobSegmentNum: formData.jobSegmentNum
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
          'Content-Type': 'application/json'
        }
      }
    );

    // Handle success response
    handleLoanApplicationSuccess(response.data);
  } catch (error) {
    // Handle error
    handleLoanApplicationError(error);
  } finally {
    setIsLoading(false);
  }
};
```

---

## 3️⃣ **Handle Success Response (200 OK)**

### Case A: AI Passed → PENDING_ADMIN

```javascript
const handleLoanApplicationSuccess = (responseData) => {
  console.log('Loan response:', responseData);

  // responseData structure:
  // {
  //   id: 1,
  //   amount: 50000000,
  //   finalStatus: "PENDING_ADMIN",
  //   aiDecision: "PASSED_AI",
  //   aiScore: 0.82,
  //   ...
  // }

  if (responseData.finalStatus === 'PENDING_ADMIN') {
    // ✅ AI passed - show success popup (Green)
    showSuccessPopup(
      "✅ Nộp đơn vay thành công!",
      "Hồ sơ của bạn đang chờ Admin phê duyệt. Bạn sẽ nhận được thông báo khi có kết quả.",
      "success"
    );

    // Store loan data (optional - for showing on history page)
    saveLoanToLocalStorage(responseData);

    // Redirect to loan history page after 2 seconds
    setTimeout(() => {
      window.location.href = '/loan-history';
    }, 2000);
  } else if (responseData.finalStatus === 'REJECTED') {
    // ❌ AI rejected - show error popup (Red)
    handleAIRejection(responseData);
  }
};
```

### Case B: AI Rejected → REJECTED

```javascript
const handleAIRejection = (responseData) => {
  // responseData:
  // {
  //   id: 2,
  //   amount: 500000000,
  //   finalStatus: "REJECTED",
  //   aiDecision: "REJECTED_BY_AI",
  //   aiScore: 0.15,
  //   adminNote: "Rejected by AI scoring: high default risk",
  //   ...
  // }

  // ❌ AI rejected - show error popup (Red/Orange)
  showErrorPopup(
    "❌ Rất tiếc hồ sơ chưa đạt chuẩn",
    "Đơn vay của bạn không đạt tiêu chuẩn duyệt. " +
    "Vui lòng cải thiện hồ sơ hoặc liên hệ với Admin để được tư vấn.",
    "error"
  );

  console.log(`AI Score: ${responseData.aiScore} (Threshold: 0.5)`);
  console.log(`Reason: ${responseData.adminNote}`);

  // Redirect to loan history
  setTimeout(() => {
    window.location.href = '/loan-history';
  }, 3000);
};
```

---

## 4️⃣ **Handle Error Response**

### Error Handler Function:

```javascript
const handleLoanApplicationError = (error) => {
  console.error('Loan application error:', error);

  if (!error.response) {
    // Network error - no response from server
    showErrorPopup(
      "❌ Lỗi kết nối",
      "Không thể kết nối đến server. Vui lòng kiểm tra kết nối Internet.",
      "error"
    );
    return;
  }

  const status = error.response.status;
  const data = error.response.data;

  switch (status) {
    case 400:
      // Validation error
      handleValidationError(data);
      break;

    case 401:
      // Unauthorized - JWT expired or missing
      handleUnauthorizedError();
      break;

    case 404:
      // User not found
      showErrorPopup(
        "❌ Lỗi",
        "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.",
        "error"
      );
      redirectToLogin();
      break;

    case 500:
      // Server error
      showErrorPopup(
        "❌ Lỗi server",
        "Có lỗi xảy ra trên server. Vui lòng thử lại sau.",
        "error"
      );
      break;

    default:
      showErrorPopup(
        "❌ Lỗi không xác định",
        `Mã lỗi: ${status}. Vui lòng thử lại sau.`,
        "error"
      );
  }
};
```

### Handle Validation Error (400):

```javascript
const handleValidationError = (errorData) => {
  // errorData structure:
  // {
  //   "timestamp": "2026-04-03T10:25:00",
  //   "status": 400,
  //   "message": "Validation failed",
  //   "details": [
  //     { "field": "amount", "message": "Minimum loan amount is 1,000,000 VND" },
  //     { "field": "term", "message": "Loan term is required" }
  //   ]
  // }

  if (errorData.details && Array.isArray(errorData.details)) {
    // Display field-specific errors
    const fieldErrors = {};
    errorData.details.forEach(detail => {
      if (!fieldErrors[detail.field]) {
        fieldErrors[detail.field] = [];
      }
      fieldErrors[detail.field].push(detail.message);
    });

    // Update form state with errors
    setFormErrors(fieldErrors);

    // Show toast or snackbar
    showErrorMessage(
      "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các trường có dấu ⚠️"
    );
  } else {
    showErrorPopup(
      "❌ Lỗi validasi",
      errorData.message || "Dữ liệu không hợp lệ",
      "error"
    );
  }
};
```

### Handle Unauthorized (401):

```javascript
const handleUnauthorizedError = () => {
  console.warn('JWT token expired or invalid');

  // Clear auth data
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('user');

  // Show message
  showErrorPopup(
    "❌ Phiên đăng nhập hết hạn",
    "Vui lòng đăng nhập lại để tiếp tục.",
    "warning"
  );

  // Redirect to login after 2 seconds
  setTimeout(() => {
    window.location.href = '/login';
  }, 2000);
};
```

---

## 5️⃣ **Show Popups / Notifications**

### React Toast Example (using react-toastify):

```javascript
import { toast } from 'react-toastify';

// Success Popup (Green) - for AI PASSED
const showSuccessPopup = (title, message, type = 'success') => {
  toast.success(
    <div>
      <h4>{title}</h4>
      <p>{message}</p>
    </div>,
    {
      position: 'top-center',
      autoClose: 3000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      className: 'toast-success'
    }
  );
};

// Error Popup (Red) - for AI REJECTED or errors
const showErrorPopup = (title, message, type = 'error') => {
  toast.error(
    <div>
      <h4>{title}</h4>
      <p>{message}</p>
    </div>,
    {
      position: 'top-center',
      autoClose: 4000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      className: 'toast-error'
    }
  );
};

// Warning Popup (Orange)
const showWarningPopup = (title, message) => {
  toast.warning(
    <div>
      <h4>{title}</h4>
      <p>{message}</p>
    </div>,
    {
      position: 'top-center',
      autoClose: 3000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      className: 'toast-warning'
    }
  );
};
```

---

## 6️⃣ **Loading Spinner State**

```javascript
// React component with loading state

const [isLoading, setIsLoading] = useState(false);

return (
  <form onSubmit={handleSubmit}>
    <input
      type="number"
      name="amount"
      placeholder="Số tiền vay"
      // ... other props
    />
    {/* ... other fields ... */}

    <button
      type="submit"
      disabled={isLoading || !isFormValid}
      className={isLoading ? 'btn-loading' : 'btn'}
    >
      {isLoading ? (
        <>
          <span className="spinner"></span>
          <span>Đang xử lý...</span>
        </>
      ) : (
        'Gửi yêu cầu'
      )}
    </button>
  </form>
);
```

---

## 7️⃣ **Loan History Page - Display Loan Status**

```javascript
// Map loan status to display text and color

const getLoanStatusDisplay = (loan) => {
  const statusMap = {
    PENDING_AI: {
      display: '⏳ Đang xử lý',
      color: '#FFA500', // Orange
      description: 'AI đang chấm điểm...'
    },
    PENDING_ADMIN: {
      display: '⏳ Chờ duyệt',
      color: '#FFA500', // Orange
      description: 'Hồ sơ chờ Admin phê duyệt'
    },
    APPROVED: {
      display: '✅ Đã duyệt',
      color: '#28A745', // Green
      description: 'Khoản vay đã được duyệt. Tiền sẽ được chuyển vào ví.'
    },
    REJECTED: {
      display: '❌ Bị từ chối',
      color: '#DC3545', // Red
      description: loan.adminNote || 'Đơn vay bị từ chối'
    }
  };

  return statusMap[loan.finalStatus] || {
    display: 'Không xác định',
    color: '#999',
    description: ''
  };
};

// Usage in list component
const LoanHistoryRow = ({ loan }) => {
  const status = getLoanStatusDisplay(loan);

  return (
    <tr>
      <td>{loan.id}</td>
      <td>{formatCurrency(loan.amount)}</td>
      <td>{loan.term} tháng</td>
      <td>
        <span style={{ color: status.color, fontWeight: 'bold' }}>
          {status.display}
        </span>
      </td>
      <td>{new Date(loan.createdAt).toLocaleDateString('vi-VN')}</td>
      <td>
        <button onClick={() => viewLoanDetails(loan.id)}>
          Chi tiết
        </button>
      </td>
    </tr>
  );
};
```

---

## 8️⃣ **AI Score Interpretation**

```javascript
// Helper function to interpret AI score

const interpretAIScore = (score) => {
  if (score >= 0.9) {
    return { level: 'Rất tốt', color: '#28A745', emoji: '⭐⭐⭐' };
  } else if (score >= 0.7) {
    return { level: 'Tốt', color: '#5CB85C', emoji: '⭐⭐' };
  } else if (score >= 0.5) {
    return { level: 'Bình thường', color: '#FFA500', emoji: '⭐' };
  } else if (score >= 0.3) {
    return { level: 'Rủi ro cao', color: '#FF6B6B', emoji: '⚠️' };
  } else {
    return { level: 'Rất rủi ro', color: '#DC3545', emoji: '❌' };
  }
};

// Usage
const score = responseData.aiScore; // 0.82
const interpretation = interpretAIScore(score);
console.log(`${interpretation.emoji} ${interpretation.level}`);
```

---

## 9️⃣ **Request/Response Interceptors (Axios)**

```javascript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080'
});

// Request interceptor - add JWT token
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Response interceptor - handle 401
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token expired - redirect to login
      localStorage.removeItem('jwtToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;

// Usage
// Instead of: axios.post(...)
// Use: apiClient.post('/api/v1/loans/apply', data)
```

---

## 🔟 **Complete Example: React Component**

```javascript
import React, { useState } from 'react';
import axios from 'axios';
import './LoanForm.css';

const LoanForm = () => {
  const [formData, setFormData] = useState({
    amount: '',
    term: '',
    purpose: '',
    declaredIncome: '',
    jobSegmentNum: 'BUSINESS_OWNER'
  });
  const [formErrors, setFormErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const validateForm = () => {
    const errors = {};
    // ... validation logic ...
    return { isValid: Object.keys(errors).length === 0, errors };
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear error for this field
    if (formErrors[name]) {
      setFormErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Client-side validation
    const { isValid, errors } = validateForm();
    if (!isValid) {
      setFormErrors(errors);
      return;
    }

    setIsLoading(true);

    try {
      const response = await axios.post(
        'http://localhost:8080/api/v1/loans/apply',
        formData,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
            'Content-Type': 'application/json'
          }
        }
      );

      const loanData = response.data;

      if (loanData.finalStatus === 'PENDING_ADMIN') {
        // Show success popup and redirect
        toast.success(
          `✅ Nộp đơn vay thành công!\nHồ sơ của bạn đang chờ Admin phê duyệt.`
        );
        setTimeout(() => {
          window.location.href = '/loan-history';
        }, 2000);
      } else if (loanData.finalStatus === 'REJECTED') {
        // Show rejection popup
        toast.error(
          `❌ Rất tiếc hồ sơ chưa đạt chuẩn.\nVui lòng liên hệ Admin để được tư vấn.`
        );
        setTimeout(() => {
          window.location.href = '/loan-history';
        }, 2000);
      }
    } catch (error) {
      if (error.response?.status === 400) {
        // Validation error from server
        setFormErrors(
          error.response.data.details.reduce((acc, detail) => {
            acc[detail.field] = detail.message;
            return acc;
          }, {})
        );
      } else {
        // Other errors
        toast.error('❌ Có lỗi xảy ra. Vui lòng thử lại.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="loan-form-container">
      <h1>Nộp Đơn Xin Vay</h1>
      <form onSubmit={handleSubmit}>
        {/* Form fields */}
        <div className="form-group">
          <label>Số tiền vay (VND) *</label>
          <input
            type="number"
            name="amount"
            value={formData.amount}
            onChange={handleChange}
            placeholder="Nhập số tiền (1,000,000 - 1,000,000,000)"
            disabled={isLoading}
          />
          {formErrors.amount && <span className="error">{formErrors.amount}</span>}
        </div>

        {/* ... other fields ... */}

        <button
          type="submit"
          disabled={isLoading}
          className={isLoading ? 'btn-loading' : 'btn-primary'}
        >
          {isLoading ? (
            <>
              <span className="spinner"></span> Đang xử lý...
            </>
          ) : (
            'Gửi yêu cầu'
          )}
        </button>
      </form>
    </div>
  );
};

export default LoanForm;
```

---

## 🔗 **Testing Tips**

1. **Use Postman/Insomnia** to test API before integrating with FE
2. **Check JWT token** - make sure it's valid and not expired
3. **Enable CORS** if testing from different port/domain
4. **Check browser console** for error details
5. **Use Swagger UI** at `http://localhost:8080/swagger-ui.html` for quick testing

---

**Status:** ✅ Ready for Frontend Integration  
**Version:** 1.0  
**Last Updated:** April 3, 2026
