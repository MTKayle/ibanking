# Tóm Tắt Cập Nhật Hệ Thống Tiết Kiệm

## ✅ Hoàn thành các yêu cầu

### 1. Lưu lãi suất vào Database
- ✅ Tạo entity `SavingTermConfig` 
- ✅ Tạo bảng `saving_terms` với migration V19
- ✅ Insert dữ liệu mặc định cho 11 kỳ hạn
- ✅ Officer có thể cập nhật lãi suất qua API

### 2. API Chi Tiết Sổ Tiết Kiệm - Có Lãi Ước Tính
- ✅ `GET /api/saving/{savingBookNumber}` trả về:
  - Thông tin cơ bản của sổ
  - **Lãi ước tính khi đáo hạn** (`estimatedInterestAtMaturity`)
  - **Tổng tiền ước tính** (`estimatedTotalAtMaturity`)
  - Số ngày còn lại đến đáo hạn (`daysUntilMaturity`)

### 3. API Tất Toán - Tách Thành 2 Bước

#### Bước 1: Preview (Xem Trước)
- ✅ `GET /api/saving/{savingBookNumber}/withdraw-preview`
- ✅ Hiển thị thông tin tất toán KHÔNG thực hiện giao dịch
- ✅ Cảnh báo rõ ràng nếu rút trước hạn:
  - `isEarlyWithdrawal: true`
  - `message: "Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là 0.20%/năm"`

#### Bước 2: Confirm (Xác Nhận)
- ✅ `POST /api/saving/{savingBookNumber}/withdraw-confirm`
- ✅ Thực hiện tất toán SAU KHI user xem preview
- ✅ Chuyển tiền và ghi transaction

---

## 📊 Các API Mới

### API Quản Lý Lãi Suất (Officer)

1. **Lấy danh sách lãi suất** (Public)
```
GET /api/saving/terms
```

2. **Cập nhật lãi suất** (Officer only)
```
PUT /api/saving/terms/update-rate
{
  "termType": "TWELVE_MONTHS",
  "interestRate": 5.8
}
```

### API Tiết Kiệm (Đã Cập Nhật)

1. **Chi tiết sổ với lãi ước tính**
```
GET /api/saving/{savingBookNumber}
→ Trả về SavingAccountDetailResponse (có estimatedInterestAtMaturity)
```

2. **Preview tất toán**
```
GET /api/saving/{savingBookNumber}/withdraw-preview
→ Xem trước, KHÔNG thực hiện giao dịch
```

3. **Confirm tất toán**
```
POST /api/saving/{savingBookNumber}/withdraw-confirm
→ Thực hiện giao dịch thực sự
```

---

## 🗂️ Files Đã Tạo/Cập Nhật

### Entities
- ✅ `SavingTermConfig.java` - Entity cho lãi suất
- ✅ `SavingAccount.java` - Cập nhật (không thay đổi lớn)
- ✅ `SavingTerm.java` - Enum kỳ hạn (giữ nguyên)

### Repositories
- ✅ `SavingTermConfigRepository.java` - Repository cho lãi suất
- ✅ `CheckingAccountRepository.java` - Thêm method `findByUser()`

### DTOs
- ✅ `SavingAccountDetailResponse.java` - Response với lãi ước tính
- ✅ `WithdrawPreviewResponse.java` - Response preview tất toán
- ✅ `UpdateSavingTermRateRequest.java` - Request cập nhật lãi suất
- ✅ `ConfirmWithdrawRequest.java` - Request confirm (nếu cần)

### Services
- ✅ `SavingAccountService.java` - Thêm methods:
  - `getSavingAccountDetailWithEstimate()` - Chi tiết + lãi ước tính
  - `previewWithdraw()` - Preview tất toán
  - `confirmWithdraw()` - Confirm tất toán
  - `getAllSavingTerms()` - Lấy danh sách lãi suất
  - `updateTermInterestRate()` - Cập nhật lãi suất
  - `getInterestRateForTerm()` - Helper lấy lãi từ DB

### Controllers
- ✅ `SavingAccountController.java` - Thêm endpoints:
  - `GET /{savingBookNumber}` - Chi tiết + lãi ước tính
  - `GET /{savingBookNumber}/withdraw-preview` - Preview
  - `POST /{savingBookNumber}/withdraw-confirm` - Confirm
  - `GET /terms` - Danh sách lãi suất
  - `PUT /terms/update-rate` - Cập nhật lãi suất

### Migrations
- ✅ `V19__create_saving_terms_table.sql` - Tạo bảng + insert data

### Documentation
- ✅ `SAVING_ACCOUNT_API_GUIDE.md` - Cập nhật đầy đủ

---

## 🔄 Luồng Hoạt Động Mới

### Luồng 1: Xem Chi Tiết Sổ
```
User → GET /api/saving/STK-xxx
Backend tính toán:
  - Lãi ước tính khi đáo hạn = Gốc × Lãi suất % × Số ngày kỳ hạn / 365
  - Tổng tiền ước tính = Gốc + Lãi ước tính
→ Hiển thị cho user
```

