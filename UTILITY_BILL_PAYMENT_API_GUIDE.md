# API THANH TOÁN HÓA ĐƠN ĐIỆN NƯỚC

## Tổng quan
Hệ thống thanh toán hóa đơn điện, nước, internet, điện thoại với các tính năng:
- ✅ Tìm kiếm hóa đơn theo mã hóa đơn
- ✅ Thanh toán hóa đơn với **PESSIMISTIC LOCK** để tránh thanh toán trùng
- ✅ Tự động trừ tiền từ tài khoản checking
- ✅ Tạo transaction tự động
- ✅ Cập nhật trạng thái hóa đơn
- ✅ Xem lịch sử hóa đơn đã thanh toán

## Cơ chế Locking (Quan trọng!)
### Pessimistic Locking
- Sử dụng `@Lock(LockModeType.PESSIMISTIC_WRITE)` trong repository
- Khi User A đang thanh toán hóa đơn, database sẽ LOCK record đó
- User B cố gắng thanh toán cùng hóa đơn sẽ phải chờ cho đến khi User A hoàn tất
- Tránh tình trạng 2 tài khoản cùng thanh toán 1 hóa đơn

### Optimistic Locking
- Sử dụng `@Version` field trong entity
- Nếu có xung đột, sẽ throw `OptimisticLockException`
- User sẽ nhận thông báo: "Hóa đơn đang được xử lý bởi người dùng khác"

---

## 1. API Tìm Kiếm Hóa Đơn

### Endpoint
```
GET /api/utility-bills/search?billCode={billCode}
```

### Request
**Query Parameter:**
- `billCode` (required): Mã hóa đơn cần tìm

**Headers:**
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Tìm thấy hóa đơn",
  "data": {
    "billId": 1,
    "billCode": "EVN202412001",
    "billType": "ELECTRICITY",
    "billTypeDisplay": "Tiền điện",
    "customerName": "Trần Thị B",
    "customerAddress": "456 Lê Lợi, Quận 3, TP.HCM",
    "customerPhone": "0912345678",
    "period": "2024-12",
    "usageAmount": 180,
    "oldIndex": 1250,
    "newIndex": 1430,
    "unitPrice": 2500.00,
    "amount": 450000.00,
    "vat": 45000.00,
    "totalAmount": 495000.00,
    "issueDate": "2024-12-15",
    "dueDate": "2025-01-10",
    "status": "UNPAID",
    "statusDisplay": "Chưa thanh toán",
    "providerName": "Tổng Công ty Điện lực TP.HCM",
    "providerCode": "EVNHCMC",
    "notes": "Hóa đơn tiền điện tháng 12/2024",
    "isOverdue": false
  }
}
```

### Response Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Không tìm thấy hóa đơn với mã: EVN202412999"
}
```

### Ví dụ sử dụng

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/utility-bills/search?billCode=EVN202412001" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**JavaScript:**
```javascript
const searchBill = async (billCode) => {
  const token = localStorage.getItem('access_token');
  
  try {
    const response = await fetch(
      `http://localhost:8080/api/utility-bills/search?billCode=${billCode}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    const result = await response.json();
    
    if (result.success) {
      const bill = result.data;
      console.log('Mã hóa đơn:', bill.billCode);
      console.log('Loại:', bill.billTypeDisplay);
      console.log('Khách hàng:', bill.customerName);
      console.log('Kỳ:', bill.period);
      console.log('Số tiền:', bill.totalAmount.toLocaleString(), 'VND');
      console.log('Hạn thanh toán:', bill.dueDate);
      console.log('Trạng thái:', bill.statusDisplay);
      
      if (bill.isOverdue) {
        console.warn('⚠️ Hóa đơn đã quá hạn!');
      }
      
      return bill;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Lỗi:', error);
  }
};

