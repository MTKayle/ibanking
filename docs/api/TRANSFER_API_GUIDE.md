# API Chuyển Tiền (Money Transfer API)

## Tổng quan
API chuyển tiền giữa hai tài khoản checking với đầy đủ các biện pháp bảo mật và tránh race condition.

## Đặc điểm kỹ thuật

### 1. Transaction Safety
- Toàn bộ quá trình chuyển tiền được thực hiện trong **1 transaction duy nhất** (@Transactional)
- Nếu có bất kỳ lỗi nào xảy ra, toàn bộ transaction sẽ được rollback

### 2. Pessimistic Locking (SELECT FOR UPDATE)
- Sử dụng `@Lock(LockModeType.PESSIMISTIC_WRITE)` trong repository
- Khi một transaction đang xử lý tài khoản, các transaction khác phải đợi
- Tránh race condition khi nhiều giao dịch cùng thao tác trên cùng một tài khoản

### 3. Deadlock Prevention
- **Lock theo thứ tự accountId nhỏ hơn trước**
- Ví dụ: 
  - Transaction 1: Chuyển từ A → B (lock A trước, lock B sau)
  - Transaction 2: Chuyển từ B → A (vẫn lock A trước, lock B sau vì A.id < B.id)
- Điều này đảm bảo không có circular wait → không xảy ra deadlock

### 4. Validation
- Chỉ cho phép chuyển tiền giữa các tài khoản **checking** (không phải saving hay mortgage)
- Kiểm tra trạng thái tài khoản phải là **active**
- Kiểm tra số dư đủ (bao gồm cả overdraft limit)
- Không cho phép chuyển tiền cho chính mình

## Endpoint

### POST /api/payment/transfer

**Phân quyền:** CUSTOMER, OFFICER, ADMIN

**Request Body:**
```json
{
    "senderAccountNumber": "1234567890",
    "receiverAccountNumber": "0987654321",
    "amount": 1000000.00,
    "description": "Chuyển tiền cho bạn"
}
```

**Validation:**
- `senderAccountNumber`: Required, not blank
- `receiverAccountNumber`: Required, not blank
- `amount`: Required, >= 0.01
- `description`: Optional

**Response (Success - 200 OK):**
```json
{
    "transactionId": 123,
    "transactionCode": "TRF-1701676800000-A1B2C3D4",
    "senderAccountNumber": "1234567890",
    "receiverAccountNumber": "0987654321",
    "amount": 1000000.00,
    "description": "Chuyển tiền cho bạn",
    "senderNewBalance": 4000000.00,
    "receiverNewBalance": 6000000.00,
    "transactionTime": "2024-12-04T10:30:00Z",
    "status": "SUCCESS"
}
```

**Response (Error):**

**400 Bad Request - Validation Errors:**
```json
{
    "message": "Số tiền chuyển phải lớn hơn 0"
}
```
```json
{
    "message": "Không thể chuyển tiền cho chính mình"
}
```

**404 Not Found:**
```json
{
    "message": "Không tìm thấy tài khoản gửi: 1234567890"
}
```

**400 Bad Request - Business Logic:**
```json
{
    "message": "Tài khoản gửi phải là tài khoản checking"
}
```
```json
{
    "message": "Tài khoản nhận phải là tài khoản checking"
}
```
```json
{
    "message": "Số dư không đủ. Số dư khả dụng: 500000.00, Số tiền chuyển: 1000000.00"
}
```

**400 Bad Request - Account Status:**
```json
{
    "message": "Tài khoản gửi không hoạt động"
}
```

## Luồng xử lý chi tiết