### Luồng 2: Tất Toán (2 Bước)
```
Bước 1 - PREVIEW:
User → Nhấn "Tất toán"
Frontend → GET /api/saving/STK-xxx/withdraw-preview
Backend → Tính toán (KHÔNG lưu DB)
  - Kiểm tra rút trước hạn?
  - Áp dụng lãi suất: 0.20% (sớm) hoặc lãi gốc (đúng hạn)
  - Tính lãi = Gốc × Lãi % × Số ngày thực tế / 365
→ Response với message cảnh báo
→ Hiển thị dialog xác nhận

Bước 2 - CONFIRM:
User → Nhấn "Xác nhận"
Frontend → POST /api/saving/STK-xxx/withdraw-confirm
Backend → Thực hiện giao dịch:
  - Lock checking account
  - Chuyển tiền
  - Đánh dấu sổ CLOSED
  - Ghi transaction
→ Response thành công
→ Hiển thị kết quả
```

### Luồng 3: Officer Cập Nhật Lãi Suất
```
Officer → Vào trang quản lý lãi suất
Frontend → GET /api/saving/terms
→ Hiển thị bảng lãi suất hiện tại

Officer → Chỉnh sửa lãi suất kỳ hạn 12 tháng: 5.5% → 5.8%
Frontend → PUT /api/saving/terms/update-rate
Backend → Update vào DB
  - Lưu lại user cập nhật
  - Timestamp
→ Response success

Lưu ý: Chỉ ảnh hưởng sổ MỚI tạo sau này
```

---

## 🎯 Use Cases

### Use Case 1: Customer Xem Lãi Ước Tính
```
Scenario: User muốn biết sẽ nhận được bao nhiêu khi đáo hạn

Given: User có sổ STK-20251216001
  - Số tiền: 100,000,000 VND
  - Kỳ hạn: 12 tháng (5.5%)
  - Đã gửi: 90 ngày
  - Còn lại: 275 ngày

When: User vào xem chi tiết sổ
  GET /api/saving/STK-20251216001

Then: Hiển thị
  - Lãi dự kiến khi đáo hạn: 5,500,000 VND
  - Tổng tiền nhận: 105,500,000 VND
  - Còn 275 ngày nữa
```

### Use Case 2: Customer Tất Toán Trước Hạn
```
Scenario: User cần tiền gấp, tất toán trước hạn

Given: User có sổ STK-20251216001
  - Kỳ hạn 12 tháng
  - Mới gửi 6 tháng (chưa đến hạn)

When: User nhấn "Tất toán"
  GET /api/saving/STK-xxx/withdraw-preview

Then: Hiển thị cảnh báo
  ⚠️ "Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là 0.20%/năm"
  - Lãi chỉ còn: ~100,000 VND (thay vì 2,750,000 VND)
  - Tổng nhận: 100,100,000 VND

When: User nhấn "Xác nhận"
  POST /api/saving/STK-xxx/withdraw-confirm

Then: Thực hiện tất toán
  - Chuyển 100,100,000 VND về checking
  - Đánh dấu sổ CLOSED
```

### Use Case 3: Officer Điều Chỉnh Lãi Suất
```
Scenario: Ngân hàng tăng lãi suất kỳ hạn 12 tháng

Given: Lãi suất hiện tại 12 tháng = 5.5%

When: Officer cập nhật
  PUT /api/saving/terms/update-rate
  {
    "termType": "TWELVE_MONTHS",
    "interestRate": 5.8
  }

Then: 
  - Lưu vào DB: 5.8%
  - Ghi log: "Updated by officer@bank.com at 16/12/2025"
  - Sổ MỚI tạo sau này: áp dụng 5.8%
  - Sổ CŨ đã tạo: vẫn giữ 5.5%
```

---

## 🔐 Bảo Mật & Transaction

### Preview API
- ✅ Read-only, không thay đổi dữ liệu
- ✅ Không tạo transaction
- ✅ Kiểm tra quyền sở hữu
- ✅ Có thể gọi nhiều lần

### Confirm API
- ✅ Pessimistic locking
- ✅ Transaction isolation
- ✅ Rollback nếu có lỗi
- ✅ Chỉ gọi 1 lần (sau khi confirm thì sổ CLOSED)

---

## 📝 Testing Checklist

- [ ] Tạo sổ tiết kiệm mới
- [ ] Xem chi tiết sổ → Kiểm tra lãi ước tính
- [ ] Preview tất toán đúng hạn → Không có cảnh báo
- [ ] Preview tất toán trước hạn → Có cảnh báo
- [ ] Confirm tất toán → Kiểm tra tiền đã chuyển
- [ ] Officer lấy danh sách lãi suất
- [ ] Officer cập nhật lãi suất
- [ ] Tạo sổ mới sau khi cập nhật → Kiểm tra lãi suất mới

---

## 🚀 Ready to Deploy

Tất cả code đã sẵn sàng. Chỉ cần:
1. Restart application (migration sẽ tự chạy)
2. Test với Postman theo guide
3. Tích hợp vào mobile app

**Hệ thống tiết kiệm đã hoàn thiện với đầy đủ tính năng như yêu cầu! ✨**

