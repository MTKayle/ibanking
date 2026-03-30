# Fingerprint Login API Guide

## Giới thiệu
Tài liệu này hướng dẫn sử dụng các API để bật/tắt đăng nhập bằng vân tay và kiểm tra trạng thái cài đặt này.

## Các API endpoints

### 1. Kiểm tra xem số điện thoại có bật đăng nhập bằng vân tay không (Dùng cho màn hình đăng nhập - KHÔNG CẦN TOKEN)

**Endpoint:** `GET /api/auth/check-fingerprint-enabled?phone={phoneNumber}`

**Mô tả:** Kiểm tra xem một số điện thoại có bật tính năng đăng nhập bằng vân tay hay không. API này **không cần authentication**, sử dụng cho màn hình đăng nhập.

**Quyền truy cập:** Public - không cần token

**Query Parameters:**
- `phone` (required): Số điện thoại cần kiểm tra

**Response thành công (200 OK):**
```json
{
  "enabled": true
}
```

**Response khi số điện thoại không tồn tại hoặc chưa bật:**
```json
{
  "enabled": false
}
```

**Ví dụ sử dụng:**
```bash
curl -X GET "http://localhost:8080/api/auth/check-fingerprint-enabled?phone=0123456789"
```

**Use Case:**
- Client nhập số điện thoại
- Gọi API này để kiểm tra xem số điện thoại có bật fingerprint không
- Nếu `enabled: true`, hiển thị nút đăng nhập bằng vân tay
- Nếu `enabled: false`, chỉ hiển thị form đăng nhập bình thường

---

### 2. Kiểm tra xem user có bật đăng nhập bằng vân tay không (Dùng trong app - CẦN TOKEN)

**Endpoint:** `GET /api/users/{userId}/features/fingerprint-login`

**Mô tả:** Kiểm tra xem một user có bật tính năng đăng nhập bằng vân tay hay không. API này cần authentication, dùng khi user đã đăng nhập.

**Quyền truy cập:**
- OFFICER: có thể kiểm tra cho bất kỳ user nào
- CUSTOMER: chỉ có thể kiểm tra cho chính mình

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response thành công (200 OK):**
```json
{
  "enabled": true
}
```

**Ví dụ sử dụng:**
```bash
curl -X GET "http://localhost:8080/api/users/1/features/fingerprint-login" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

### 3. Bật/Tắt đăng nhập bằng vân tay (và các cài đặt khác)

**Endpoint:** `PATCH /api/users/{userId}/settings`

**Mô tả:** Cập nhật các cài đặt bao gồm:
- `fingerprintLoginEnabled`: Bật/tắt đăng nhập bằng vân tay
- `faceRecognitionEnabled`: Bật/tắt đăng nhập bằng nhận diện khuôn mặt
- `smartEkycEnabled`: Bật/tắt Smart eKYC

**Quyền truy cập:**
- OFFICER: có thể cập nhật cho bất kỳ user nào
- CUSTOMER: chỉ có thể cập nhật cho chính mình

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "fingerprintLoginEnabled": true
}
```

Hoặc cập nhật nhiều cài đặt cùng lúc:
```json
{
  "fingerprintLoginEnabled": true,
  "faceRecognitionEnabled": false,
  "smartEkycEnabled": true
}
```

**Note:** Bạn chỉ cần gửi các field muốn cập nhật, không cần gửi tất cả.

**Response thành công (200 OK):**
```json
{
  "userId": 1,
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phone": "0123456789",
  "dateOfBirth": "1990-01-01",
  "cccdNumber": "001234567890",
  "permanentAddress": "123 ABC Street",
  "temporaryAddress": "456 XYZ Street",
  "photoUrl": "https://example.com/photo.jpg",
  "role": "customer",
  "isLocked": false,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

**Ví dụ sử dụng:**

Bật đăng nhập bằng vân tay:
```bash
curl -X PATCH "http://localhost:8080/api/users/1/settings" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"fingerprintLoginEnabled": true}'
```

Tắt đăng nhập bằng vân tay:
```bash
curl -X PATCH "http://localhost:8080/api/users/1/settings" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"fingerprintLoginEnabled": false}'
```

---

## Kiểm tra với Postman

### 1. Kiểm tra trạng thái fingerprint login (Không cần token - dùng cho đăng nhập)

1. Tạo request mới trong Postman
2. Method: `GET`
3. URL: `http://localhost:8080/api/auth/check-fingerprint-enabled?phone=0123456789`
4. **KHÔNG CẦN** Headers Authorization
5. Click **Send**