// Sử dụng
searchBill('EVN202412001');
```

---

## 2. API Thanh Toán Hóa Đơn

### Endpoint
```
POST /api/utility-bills/pay
```

### Request
**Headers:**
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Body:**
```json
{
  "billCode": "EVN202412001"
}
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Thanh toán hóa đơn thành công",
  "data": {
    "billCode": "EVN202412001",
    "amount": 495000.00,
    "status": "SUCCESS",
    "paymentTime": "2024-12-18T14:30:00",
    "transactionId": "123",
    "balanceAfter": 9505000.00,
    "message": "Thanh toán hóa đơn thành công"
  }
}
```

### Response Error - Hóa đơn đã thanh toán (400 Bad Request)
```json
{
  "success": false,
  "message": "Hóa đơn này đã được thanh toán"
}
```

### Response Error - Không đủ tiền (400 Bad Request)
```json
{
  "success": false,
  "message": "Số dư không đủ để thanh toán hóa đơn. Số dư hiện tại: 100000 VND, Số tiền cần thanh toán: 495000 VND"
}
```

### Response Error - Xung đột thanh toán (400 Bad Request)
```json
{
  "success": false,
  "message": "Hóa đơn đang được xử lý bởi người dùng khác. Vui lòng thử lại sau"
}
```

### Quy trình thanh toán
1. **User gửi request** với mã hóa đơn
2. **Hệ thống xác thực** JWT token, lấy thông tin user
3. **Lấy tài khoản checking** của user
4. **LOCK hóa đơn** với PESSIMISTIC_WRITE (tránh thanh toán trùng)
5. **Kiểm tra trạng thái** hóa đơn (đã thanh toán? đã hủy?)
6. **Kiểm tra số dư** tài khoản
7. **Trừ tiền** từ checking account
8. **Tạo transaction** (type: WITHDRAW)
9. **Cập nhật hóa đơn**: status = PAID, payment_time, paid_by_user_id, transaction_id
10. **Trả về kết quả** cho user

### Ví dụ sử dụng

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/utility-bills/pay" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "billCode": "EVN202412001"
  }'
```

**JavaScript:**
```javascript
const payBill = async (billCode) => {
  const token = localStorage.getItem('access_token');
  
  // Hiển thị loading
  console.log('Đang xử lý thanh toán...');
  
  try {
    const response = await fetch('http://localhost:8080/api/utility-bills/pay', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ billCode })
    });
    
    const result = await response.json();
    
    if (result.success) {
      const payment = result.data;
      console.log('✅ Thanh toán thành công!');
      console.log('Mã hóa đơn:', payment.billCode);
      console.log('Số tiền:', payment.amount.toLocaleString(), 'VND');
      console.log('Thời gian:', payment.paymentTime);
      console.log('Mã giao dịch:', payment.transactionId);
      console.log('Số dư còn lại:', payment.balanceAfter.toLocaleString(), 'VND');
      
      return payment;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('❌ Lỗi thanh toán:', error.message);
    throw error;
  }
};

// Sử dụng
payBill('EVN202412001')
  .then(payment => {
    alert('Thanh toán thành công!');
  })
  .catch(error => {
    alert('Thanh toán thất bại: ' + error.message);
  });
```

**React Native:**
```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Alert } from 'react-native';

const payUtilityBill = async (billCode) => {
  try {
    const token = await AsyncStorage.getItem('access_token');
    
    const response = await fetch('http://localhost:8080/api/utility-bills/pay', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ billCode })
    });
    
    const result = await response.json();
    
    if (result.success) {
      Alert.alert(
        'Thành công',
        `Đã thanh toán ${result.data.amount.toLocaleString()} VND\nS��� dư: ${result.data.balanceAfter.toLocaleString()} VND`,
        [{ text: 'OK' }]
      );
      return result.data;
    } else {
      Alert.alert('Lỗi', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};
```

---

## 3. API Lấy Lịch Sử Hóa Đơn Đã Thanh Toán

### Endpoint
```
GET /api/utility-bills/my-paid-bills
```

