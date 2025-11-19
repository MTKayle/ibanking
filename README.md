# iBanking Backend - Hệ thống ngân hàng Android

## Tính năng chính

### 1. Xác thực người dùng (Authentication)
- ✅ Đăng ký tài khoản với thông tin cơ bản
- ✅ Đăng ký với xác thực khuôn mặt (Face Recognition)
- ✅ Đăng nhập bằng số điện thoại + mật khẩu
- ✅ JWT Token authentication

### 2. Xác thực khuôn mặt (Face Verification)
- So sánh ảnh CCCD và ảnh selfie sử dụng Face++ API
- Ngưỡng độ tương đồng: 80% (có thể cấu hình)
- Lưu trữ ảnh trên Firebase Storage

## Cấu hình

### 1. Database (PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ibanking
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

### 2. Face++ API
Đăng ký tài khoản tại: https://www.faceplusplus.com/

```properties
faceplus.api.key=YOUR_FACE_PLUS_API_KEY
faceplus.api.secret=YOUR_FACE_PLUS_API_SECRET
faceplus.confidence.threshold=80.0
```

### 3. Firebase Storage
1. Tạo project trên Firebase Console: https://console.firebase.google.com/
2. Vào Project Settings > Service Accounts
3. Generate new private key và tải về file JSON
4. Đặt file vào `src/main/resources/firebase-service-account.json`

```properties
firebase.storage.bucket=your-project-id.appspot.com
firebase.credentials.path=classpath:firebase-service-account.json
```

### 4. JWT Secret
```properties
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000
```

## API Endpoints

### 1. Đăng ký thông thường
**POST** `/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "phone": "0912345678",
  "email": "user@example.com",
  "password": "Password123",
  "fullName": "Nguyễn Văn A",
  "cccdNumber": "001234567890",
  "dateOfBirth": "1990-01-01",
  "permanentAddress": "123 ABC, Quận 1, TP.HCM",
  "temporaryAddress": "456 XYZ, Quận 2, TP.HCM"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyễn Văn A",
  "phone": "0912345678",
  "role": "customer"
}
```

### 2. Đăng ký với xác thực khuôn mặt
**POST** `/api/auth/register-with-face`

**Headers:**
```
Content-Type: multipart/form-data
```

**Form Data:**
- `phone` (string, required): "0912345678"
- `email` (string, required): "user@example.com"
- `password` (string, required): "Password123"
- `fullName` (string, required): "Nguyễn Văn A"
- `cccdNumber` (string, required): "001234567890"
- `dateOfBirth` (string, optional): "1990-01-01"
- `permanentAddress` (string, optional): "123 ABC, Quận 1"
- `temporaryAddress` (string, optional): "456 XYZ, Quận 2"
- `cccdPhoto` (file, required): Ảnh chụp CCCD (jpg/png, max 5MB)
- `selfiePhoto` (file, required): Ảnh selfie (jpg/png, max 5MB)

**Response thành công:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyễn Văn A",
  "phone": "0912345678",
  "role": "customer"
}
```

**Response lỗi khi xác thực khuôn mặt thất bại:**
```json
{
  "message": "Xác thực khuôn mặt thất bại. Độ tương đồng: 65.50% (yêu cầu >= 80.00%)"
}
```

### 3. Đăng nhập
**POST** `/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "phone": "0912345678",
  "password": "Password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyễn Văn A",
  "phone": "0912345678",
  "role": "customer"
}
```

## Test với Postman

### Test đăng ký với xác thực khuôn mặt:

1. **Tạo request mới:**
   - Method: POST
   - URL: `http://localhost:8089/api/auth/register-with-face`

2. **Headers:**
   - Để Postman tự động set `Content-Type: multipart/form-data`

3. **Body:**
   - Chọn tab "form-data"
   - Thêm các field:
     ```
     phone: 0912345678
     email: user@example.com
     password: Password123
     fullName: Nguyễn Văn A
     cccdNumber: 001234567890
     dateOfBirth: 1990-01-01
     permanentAddress: 123 ABC Street
     temporaryAddress: 456 XYZ Street
     ```
   - Thêm 2 file:
     - `cccdPhoto`: Chọn "File" và upload ảnh CCCD
     - `selfiePhoto`: Chọn "File" và upload ảnh selfie

4. **Send request**

5. **Kiểm tra response:**
   - Status: 201 Created
   - Body chứa accessToken và thông tin user

### Test đăng nhập:

1. **Tạo request mới:**
   - Method: POST
   - URL: `http://localhost:8089/api/auth/login`

