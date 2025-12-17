# iBanking Backend - Hệ thống ngân hàng Android

Backend API cho ứng dụng ngân hàng di động với tính năng xác thực khuôn mặt (Face Recognition) và lưu trữ ảnh trên Cloudinary.

## 🚀 Tính năng chính

### 1. Xác thực người dùng (Authentication)
- ✅ Đăng ký tài khoản cơ bản (không cần xác thực khuôn mặt)
- ✅ Đăng ký với xác thực khuôn mặt (Face Recognition)
- ✅ Đăng nhập bằng số điện thoại + mật khẩu
- ✅ JWT Token authentication
- ✅ Spring Security

### 2. Xác thực khuôn mặt (Face Verification)
- So sánh ảnh CCCD và ảnh selfie sử dụng Face++ API
- Ngưỡng độ tương đồng: 70% (có thể cấu hình)
- Lưu trữ ảnh selfie trên Cloudinary

### 3. Quản lý thông tin người dùng
- Lưu thông tin CCCD (số CCCD, ngày sinh)
- Địa chỉ thường trú và tạm trú
- Ảnh selfie xác thực

## 📋 Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.6+
- PostgreSQL 12+
- Spring Boot 3.5.7

## 🛠️ Cài đặt và cấu hình

### Bước 1: Clone project
```bash
git clone <repository-url>
cd ibanking
```

### Bước 2: Cấu hình Database (PostgreSQL)

1. Cài đặt PostgreSQL và tạo database:
```sql
CREATE DATABASE ibanking;
```

2. Cập nhật thông tin database trong `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ibanking
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

3. Chạy migration script (nếu cần):
```bash
# Bật Flyway trong application.properties
spring.flyway.enabled=true
```

### Bước 3: Cấu hình Face++ API (Xác thực khuôn mặt)

1. Đăng ký tài khoản miễn phí tại: https://www.faceplusplus.com/
2. Tạo API Key và API Secret
3. Cập nhật trong `application.properties`:

```properties
faceplus.api.key=YOUR_FACE_PLUS_API_KEY
faceplus.api.secret=YOUR_FACE_PLUS_API_SECRET
faceplus.api.url=https://api-us.faceplusplus.com/facepp/v3/compare
faceplus.confidence.threshold=70.0
```

**Lưu ý:** Nếu bạn không cấu hình Face++ API, vẫn có thể sử dụng API đăng ký thông thường (`/api/auth/register`).

### Bước 4: Cấu hình Cloudinary (Lưu trữ ảnh)

1. Đăng ký tài khoản miễn phí tại: https://cloudinary.com/
2. Vào Dashboard và lấy thông tin:
   - Cloud Name
   - API Key
   - API Secret

3. Cập nhật trong `application.properties`:

```properties
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

**Hướng dẫn chi tiết:**
- Truy cập: https://console.cloudinary.com/
- Đăng nhập và vào Dashboard
- Sao chép thông tin từ phần "Account Details":
  - Cloud name: `dxyz123abc`
  - API Key: `123456789012345`
  - API Secret: `AbcDefGhiJklMnoPqrStuvWxyz`

**Lưu ý:** 
- Cloudinary free tier cho phép: 25 GB storage và 25 GB bandwidth/tháng
- Ảnh sẽ được lưu trong folder: `ibanking/users/{userId}/`
- Nếu không cấu hình Cloudinary, chỉ có thể sử dụng API đăng ký cơ bản

### Bước 5: Cấu hình JWT Secret

JWT secret đã được cấu hình sẵn trong `application.properties`. Bạn có thể thay đổi:

```properties
app.jwt.secret=YOUR_SECRET_KEY_HERE
app.jwt.expiration=86400000
```

**Tạo secret key mới:**
```java
// Sử dụng code Java để tạo secret key ngẫu nhiên
SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
String secretString = Encoders.BASE64.encode(key.getEncoded());
```

### Bước 6: Build và chạy ứng dụng

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Hoặc chạy file JAR:
```bash
java -jar target/ibanking-0.0.1-SNAPSHOT.jar
```

Server sẽ chạy tại: `http://localhost:8089`

## 📚 API Endpoints

### 1. Đăng ký tài khoản (Không xác thực khuôn mặt)

**POST** `/api/auth/register`

**Body (JSON):**
```json
{
  "phone": "0912345678",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "cccdNumber": "001234567890",
  "dateOfBirth": "1990-01-01",
  "permanentAddress": "123 ABC Street, Ho Chi Minh City",
  "temporaryAddress": "456 XYZ Street, Ho Chi Minh City"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "role": "customer"
}
```

### 2. Đăng ký với xác thực khuôn mặt

