# Hệ Thống Tiết Kiệm Ngân Hàng - API Guide (Updated)

## Giới thiệu
Hệ thống tiết kiệm ngân hàng cho phép người dùng:
- Tạo sổ tiết kiệm với các kỳ hạn khác nhau
- Xem danh sách và chi tiết sổ tiết kiệm **với lãi ước tính khi đáo hạn**
- Preview thông tin tất toán trước khi thực hiện
- Xác nhận tất toán sau khi xem preview
- Officer có thể quản lý lãi suất các kỳ hạn

## Lãi Suất Theo Kỳ Hạn (Quản lý trong Database)

Lãi suất được lưu trong bảng `saving_terms` và có thể được Officer cập nhật.

### Lấy danh sách lãi suất hiện tại

**Endpoint:** `GET /api/saving/terms` (Public - không cần token)

**Response:**
```json
[
  {
    "termId": 1,
    "termType": "NON_TERM",
    "interestRate": 0.20,
    "updatedAt": "2025-12-16T10:00:00Z",
    "updatedBy": "officer@bank.com"
  },
  {
    "termId": 2,
    "termType": "TWELVE_MONTHS",
    "interestRate": 5.50,
    "updatedAt": "2025-12-16T10:00:00Z",
    "updatedBy": "officer@bank.com"
  }
]
```

### Cập nhật lãi suất (chỉ OFFICER)

**Endpoint:** `PUT /api/saving/terms/update-rate`

**Headers:**
```
Authorization: Bearer <officer_token>
Content-Type: application/json
```

**Request:**
```json
{
  "termType": "TWELVE_MONTHS",
  "interestRate": 5.8
}
```

**Response:**
```json
{
  "termId": 7,
  "termType": "TWELVE_MONTHS",
  "interestRate": 5.80,
  "updatedAt": "2025-12-16T11:00:00Z",
  "updatedBy": "officer@bank.com"
}
```

---

## Các API Endpoints

### 1. Tạo Sổ Tiết Kiệm

**Endpoint:** `POST /api/saving/create`

**Mô tả:**
- Trừ tiền từ tài khoản checking của user
- Tạo sổ tiết kiệm mới với số tiền và kỳ hạn được chọn
- Tự động tạo số sổ tiết kiệm (format: STK-YYYYMMDD###)
- Ghi transaction DEPOSIT

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "senderAccountNumber": "1234567890",
  "amount": 100000000,
  "term": "TWELVE_MONTHS"
}
```

**Các giá trị term hợp lệ:**
- `NON_TERM` - Không kỳ hạn (0.20%)
- `ONE_MONTH` - 1 tháng (3.2%)
- `TWO_MONTHS` - 2 tháng (3.4%)
- `THREE_MONTHS` - 3 tháng (3.6%)
- `SIX_MONTHS` - 6 tháng (4.8%)
- `NINE_MONTHS` - 9 tháng (5.0%)
- `TWELVE_MONTHS` - 12 tháng (5.5%)
- `FIFTEEN_MONTHS` - 15 tháng (5.8%)
- `EIGHTEEN_MONTHS` - 18 tháng (6.0%)
- `TWENTY_FOUR_MONTHS` - 24 tháng (6.4%)
- `THIRTY_SIX_MONTHS` - 36 tháng (6.8%)

**Response thành công (201 CREATED):**
```json
{
  "savingId": 123,
  "savingBookNumber": "STK-20251216001",
  "accountNumber": "SAV1234567890",
  "balance": 100000000,
  "term": "12 tháng",
  "termMonths": 12,
  "interestRate": 5.5,
  "openedDate": "2025-12-16",
  "maturityDate": "2026-12-16",
  "status": "ACTIVE",
  "userId": 1,
  "userFullName": "Nguyễn Văn A"
}
```

**Ví dụ sử dụng:**
```bash
curl -X POST "http://localhost:8080/api/saving/create" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "senderAccountNumber": "1234567890",
    "amount": 100000000,
    "term": "TWELVE_MONTHS"
  }'
