# API Nạp Tiền Vào Tài Khoản Checking - Dành Cho Officer

## Mô tả
API này cho phép officer nạp tiền vào tài khoản checking của khách hàng. API sử dụng **Pessimistic Lock (SELECT FOR UPDATE)** để đảm bảo tính toàn vẹn dữ liệu trong môi trường đa luồng. **Sau khi nạp tiền thành công, hệ thống tự động ghi giao dịch vào bảng `transactions`**.

## Kiến trúc mới
- **Controller**: `PaymentManagementController`
- **Service**: `PaymentService` và `PaymentServiceImpl`
- **Repositories**: `CheckingAccountRepository`, `TransactionRepository`

## Endpoint
```
POST /api/payment-management/checking/deposit
```

## Phân quyền
- Chỉ user có role `OFFICER` hoặc `ADMIN` mới có thể sử dụng API này
- Cần có JWT token trong header

## Request

### Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Request Body
```json
{
  "accountNumber": "0123456789",
  "amount": 1000000.50,
  "description": "Nạp tiền từ quầy giao dịch"
}
```

### Validation Rules
- `accountNumber`: Bắt buộc, không được để trống
- `amount`: Bắt buộc, phải lớn hơn 0.01
- `description`: Tùy chọn, mô tả giao dịch

## Response

### Success Response (200 OK)
```json
{
  "accountNumber": "0123456789",
  "depositAmount": 1000000.50,
  "newBalance": 5000000.50,
  "description": "Nạp tiền từ quầy giao dịch",
  "timestamp": "2025-12-02T16:11:25.123Z",
  "message": "Nạp tiền thành công 1000000.50 vào tài khoản 0123456789. Mã giao dịch: DEP-1701531085123-A1B2C3D4"
}
```

**Lưu ý**: Response message bây giờ bao gồm mã giao dịch (transaction code) để tra cứu sau này.

### Error Responses

#### 404 Not Found - Tài khoản không tồn tại
```json
{
  "timestamp": "2025-12-02T16:11:25.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Không tìm thấy tài khoản checking với số: 0123456789"
}
```

#### 400 Bad Request - Tài khoản không active
```json
{
  "timestamp": "2025-12-02T16:11:25.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Tài khoản không ở trạng thái hoạt động. Không thể nạp tiền."
}
```

#### 400 Bad Request - Validation Error
```json
{
  "timestamp": "2025-12-02T16:11:25.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be greater than 0"
}
```

#### 403 Forbidden - Không có quyền
```json
{
  "timestamp": "2025-12-02T16:11:25.123Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

## Quy trình xử lý trong Transaction

### 1. Lock tài khoản (Pessimistic Lock)
```sql
SELECT * FROM checking_accounts ca 
JOIN accounts a ON ca.checking_id = a.account_id 
WHERE a.account_number = '0123456789'
FOR UPDATE;
```

### 2. Kiểm tra trạng thái
- Kiểm tra account status = 'active'

### 3. Cập nhật số dư
- Tính toán: `newBalance = currentBalance + depositAmount`
- Update vào database

### 4. **Ghi giao dịch vào bảng transactions** (MỚI!)
- Tạo mã giao dịch unique: `DEP-{timestamp}-{random8chars}`
- Insert record vào bảng `transactions`:
  - `account_id`: ID của tài khoản
  - `amount`: Số tiền nạp (dương)
  - `transaction_type`: `DEPOSIT`
  - `description`: Mô tả từ request hoặc default
  - `code`: Mã giao dịch unique
  - `created_at`: Timestamp hiện tại

### 5. Commit transaction
- Tất cả thay đổi được commit cùng lúc
- Nếu có lỗi ở bất kỳ bước nào → rollback toàn bộ

## Cấu trúc bảng Transactions

```sql
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- DEPOSIT, WITHDRAW, TRANSFER, etc.
    description VARCHAR(255),
    code VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
```

## Test với cURL (Windows CMD)

### 1. Login với officer account để lấy JWT token
```bash
curl -X POST "http://localhost:8080/api/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"phone\":\"officer_phone\",\"password\":\"officer_password\"}"
```

### 2. Sử dụng token để nạp tiền
```bash
curl -X POST "http://localhost:8080/api/payment-management/checking/deposit" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" ^
  -d "{\"accountNumber\":\"0123456789\",\"amount\":1000000,\"description\":\"Nạp tiền test\"}"
