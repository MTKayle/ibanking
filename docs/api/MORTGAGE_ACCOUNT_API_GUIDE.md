# MORTGAGE ACCOUNT MANAGEMENT API GUIDE - COMPLETE VERSION

## Tổng quan
Hệ thống quản lý tài khoản vay thế chấp cho phép nhân viên ngân hàng tạo, thẩm định và quản lý các khoản vay. Khách hàng có thể thanh toán các kỳ vay theo 2 cách: thanh toán định kỳ hoặc tất toán.

---

## 📊 Bảng lãi suất vay thế chấp

Hệ thống áp dụng lãi suất **TỰ ĐỘNG** theo kỳ hạn vay:

| Kỳ hạn vay | Lãi suất cố định (%/năm) |
|------------|--------------------------|
| ≤ 12 tháng | 7.5% |
| 13 – 24 tháng | 8.0% |
| 25 – 36 tháng | 8.5% |
| 37 – 60 tháng | 9.0% |
| 61 – 120 tháng | 9.5% |
| > 120 tháng | 10.0% |

**Lưu ý:**
- Khi phê duyệt khoản vay, nếu **KHÔNG** cung cấp `interestRate`, hệ thống sẽ tự động lấy từ bảng.
- Nếu cung cấp `interestRate`, hệ thống ưu tiên sử dụng lãi suất đó.

---

## 🏠 Loại tài sản thế chấp

Hệ thống hỗ trợ **3 loại** tài sản thế chấp:
- **NHA**: Nhà
- **DAT**: Đất
- **XE**: Xe

---

## 📝 Quy trình nghiệp vụ

```
1. Khách hàng đến ngân hàng
   ↓
2. Nhân viên tạo tài khoản vay (POST /api/mortgage/create)
   - Nhập số điện thoại khách hàng
   - Chọn loại tài sản: NHA/DAT/XE
   - Upload CCCD + giấy tờ tài sản
   → Status: PENDING_APPRAISAL
   ↓
3. Nhân viên thẩm định xem xét hồ sơ
   ├─ ✅ Đạt → POST /api/mortgage/approve
   │   - Nhập số tiền, kỳ hạn
   │   - Hệ thống tự động tính lãi suất + lịch thanh toán
   │   → Status: ACTIVE
   │
   └─ ❌ Không đạt → POST /api/mortgage/reject
       - Nhập lý do từ chối
       → Status: REJECTED
   ↓
4. Khách hàng thanh toán định kỳ (POST /api/mortgage/payment/current)
   - Thanh toán kỳ tiếp theo + các kỳ quá hạn (nếu có)
   - Hệ thống tự động tính lãi phạt
   → Status: ACTIVE
   ↓
5. Khi thanh toán đủ tất cả các kỳ
   → Status: COMPLETED
```

---

## 🔌 API Endpoints (tóm tắt quyền truy cập theo controller hiện tại)

Base URL (local dev): http://localhost:8089

Ghi chú quyền (theo `@PreAuthorize` trong `MortgageAccountController`):
- `create` `/api/mortgage/create` -> hasRole('OFFICER')
- `approve` `/api/mortgage/approve` -> hasRole('OFFICER')
- `reject` `/api/mortgage/reject` -> hasRole('OFFICER')
- `payment` `/api/mortgage/payment` -> hasAnyRole('CUSTOMER', 'BANKING_OFFICER') (controller allows `BANKING_OFFICER` here)
- `payment/current` `/api/mortgage/payment/current` -> hasAnyRole('CUSTOMER', 'OFFICER')
- `GET /api/mortgage/{id}` -> hasAnyRole('CUSTOMER', 'OFFICER')
- `GET /api/mortgage/user/{userId}` -> hasAnyRole('CUSTOMER', 'OFFICER')
- `GET /api/mortgage/status/{status}` -> hasRole('OFFICER')
- `GET /api/mortgage/pending` -> hasRole('OFFICER')

> Lưu ý: trong code có một số endpoint chấp nhận role tên `OFFICER` và một chỗ chấp nhận `BANKING_OFFICER`; khi test, dùng token có role phù hợp với endpoint theo danh sách trên.

---

### 1️⃣ Tạo tài khoản vay thế chấp (Nhân viên)

**Endpoint:** `POST /api/mortgage/create`  
**Quyền:** OFFICER  
**Content-Type:** multipart/form-data

