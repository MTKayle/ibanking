# OTP Transfer API Guide

## Giới thiệu
Tài liệu này hướng dẫn sử dụng hệ thống OTP (One-Time Password) cho giao dịch chuyển tiền. Hệ thống đảm bảo tính bảo mật cao bằng cách yêu cầu OTP trước khi thực hiện giao dịch.

## Luồng hoạt động

1. **User nhấn "Xác nhận chuyển tiền"** → Gọi API `/api/payment/transfer/initiate`
2. **Backend:**
   - Validate các ràng buộc (số dư, trạng thái tài khoản, quyền sở hữu)
   - Tạo transaction với trạng thái PENDING
   - Tạo OTP 6 số ngẫu nhiên
   - Lưu OTP vào database với thời gian hết hạn 1 phút
   - Trả về OTP và transaction code cho client
3. **User nhập OTP** → Gọi API `/api/payment/transfer/confirm`
4. **Backend:**
   - Verify OTP (kiểm tra mã, thời gian hết hạn, số lần thử)
   - Thực hiện chuyển tiền với pessimistic lock
   - Cập nhật transaction thành SUCCESS hoặc FAILED

## Các API Endpoints

### 1. Khởi tạo giao dịch chuyển tiền và tạo OTP

**Endpoint:** `POST /api/payment/transfer/initiate`

**Mô tả:** 
- Validate tất cả các ràng buộc (số dư, quyền sở hữu, trạng thái tài khoản)
- Tạo transaction với trạng thái PENDING
- Tạo OTP 6 số với thời gian hết hạn 1 phút
- Transaction và OTP đều là unique

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "senderAccountNumber": "1234567890",
  "receiverAccountNumber": "0987654321",
  "amount": 100000,
  "description": "Chuyển tiền cho bạn"
}
```

**Response thành công (200 OK):**
```json
{
  "transactionCode": "TRF-1734307200000-ABC12345",
  "otpCode": "123456",
  "expiresAt": "2025-12-16T02:21:00Z",
  "message": "OTP đã được tạo thành công. OTP sẽ hết hạn sau 1 phút."
}
```

**Response khi có lỗi:**
```json
{
  "status": 400,
  "message": "Số dư không đủ. Số dư khả dụng: 50000.00, Số tiền chuyển: 100000.00",
  "timestamp": "2025-12-16T02:20:00Z"
}
```

**Lưu ý:**
- Transaction được tạo với trạng thái PENDING ngay lập tức
- Nếu có lỗi validation, transaction vẫn được lưu với trạng thái FAILED
- OTP chỉ được tạo nếu tất cả validation đều pass

---

### 2. Xác nhận giao dịch bằng OTP

**Endpoint:** `POST /api/payment/transfer/confirm`

**Mô tả:**
- Verify OTP (kiểm tra mã OTP, thời gian hết hạn, số lần thử)
- Thực hiện chuyển tiền với pessimistic lock
- Cập nhật transaction thành SUCCESS hoặc FAILED

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "transactionCode": "TRF-1734307200000-ABC12345",
  "otpCode": "123456"
}
```

**Response thành công (200 OK):**
```json
{
  "transactionId": 123,
  "transactionCode": "TRF-1734307200000-ABC12345",
  "senderAccountNumber": "1234567890",
  "receiverAccountNumber": "0987654321",
  "amount": 100000,
  "description": "Chuyển tiền cho bạn",
  "senderNewBalance": 900000,
  "receiverNewBalance": 1100000,
  "receiverUserFullName": "Nguyễn Văn B",
  "transactionTime": "2025-12-16T02:20:00Z",
  "status": "SUCCESS"
}
```

**Response khi OTP sai:**
```json
{
  "status": 500,
  "message": "Invalid OTP code",
  "timestamp": "2025-12-16T02:21:00Z"
}
```

**Response khi OTP hết hạn:**
```json
{
  "status": 500,
  "message": "OTP has expired",
  "timestamp": "2025-12-16T02:22:00Z"
}
```

**Response khi nhập sai quá 3 lần:**
```json
{
  "status": 500,
  "message": "OTP verification failed: Maximum attempts exceeded",
  "timestamp": "2025-12-16T02:21:30Z"
}
```

---

## Cơ chế bảo mật

### 1. Transaction Isolation
- Transaction PENDING được tạo trong transaction riêng biệt (`REQUIRES_NEW`)
- Nếu có lỗi, transaction vẫn được commit và đánh dấu FAILED
- Đảm bảo không mất dữ liệu giao dịch

### 2. OTP Security
- **Unique:** Mỗi transaction chỉ có 1 OTP duy nhất
- **Time-based:** OTP hết hạn sau 1 phút
- **Attempt limit:** Tối đa 3 lần thử
- **One-time use:** OTP chỉ được sử dụng 1 lần

### 3. Pessimistic Locking
- Sử dụng `SELECT FOR UPDATE` khi chuyển tiền
- Lock accounts theo thứ tự ID để tránh deadlock
- Đảm bảo không mất tiền do race condition

### 4. Validation
- Kiểm tra quyền sở hữu tài khoản
- Kiểm tra trạng thái tài khoản
- Kiểm tra số dư (bao gồm overdraft limit)
- Kiểm tra số dư lại trước khi thực hiện chuyển tiền (có thể đã thay đổi)

---

## Database Schema

### Bảng `transaction_otp`

```sql
CREATE TABLE transaction_otp (
    otp_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL UNIQUE,
    otp_code VARCHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    attempts INT NOT NULL DEFAULT 0,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
);
```