```

### 3. Kiểm tra transaction đã được ghi
Sau khi nạp tiền thành công, kiểm tra database:
```sql
SELECT * FROM transactions 
WHERE code LIKE 'DEP-%' 
ORDER BY created_at DESC 
LIMIT 10;
```

## Test với Postman

### Step 1: Login
1. Method: `POST`
2. URL: `http://localhost:8080/api/auth/login`
3. Body (JSON):
```json
{
  "phone": "officer_phone",
  "password": "officer_password"
}
```
4. Lưu `token` từ response

### Step 2: Deposit
1. Method: `POST`
2. URL: `http://localhost:8080/api/payment-management/checking/deposit`
3. Headers:
   - `Authorization`: `Bearer <token_from_step_1>`
   - `Content-Type`: `application/json`
4. Body (JSON):
```json
{
  "accountNumber": "0123456789",
  "amount": 1000000.50,
  "description": "Nạp tiền từ quầy"
}
```

### Step 3: Verify Transaction
Check database hoặc tạo API để query transactions by account number

## Các file đã tạo/sửa đổi

### Mới tạo:
1. **Repository:**
   - `TransactionRepository.java` - Repository để làm việc với bảng transactions

2. **Service:**
   - `PaymentService.java` - Interface cho payment operations
   - `PaymentServiceImpl.java` - Implementation với logic nạp tiền + ghi transaction

3. **Controller:**
   - `PaymentManagementController.java` - REST endpoint cho payment management

### Đã sửa:
1. **AccountService/Impl:**
   - Đã xóa method `depositToCheckingAccount` (chuyển sang PaymentService)
   - Chỉ giữ lại method `getCheckingAccountInfo`

2. **AccountController:**
   - Chỉ còn endpoint GET để lấy thông tin account

## Luồng dữ liệu hoàn chỉnh

```
Client Request
    ↓
PaymentManagementController (@PreAuthorize OFFICER/ADMIN)
    ↓
PaymentServiceImpl (@Transactional)
    ↓
1. CheckingAccountRepository.findByAccountNumberForUpdate() [LOCK]
2. Validate account status
3. Calculate new balance
4. Update CheckingAccount.balance
5. Generate transaction code
6. Create Transaction entity
7. TransactionRepository.save()
    ↓
Commit Transaction (balance + transaction record)
    ↓
Return DepositResponse with transaction code
```

## Lợi ích của kiến trúc mới

### 1. **Separation of Concerns**
- `AccountService`: Quản lý thông tin tài khoản (read operations)
- `PaymentService`: Xử lý các giao dịch tài chính (write operations)

### 2. **Audit Trail**
- Mọi giao dịch nạp tiền đều được ghi lại trong bảng `transactions`
- Có mã giao dịch unique để tra cứu
- Timestamp chính xác cho mỗi giao dịch

### 3. **Transaction Safety**
- Toàn bộ trong một `@Transactional`
- Nếu ghi transaction thất bại → rollback cả việc cập nhật balance
- Đảm bảo consistency giữa balance và transaction history

### 4. **Traceability**
- Officer có thể tra cứu lịch sử giao dịch
- Customer có thể xem statement
- Dễ dàng debug và audit

## Mã giao dịch (Transaction Code)

Format: `DEP-{timestamp}-{random8chars}`

Ví dụ:
- `DEP-1701531085123-A1B2C3D4`
- `DEP-1701531085456-F7G8H9I0`

**Đặc điểm:**
- Prefix `DEP` cho DEPOSIT
- Timestamp milliseconds đảm bảo uniqueness
- 8 ký tự random (uppercase) để tăng entropy
- Tổng độ dài: ~30 ký tự

## Next Steps

### Các API nên thêm tiếp:
1. **GET /api/payment-management/transactions/{accountNumber}**
   - Lấy lịch sử giao dịch của một account
   
2. **POST /api/payment-management/checking/withdraw**
   - Rút tiền từ checking account
   
3. **POST /api/payment-management/transfer**
   - Chuyển tiền giữa các accounts

4. **GET /api/payment-management/transactions/{transactionCode}**
   - Tra cứu chi tiết một giao dịch bằng mã

## Lưu ý quan trọng

### 1. Transaction Atomicity
Nếu bất kỳ step nào fail:
- Update balance ✓ → Insert transaction ✗ = **ROLLBACK ALL**
- Không bao giờ có trường hợp balance thay đổi nhưng không có transaction record

### 2. Performance
- Pessimistic lock giữ record cho đến khi commit
- Các transaction khác phải chờ
- Phù hợp cho financial operations (correctness > speed)

### 3. Monitoring
Nên theo dõi:
- Transaction code generation conflicts (rất hiếm)
- Lock timeout (nếu transaction quá lâu)
- Failed deposits vs successful deposits ratio