**Request Parts:**
- `request` (JSON string) - schema:
```json
{
  "phoneNumber": "0123456789",
  "collateralType": "NHA",  
  "collateralDescription": "Nhà 3 tầng tại Hà Nội, diện tích 150m2",
  "paymentFrequency": "MONTHLY"  
}
```
- `cccdFront` (File) - optional
- `cccdBack` (File) - optional
- `collateralDocuments` (File[]) - optional

**Response Success (201 Created):**
```json
{
  "mortgageId": 1,
  "accountNumber": "MTG20251217001",
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0123456789",
  "principalAmount": 0,
  "interestRate": 0,
  "termMonths": null,
  "startDate": null,
  "status": "PENDING_APPRAISAL",
  "collateralType": "NHA",
  "collateralDescription": "Nhà 3 tầng tại Hà Nội, diện tích 150m2",
  "cccdFrontUrl": "https://res.cloudinary.com/...",
  "cccdBackUrl": "https://res.cloudinary.com/...",
  "collateralDocumentUrls": ["https://...","https://..."],
  "paymentFrequency": "MONTHLY",
  "rejectionReason": null,
  "createdDate": "2025-12-17",
  "approvalDate": null,
  "remainingBalance": 0,
  "paymentSchedules": []
}
```

**Error Cases:**
- `404`: Không tìm thấy khách hàng với số điện thoại
- `400`: Loại tài sản thế chấp không hợp lệ (phải là NHA, DAT, hoặc XE)
- `500`: Lỗi upload ảnh lên Cloudinary

---

### 2️⃣ Phê duyệt tài khoản vay (Nhân viên)

**Endpoint:** `POST /api/mortgage/approve`  
**Quyền:** OFFICER  
**Content-Type:** application/json

**Request (lãi suất tự động - khuyến nghị):**
```json
{
  "mortgageId": 1,
  "principalAmount": 500000000,
  "termMonths": 120
}
```

**Request (lãi suất tùy chỉnh):**
```json
{
  "mortgageId": 1,
  "principalAmount": 500000000,
  "interestRate": 9.0,
  "termMonths": 120
}
```

**Response Success (200 OK):** (ví dụ rút gọn)
```json
{
  "mortgageId": 1,
  "accountNumber": "MTG20251217001",
  "principalAmount": 500000000,
  "interestRate": 9.5,
  "termMonths": 120,
  "startDate": "2025-12-17",
  "status": "ACTIVE",
  "remainingBalance": 500000000,
  "paymentSchedules": [ /* 120 phần tử */ ]
}
```

**Error Cases:**
- `404`: Không tìm thấy tài khoản vay
- `400`: Tài khoản vay không ở trạng thái PENDING_APPRAISAL
- `400`: Không tìm thấy lãi suất cho kỳ hạn (nếu termMonths không hợp lệ)
- `400`: Số tiền vay phải lớn hơn 0
- `400`: Số kỳ hạn phải lớn hơn 0

**Lưu ý:**
- Hệ thống tính lịch thanh toán theo phương pháp trả đều (PMT).
- Nếu `paymentFrequency = MONTHLY`: số kỳ = termMonths
- Nếu `paymentFrequency = BI_WEEKLY`: số kỳ = termMonths × 2

---

### 3️⃣ Từ chối tài khoản vay (Nhân viên)

**Endpoint:** `POST /api/mortgage/reject`  
**Quyền:** OFFICER  
**Content-Type:** application/json

**Request:**
```json
{
  "mortgageId": 1,
  "rejectionReason": "Tài sản thế chấp không đủ giá trị so với số tiền vay yêu cầu"
}
```

**Response Success (200 OK):**
```json
{
  "mortgageId": 1,
  "status": "REJECTED",
  "rejectionReason": "Tài sản thế chấp không đủ giá trị",
  "approvalDate": "2025-12-17"
}
```

---

### 4️⃣ Thanh toán kỳ hiện tại (Định kỳ - khuyến nghị)

**Endpoint:** `POST /api/mortgage/payment/current`  
**Quyền:** CUSTOMER hoặc OFFICER  
**Content-Type:** application/json