**Các trường:**
- `otp_id`: Primary key
- `transaction_id`: Foreign key đến bảng transactions (UNIQUE)
- `otp_code`: Mã OTP 6 số
- `created_at`: Thời gian tạo OTP
- `expires_at`: Thời gian hết hạn (created_at + 1 phút)
- `is_verified`: Đã xác thực chưa
- `verified_at`: Thời gian xác thực
- `attempts`: Số lần thử (tối đa 3)

---

## Ví dụ sử dụng với Postman

### Bước 1: Khởi tạo giao dịch và nhận OTP

1. Tạo request mới trong Postman
2. Method: `POST`
3. URL: `http://localhost:8080/api/payment/transfer/initiate`
4. Headers:
   - `Authorization`: `Bearer <your_access_token>`
   - `Content-Type`: `application/json`
5. Body → raw → JSON:
```json
{
  "senderAccountNumber": "1234567890",
  "receiverAccountNumber": "0987654321",
  "amount": 100000,
  "description": "Chuyển tiền test OTP"
}
```
6. Click **Send**
7. **Lưu lại** `transactionCode` và `otpCode` từ response

### Bước 2: Xác nhận giao dịch bằng OTP

1. Tạo request mới trong Postman
2. Method: `POST`
3. URL: `http://localhost:8080/api/payment/transfer/confirm`
4. Headers:
   - `Authorization`: `Bearer <your_access_token>`
   - `Content-Type`: `application/json`
5. Body → raw → JSON:
```json
{
  "transactionCode": "TRF-1734307200000-ABC12345",
  "otpCode": "123456"
}
```
6. Click **Send** (phải gửi trong vòng 1 phút)

---

## Các trường hợp lỗi và xử lý

### 1. Số dư không đủ
```json
{
  "status": 400,
  "message": "Số dư không đủ. Số dư khả dụng: 50000.00, Số tiền chuyển: 100000.00"
}
```
→ Transaction được tạo với status FAILED, OTP không được tạo

### 2. OTP đã hết hạn
```json
{
  "status": 500,
  "message": "OTP has expired"
}
```
→ Transaction vẫn ở trạng thái PENDING, cần tạo lại giao dịch mới

### 3. OTP không đúng
```json
{
  "status": 500,
  "message": "Invalid OTP code"
}
```
→ Số lần thử tăng lên, còn tối đa 3 lần

### 4. Vượt quá số lần thử
```json
{
  "status": 500,
  "message": "OTP verification failed: Maximum attempts exceeded"
}
```
→ OTP bị khóa, cần tạo lại giao dịch mới

### 5. OTP đã được sử dụng
```json
{
  "status": 500,
  "message": "OTP has already been verified"
}
```
→ Không thể sử dụng lại OTP

### 6. Số dư thay đổi trong khi chờ OTP
```json
{
  "status": 400,
  "message": "Số dư không đủ để thực hiện giao dịch"
}
```
→ Số dư đã thay đổi sau khi tạo OTP, transaction được đánh dấu FAILED

---

## So sánh với API chuyển tiền thông thường

| Đặc điểm | `/api/payment/transfer` | `/api/payment/transfer/initiate` + `/confirm` |
|----------|-------------------------|-----------------------------------------------|
| **Bảo mật** | Trực tiếp | OTP 2-factor |
| **Số bước** | 1 API call | 2 API calls |
| **Transaction** | Tạo và execute ngay | Tạo PENDING → Confirm → SUCCESS |
| **Rollback** | Tự động nếu fail | Transaction riêng, không rollback record |
| **Use case** | Internal, testing | Production, user-facing |

---

## Lưu ý quan trọng

1. **Thời gian OTP:** OTP chỉ có hiệu lực 1 phút, sau đó phải tạo lại giao dịch mới

2. **Số lần thử:** Tối đa 3 lần nhập OTP sai, sau đó phải tạo lại

3. **One-time use:** Mỗi OTP chỉ được sử dụng 1 lần, không thể reuse

4. **Transaction persistence:** Transaction PENDING được lưu vĩnh viễn, giúp audit trail

5. **Concurrent check:** Khi confirm, hệ thống sẽ kiểm tra lại số dư (có thể đã thay đổi)

6. **Deadlock prevention:** Accounts được lock theo thứ tự ID tăng dần

7. **Unique constraints:**
   - Mỗi transaction chỉ có 1 OTP
   - OTP code được generate random, tránh duplicate

---

## Test Scenarios

### Scenario 1: Happy Path
1. Initiate transfer → Nhận OTP
2. Confirm với OTP đúng trong 1 phút
3. Transaction SUCCESS, tiền được chuyển

### Scenario 2: OTP Expired
1. Initiate transfer → Nhận OTP
2. Đợi > 1 phút
3. Confirm → Error "OTP has expired"
4. Phải initiate lại

### Scenario 3: Wrong OTP
1. Initiate transfer → Nhận OTP
2. Nhập OTP sai lần 1 → Error, còn 2 lần
3. Nhập OTP sai lần 2 → Error, còn 1 lần
4. Nhập OTP sai lần 3 → Error "Maximum attempts exceeded"
5. Phải initiate lại

### Scenario 4: Balance Changed
1. Initiate transfer 100k (balance: 150k) → OTP created
2. Trong khi đó có giao dịch khác trừ 100k
3. Confirm OTP → Error "Số dư không đủ"
4. Transaction FAILED

---

## Migration

Migration file `V17__create_transaction_otp_table.sql` sẽ tự động chạy khi khởi động ứng dụng, tạo bảng `transaction_otp` với đầy đủ constraints và indexes.

