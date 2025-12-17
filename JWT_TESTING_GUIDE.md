# Hướng dẫn test JWT trên jwt.io

## ⚠️ Vấn đề bạn gặp phải

Khi bạn paste token vào https://jwt.io và nhập secret key nhưng token vẫn hiển thị **"Invalid Signature"** hoặc thuật toán hiển thị là **HS384** thay vì **HS256**.

## ✅ Giải pháp

### Bước 1: Test JWT với endpoint mới

Tôi đã tạo endpoint test để bạn dễ dàng lấy token và secret key:

**GET** `http://localhost:8089/api/test/jwt?phone=0912345678`

Response sẽ trả về:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTEyMzQ1Njc4IiwiaWF0IjoxNzAwNTAwMDAwLCJleHAiOjE3MDA1ODY0MDB9.xyz...",
  "secretKey": "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
  "algorithm": "HS256",
  "isValid": true,
  "phone": "0912345678",
  "extractedPhone": "0912345678",
  "instruction": "Copy token và secretKey, paste vào https://jwt.io với algorithm HS256"
}
```

### Bước 2: Verify trên jwt.io

1. **Truy cập**: https://jwt.io
2. **Paste token** vào ô "Encoded" (phần bên trái)
3. **Kiểm tra Header**: Bạn sẽ thấy:
   ```json
   {
     "alg": "HS256",
     "typ": "JWT"
   }
   ```
4. **Chọn thuật toán**: Trong phần "Verify Signature" (bên phải), đảm bảo chọn **HMACSHA256** (HS256)
5. **Nhập secret key**: 
   ```
   404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
   ```
6. **Kiểm tra**: Nếu đúng, bạn sẽ thấy chữ **"Signature Verified"** màu xanh

### Bước 3: Test với Login thực tế

1. **Đăng ký user mới:**
   ```
   POST http://localhost:8089/api/auth/register
   Body:
   {
     "phone": "0912345678",
     "email": "test@example.com",
     "password": "password123",
     "fullName": "Test User",
     "cccdNumber": "001234567890",
     "dateOfBirth": "1990-01-01",
     "permanentAddress": "123 Street",
     "temporaryAddress": "456 Street"
   }
   ```

2. **Đăng nhập:**
   ```
   POST http://localhost:8089/api/auth/login
   Body:
   {
     "phone": "0912345678",
     "password": "password123"
   }
   ```

3. **Copy token** từ response và test trên jwt.io như Bước 2

## 🔍 Giải thích vấn đề

### Tại sao trước đây token không hợp lệ?

1. **Sử dụng API deprecated**: 
   - Trước: `Jwts.parser().setSigningKey()` (deprecated)
   - Sau: `Jwts.parserBuilder().setSigningKey().build()` (mới)

2. **Thuật toán không nhất quán**:
   - Phải chỉ định rõ `SignatureAlgorithm.HS256` khi tạo token
   - Phải sử dụng cùng thuật toán khi verify

3. **Secret key format**:
   - Secret key là chuỗi UTF-8 thuần túy
   - Không phải Base64, không phải Hex

## 📊 Cấu trúc JWT

Token của bạn gồm 3 phần (cách nhau bởi dấu `.`):

```
[HEADER].[PAYLOAD].[SIGNATURE]
```

**1. Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**2. Payload:**
```json
{
  "sub": "0912345678",
  "iat": 1700500000,
  "exp": 1700586400
}
```
- `sub`: Subject (phone number)
- `iat`: Issued At (thời gian tạo token)
- `exp`: Expiration (thời gian hết hạn = iat + 24h)

**3. Signature:**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

## 🧪 Test với Postman

### 1. Test JWT Generation
```
GET http://localhost:8089/api/test/jwt?phone=0912345678
```

### 2. Test JWT Validation
```
POST http://localhost:8089/api/test/validate
Body:
{
  "token": "your_jwt_token_here"
}
```

Response nếu valid:
```json
{
  "valid": true,
  "phone": "0912345678"
}
```

Response nếu invalid:
```json
{
  "valid": false,
  "error": "Expired JWT token"
}
```

## 🔐 Bảo mật

**Lưu ý quan trọng:**

1. ⚠️ **KHÔNG** expose secret key trong production
2. ⚠️ Test endpoint `/api/test/jwt` chỉ dùng cho development
3. ⚠️ Xóa hoặc disable endpoint này trước khi deploy
4. ✅ Sử dụng environment variables cho secret key:
   ```bash
   set APP_JWT_SECRET=your_long_random_secret_key_here
   ```

## 🐛 Troubleshooting

### Token hiển thị HS384 thay vì HS256
- **Nguyên nhân**: Token cũ được tạo bằng code cũ
- **Giải pháp**: Generate token mới sau khi đã sửa code

### "Invalid Signature" trên jwt.io
- Kiểm tra secret key có paste đúng không (không có space thừa)
- Đảm bảo chọn đúng thuật toán HS256
- Token phải được generate bằng code mới (sau khi fix)

### Token expired
- Token có thời hạn 24 giờ
- Generate token mới nếu đã hết hạn

### Cannot verify token trong code
- Kiểm tra secret key trong application.properties
- Restart lại Spring Boot application
- Clear browser cache và cookies

## 📚 Tài liệu tham khảo

- JWT Official: https://jwt.io
- JJWT Library: https://github.com/jwtk/jjwt
- Spring Security JWT: https://spring.io/guides/tutorials/spring-boot-oauth2/

---

**Nếu vẫn gặp vấn đề, check:**
1. Console log của Spring Boot
2. Network tab trong Chrome DevTools
3. Response từ API có chứa token không

