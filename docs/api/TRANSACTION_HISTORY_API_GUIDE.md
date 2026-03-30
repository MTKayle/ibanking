# API LỊCH SỬ GIAO DỊCH

## Tổng quan
Hệ thống cung cấp 2 API để xem lịch sử giao dịch:
- **API cho Officer**: Xem tất cả giao dịch của bất kỳ user nào
- **API cho User**: Xem chỉ giao dịch thành công của chính mình

---

## 1. API Lấy Tất Cả Giao Dịch Theo UserId (Officer)

### Endpoint
```
GET /api/transactions/user/{userId}
```

### Yêu cầu
- **Role**: OFFICER
- **Authentication**: Bearer Token (JWT)

### Path Parameter
- `userId` (required): ID của user cần xem lịch sử giao dịch

### Headers
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Lấy lịch sử giao dịch thành công",
  "total": 5,
  "data": [
    {
      "transactionId": 123,
      "code": "TXN20241219001",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": "0987654321",
      "receiverAccountName": "Trần Thị B",
      "amount": 500000,
      "transactionType": "TRANSFER",
      "description": "Chuyển tiền cho bạn",
      "status": "SUCCESS",
      "createdAt": "2024-12-19T10:30:00Z"
    },
    {
      "transactionId": 122,
      "code": "TXN20241219002",
      "senderAccountNumber": null,
      "senderAccountName": null,
      "receiverAccountNumber": "1234567890",
      "receiverAccountName": "Nguyễn Văn A",
      "amount": 1000000,
      "transactionType": "DEPOSIT",
      "description": "Nạp tiền vào tài khoản",
      "status": "SUCCESS",
      "createdAt": "2024-12-19T09:15:00Z"
    },
    {
      "transactionId": 121,
      "code": "UTILITY_ABC12345",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": null,
      "receiverAccountName": null,
      "amount": 495000,
      "transactionType": "WITHDRAW",
      "description": "Thanh toán Tiền điện - Mã HĐ: EVN202412001",
      "status": "SUCCESS",
      "createdAt": "2024-12-18T14:30:00Z"
    },
    {
      "transactionId": 120,
      "code": "TXN20241218001",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": "1111222233",
      "receiverAccountName": "Lê Văn C",
      "amount": 200000,
      "transactionType": "TRANSFER",
      "description": "Trả tiền ăn",
      "status": "FAILED",
      "createdAt": "2024-12-18T11:20:00Z"
    },
    {
      "transactionId": 119,
      "code": "TXN20241217001",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": "5555666677",
      "receiverAccountName": "Phạm Thị D",
      "amount": 150000,
      "transactionType": "TRANSFER",
      "description": "Chuyển tiền",
      "status": "PENDING",
      "createdAt": "2024-12-17T16:45:00Z"
    }
  ]
}
```

### Response Error - Không tìm thấy user (400 Bad Request)
```json
{
  "success": false,
  "message": "Không tìm thấy user với ID: 999"
}
```

### Response Error - Không có quyền (403 Forbidden)
```json
{
  "success": false,
  "message": "Access Denied"
}
```

### Ví dụ sử dụng

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/transactions/user/1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**JavaScript:**
```javascript
const getAllTransactionsByUserId = async (userId) => {
  const token = localStorage.getItem('access_token');
  
  try {
    const response = await fetch(`http://localhost:8080/api/transactions/user/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('Tổng số giao dịch:', result.total);
      
      result.data.forEach(tx => {
        console.log('-------------------');
        console.log('Mã GD:', tx.code);
        console.log('Loại:', tx.transactionType);
        console.log('Từ:', tx.senderAccountName || 'Bên ngoài');
        console.log('Đến:', tx.receiverAccountName || 'Bên ngoài');
        console.log('Số tiền:', tx.amount.toLocaleString(), 'VND');
        console.log('Trạng thái:', tx.status);
        console.log('Thời gian:', new Date(tx.createdAt).toLocaleString());
      });
      
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Lỗi:', error);
  }
};

// Sử dụng
getAllTransactionsByUserId(1);
```

---

## 2. API Lấy Giao Dịch Thành Công Của User Hiện Tại

### Endpoint
```
GET /api/transactions/my-transactions
```

### Yêu cầu
- **Role**: Bất kỳ user đã đăng nhập
- **Authentication**: Bearer Token (JWT)
- **Filter**: Chỉ trả về giao dịch có status = SUCCESS

### Headers
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Lấy lịch sử giao dịch thành công",
  "total": 3,
  "data": [
    {
      "transactionId": 123,
      "code": "TXN20241219001",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": "0987654321",
      "receiverAccountName": "Trần Thị B",
      "amount": 500000,
      "transactionType": "TRANSFER",
      "description": "Chuyển tiền cho bạn",
      "status": "SUCCESS",
      "createdAt": "2024-12-19T10:30:00Z"
    },
    {
      "transactionId": 122,
      "code": "TXN20241219002",
      "senderAccountNumber": null,
      "senderAccountName": null,
      "receiverAccountNumber": "1234567890",
      "receiverAccountName": "Nguyễn Văn A",
      "amount": 1000000,
      "transactionType": "DEPOSIT",
      "description": "Nạp tiền vào tài khoản",
      "status": "SUCCESS",
      "createdAt": "2024-12-19T09:15:00Z"
    },
    {
      "transactionId": 121,
      "code": "UTILITY_ABC12345",
      "senderAccountNumber": "1234567890",
      "senderAccountName": "Nguyễn Văn A",
      "receiverAccountNumber": null,
      "receiverAccountName": null,
      "amount": 495000,
      "transactionType": "WITHDRAW",
      "description": "Thanh toán Tiền điện - Mã HĐ: EVN202412001",
      "status": "SUCCESS",
      "createdAt": "2024-12-18T14:30:00Z"
    }
  ]
}
```

**Lưu ý**: Chỉ trả về giao dịch có `status = "SUCCESS"`. Các giao dịch PENDING hoặc FAILED sẽ không xuất hiện.

### Response Error - Chưa đăng nhập (401 Unauthorized)
```json
{
  "success": false,
  "message": "Unauthorized"
}
```

### Ví dụ sử dụng

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/transactions/my-transactions" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**JavaScript:**
```javascript
const getMyTransactions = async () => {
  const token = localStorage.getItem('access_token');
  
  try {
    const response = await fetch('http://localhost:8080/api/transactions/my-transactions', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('✅ Có', result.total, 'giao dịch thành công');
      
      // Tính tổng tiền đã chuyển
      const totalSent = result.data
        .filter(tx => tx.senderAccountNumber !== null)
        .reduce((sum, tx) => sum + tx.amount, 0);
      
      // Tính tổng tiền đã nhận
      const totalReceived = result.data
        .filter(tx => tx.receiverAccountNumber !== null && tx.senderAccountNumber === null)
        .reduce((sum, tx) => sum + tx.amount, 0);
      
      console.log('Tổng đã chuyển đi:', totalSent.toLocaleString(), 'VND');
      console.log('Tổng đã nhận:', totalReceived.toLocaleString(), 'VND');
      
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Lỗi:', error);
  }
};

// Sử dụng
getMyTransactions();
```

**React Native:**
```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';

const fetchMyTransactions = async () => {
  try {
    const token = await AsyncStorage.getItem('access_token');
    
    const response = await fetch('http://localhost:8080/api/transactions/my-transactions', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Hiển thị danh sách giao dịch
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};
```

---

## So Sánh 2 API

| Tiêu chí | Officer API | User API |
|----------|-------------|----------|
| **Endpoint** | `/api/transactions/user/{userId}` | `/api/transactions/my-transactions` |
| **Role yêu cầu** | OFFICER | Bất kỳ user đã đăng nhập |
| **Xem giao dịch của** | Bất kỳ user nào (theo userId) | Chỉ chính mình |
| **Filter status** | Tất cả (SUCCESS, PENDING, FAILED) | Chỉ SUCCESS |
| **Use case** | Admin/Officer kiểm tra giao dịch user | User xem lịch sử giao dịch của mình |

---

## Loại Giao Dịch (Transaction Types)

| Type | Mô tả | Sender Account | Receiver Account |
|------|-------|----------------|------------------|
| **DEPOSIT** | Nạp tiền | null | Có |
| **WITHDRAW** | Rút tiền / Thanh toán | Có | null |
| **TRANSFER** | Chuyển khoản | Có | Có |
| **LOAN_PAYMENT** | Trả nợ | Có | Có |
| **INTEREST_INCOME** | Lãi | null | Có |

---

## Trạng Thái Giao Dịch (Transaction Status)

| Status | Mô tả |
|--------|-------|
| **PENDING** | Đang chờ xử lý (VD: chờ xác nhận OTP) |
| **SUCCESS** | Giao dịch thành công |
| **FAILED** | Giao dịch thất bại |

---

## Lưu ý

### 1. Sắp xếp
- Tất cả giao dịch được sắp xếp theo thời gian tạo mới nhất (`ORDER BY createdAt DESC`)

### 2. Null values
- `senderAccountNumber` và `senderAccountName` = null khi giao dịch là DEPOSIT (nạp tiền từ bên ngoài)
- `receiverAccountNumber` và `receiverAccountName` = null khi giao dịch là WITHDRAW (rút tiền ra ngoài)

### 3. Bảo mật
- Officer API yêu cầu role `OFFICER` - nếu user thường gọi sẽ nhận lỗi 403 Forbidden
- User API chỉ trả về giao dịch của chính user đó, không thể xem của người khác

### 4. Performance
- Cả 2 API đều đánh dấu `@Transactional(readOnly = true)` để tối ưu hiệu suất
- Query đã được tối ưu với join và filter ở database level

---

## Testing với Postman

### Test Officer API:
1. Đăng nhập với tài khoản có role OFFICER
2. Copy access token
3. Tạo request:
   - Method: GET
   - URL: `http://localhost:8080/api/transactions/user/1`
   - Headers: `Authorization: Bearer {token}`
4. Send và kiểm tra response

### Test User API:
1. Đăng nhập với tài khoản user bình thường
2. Copy access token
3. Tạo request:
   - Method: GET
   - URL: `http://localhost:8080/api/transactions/my-transactions`
   - Headers: `Authorization: Bearer {token}`
4. Send và kiểm tra chỉ thấy giao dịch SUCCESS

---

## Kết luận

✅ **Officer API**: Công cụ mạnh mẽ để officer/admin theo dõi toàn bộ hoạt động giao dịch của user  
✅ **User API**: Giao diện đơn giản cho user xem lịch sử giao dịch thành công của mình  
✅ **Bảo mật**: Phân quyền rõ ràng, mỗi role chỉ truy cập được dữ liệu phù hợp  
✅ **Performance**: Query được tối ưu, response nhanh

Hệ thống sẵn sàng sử dụng! 🚀