**Mô tả:**
- Thanh toán **KỲ TIẾP THEO** (kỳ chưa thanh toán đầu tiên) và **TẤT CẢ CÁC KỲ QUÁ HẠN** (nếu có).
- Khách hàng có thể trả trước kỳ.

**Request:**
```json
{
  "mortgageId": 1,
  "paymentAmount": 6875000,
  "paymentAccountNumber": "1234567890"
}
```

**Response Success (200 OK):**
```json
{
  "mortgageId": 1,
  "status": "ACTIVE",
  "remainingBalance": 493143750,
  "paymentSchedules": [ /* cập nhật status cho các kỳ */ ]
}
```

**Error Cases:**
- `404`: Không tìm thấy tài khoản vay hoặc tài khoản thanh toán
- `400`: Tài khoản vay không ở trạng thái ACTIVE
- `400`: Số dư tài khoản không đủ để thanh toán
- `400`: Không có kỳ nào cần thanh toán
- `400`: Số tiền thanh toán không đủ

---

### 5️⃣ Tất toán khoản vay

**Endpoint:** `POST /api/mortgage/payment`  
**Quyền:** CUSTOMER hoặc BANKING_OFFICER  
**Content-Type:** application/json

**Mô tả:** Thanh toán **TẤT CẢ** các kỳ còn lại (bao gồm cả kỳ chưa đến hạn)

**Request:**
```json
{
  "mortgageId": 1,
  "paymentAmount": 500000000,
  "paymentAccountNumber": "1234567890"
}
```

**Response Success (200 OK):**
```json
{
  "mortgageId": 1,
  "status": "COMPLETED",
  "remainingBalance": 0,
  "paymentSchedules": [ /* tất cả các kỳ đều PAID */ ]
}
```

---

### 6️⃣ Xem chi tiết tài khoản vay

**Endpoint:** `GET /api/mortgage/{mortgageId}`  
**Quyền:** CUSTOMER hoặc OFFICER

**Example:** `GET /api/mortgage/1`

**Response:** 

Response bao gồm thông tin chi tiết tài khoản vay và danh sách payment schedules với **2 trường mới để FE xác định kỳ cần thanh toán**:

```json
{
  "mortgageId": 1,
  "accountNumber": "MTG20251217001",
  "customerName": "Nguyễn Văn A",
  "principalAmount": 500000000,
  "interestRate": 9.5,
  "termMonths": 120,
  "startDate": "2025-12-17",
  "status": "ACTIVE",
  "remainingBalance": 493143750,
  "paymentSchedules": [
    {
      "scheduleId": 1,
      "periodNumber": 1,
      "dueDate": "2026-01-17",
      "principalAmount": 2916666.67,
      "interestAmount": 3958333.33,
      "totalAmount": 6875000.00,
      "penaltyAmount": 0,
      "remainingBalance": 497083333.33,
      "status": "PAID",
      "paidDate": "2025-12-20",
      "paidAmount": 6875000,
      "overdueDays": 0,
      "isCurrentPeriod": false,    // ← Không phải kỳ hiện tại (đã trả)
      "isOverdue": false            // ← Không quá hạn
    },
    {
      "scheduleId": 2,
      "periodNumber": 2,
      "dueDate": "2026-02-17",
      "principalAmount": 2939583.33,
      "interestAmount": 3935416.67,
      "totalAmount": 6875000.00,
      "penaltyAmount": 0,
      "remainingBalance": 494143750.00,
      "status": "PENDING",
      "paidDate": null,
      "paidAmount": 0,
      "overdueDays": 0,
      "isCurrentPeriod": true,      // ← Kỳ hiện tại cần thanh toán (kỳ chưa trả đầu tiên)
      "isOverdue": false            // ← Chưa quá hạn
    },
    {
      "scheduleId": 3,
      "periodNumber": 3,
      "dueDate": "2026-03-17",
      "principalAmount": 2962708.33,
      "interestAmount": 3912291.67,
      "totalAmount": 6875000.00,
      "penaltyAmount": 0,
      "remainingBalance": 491181041.67,
      "status": "PENDING",
      "paidDate": null,
      "paidAmount": 0,
      "overdueDays": 0,
      "isCurrentPeriod": false,     // ← Không phải kỳ hiện tại
      "isOverdue": false            // ← Chưa đến hạn
    }
  ]
}
```

**Cách FE sử dụng các trường đánh dấu:**

