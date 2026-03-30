# API Rút Tiền và Cập Nhật Ảnh User

## 1. API Rút Tiền (Withdrawal) - Dành cho OFFICER

### Endpoint
```
POST /api/payment/checking/withdraw
```

### Authorization
- Chỉ **OFFICER** và **ADMIN** mới có quyền truy cập
- Yêu cầu JWT token trong header

### Request Body
```json
{
  "userId": 123,
  "amount": 500000,
  "description": "Rút tiền mặt tại quầy"
}
```

### Response Success (200 OK)
```json
{
  "accountNumber": "1234567890",
  "withdrawAmount": 500000,
  "newBalance": 9500000,
  "description": "Rút tiền mặt tại quầy",
  "timestamp": "2025-12-20T10:30:00Z",
  "message": "Rút tiền thành công 500000.00 từ tài khoản 1234567890. Mã giao dịch: DEP-1734692400000-A1B2C3D4"
}
```

### Response Error (400 Bad Request)
```json
{
  "message": "Số dư không đủ để rút tiền. Số dư hiện tại: 300000"
}
```

### Response Error - No Checking Account (404 Not Found)
```json
{
  "message": "Không tìm thấy tài khoản checking cho user ID: 123"
}
```

### Các bước thực hiện:
1. Sử dụng userId để tìm **tài khoản checking đầu tiên** của user
2. Lock tài khoản với pessimistic lock (SELECT FOR UPDATE) để tránh race condition
3. Kiểm tra trạng thái tài khoản (phải là active)
4. Kiểm tra số dư (phải đủ để rút)
5. Tạo transaction PENDING với type = WITHDRAW
6. Trừ số dư tài khoản
7. Cập nhật transaction thành SUCCESS
8. Ghi lại lịch sử giao dịch

### Lưu ý quan trọng:
- API tự động tìm **tài khoản checking đầu tiên** của user dựa vào userId
- Không cần biết số tài khoản, chỉ cần userId
- Nếu user không có tài khoản checking, sẽ trả về lỗi 404

### Transaction Type
- Transaction type: `WITHDRAW`
- Chỉ có `senderAccount` (người rút tiền)
- Không có `receiverAccount`

---

## 2. API Cập Nhật Ảnh User - Dành cho OFFICER

### Endpoint
```
POST /api/users/{userId}/update-photo
```

### Authorization
- Chỉ **OFFICER** và **ADMIN** mới có quyền truy cập
- Yêu cầu JWT token trong header

### Request Parameters
- **Path Parameter**: `userId` - ID của user (required)
- **Body**: `multipart/form-data` với field `photo` chứa file ảnh

### Example Request (cURL)
```bash
curl -X POST "http://localhost:8089/api/users/123/update-photo" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "photo=@/path/to/image.jpg"
```

### Example Request (Postman)
1. Method: POST
2. URL: `http://localhost:8089/api/users/123/update-photo`
3. Headers:
   - Authorization: Bearer YOUR_JWT_TOKEN
4. Body: form-data
   - Key: `photo` (type: File)
   - Value: Chọn file ảnh

### Response Success (200 OK)
```json
{
  "success": true,
  "message": "Cập nhật ảnh thành công",
  "data": {
    "userId": 123,
    "phone": "0123456789",
    "photoUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1734692400/ibanking/users/123/abc-def-ghi.jpg",
    "message": "Cập nhật ảnh thành công"
  }
}
```

### Response Error - User Not Found (400 Bad Request)
```json
{
  "success": false,
  "message": "Không tìm thấy user với ID: 123"
}
```

### Response Error - No Image (400 Bad Request)
```json
{
  "success": false,
  "message": "Ảnh không được để trống"
}
```

### Các bước thực hiện:
1. Tìm user theo userId
2. Upload ảnh lên Cloudinary với folder structure: `ibanking/users/{userId}/{uuid}`
3. Lấy secure URL từ Cloudinary
4. Cập nhật field `photoUrl` trong bảng `users`
5. Trả về thông tin user và URL ảnh mới