```

---

### 2. Lấy Danh Sách Sổ Tiết Kiệm Của Tôi

**Endpoint:** `GET /api/saving/my-accounts`

**Mô tả:** Lấy danh sách tất cả sổ tiết kiệm của user hiện tại

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response thành công (200 OK):**
```json
[
  {
    "savingId": 123,
    "savingBookNumber": "STK-20251216001",
    "accountNumber": "SAV1234567890",
    "balance": 100000000,
    "term": "12 tháng",
    "termMonths": 12,
    "interestRate": 5.5,
    "openedDate": "2025-12-16",
    "maturityDate": "2026-12-16",
    "status": "ACTIVE",
    "userId": 1,
    "userFullName": "Nguyễn Văn A"
  },
  {
    "savingId": 124,
    "savingBookNumber": "STK-20251216002",
    "accountNumber": "SAV1234567891",
    "balance": 50000000,
    "term": "6 tháng",
    "termMonths": 6,
    "interestRate": 4.8,
    "openedDate": "2025-12-16",
    "maturityDate": "2026-06-16",
    "status": "ACTIVE",
    "userId": 1,
    "userFullName": "Nguyễn Văn A"
  }
]
```

**Ví dụ sử dụng:**
```bash
curl -X GET "http://localhost:8080/api/saving/my-accounts" \
  -H "Authorization: Bearer <token>"
```

---

### 3. Xem Chi Tiết Sổ Tiết Kiệm (CÓ TÍNH LÃI ƯỚC TÍNH)

**Endpoint:** `GET /api/saving/{savingBookNumber}`

**Mô tả:** Lấy thông tin chi tiết kèm **tính toán lãi ước tính khi đáo hạn**

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response thành công (200 OK):**
```json
{
  "savingId": 123,
  "savingBookNumber": "STK-20251216001",
  "accountNumber": "SAV1234567890",
  "balance": 100000000,
  "term": "12 tháng",
  "termMonths": 12,
  "interestRate": 5.5,
  "openedDate": "16/12/2025",
  "maturityDate": "16/12/2026",
  "status": "ACTIVE",
  "userId": 1,
  "userFullName": "Nguyễn Văn A",
  "estimatedInterestAtMaturity": 5500000,
  "estimatedTotalAtMaturity": 105500000,
  "daysUntilMaturity": 365,
  "totalDaysOfTerm": 365
}
```

**Hiển thị cho người dùng:**
```
Sổ tiết kiệm:       STK-20251216001
Số tiền gửi:        100,000,000 VND
Kỳ hạn:             12 tháng
Lãi suất:           5.5% / năm
Ngày gửi:           16/12/2025
Ngày đáo hạn:       16/12/2026
Trạng thái:         Đang hoạt động

--- Ước tính khi đáo hạn ---
Lãi dự kiến:        5,500,000 VND
Tổng tiền nhận:     105,500,000 VND
Còn lại:            365 ngày
```

---

### 4. Preview Tất Toán (BƯỚC 1 - XEM TRƯỚC)

**Endpoint:** `GET /api/saving/{savingBookNumber}/withdraw-preview`

**Mô tả:** 
- Xem trước thông tin tất toán **KHÔNG thực hiện** giao dịch
- Hiển thị cảnh báo nếu rút trước hạn
- User xem thông tin và quyết định có muốn tất toán không

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response - Trường hợp rút trước hạn:**
```json
{
  "savingBookNumber": "STK-20251216001",
  "principalAmount": 100000000,
  "appliedInterestRate": 0.20,
  "interestEarned": 99726.03,
  "totalAmount": 100099726.03,
  "openedDate": "2025-12-16",
  "withdrawDate": "2026-06-16",
  "daysHeld": 182,
  "isEarlyWithdrawal": true,
  "message": "Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là 0.20%/năm"
}
```

**Response - Trường hợp rút đúng hạn:**
```json
{
  "savingBookNumber": "STK-20251216001",
  "principalAmount": 100000000,
  "appliedInterestRate": 5.50,
  "interestEarned": 5500000,
  "totalAmount": 105500000,
  "openedDate": "2025-12-16",
  "withdrawDate": "2026-12-16",
  "daysHeld": 365,
  "isEarlyWithdrawal": false,
  "message": "Tất toán đúng hạn/sau đáo hạn. Lãi suất áp dụng: 5.50%/năm"
}
```

**Hiển thị cho người dùng (rút trước hạn):**
```
⚠️ CẢNH BÁO: Tất toán trước hạn
Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là 0.20%/năm