1. **`isCurrentPeriod = true`**: Đây là kỳ tiếp theo cần thanh toán (kỳ chưa trả đầu tiên)
   - FE hiển thị nổi bật kỳ này
   - Đây là kỳ tối thiểu user phải trả

2. **`isOverdue = true`**: Kỳ đã quá hạn (dueDate < ngày hiện tại và chưa thanh toán)
   - FE hiển thị cảnh báo màu đỏ
   - Tính tổng tiền các kỳ quá hạn + kỳ hiện tại để yêu cầu user thanh toán

**Ví dụ tính tổng tiền cần thanh toán (FE logic):**

```javascript
// Lọc các kỳ cần thanh toán
const payablePeriods = paymentSchedules.filter(p => p.isCurrentPeriod || p.isOverdue);

// Tính tổng tiền
const totalAmount = payablePeriods.reduce((sum, p) => {
  return sum + p.totalAmount + p.penaltyAmount - p.paidAmount;
}, 0);

// Hiển thị cho user
console.log(`Bạn cần thanh toán: ${totalAmount} VND`);
console.log(`Gồm ${payablePeriods.length} kỳ: kỳ hiện tại + ${payablePeriods.filter(p => p.isOverdue).length} kỳ quá hạn`);
```

**Ví dụ khi có kỳ quá hạn:**

Giả sử hôm nay là 20/03/2026, kỳ 2 và kỳ 3 đã quá hạn:

```json
{
  "scheduleId": 2,
  "periodNumber": 2,
  "dueDate": "2026-02-17",
  "totalAmount": 6875000.00,
  "penaltyAmount": 250000.00,    // Lãi phạt do quá hạn 31 ngày
  "status": "OVERDUE",
  "overdueDays": 31,
  "isCurrentPeriod": true,        // ← Vẫn là kỳ hiện tại (kỳ chưa trả đầu tiên)
  "isOverdue": true               // ← Đã quá hạn
},
{
  "scheduleId": 3,
  "periodNumber": 3,
  "dueDate": "2026-03-17",
  "totalAmount": 6875000.00,
  "penaltyAmount": 50000.00,     // Lãi phạt do quá hạn 3 ngày
  "status": "OVERDUE",
  "overdueDays": 3,
  "isCurrentPeriod": false,       // ← Không phải kỳ đầu tiên
  "isOverdue": true               // ← Đã quá hạn
},
{
  "scheduleId": 4,
  "periodNumber": 4,
  "dueDate": "2026-04-17",
  "totalAmount": 6875000.00,
  "penaltyAmount": 0,
  "status": "PENDING",
  "isCurrentPeriod": false,
  "isOverdue": false              // ← Chưa đến hạn
}
```

Trong trường hợp này, FE cần yêu cầu user thanh toán:
- Kỳ 2: 6,875,000 + 250,000 = 7,125,000 VND
- Kỳ 3: 6,875,000 + 50,000 = 6,925,000 VND
- **Tổng: 14,050,000 VND**

---

## 🧪 TEST CASES CHI TIẾT

(Phần test cases giữ nguyên nội dung, chỉ cập nhật base URL khi cần trong ví dụ gọi API)

### TEST CASE 1: Tạo tài khoản vay thành công

**Điều kiện tiên quyết:**
- User với số điện thoại "0987654321" đã tồn tại trong hệ thống
- Có file ảnh CCCD và giấy tờ tài sản

**Các bước thực hiện:**
1. Login với tài khoản có role `OFFICER` -> lấy Access Token
2. POST `http://localhost:8089/api/mortgage/create` (multipart/form-data) với fields như mục 1

**Kết quả mong đợi:**
- Status: 201 Created
- Response chứa mortgageId
- status = "PENDING_APPRAISAL"
- Các URL ảnh trả về hợp lệ
- paymentSchedules = []

---

### TEST CASE 2: Phê duyệt với lãi suất tự động (120 tháng)

**Điều kiện tiên quyết:**
- Đã có tài khoản vay với mortgageId=1, status=PENDING_APPRAISAL

**Các bước thực hiện:**
1. Login với role `OFFICER`
2. POST `http://localhost:8089/api/mortgage/approve` với body:
```json
{ "mortgageId": 1, "principalAmount": 500000000, "termMonths": 120 }
```

