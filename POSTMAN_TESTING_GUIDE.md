# Hướng Dẫn Test API Quản Lý User trên Postman

## Bước 1: Chuẩn bị dữ liệu

### 1.1. Tạo tài khoản OFFICER để test
Có 2 cách:

**Cách 1: Thông qua Database (Khuyến nghị)**
```sql
-- Đăng ký 1 tài khoản customer trước, sau đó update role thành officer
UPDATE users SET role = 'officer' WHERE phone = '0987654321';
```

**Cách 2: Đăng ký và thay đổi trực tiếp trong database**
1. Đăng ký tài khoản mới qua API `/api/auth/register`
2. Vào database và chạy câu lệnh SQL ở trên

---

## Bước 2: Đăng ký tài khoản test

### Test Account 1 (CUSTOMER)
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "email": "nguyenvana@gmail.com",
  "cccdNumber": "001234567890",
  "dateOfBirth": "1990-01-15",
  "permanentAddress": "123 ABC Street, District 1, HCMC",
  "temporaryAddress": "456 XYZ Street, District 2, HCMC"
}
```

### Test Account 2 (OFFICER - sau khi update role trong database)
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "phone": "0987654321",
  "password": "officer123",
  "fullName": "Tran Thi B",
  "email": "tranthib@gmail.com",
  "cccdNumber": "009876543210",
  "dateOfBirth": "1985-05-20",
  "permanentAddress": "789 DEF Street, District 3, HCMC",
  "temporaryAddress": "321 GHI Street, District 4, HCMC"
}
```

**Sau đó chạy SQL:**
```sql
UPDATE users SET role = 'officer' WHERE phone = '0987654321';
```

---

## Bước 3: Test Login và lấy Token

### 3.1. Login với CUSTOMER
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImZ1bGxOYW1lIjoiTmd1eWVuIFZhbiBBIiwicGhvbmUiOiIwMTIzNDU2Nzg5Iiwicm9sZSI6ImN1c3RvbWVyIiwic3ViIjoiMDEyMzQ1Njc4OSIsImlhdCI6MTcwMDU2MDAwMCwiZXhwIjoxNzAwNjQ2NDAwfQ...",
  "userId": 1,
  "email": "nguyenvana@gmail.com",
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "role": "customer"
}
```

**Lưu token này vào biến môi trường Postman:**
- Variable name: `customer_token`
- Value: Copy toàn bộ accessToken

### 3.2. Login với OFFICER
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "phone": "0987654321",
  "password": "officer123"
}
```

**Lưu token vào biến:**
- Variable name: `officer_token`
- Value: Copy toàn bộ accessToken

---

## Bước 4: Test API Quản Lý User

### 4.1. Test với CUSTOMER (Sẽ bị từ chối - 403 Forbidden)

```http
GET http://localhost:8080/api/users
Authorization: Bearer {{customer_token}}
```

**Expected Response: 403 Forbidden**
```json
{
  "timestamp": "2025-11-21T00:50:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/users"
}
```

### 4.2. Test với OFFICER (Thành công)

#### A. Lấy danh sách tất cả user
```http
GET http://localhost:8080/api/users
Authorization: Bearer {{officer_token}}
```

**Expected Response: 200 OK**
```json
[
  {
    "userId": 1,
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@gmail.com",
    "phone": "0123456789",
    "dateOfBirth": "1990-01-15",
    "cccdNumber": "001234567890",
    "permanentAddress": "123 ABC Street, District 1, HCMC",
    "temporaryAddress": "456 XYZ Street, District 2, HCMC",
    "photoUrl": null,
    "role": "customer",
    "isLocked": false,
    "createdAt": "2025-11-20T10:00:00Z",
    "updatedAt": "2025-11-20T10:00:00Z"
  }
]
```

#### B. Lấy thông tin user theo ID
```http
GET http://localhost:8080/api/users/1
Authorization: Bearer {{officer_token}}
```

#### C. Cập nhật thông tin user
```http
PUT http://localhost:8080/api/users/1
Authorization: Bearer {{officer_token}}
Content-Type: application/json

{
  "fullName": "Nguyen Van A Updated",
  "email": "nguyenvana.updated@gmail.com",
  "permanentAddress": "New Address 999"
}
```

**Expected Response: 200 OK** (Trả về user đã update)

#### D. Khóa tài khoản user
```http
PATCH http://localhost:8080/api/users/1/lock
Authorization: Bearer {{officer_token}}
Content-Type: application/json

{
  "locked": true
}
```

**Expected Response: 200 OK**
```json
{
  "userId": 1,
  "fullName": "Nguyen Van A Updated",
  "email": "nguyenvana.updated@gmail.com",
  "phone": "0123456789",
  "dateOfBirth": "1990-01-15",
  "cccdNumber": "001234567890",
  "permanentAddress": "New Address 999",
  "temporaryAddress": "456 XYZ Street, District 2, HCMC",
  "photoUrl": null,
  "role": "customer",
  "isLocked": true,
  "createdAt": "2025-11-20T10:00:00Z",
  "updatedAt": "2025-11-21T00:55:00Z"
}
```

---

## Bước 5: Test Login với tài khoản bị khóa

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123"
}
```

**Expected Response: 423 Locked**
```json
{
  "status": 423,
  "message": "Account is locked. Please contact support.",
  "timestamp": "2025-11-21T00:56:00.123"
}
```

---

## Bước 6: Mở khóa tài khoản

```http
PATCH http://localhost:8080/api/users/1/lock
Authorization: Bearer {{officer_token}}
Content-Type: application/json

{
  "locked": false
}
```

**Expected Response: 200 OK** (isLocked = false)

---

## Bước 7: Test Login lại sau khi mở khóa

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123"
}
```

**Expected Response: 200 OK** (Login thành công)

---

## Cấu hình Postman Environment

Tạo Environment mới với các biến:

| Variable Name    | Initial Value      | Current Value      |
|------------------|--------------------|--------------------|
| base_url         | http://localhost:8080 | http://localhost:8080 |
| customer_token   |                    | (paste token)      |
| officer_token    |                    | (paste token)      |

Sau đó có thể dùng `{{base_url}}` trong các request.

---

## Kiểm tra JWT Token

Vào https://jwt.io và paste token vào để xem payload:

**Payload sẽ có dạng:**
```json
{
  "userId": 1,
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "role": "customer",
  "sub": "0123456789",
  "iat": 1700560000,
  "exp": 1700646400
}
```

---

## Troubleshooting

### 1. Lỗi 403 Forbidden khi dùng OFFICER token
- Kiểm tra role trong database: `SELECT role FROM users WHERE phone = '0987654321';`
- Đảm bảo role là 'officer' (viết thường)
- Decode JWT token trên jwt.io để kiểm tra claim "role"

### 2. Token không hợp lệ
- Kiểm tra secret key trong `application.properties` khớp với secret trên jwt.io
- Đảm bảo thuật toán là HS256

### 3. Database không có field is_locked
- Chạy lại ứng dụng để Flyway tự động chạy migration V3

### 4. Cannot update email - already exists
- Email phải unique, kiểm tra trong database xem email đã tồn tại chưa