2. **Headers:**
   ```
   Content-Type: application/json
   ```

3. **Body (raw JSON):**
   ```json
   {
     "phone": "0912345678",
     "password": "Password123"
   }
   ```

4. **Send request**

5. **Lưu token vào Environment:**
   - Vào tab "Tests" trong Postman
   - Thêm script:
   ```javascript
   pm.test("Login successful", function () {
       pm.response.to.have.status(200);
       var jsonData = pm.response.json();
       pm.environment.set("accessToken", jsonData.accessToken);
   });
   ```

6. **Sử dụng token cho request khác:**
   - Headers:
   ```
   Authorization: Bearer {{accessToken}}
   ```

## Quy trình xác thực khuôn mặt

```
1. Client gửi form data (thông tin user + 2 ảnh)
   ↓
2. Validate dữ liệu đầu vào
   - Kiểm tra định dạng ảnh (jpg/png)
   - Kiểm tra kích thước (max 5MB)
   ↓
3. Gọi Face++ API để so sánh 2 ảnh
   - API trả về confidence score (0-100)
   ↓
4. Kiểm tra ngưỡng confidence
   - Nếu < 80%: Trả về lỗi "Xác thực thất bại"
   - Nếu >= 80%: Tiếp tục
   ↓
5. Tạo user trong database
   ↓
6. Upload ảnh selfie lên Firebase Storage
   ↓
7. Lưu URL ảnh vào bảng ekyc_photos
   ↓
8. Generate JWT token
   ↓
9. Trả về response với token và thông tin user
```

## Cấu trúc Database

### Bảng `users`
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    cccd_number VARCHAR(50) UNIQUE NOT NULL,
    date_of_birth DATE,
    permanent_address VARCHAR(255),
    temporary_address VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bảng `ekyc_photos`
```sql
CREATE TABLE ekyc_photos (
    photo_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    photo_url TEXT NOT NULL,
    image_type VARCHAR(20) NOT NULL CHECK (image_type IN ('CCCD', 'SELFIE')),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Dependencies

```xml
<!-- Firebase Admin SDK -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>

<!-- OkHttp for Face++ API -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
```

## Chạy ứng dụng

### 1. Build project
```bash
mvnw clean package -DskipTests
```

### 2. Chạy ứng dụng
```bash
mvnw spring-boot:run
```

hoặc

```bash
java -jar target/ibanking-0.0.1-SNAPSHOT.jar
```

### 3. Kiểm tra ứng dụng đã chạy
```
Application started on port: 8089
```

## Xử lý lỗi thường gặp

### 1. Lỗi Face++ API
```
Face++ API error: INVALID_API_KEY
```
**Giải pháp:** Kiểm tra lại `faceplus.api.key` và `faceplus.api.secret` trong `application.properties`

### 2. Lỗi Firebase
```
Firebase Storage chưa được khởi tạo
```
**Giải pháp:** 
- Kiểm tra file `firebase-service-account.json` đã có trong `src/main/resources/`
- Kiểm tra `firebase.storage.bucket` đúng với project của bạn

### 3. Lỗi Port đã được sử dụng
```
Port 8089 was already in use
```
**Giải pháp:** 
```bash
# Windows
netstat -ano | findstr :8089
taskkill /PID <PID> /F

# Hoặc đổi port trong application.properties
server.port=8080
```

### 4. Xác thực khuôn mặt thất bại
```
Độ tương đồng: 65.50% (yêu cầu >= 80.00%)
```
**Giải pháp:**
- Đảm bảo ảnh CCCD và selfie rõ nét, có ánh sáng tốt
- Khuôn mặt trong 2 ảnh cùng 1 người
- Có thể giảm ngưỡng trong `application.properties`: `faceplus.confidence.threshold=70.0`

## Bảo mật

- Mật khẩu được mã hóa bằng BCrypt
- JWT token có thời hạn 24 giờ
- API keys không được commit vào git
- HTTPS nên được sử dụng trong production
- Upload ảnh giới hạn kích thước và loại file

## Tech Stack

- **Framework:** Spring Boot 3.5.7
- **Database:** PostgreSQL
- **Authentication:** JWT (JSON Web Token)
- **Face Recognition:** Face++ API
- **Storage:** Firebase Cloud Storage
- **Security:** Spring Security
- **Build Tool:** Maven

## License

Dự án này được phát triển cho mục đích học tập.

---

**Liên hệ:** 
- GitHub: [your-github]
- Email: [your-email]

