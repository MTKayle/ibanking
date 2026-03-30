# Refresh Token Implementation Guide

## Tổng quan
Hệ thống đã được cập nhật để hỗ trợ refresh token, giúp người dùng duy trì phiên đăng nhập mà không cần đăng nhập lại liên tục.

## Các thay đổi chính

### 1. JWT Token Provider
**File:** `JwtTokenProvider.java`

Đã thêm các phương thức mới:
- `generateRefreshToken(String phone)`: Tạo refresh token với thời hạn 7 ngày
- `generateRefreshTokenFromAuthentication(Authentication authentication)`: Tạo refresh token từ Authentication object
- `isRefreshToken(String token)`: Kiểm tra xem token có phải là refresh token không

**Cấu hình:**
- Access Token: 24 giờ (mặc định)
- Refresh Token: 7 ngày (mặc định)

Có thể tùy chỉnh trong `application.properties`:
```properties
app.jwt.expiration=86400000           # 24 hours
app.jwt.refresh-expiration=604800000  # 7 days
```

### 2. AuthResponse DTO
**File:** `AuthResponse.java`

Đã thêm trường `refreshToken` và constructor mới:
```java
public AuthResponse(String token, String refreshToken, Long userId, 
                   String email, String fullName, String phone, String role)
```

### 3. RefreshTokenRequest DTO
**File:** `RefreshTokenRequest.java` (MỚI)

DTO để nhận refresh token request:
```java
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 4. Auth Service & Implementation
**Files:** `AuthService.java`, `AuthServiceImpl.java`

#### Phương thức mới:
```java
AuthResponse refreshToken(String refreshToken);
```

#### Các phương thức đã cập nhật:
- `login()`: Trả về cả access token và refresh token
- `loginWithFaceRecognition()`: Trả về cả access token và refresh token

#### Logic xử lý refresh token:
1. Validate refresh token (kiểm tra hợp lệ và chưa hết hạn)
2. Kiểm tra token có phải là refresh token không (dựa vào tokenType claim)
3. Lấy phone từ refresh token
4. Tìm user trong database
5. Kiểm tra tài khoản có bị khóa không
6. Tạo access token mới và refresh token mới
7. Trả về cả hai token

### 5. Auth Controller
**File:** `AuthController.java`

#### Endpoint mới:
```
POST /api/auth/refresh-token
Content-Type: application/json

Request Body:
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}

Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",      // access token mới
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...", // refresh token mới
    "type": "Bearer",
    "userId": 1,
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0123456789",
    "role": "customer"
}
```

## Cách sử dụng

### 1. Đăng nhập
```bash
POST /api/auth/login
Content-Type: application/json

{
    "phone": "0123456789",
    "password": "password123"
}

Response:
{
    "token": "ACCESS_TOKEN",
    "refreshToken": "REFRESH_TOKEN",
    "userId": 1,
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0123456789",
    "role": "customer"
}
```

### 2. Sử dụng Access Token
Gửi access token trong header để gọi API:
```
Authorization: Bearer ACCESS_TOKEN
```

### 3. Làm mới Access Token
Khi access token hết hạn (sau 24 giờ), sử dụng refresh token để lấy token mới:
```bash
POST /api/auth/refresh-token
Content-Type: application/json

{
    "refreshToken": "REFRESH_TOKEN"
}

Response:
{
    "token": "NEW_ACCESS_TOKEN",
    "refreshToken": "NEW_REFRESH_TOKEN",
    ...
}
```

### 4. Lưu trữ Token ở Frontend
**Khuyến nghị:**
- Lưu access token trong memory (biến JavaScript)
- Lưu refresh token trong HttpOnly Cookie hoặc Secure Storage
- Không lưu token trong localStorage để tránh XSS attack

## Luồng xử lý

```
1. User đăng nhập
   ↓
2. Backend trả về: access token (24h) + refresh token (7d)
   ↓
3. Frontend lưu cả hai token
   ↓
4. Gọi API với access token
   ↓
5. Access token hết hạn (401 Unauthorized)
   ↓
6. Frontend tự động gọi /refresh-token với refresh token
   ↓
7. Backend trả về access token mới + refresh token mới
   ↓
8. Retry API call với access token mới
   ↓
9. Lặp lại từ bước 4
```

## Security Notes

### 1. Token Rotation
Mỗi khi refresh, hệ thống tạo cả access token MỚI và refresh token MỚI. Điều này giúp:
- Giảm thiểu rủi ro nếu refresh token bị đánh cắp
- Token cũ không còn sử dụng được

### 2. Token Claims
**Access Token chứa:**
- phone
- userId
- fullName
- role
- issuedAt
- expiration

**Refresh Token chứa:**
- phone
- tokenType: "refresh"
- issuedAt
- expiration

### 3. Validation
Refresh token được validate qua:
- Chữ ký JWT
- Thời gian hết hạn
- Token type (phải là "refresh")
- User tồn tại trong database
- User không bị khóa

## Error Handling

### Các lỗi có thể xảy ra:

1. **Refresh token không hợp lệ hoặc hết hạn**
   ```json
   {
       "message": "Refresh token không hợp lệ hoặc đã hết hạn"
   }
   ```
   → Frontend cần redirect user về trang đăng nhập

2. **Token không phải là refresh token**
   ```json
   {
       "message": "Token không phải là refresh token"
   }
   ```
   → User đã gửi access token thay vì refresh token

3. **User not found**
   ```json
   {
       "message": "User not found with phone: 0123456789"
   }
   ```
   → User đã bị xóa khỏi hệ thống

4. **Account is locked**
   ```json
   {
       "message": "Account is locked. Please contact support."
   }
   ```
   → Tài khoản bị khóa, không thể refresh token

## Testing với Postman

### 1. Test Login
```
POST {{baseUrl}}/api/auth/login
Body:
{
    "phone": "0123456789",
    "password": "password123"
}

Save: 
- accessToken từ response.token
- refreshToken từ response.refreshToken
```

### 2. Test API Call
```
GET {{baseUrl}}/api/users/me
Headers:
Authorization: Bearer {{accessToken}}
```

### 3. Test Refresh Token
```
POST {{baseUrl}}/api/auth/refresh-token
Body:
{
    "refreshToken": "{{refreshToken}}"
}

Save new tokens từ response
```

## Frontend Implementation Example

```javascript
// Interceptor để tự động refresh token
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // Nếu 401 và chưa retry
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Gọi refresh token API
        const refreshToken = getRefreshToken(); // Lấy từ storage
        const response = await axios.post('/api/auth/refresh-token', {
          refreshToken: refreshToken
        });
        
        // Lưu token mới
        const { token, refreshToken: newRefreshToken } = response.data;
        setAccessToken(token);
        setRefreshToken(newRefreshToken);
        
        // Retry request với token mới
        originalRequest.headers['Authorization'] = 'Bearer ' + token;
        return axios(originalRequest);
      } catch (refreshError) {
        // Refresh failed → redirect to login
        redirectToLogin();
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);
```

## Kết luận

Hệ thống refresh token đã được triển khai hoàn chỉnh với các tính năng:
✅ Access token (24h) và Refresh token (7 ngày)
✅ Token rotation (tạo token mới mỗi lần refresh)
✅ Validation đầy đủ (signature, expiration, type, user status)
✅ Áp dụng cho cả login thường và login với face recognition
✅ Error handling đầy đủ

Hệ thống giúp cải thiện UX (user không cần đăng nhập lại liên tục) và bảo mật (token có thời gian sống ngắn).