```
1. Validate request
   - Kiểm tra sender != receiver
   - Kiểm tra amount > 0
   ↓
2. Tìm sender và receiver account (chưa lock)
   - Để xác định accountId
   ↓
3. Validate account type
   - Cả hai phải là CHECKING
   ↓
4. Validate account status
   - Cả hai phải là ACTIVE
   ↓
5. LOCK accounts theo thứ tự accountId
   - Lock account có ID nhỏ hơn trước
   - Lock account có ID lớn hơn sau
   - Sử dụng SELECT FOR UPDATE
   ↓
6. Kiểm tra số dư
   - Balance + OverdraftLimit >= Amount
   ↓
7. Thực hiện chuyển tiền
   - Trừ tiền từ sender
   - Cộng tiền vào receiver
   - Save cả hai
   ↓
8. Ghi transaction vào database
   - Type: TRANSFER
   - Code: TRF-{timestamp}-{random}
   - SenderAccount + ReceiverAccount + Amount
   ↓
9. Commit transaction & Release locks
   ↓
10. Return response
```

## Cơ chế phòng tránh Deadlock

### Ví dụ minh họa:

**Trường hợp có thể gây deadlock (nếu không lock theo thứ tự):**

```
Time | Transaction 1 (A → B)      | Transaction 2 (B → A)
-----+----------------------------+---------------------------
T1   | Lock A                     | Lock B
T2   | Wait for B (B bị lock)    | Wait for A (A bị lock)
T3   | DEADLOCK! ❌              | DEADLOCK! ❌
```

**Giải pháp: Lock theo thứ tự accountId:**

```
Giả sử: A.accountId = 100, B.accountId = 200

Time | Transaction 1 (A → B)      | Transaction 2 (B → A)
-----+----------------------------+---------------------------
T1   | Lock A (100 < 200)        | Lock A (100 < 200)
T2   | Lock B                     | Wait for A to release...
T3   | Update balances            | ...still waiting
T4   | Save & Commit              | ...still waiting
T5   | Release locks ✅          | Now lock A
T6   |                            | Lock B
T7   |                            | Update balances
T8   |                            | Save & Commit
T9   |                            | Release locks ✅
```

**Kết quả:** Không deadlock! Cả hai transaction đều thành công.

## Overdraft Limit

Hệ thống hỗ trợ overdraft limit (hạn mức thấu chi):

```
Số dư khả dụng = Balance + OverdraftLimit

Ví dụ:
- Balance: 100,000
- OverdraftLimit: 50,000
- Available: 150,000

→ Có thể chuyển tối đa 150,000
→ Sau khi chuyển 150,000: Balance = -50,000
```

## Transaction Code Format

Mã giao dịch được tạo tự động:
```
TRF-{timestamp}-{random-8-chars}

Ví dụ:
TRF-1701676800000-A1B2C3D4
TRF-1701676801234-F5E6D7C8
```

## Ghi nhận giao dịch

Mỗi lần chuyển tiền thành công, hệ thống tự động tạo 1 record trong bảng `transactions`:

| Field | Value |
|-------|-------|
| transaction_id | Auto-generated |
| sender_account_id | Account gửi tiền |
| receiver_account_id | Account nhận tiền |
| amount | Số tiền chuyển |
| transaction_type | TRANSFER |
| description | Nội dung chuyển tiền |
| code | Mã giao dịch (unique) |
| created_at | Thời gian tạo |

## Testing với Postman

### Bước 1: Đăng nhập để lấy token
```
POST /api/auth/login
Body:
{
    "phone": "0123456789",
    "password": "password123"
}

→ Lưu token từ response
```

### Bước 2: Test chuyển tiền thành công
```
POST /api/payment/transfer
Headers:
Authorization: Bearer {{token}}

Body:
{
    "senderAccountNumber": "1234567890",
    "receiverAccountNumber": "0987654321",
    "amount": 500000,
    "description": "Test chuyển tiền"
}

Expected: 200 OK với transaction details
```

### Bước 3: Test validation - Số dư không đủ
```
POST /api/payment/transfer
Headers:
Authorization: Bearer {{token}}

Body:
{
    "senderAccountNumber": "1234567890",
    "receiverAccountNumber": "0987654321",
    "amount": 99999999999,
    "description": "Test số dư không đủ"
}

Expected: 400 Bad Request
Message: "Số dư không đủ..."
```