### 2. Kiểm tra trạng thái fingerprint login (Cần token - dùng trong app)

1. Tạo request mới trong Postman
2. Method: `GET`
3. URL: `http://localhost:8080/api/users/{userId}/features/fingerprint-login`
4. Headers:
   - Key: `Authorization`
   - Value: `Bearer <your_access_token>`
5. Click **Send**

### 3. Bật/Tắt fingerprint login

1. Tạo request mới trong Postman
2. Method: `PATCH`
3. URL: `http://localhost:8080/api/users/{userId}/settings`
4. Headers:
   - Key: `Authorization`, Value: `Bearer <your_access_token>`
   - Key: `Content-Type`, Value: `application/json`
5. Body → raw → JSON:
```json
{
  "fingerprintLoginEnabled": true
}
```
6. Click **Send**

---

## Luồng hoạt động (Flow)

### Luồng 1: Đăng nhập lần đầu
1. User nhập số điện thoại
2. App gọi API `GET /api/auth/check-fingerprint-enabled?phone={phone}`
3. Nếu `enabled: false`, hiển thị form đăng nhập bình thường
4. User đăng nhập bằng mật khẩu
5. Sau khi đăng nhập thành công, app có thể gợi ý user bật fingerprint login

### Luồng 2: User bật fingerprint login
1. User đã đăng nhập, vào Settings
2. App gọi API `GET /api/users/{userId}/features/fingerprint-login` để lấy trạng thái hiện tại
3. User bật/tắt toggle
4. App gọi API `PATCH /api/users/{userId}/settings` với `{"fingerprintLoginEnabled": true}`

### Luồng 3: Đăng nhập với fingerprint (lần sau)
1. User nhập số điện thoại
2. App gọi API `GET /api/auth/check-fingerprint-enabled?phone={phone}`
3. Nếu `enabled: true`, hiển thị nút fingerprint
4. User chạm vào nút fingerprint
5. App sử dụng fingerprint API của thiết bị để xác thực
6. Sau khi xác thực thành công, app tự động đăng nhập (sử dụng token đã lưu hoặc login API)

---

## Lưu ý quan trọng

1. **Database Migration:** Đã tạo migration file `V16__add_fingerprint_login_enabled_to_users.sql` để thêm cột vào database. Migration sẽ tự động chạy khi khởi động ứng dụng.

2. **Giá trị mặc định:** Khi tạo user mới, `fingerprintLoginEnabled` mặc định là `false`.

3. **Quyền truy cập:**
   - API `/api/auth/check-fingerprint-enabled` là **PUBLIC**, không cần token
   - API `/api/users/{userId}/features/fingerprint-login` cần authentication
   - CUSTOMER chỉ có thể xem và cập nhật cài đặt cho chính mình
   - OFFICER có thể xem và cập nhật cài đặt cho tất cả users

4. **Bảo mật:**
   - API public chỉ trả về `true/false`, không lộ thông tin nhạy cảm
   - Nếu số điện thoại không tồn tại, API vẫn trả về `{"enabled": false}` để tránh lộ thông tin user có tồn tại hay không

5. **Cập nhật linh hoạt:** API `/api/users/{userId}/settings` cho phép cập nhật một hoặc nhiều cài đặt cùng lúc, bạn chỉ cần gửi các field muốn thay đổi.

---

## Các API liên quan khác

### Kiểm tra Face Recognition
```
GET /api/users/{userId}/features/face-recognition
```

### Kiểm tra Smart eKYC
```
GET /api/users/{userId}/features/smart-ekyc
```

Tất cả đều có cùng format response:
```json
{
  "enabled": true
}
```

---