### Lưu ý:
- Ảnh được upload lên Cloudinary tự động
- URL ảnh là secure URL (HTTPS)
- Ảnh được lưu với structure: `ibanking/users/{userId}/{random-uuid}`
- Ảnh cũ không tự động xóa (có thể thêm logic xóa ảnh cũ nếu cần)

---

## Testing với Postman

### 1. Test API Rút Tiền

**Step 1**: Login với tài khoản OFFICER để lấy JWT token

**Step 2**: Tạo request mới:
- Method: POST
- URL: `http://localhost:8089/api/payment/checking/withdraw`
- Headers:
  ```
  Authorization: Bearer YOUR_JWT_TOKEN
  Content-Type: application/json
  ```
- Body (raw JSON):
  ```json
  {
    "userId": 1,
    "amount": 500000,
    "description": "Rút tiền mặt"
  }
  ```

### 2. Test API Cập Nhật Ảnh

**Step 1**: Login với tài khoản OFFICER để lấy JWT token

**Step 2**: Tạo request mới:
- Method: POST
- URL: `http://localhost:8089/api/users/1/update-photo`
- Headers:
  ```
  Authorization: Bearer YOUR_JWT_TOKEN
  ```
- Body (form-data):
  - Key: `photo` (select File type)
  - Value: Chọn file ảnh từ máy tính

---

## Files Created/Modified

### New DTOs:
1. `WithdrawRequest.java` - Request DTO cho rút tiền (đã sửa: dùng userId)
2. `WithdrawResponse.java` - Response DTO cho rút tiền
3. `UpdatePhotoRequest.java` - Request DTO cho cập nhật ảnh (đã sửa: dùng userId)
4. `UpdatePhotoResponse.java` - Response DTO cho cập nhật ảnh

### Modified Services:
1. `PaymentService.java` - Thêm method `withdrawFromCheckingAccount()`
2. `PaymentServiceImpl.java` - Implement logic rút tiền (tìm checking account đầu tiên theo userId)
3. `UserManagementService.java` - Thêm method `updateUserPhoto()` (dùng userId)

### Modified Controllers:
1. `PaymentController.java` - Thêm endpoint `/checking/withdraw`
2. `UserManagementController.java` - Thêm endpoint `/{userId}/update-photo`

---

## Security Notes

1. **API Rút Tiền**:
   - Chỉ OFFICER và ADMIN mới được phép
   - Tự động tìm tài khoản checking đầu tiên của user
   - Sử dụng pessimistic lock để tránh race condition
   - Kiểm tra số dư trước khi rút
   - Ghi lại transaction với trạng thái (PENDING -> SUCCESS/FAILED)

2. **API Cập Nhật Ảnh**:
   - Chỉ OFFICER và ADMIN mới được phép
   - Sử dụng userId thay vì phone number
   - Ảnh được upload lên Cloudinary (cloud storage)
   - URL ảnh được lưu trong database
   - Validate file có tồn tại trước khi upload

---

## Error Handling

Cả hai API đều có xử lý lỗi đầy đủ:
- Validation errors (400 Bad Request)
- Resource not found (404 Not Found)
- Insufficient balance (400 Bad Request)
- Server errors (500 Internal Server Error)
- Transaction rollback nếu có lỗi xảy ra

---

## Key Changes (So với phiên bản trước)

### API Rút Tiền:
- **THAY ĐỔI**: Request nhận `userId` thay vì `accountNumber`
- **LÝ DO**: Đơn giản hóa việc gọi API, không cần biết số tài khoản
- **LOGIC**: Hệ thống tự động tìm tài khoản checking đầu tiên của user
- **TRUY VẤN**: Sử dụng `findFirstByUserId()` trong CheckingAccountRepository

### API Cập Nhật Ảnh:
- **THAY ĐỔI**: Endpoint từ `/update-photo?phone=xxx` thành `/{userId}/update-photo`
- **LÝ DO**: Sử dụng userId làm định danh chính, phù hợp với RESTful design
- **LOGIC**: Tìm user theo userId thay vì phone number