### Bước 4: Test validation - Chuyển cho chính mình
```
POST /api/payment/transfer
Headers:
Authorization: Bearer {{token}}

Body:
{
    "senderAccountNumber": "1234567890",
    "receiverAccountNumber": "1234567890",
    "amount": 100000,
    "description": "Test chuyển cho chính mình"
}

Expected: 400 Bad Request
Message: "Không thể chuyển tiền cho chính mình"
```

### Bước 5: Test validation - Account type không hợp lệ
```
POST /api/payment/transfer
Headers:
Authorization: Bearer {{token}}

Body:
{
    "senderAccountNumber": "SAVING_ACCOUNT_123",
    "receiverAccountNumber": "0987654321",
    "amount": 100000,
    "description": "Test account type"
}

Expected: 400 Bad Request
Message: "Tài khoản gửi phải là tài khoản checking"
```

### Bước 6: Test concurrency (Advanced)

**Mở 2 terminal và chạy đồng thời:**

Terminal 1:
```bash
curl -X POST http://localhost:8080/api/payment/transfer \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "senderAccountNumber": "A",
    "receiverAccountNumber": "B",
    "amount": 1000
  }'
```

Terminal 2 (chạy ngay sau đó):
```bash
curl -X POST http://localhost:8080/api/payment/transfer \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "senderAccountNumber": "A",
    "receiverAccountNumber": "B",
    "amount": 2000
  }'
```

**Kết quả mong đợi:**
- Transaction 1 thực hiện trước (hold lock)
- Transaction 2 đợi transaction 1 hoàn thành
- Cả hai transaction đều thành công
- Số dư cuối cùng = số dư ban đầu - 3000

## Security Notes

### 1. Authorization
- Endpoint được bảo vệ bởi Spring Security
- Chỉ CUSTOMER, OFFICER, ADMIN mới có quyền gọi
- Frontend cần gửi JWT token trong header

### 2. Permission Check (Recommended)
Nên thêm logic kiểm tra quyền sở hữu tài khoản:
```java
// Chỉ cho phép user chuyển tiền từ tài khoản của chính mình
// Hoặc officer/admin có thể chuyển cho bất kỳ ai
```

### 3. Rate Limiting (Recommended)
Nên implement rate limiting để tránh abuse:
- Giới hạn số lần chuyển tiền trong 1 phút
- Giới hạn tổng số tiền chuyển trong 1 ngày

### 4. Audit Trail
Mọi giao dịch đều được ghi nhận trong bảng `transactions`:
- Có thể truy vết lại lịch sử
- Có mã giao dịch unique để tra cứu

## Troubleshooting

### Vấn đề: Transaction bị timeout
**Nguyên nhân:** Lock quá lâu
**Giải pháp:** 
- Tăng transaction timeout
- Optimize code để giảm thời gian hold lock

### Vấn đề: Deadlock vẫn xảy ra
**Nguyên nhân:** Có thể do lock không đúng thứ tự
**Giải pháp:**
- Kiểm tra lại logic lock theo accountId
- Check database logs để xem deadlock details

### Vấn đề: Balance bị sai
**Nguyên nhân:** Race condition
**Giải pháp:**
- Đảm bảo sử dụng pessimistic lock
- Kiểm tra isolation level của database

## Best Practices

1. **Luôn validate input** trước khi thực hiện transaction
2. **Lock theo thứ tự** để tránh deadlock
3. **Keep transaction short** - chỉ lock khi cần thiết
4. **Log everything** - ghi nhận tất cả giao dịch
5. **Handle exceptions gracefully** - rollback khi có lỗi
6. **Test concurrent scenarios** - test nhiều request đồng thời

## Kết luận

API chuyển tiền đã được implement với:
✅ Transaction safety (@Transactional)
✅ Pessimistic locking (SELECT FOR UPDATE)
✅ Deadlock prevention (lock by accountId order)
✅ Checking account type validation
✅ Balance validation (including overdraft)
✅ Transaction recording
✅ Unique transaction code generation
✅ Complete error handling

Hệ thống đảm bảo tính toàn vẹn dữ liệu và tránh race condition trong môi trường concurrent.