**POST** `/api/auth/register-with-face`

**Body (form-data):**
- `registerRequest` (JSON string): Thông tin đăng ký
- `cccdPhoto` (file): Ảnh CCCD
- `selfiePhoto` (file): Ảnh selfie

**Ví dụ với Postman:**
1. Chọn `POST` method
2. URL: `http://localhost:8089/api/auth/register-with-face`
3. Chọn tab `Body` > `form-data`
4. Thêm các field:
   - Key: `registerRequest`, Type: `Text`, Value:
   ```json
   {
     "phone": "0912345678",
     "email": "user@example.com",
     "password": "password123",
     "fullName": "Nguyen Van A",
     "cccdNumber": "001234567890",
     "dateOfBirth": "1990-01-01",
     "permanentAddress": "123 ABC Street",
     "temporaryAddress": "456 XYZ Street"
   }
   ```
   - Key: `cccdPhoto`, Type: `File`, chọn file ảnh CCCD
   - Key: `selfiePhoto`, Type: `File`, chọn file ảnh selfie

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "role": "customer"
}
```

**Lỗi có thể gặp:**
- `Xác thực khuôn mặt thất bại. Độ tương đồng: 65.5% (yêu cầu >= 70%)`: Hai khuôn mặt không khớp
- `Face recognition service chưa được cấu hình`: Chưa cấu hình Face++ API
- `Cloudinary service chưa được cấu hình`: Chưa cấu hình Cloudinary

### 3. Đăng nhập

**POST** `/api/auth/login`

**Body (JSON):**
```json
{
  "phone": "0912345678",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "role": "customer"
}
```

## 🧪 Test API với Postman

### Collection mẫu:

1. **Register (Basic)**
   - Method: POST
   - URL: `http://localhost:8089/api/auth/register`
   - Body: raw JSON (xem mẫu ở trên)

2. **Register with Face Verification**
   - Method: POST
   - URL: `http://localhost:8089/api/auth/register-with-face`
   - Body: form-data (xem mẫu ở trên)

3. **Login**
   - Method: POST
   - URL: `http://localhost:8089/api/auth/login`
   - Body: raw JSON (xem mẫu ở trên)

4. **Test Protected Endpoint**
   - Method: GET
   - URL: `http://localhost:8089/api/protected-resource`
   - Headers: `Authorization: Bearer {token}`

## 🔒 Bảo mật

- Mật khẩu được mã hóa bằng BCrypt
- JWT token có thời gian hết hạn (24 giờ)
- Validation đầy đủ cho tất cả input
- Spring Security cho authentication và authorization

## 📝 Database Schema

### Table: users
```sql
user_id SERIAL PRIMARY KEY
phone VARCHAR(20) UNIQUE NOT NULL
email VARCHAR(100) UNIQUE NOT NULL
password_hash VARCHAR(255) NOT NULL
full_name VARCHAR(100) NOT NULL
cccd_number VARCHAR(20) UNIQUE NOT NULL
date_of_birth DATE
permanent_address TEXT
temporary_address TEXT
photo_url TEXT
role VARCHAR(20) NOT NULL (customer/officer)
created_at TIMESTAMP
updated_at TIMESTAMP
```

## ⚙️ Cấu trúc Project

```
src/main/java/org/example/storyreading/ibanking/
├── config/              # Configuration classes
├── controller/          # REST Controllers
├── dto/                # Data Transfer Objects
├── entity/             # JPA Entities
├── exception/          # Custom Exceptions
├── repository/         # JPA Repositories
├── security/           # Security Configuration
├── service/            # Business Logic
│   ├── AuthService.java
│   ├── CloudinaryService.java
│   ├── FaceRecognitionService.java
│   └── impl/
└── utils/              # Utility classes (JWT)
```

## 🐛 Troubleshooting

### Lỗi: Cannot connect to database
- Kiểm tra PostgreSQL đã chạy chưa
- Kiểm tra thông tin kết nối trong `application.properties`

### Lỗi: Face++ API không hoạt động
- Kiểm tra API Key và Secret
- Kiểm tra kết nối internet
- Kiểm tra quota của Face++ (free tier: 1000 calls/month)

### Lỗi: Cloudinary upload failed
- Kiểm tra Cloud Name, API Key, API Secret
- Kiểm tra kích thước file (max 10MB)
- Kiểm tra quota của Cloudinary

### Lỗi: JWT Token invalid
- Token đã hết hạn (24 giờ)
- Token bị sai format
- Secret key không khớp

## 📞 Liên hệ & Hỗ trợ

- Email: support@ibanking.com
- Documentation: [API Docs]

## 📄 License

MIT License

---

**Developed with ❤️ by iBanking Team**
