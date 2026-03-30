# API Quản Lý User - User Management API

## Tổng quan
Các API này cho phép quản lý thông tin user và khóa/mở khóa tài khoản. **Chỉ user có role OFFICER mới được phép truy cập.**

## Cập nhật Login API

### 1. Đăng nhập (POST /api/auth/login)
Đã được cập nhật để kiểm tra tài khoản bị khóa.

**Request:**
```json
{
  "phone": "0123456789",
  "password": "password123"
}
```

**Response nếu tài khoản bị khóa:**
```json
{
  "timestamp": "2025-11-21T10:30:00",
  "message": "Account is locked. Please contact support.",
  "details": "uri=/api/auth/login"
}
```

**Response thành công:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "role": "customer"
}
```

---

## API Quản Lý User (Chỉ OFFICER)

### 2. Lấy danh sách tất cả user (GET /api/users)

**Headers:**
```
Authorization: Bearer {token_of_officer}
```

**Response:**
```json
[
  {
    "userId": 1,
    "fullName": "Nguyen Van A",
    "email": "user1@example.com",
    "phone": "0123456789",
    "dateOfBirth": "1990-01-01",
    "cccdNumber": "001234567890",
    "permanentAddress": "123 ABC Street",
    "temporaryAddress": "456 XYZ Street",
    "photoUrl": "https://res.cloudinary.com/...",
    "role": "customer",
    "isLocked": false,
    "createdAt": "2025-11-20T10:00:00Z",
    "updatedAt": "2025-11-20T10:00:00Z"
  }
]
```

---

### 3. Lấy thông tin user theo ID (GET /api/users/{userId})

**Headers:**
```
Authorization: Bearer {token_of_officer}
```

**Response:**
```json
{
  "userId": 1,
  "fullName": "Nguyen Van A",
  "email": "user1@example.com",
  "phone": "0123456789",
  "dateOfBirth": "1990-01-01",
  "cccdNumber": "001234567890",
  "permanentAddress": "123 ABC Street",
  "temporaryAddress": "456 XYZ Street",
  "photoUrl": "https://res.cloudinary.com/...",
  "role": "customer",
  "isLocked": false,
  "createdAt": "2025-11-20T10:00:00Z",
  "updatedAt": "2025-11-20T10:00:00Z"
}
```

---

### 4. Cập nhật thông tin user (PUT /api/users/{userId})

**Headers:**
```
Authorization: Bearer {token_of_officer}
```

**Request Body:** (Tất cả các field đều optional, chỉ gửi field muốn cập nhật)
```json
{
  "fullName": "Nguyen Van B",
  "email": "newmail@example.com",
  "dateOfBirth": "1990-05-15",
  "permanentAddress": "New address 123",
  "temporaryAddress": "New temp address 456"
}
```

**Response:**
```json
{
  "userId": 1,
  "fullName": "Nguyen Van B",
  "email": "newmail@example.com",
  "phone": "0123456789",
  "dateOfBirth": "1990-05-15",
  "cccdNumber": "001234567890",
  "permanentAddress": "New address 123",
  "temporaryAddress": "New temp address 456",
  "photoUrl": "https://res.cloudinary.com/...",
  "role": "customer",
  "isLocked": false,
  "createdAt": "2025-11-20T10:00:00Z",
  "updatedAt": "2025-11-21T10:30:00Z"
}
```

---

### 5. Khóa/Mở khóa tài khoản user (PATCH /api/users/{userId}/lock)

**Headers:**
```
Authorization: Bearer {token_of_officer}
```

**Request Body:**
```json
{
  "locked": true
}
```
- `locked: true` - Khóa tài khoản
- `locked: false` - Mở khóa tài khoản

**Response:**
```json
{
  "userId": 1,
  "fullName": "Nguyen Van A",
  "email": "user1@example.com",
  "phone": "0123456789",
  "dateOfBirth": "1990-01-01",
  "cccdNumber": "001234567890",
  "permanentAddress": "123 ABC Street",
  "temporaryAddress": "456 XYZ Street",
  "photoUrl": "https://res.cloudinary.com/...",
  "role": "customer",
  "isLocked": true,
  "createdAt": "2025-11-20T10:00:00Z",
  "updatedAt": "2025-11-21T10:35:00Z"
}
```

---

## Phân Quyền

### Role Requirements:
- **OFFICER**: Có quyền truy cập tất cả API trong `/api/users/*`
- **CUSTOMER**: Không có quyền truy cập các API quản lý user

### Response khi không có quyền (403 Forbidden):
```json
{
  "timestamp": "2025-11-21T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/users"
}
```

---

## Test Scenarios

### Scenario 1: Tạo OFFICER account để test
```sql
-- Thực hiện trong database
UPDATE users SET role = 'officer' WHERE phone = '0123456789';
```

### Scenario 2: Test khóa tài khoản
1. Login với tài khoản OFFICER
2. Gọi API `PATCH /api/users/1/lock` với body `{"locked": true}`
3. Thử login với tài khoản customer bị khóa
4. Kết quả: Nhận được lỗi "Account is locked"

### Scenario 3: Test phân quyền
1. Login với tài khoản CUSTOMER
2. Thử gọi API `GET /api/users`
3. Kết quả: Nhận được lỗi 403 Forbidden

---

## Lưu ý

1. **Migration Database**: Chạy ứng dụng để Flyway tự động thêm field `is_locked` vào bảng `users`

2. **Default Value**: Tất cả user mới đều có `isLocked = false` (không bị khóa)

3. **Không thể thay đổi**:
   - Phone number (unique identifier)
   - CCCD number (số CCCD)
   - Password (cần API riêng để đổi mật khẩu)
   - Role (cần API riêng nếu muốn thay đổi role)

4. **Email validation**: Khi cập nhật email, hệ thống sẽ kiểm tra email đã được user khác sử dụng chưa