Tiền gốc:            100,000,000 VND
Lãi áp dụng:         0.20% / năm (không kỳ hạn)
Số ngày gửi:         182 ngày
Lãi thực nhận:          99,726 VND
Tổng tiền nhận:      100,099,726 VND

[Hủy]  [Xác nhận tất toán]
```

**Ví dụ sử dụng:**
```bash
curl -X GET "http://localhost:8080/api/saving/STK-20251216001/withdraw-preview" \
  -H "Authorization: Bearer <token>"
```

---

### 5. Xác Nhận Tất Toán (BƯỚC 2 - THỰC HIỆN)

**Endpoint:** `POST /api/saving/{savingBookNumber}/withdraw-confirm`

**Mô tả:**
- Thực hiện tất toán SAU KHI user đã xem preview
- Chuyển tiền gốc + lãi về tài khoản checking
- Đánh dấu sổ tiết kiệm là CLOSED
- Ghi transaction

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response thành công (200 OK):**
```json
{
  "savingBookNumber": "STK-20251216001",
  "principalAmount": 100000000,
  "appliedInterestRate": 0.20,
  "interestEarned": 99726.03,
  "totalAmount": 100099726.03,
  "checkingAccountNumber": "1234567890",
  "newCheckingBalance": 200099726.03,
  "openedDate": "2025-12-16",
  "closedDate": "2026-06-16",
  "daysHeld": 182,
  "transactionCode": "WDR-1734307200000-ABC12345",
  "message": "Tất toán trước hạn thành công. Lãi suất áp dụng: 0.20% (không kỳ hạn)"
}
```

**Hiển thị cho người dùng:**
```
✅ Tất toán thành công!

Tiền gốc:            100,000,000 VND
Lãi thực nhận:          99,726 VND
Tổng tiền nhận:      100,099,726 VND

Đã chuyển về TK:     1234567890
Số dư mới:           200,099,726 VND
Mã GD:               WDR-1734307200000-ABC12345
```

**Ví dụ sử dụng:**
```bash
curl -X POST "http://localhost:8080/api/saving/STK-20251216001/withdraw-confirm" \
  -H "Authorization: Bearer <token>"