### Request
**Headers:**
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Lấy danh sách hóa đơn đã thanh toán thành công",
  "total": 2,
  "data": [
    {
      "billId": 1,
      "billCode": "EVN202412001",
      "billType": "ELECTRICITY",
      "billTypeDisplay": "Tiền điện",
      "customerName": "Trần Thị B",
      "customerAddress": "456 Lê Lợi, Quận 3, TP.HCM",
      "customerPhone": "0912345678",
      "period": "2024-12",
      "usageAmount": 180,
      "oldIndex": 1250,
      "newIndex": 1430,
      "unitPrice": 2500.00,
      "amount": 450000.00,
      "vat": 45000.00,
      "totalAmount": 495000.00,
      "issueDate": "2024-12-15",
      "dueDate": "2025-01-10",
      "status": "PAID",
      "statusDisplay": "Đã thanh toán",
      "providerName": "Tổng Công ty Điện lực TP.HCM",
      "providerCode": "EVNHCMC",
      "notes": "Hóa đơn tiền điện tháng 12/2024",
      "isOverdue": false
    },
    {
      "billId": 4,
      "billCode": "VNW202411001",
      "billType": "WATER",
      "billTypeDisplay": "Tiền nước",
      "customerName": "Phạm Thị D",
      "period": "2024-11",
      "totalAmount": 132000.00,
      "status": "PAID",
      "statusDisplay": "Đã thanh toán"
    }
  ]
}
```

### Ví dụ sử dụng

**JavaScript:**
```javascript
const getMyPaidBills = async () => {
  const token = localStorage.getItem('access_token');
  
  try {
    const response = await fetch('http://localhost:8080/api/utility-bills/my-paid-bills', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('Tổng số hóa đơn đã thanh toán:', result.total);
      
      result.data.forEach(bill => {
        console.log('-------------------');
        console.log('Mã HĐ:', bill.billCode);
        console.log('Loại:', bill.billTypeDisplay);
        console.log('Kỳ:', bill.period);
        console.log('Số tiền:', bill.totalAmount.toLocaleString(), 'VND');
      });
      
      return result.data;
    }
  } catch (error) {
    console.error('Lỗi:', error);
  }
};
```

---

## 4. API Lấy Danh Sách Hóa Đơn Chưa Thanh Toán

### Endpoint
```
GET /api/utility-bills/unpaid
```

### Request
**Headers:**
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Lấy danh sách hóa đơn chưa thanh toán thành công",
  "total": 5,
  "data": [
    {
      "billCode": "EVN202412001",
      "billTypeDisplay": "Tiền điện",
      "totalAmount": 495000.00,
      "dueDate": "2025-01-10",
      "status": "UNPAID",
      "isOverdue": false
    },
    {
      "billCode": "VNW202412001",
      "billTypeDisplay": "Tiền nước",
      "totalAmount": 176000.00,
      "dueDate": "2025-01-12",
      "status": "UNPAID",
      "isOverdue": false
    },
    {
      "billCode": "EVN202410001",
      "billTypeDisplay": "Tiền điện",
      "totalAmount": 880000.00,
      "dueDate": "2024-11-20",
      "status": "OVERDUE",
      "isOverdue": true
    }
  ]
}
```

---

## Database Schema

### Table: utility_bills
```sql
CREATE TABLE utility_bills (
    bill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_code VARCHAR(20) NOT NULL UNIQUE,
    bill_type VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_address VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(15),
    period VARCHAR(7) NOT NULL,
    usage_amount INT,
    old_index INT,
    new_index INT,
    unit_price DECIMAL(19, 2),
    amount DECIMAL(19, 2) NOT NULL,
    vat DECIMAL(19, 2),
    total_amount DECIMAL(19, 2) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    payment_time DATETIME,
    paid_by_user_id BIGINT,
    transaction_id VARCHAR(255),
    provider_name VARCHAR(100),
    provider_code VARCHAR(20),
    notes VARCHAR(500),
    version BIGINT DEFAULT 0,  -- Optimistic Locking
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_utility_bill_user FOREIGN KEY (paid_by_user_id) REFERENCES users(user_id),
    INDEX idx_bill_code (bill_code),
    INDEX idx_bill_status (status),
    INDEX idx_bill_due_date (due_date),
    INDEX idx_paid_by_user (paid_by_user_id)
);
```

---

## Sample Data

Hệ thống đã có sẵn sample data trong `sample_data.sql`:

### Hóa đơn điện (EVN)
- `EVN202411001`: 687,500 VND - Hạn 20/12/2024 - UNPAID
- `EVN202412001`: 495,000 VND - Hạn 10/01/2025 - UNPAID
- `EVN202410001`: 880,000 VND - Hạn 20/11/2024 - OVERDUE (Quá hạn)

### Hóa đơn nước (SAWACO)
- `VNW202411001`: 132,000 VND - Hạn 25/12/2024 - UNPAID
- `VNW202412001`: 176,000 VND - Hạn 12/01/2025 - UNPAID

### Hóa đơn Internet (VNPT)
- `VNP202412001`: 220,000 VND - Hạn 31/12/2024 - UNPAID

### Hóa đơn điện thoại (Viettel)
- `VTL202412001`: 165,000 VND - Hạn 05/01/2025 - UNPAID