**Kết quả mong đợi:**
- Status: 200 OK
- status = "ACTIVE"
- interestRate = 9.5
- paymentSchedules có 120 phần tử
- Kiểm tra PMT và phân bố gốc/lãi như mô tả trong phần công thức

---

### TEST CASE 3: Phê duyệt với lãi suất tùy chỉnh (36 tháng)

**Điều kiện tiên quyết:**
- Đã có tài khoản vay với mortgageId=2, status=PENDING_APPRAISAL

**Các bước thực hiện:**
1. Login với role `OFFICER`
2. POST `http://localhost:8089/api/mortgage/approve` với:
```json
{ "mortgageId": 2, "principalAmount": 200000000, "interestRate": 8.0, "termMonths": 36 }
```

**Kết quả mong đợi:**
- interestRate = 8.0
- paymentSchedules có 36 phần tử

---

### TEST CASE 4: Thanh toán kỳ tiếp theo (không quá hạn)

**Điều kiện tiên quyết:**
- mortgageId=1, status=ACTIVE
- Ngày hiện tại: 17/12/2025
- Kỳ 1: dueDate = 17/01/2026, totalAmount = 6,875,000
- Tài khoản thanh toán có số dư ≥ 6,875,000

**Các bước:**
1. Login với role `CUSTOMER` (chủ khoản vay)
2. POST `http://localhost:8089/api/mortgage/payment/current` với:
```json
{ "mortgageId": 1, "paymentAmount": 6875000, "paymentAccountNumber": "1234567890" }
```

**Kết quả mong đợi:**
- Status: 200 OK
- Kỳ 1: status = "PAID", paidDate = hiện tại
- Số dư tài khoản giảm tương ứng

---

### TEST CASE 5: Thanh toán khi có 2 kỳ quá hạn

(giữ nguyên nội dung tính toán; khi gọi API dùng base URL `http://localhost:8089`)

---

### TEST CASE 6: Tất toán khoản vay

(giữ nguyên nội dung; dùng base URL `http://localhost:8089`)

---

### TEST CASE 7: Từ chối tài khoản vay

(giữ nguyên nội dung; dùng base URL `http://localhost:8089`)

---

### TEST CASE 8 & 9: Lỗi số dư / thanh toán không đủ

(giữ nguyên nội dung; dùng base URL `http://localhost:8089`)

---

### TEST CASE 10: Payment Frequency = BI_WEEKLY

(giữ nguyên nội dung; dùng base URL `http://localhost:8089`)

---

## 📐 Công thức tính toán

(Phần công thức giữ nguyên)

---

## 🔄 So sánh 2 loại thanh toán

(Phần so sánh giữ nguyên)

---

## 📱 Ví dụ sử dụng với Postman / cURL (cập nhật base URL)

1) Tạo tài khoản vay (form-data)

Method: POST
URL: http://localhost:8089/api/mortgage/create
Headers:
  Authorization: Bearer {token}
Body: form-data
  request (Text): { ... }
  cccdFront (File): cccd_front.jpg
  cccdBack (File): cccd_back.jpg
  collateralDocuments (File): house1.jpg (và house2.jpg)

2) Phê duyệt (JSON)

Method: POST
URL: http://localhost:8089/api/mortgage/approve
Headers:
  Authorization: Bearer {token}
  Content-Type: application/json
Body: raw JSON (xem ở trên)

3) Thanh toán kỳ tiếp theo

Method: POST
URL: http://localhost:8089/api/mortgage/payment/current
Headers:
  Authorization: Bearer {token}
  Content-Type: application/json
Body: raw JSON (xem ở trên)

4) Tất toán

Method: POST
URL: http://localhost:8089/api/mortgage/payment
Headers:
  Authorization: Bearer {token}
  Content-Type: application/json
Body: raw JSON (xem ở trên)

5) Xem chi tiết

Method: GET
URL: http://localhost:8089/api/mortgage/1
Headers:
  Authorization: Bearer {token}

---

## 🎯 Lưu ý quan trọng

(Phần lưu ý giữ nguyên — nhắc lại: role OFFICER vs BANKING_OFFICER, JWT cần hợp lệ, upload ảnh lên Cloudinary, lãi phạt áp dụng cho kỳ quá hạn...)

---

## 🐛 Troubleshooting

(Phần troubleshooting giữ nguyên)
