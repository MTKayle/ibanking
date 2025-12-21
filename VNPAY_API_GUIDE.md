# VNPay Deposit API Guide

## Tổng quan

API nạp tiền qua VNPay cho phép người dùng nạp tiền vào tài khoản checking thông qua cổng thanh toán VNPay Sandbox.

## API Endpoints

### 1. Tạo link thanh toán VNPay

**Endpoint:** `POST /api/vnpay/create-payment`

**Authorization:** Bearer Token (CUSTOMER, OFFICER, ADMIN)

**Request Body:**
```json
{
    "amount": 100000,
    "orderInfo": "Nap tien vao tai khoan",
    "bankCode": "NCB",
    "language": "vn"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| amount | BigDecimal | ✅ Yes | Số tiền nạp (tối thiểu 10,000 VND) |
| orderInfo | String | ❌ No | Thông tin đơn hàng (tự động tạo nếu không truyền) |
| bankCode | String | ❌ No | Mã ngân hàng (NCB, VNPAYQR, etc.). Nếu không truyền, người dùng sẽ chọn trên cổng VNPay |
| language | String | ❌ No | Ngôn ngữ: "vn" hoặc "en" (mặc định "vn") |

**Response (Success):**
```json
{
    "success": true,
    "message": "Tạo link thanh toán thành công",
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=10000000&vnp_Command=pay&...",
    "txnRef": "12345678123456789",
    "amount": 100000,
    "accountNumber": "1234567890",
    "createdAt": "2025-12-21T10:30:00"
}
```

**Response (Error):**
```json
{
    "success": false,
    "message": "Không tìm thấy tài khoản checking"
}
```

---

### 2. VNPay Callback (Tự động gọi bởi VNPay)

**Endpoint:** `GET /api/vnpay/callback`

**Authorization:** Không cần (VNPay gọi trực tiếp)

VNPay sẽ redirect về URL này sau khi người dùng thanh toán xong.

**Response (Success):**
```json
{
    "success": true,
    "message": "Nạp tiền thành công",
    "txnRef": "12345678123456789",
    "vnpTransactionNo": "14428618",
    "amount": 100000,
    "accountNumber": "1234567890",
    "bankCode": "NCB",
    "status": "SUCCESS",
    "payDate": "2025-12-21T10:35:00"
}
```

**Response (Error):**
```json
{
    "success": false,
    "message": "Khách hàng hủy giao dịch.",
    "status": "FAILED"
}
```

---

## Luồng thanh toán

```
┌─────────────────────────────────────────────────────────────────────┐
│  1. FE gọi POST /api/vnpay/create-payment với số tiền               │
│                          ↓                                          │
│  2. BE trả về paymentUrl                                            │
│                          ↓                                          │
│  3. FE redirect/open paymentUrl để người dùng thanh toán            │
│                          ↓                                          │
│  4. Người dùng nhập thông tin thẻ và OTP trên trang VNPay           │
│                          ↓                                          │
│  5. VNPay redirect về /api/vnpay/callback                           │
│                          ↓                                          │
│  6. BE xử lý callback:                                              │
│     - Verify chữ ký                                                 │
│     - Cộng tiền vào tài khoản                                       │
│     - Lưu transaction vào bảng transactions                         │
│                          ↓                                          │
│  7. Giao dịch hoàn tất - User có thể xem trong lịch sử giao dịch    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Test với VNPay Sandbox

### Thông tin thẻ test:

| Field | Value |
|-------|-------|
| **Ngân hàng** | NCB |
| **Số thẻ** | 9704198526191432198 |
| **Tên chủ thẻ** | NGUYEN VAN A |
| **Ngày phát hành** | 07/15 |
| **Mật khẩu OTP** | 123456 |

---

## Test với Postman

### Bước 1: Đăng nhập lấy token

```
POST http://localhost:8089/api/auth/login
Content-Type: application/json

{
    "phone": "0123456789",
    "password": "password123"
}
```

Lấy `accessToken` từ response.

### Bước 2: Tạo link thanh toán

```
POST http://localhost:8089/api/vnpay/create-payment
Authorization: Bearer {accessToken}
Content-Type: application/json

{
    "amount": 100000,
    "bankCode": "NCB"
}
```

### Bước 3: Mở paymentUrl trong trình duyệt

Copy `paymentUrl` từ response và mở trong trình duyệt.

### Bước 4: Nhập thông tin thẻ test

- Chọn ngân hàng NCB
- Nhập số thẻ: `9704198526191432198`
- Tên: `NGUYEN VAN A`
- Ngày phát hành: `07/15`
- Nhấn tiếp tục và nhập OTP: `123456`

### Bước 5: Xác nhận kết quả

Sau khi thanh toán thành công, VNPay sẽ redirect về callback URL và tiền sẽ được cộng vào tài khoản.

Kiểm tra lịch sử giao dịch:
```
GET http://localhost:8089/api/transactions/my-transactions
Authorization: Bearer {accessToken}
```

---

## VNPay Response Codes

| Code | Description |
|------|-------------|
| 00 | Giao dịch thành công |
| 07 | Trừ tiền thành công, giao dịch bị nghi ngờ |
| 09 | Thẻ/Tài khoản chưa đăng ký Internet Banking |
| 10 | Xác thực thông tin thẻ/tài khoản sai quá 3 lần |
| 11 | Hết hạn chờ thanh toán |
| 12 | Thẻ/Tài khoản bị khóa |
| 13 | Nhập sai mật khẩu OTP |
| 24 | Khách hàng hủy giao dịch |
| 51 | Tài khoản không đủ số dư |
| 65 | Vượt quá hạn mức giao dịch trong ngày |
| 75 | Ngân hàng thanh toán đang bảo trì |
| 79 | Nhập sai mật khẩu thanh toán quá số lần quy định |
| 99 | Các lỗi khác |

---

## Cấu hình VNPay (Sandbox)

```properties
# application.properties
vnpay.tmn-code=S0AUOO0R
vnpay.hash-secret=M5ALW73FN5F89ZV97KUMKWMKD212IJWL
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8089/api/vnpay/callback
vnpay.version=2.1.0
vnpay.command=pay
vnpay.order-type=other
```

---

## Lưu ý

1. **Callback URL**: Trong môi trường production, callback URL phải là HTTPS và accessible từ internet.

2. **Test trên mobile**: Nếu test trên mobile app, cần mở WebView để hiển thị trang thanh toán VNPay.

3. **Xem lịch sử nạp tiền**: Sau khi nạp tiền thành công, giao dịch sẽ được lưu vào bảng `transactions` với:
   - `transaction_type`: `DEPOSIT`
   - `description`: `Nạp tiền từ VNPay`
   - `status`: `SUCCESS`

4. **Số tiền tối thiểu**: VNPay yêu cầu số tiền tối thiểu là 10,000 VND.