---

## Loại hóa đơn (Bill Types)

| Enum | Display Name | Mô tả |
|------|--------------|-------|
| ELECTRICITY | Tiền điện | Hóa đơn điện lực |
| WATER | Tiền nước | Hóa đơn cấp nước |
| INTERNET | Internet | Hóa đơn Internet |
| PHONE | Điện thoại | Hóa đơn điện thoại |

---

## Trạng thái hóa đơn (Bill Status)

| Status | Display Name | Mô tả |
|--------|--------------|-------|
| UNPAID | Chưa thanh toán | Hóa đơn chưa được thanh toán |
| PAID | Đã thanh toán | Hóa đơn đã thanh toán thành công |
| OVERDUE | Quá hạn | Hóa đơn chưa thanh toán và đã quá hạn |
| CANCELLED | Đã hủy | Hóa đơn đã bị hủy |

---

## Cơ chế bảo mật và xử lý đồng thời

### 1. Pessimistic Locking (Database Level)
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT b FROM UtilityBill b WHERE b.billCode = :billCode")
Optional<UtilityBill> findByBillCodeWithLock(@Param("billCode") String billCode);
```
- Khi transaction bắt đầu, database sẽ LOCK row của hóa đơn
- Các transaction khác phải chờ cho đến khi lock được giải phóng
- Đảm bảo chỉ 1 user có thể thanh toán tại 1 thời điểm

### 2. Optimistic Locking (Application Level)
```java
@Version
@Column(name = "version")
private Long version;
```
- Mỗi lần update, version tự động tăng
- Nếu 2 transaction cùng đọc version=1, chỉ 1 transaction được update lên version=2
- Transaction thứ 2 sẽ throw `OptimisticLockException`

### 3. Transaction Isolation
```java
@Transactional
public UtilityBillPaymentResponseDTO payBill(...)
```
- Tất cả thao tác (trừ tiền, tạo transaction, update hóa đơn) trong 1 transaction
- Nếu bất kỳ bước nào lỗi, toàn bộ sẽ rollback

---

## Testing với Postman

### 1. Tìm kiếm hóa đơn
```
GET http://localhost:8080/api/utility-bills/search?billCode=EVN202412001
Authorization: Bearer {token}
```

### 2. Thanh toán hóa đơn
```
POST http://localhost:8080/api/utility-bills/pay
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "billCode": "EVN202412001"
}
```

### 3. Xem lịch sử
```
GET http://localhost:8080/api/utility-bills/my-paid-bills
Authorization: Bearer {token}
```

---

## Error Handling

| Error | HTTP Code | Message |
|-------|-----------|---------|
| Hóa đơn không tồn tại | 400 | "Không tìm thấy hóa đơn với mã: {billCode}" |
| Hóa đơn đã thanh toán | 400 | "Hóa đơn này đã được thanh toán" |
| Hóa đơn đã hủy | 400 | "Hóa đơn này đã bị hủy" |
| Không đủ tiền | 400 | "Số dư không đủ để thanh toán hóa đơn..." |
| Xung đột thanh toán | 400 | "Hóa đơn đang được xử lý bởi người dùng khác..." |
| Không có tài khoản | 400 | "Không tìm thấy tài khoản thanh toán" |
| Chưa đăng nhập | 401 | "Unauthorized" |

---

## Best Practices

1. **Luôn kiểm tra trạng thái** hóa đơn trước khi thanh toán
2. **Hiển thị loading** khi đang xử lý thanh toán
3. **Xử lý lỗi** đầy đủ và hiển thị message cho user
4. **Cập nhật UI** ngay sau khi thanh toán thành công
5. **Log transaction ID** để tracking
6. **Xác nhận lại** trước khi thanh toán (confirmation dialog)

---

## Kết luận

Hệ thống thanh toán hóa đơn đã được thiết kế với:
- ✅ **Bảo mật cao**: JWT authentication, transaction isolation
- ✅ **Tránh thanh toán trùng**: Pessimistic + Optimistic locking
- ✅ **Tự động hóa**: Trừ tiền, tạo transaction, update status
- ✅ **Dễ mở rộng**: Có thể thêm loại hóa đơn mới dễ dàng
- ✅ **Tracking đầy đủ**: Lưu transaction_id, payment_time, paid_by_user

Hệ thống sẵn sàng sử dụng trong production!