```

---

## Luồng Hoạt Động Mới

### Luồng 1: Xem Chi Tiết Sổ Tiết Kiệm

1. User vào xem chi tiết sổ tiết kiệm
2. Frontend gọi `GET /api/saving/{savingBookNumber}`
3. Backend tính toán và trả về:
   - Thông tin cơ bản của sổ
   - **Lãi ước tính khi đáo hạn**
   - **Tổng tiền ước tính khi đáo hạn**
   - Số ngày còn lại
4. Hiển thị đầy đủ cho user

### Luồng 2: Tất Toán Sổ Tiết Kiệm (2 bước)

**Bước 1: Preview (Xem trước)**
1. User nhấn nút "Tất toán"
2. Frontend gọi `GET /api/saving/{savingBookNumber}/withdraw-preview`
3. Backend tính toán và trả về:
   - Số tiền sẽ nhận (gốc + lãi)
   - Lãi suất áp dụng
   - **Cảnh báo nếu rút trước hạn**
4. Hiển thị dialog xác nhận với đầy đủ thông tin
5. User có 2 lựa chọn: **Hủy** hoặc **Xác nhận**

**Bước 2: Confirm (Thực hiện)**
6. Nếu user nhấn "Xác nhận"
7. Frontend gọi `POST /api/saving/{savingBookNumber}/withdraw-confirm`
8. Backend:
   - Kiểm tra lại trạng thái
   - Tính lãi thực tế
   - Chuyển tiền về checking
   - Đánh dấu sổ là CLOSED
   - Ghi transaction
9. Hiển thị kết quả thành công

### Luồng 3: Officer Cập Nhật Lãi Suất

1. Officer đăng nhập
2. Vào màn hình quản lý lãi suất
3. Frontend gọi `GET /api/saving/terms` để lấy danh sách hiện tại
4. Officer chỉnh sửa lãi suất
5. Frontend gọi `PUT /api/saving/terms/update-rate`
6. Backend cập nhật lãi suất vào database
7. **Lưu ý:** Chỉ ảnh hưởng đến sổ tiết kiệm MỚI tạo sau khi cập nhật

---

## So Sánh API Cũ vs Mới

| Tính năng | API Cũ | API Mới |
|-----------|---------|---------|
| **Lãi suất** | Hard-code trong enum | Lưu database, Officer quản lý |
| **Chi tiết sổ** | Chỉ thông tin cơ bản | + Lãi ước tính khi đáo hạn |
| **Tất toán** | 1 bước, thực hiện ngay | 2 bước: Preview → Confirm |
| **Cảnh báo rút sớm** | Không có | Có message cảnh báo rõ ràng |
| **Preview** | ❌ Không có | ✅ Có |

---

## Migration và Backward Compatibility

1. **Migration V19** tự động tạo bảng `saving_terms` và insert dữ liệu mặc định
2. Các sổ tiết kiệm cũ vẫn hoạt động bình thường
3. Lãi suất trong enum vẫn được giữ làm fallback

---

## Ví Dụ Tính Lãi Chi Tiết

### Ví dụ 1: Xem chi tiết sổ đang hoạt động

**Request:**
```
GET /api/saving/STK-20251216001
```

**Thông tin:**
- Ngày gửi: 16/12/2025
- Ngày xem: 16/03/2026 (đã gửi 90 ngày)
- Ngày đáo hạn: 16/12/2026 (còn 275 ngày)
- Số tiền: 100,000,000 VND
- Kỳ hạn: 12 tháng (5.5%)

**Lãi ước tính khi đáo hạn:**
```
Lãi = 100,000,000 × 5.5% × 365/365 = 5,500,000 VND
Tổng = 105,500,000 VND
```

### Ví dụ 2: Preview rút trước hạn

**Request:**
```
GET /api/saving/STK-20251216001/withdraw-preview
```

**Thông tin:**
- Ngày gửi: 16/12/2025
- Ngày rút dự kiến: 16/06/2026 (182 ngày)
- Ngày đáo hạn: 16/12/2026 (RÚT TRƯỚC HẠN!)

**Lãi thực tế nếu rút:**
```
Lãi suất áp dụng: 0.20% (không kỳ hạn)
Lãi = 100,000,000 × 0.20% × 182/365 = 99,726 VND
Tổng = 100,099,726 VND

⚠️ Cảnh báo: "Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là 0.20%/năm"
```

---

## API Testing với Postman

### Test 1: Lấy danh sách lãi suất

```
GET http://localhost:8080/api/saving/terms
```

### Test 2: Xem chi tiết sổ với lãi ước tính

```
GET http://localhost:8080/api/saving/STK-20251216001
Authorization: Bearer <token>
```

### Test 3: Preview tất toán

```
GET http://localhost:8080/api/saving/STK-20251216001/withdraw-preview
Authorization: Bearer <token>
```

### Test 4: Confirm tất toán

```
POST http://localhost:8080/api/saving/STK-20251216001/withdraw-confirm
Authorization: Bearer <token>
```

### Test 5: Officer cập nhật lãi suất

```
PUT http://localhost:8080/api/saving/terms/update-rate
Authorization: Bearer <officer_token>
Content-Type: application/json

{
  "termType": "TWELVE_MONTHS",
  "interestRate": 5.8
}
```

---

## Lưu Ý Quan Trọng

1. **Preview không thay đổi dữ liệu:** API preview chỉ đọc, không tạo transaction, không thay đổi số dư

2. **Confirm thực hiện giao dịch:** API confirm mới thực sự chuyển tiền và ghi transaction

3. **Lãi suất động:** Lãi suất được lấy từ database, Officer có thể thay đổi bất cứ lúc nào

4. **Ảnh hưởng của thay đổi lãi suất:**
   - Sổ tiết kiệm CŨ: Giữ nguyên lãi suất khi tạo
   - Sổ tiết kiệm MỚI: Áp dụng lãi suất mới từ database

5. **Message cảnh báo:** 
   - Rút trước hạn: Hiển thị cảnh báo rõ ràng về lãi suất giảm
   - Rút đúng hạn: Thông báo bình thường

6. **Transaction Safety:** Vẫn đảm bảo pessimistic locking và rollback như cũ
