4. **BE kiểm tra trạng thái:**
   - Kiểm tra tài khoản có bị khóa không
   - Nếu bị khóa → trả về lỗi

5. **BE hash mật khẩu:**
   - Sử dụng BCryptPasswordEncoder để hash mật khẩu mới
   - Hash này là one-way encryption, không thể decrypt

6. **BE cập nhật database:**
   - Update passwordHash trong bảng users
   - Save user vào database

7. **BE trả về kết quả:**
   - Success: HTTP 200 với message thành công
   - Error: HTTP 400/500 với message lỗi

## Security Features

### 1. Password Hashing
- Sử dụng **BCryptPasswordEncoder** để hash mật khẩu
- BCrypt là thuật toán one-way hashing, không thể decrypt
- Mỗi lần hash tạo ra salt ngẫu nhiên khác nhau
- Độ bảo mật cao, chống được rainbow table attack

### 2. Password Validation
- Mật khẩu tối thiểu 6 ký tự
- Có thể mở rộng thêm yêu cầu:
  - Chữ hoa, chữ thường
  - Ký tự đặc biệt
  - Số

### 3. Account Lock Check
- Không cho phép đổi mật khẩu nếu tài khoản bị khóa
- Bảo vệ tài khoản khỏi unauthorized access

## Use Cases

### 1. Forgot Password Flow
```
User clicks "Forgot Password" 
→ Enter phone number
→ Receive OTP via SMS (separate API)
→ Verify OTP (separate API)
→ Call Change Password API with phone + new password
→ Success → Redirect to login
```

### 2. Reset Password (Admin)
```
Admin selects user
→ Admin enters new password
→ Call Change Password API
→ User notified via SMS/Email
```

### 3. First Time Login
```
User receives temp password
→ Login with temp password
→ System forces password change
→ Call Change Password API
→ Success → Continue to app
```

## Notes

1. **No authentication required:**
   - API này không yêu cầu JWT token
   - Phù hợp cho forgot password flow
   - Nên kết hợp với OTP verification trước khi gọi API này

2. **Consider adding OTP verification:**
   - Trước khi đổi mật khẩu, nên gửi OTP về phone
   - User nhập OTP để xác thực
   - Sau khi OTP đúng mới cho phép đổi mật khẩu

3. **Rate limiting:**
   - Nên thêm rate limiting để tránh brute force
   - Giới hạn số lần đổi mật khẩu trong 1 khoảng thời gian

4. **Password history:**
   - Có thể lưu lịch sử mật khẩu cũ
   - Không cho phép dùng lại mật khẩu cũ

5. **Notification:**
   - Nên gửi email/SMS thông báo khi mật khẩu được đổi
   - Cảnh báo user nếu không phải họ đổi

## Recommended Improvements

### 1. Add OTP Verification

```java
// Add to ChangePasswordRequest
private String otp;

// Verify OTP before changing password
if (!otpService.verifyOtp(request.getPhone(), request.getOtp())) {
    throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn");
}
```

### 2. Add Password Strength Validation

```java
private void validatePasswordStrength(String password) {
    if (password.length() < 8) {
        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự");
    }
    if (!password.matches(".*[A-Z].*")) {
        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 chữ hoa");
    }
    if (!password.matches(".*[a-z].*")) {
        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 chữ thường");
    }
    if (!password.matches(".*[0-9].*")) {
        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 chữ số");
    }
    if (!password.matches(".*[!@#$%^&*()].*")) {
        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
    }
}
```

### 3. Add Notification

```java
// After password change
notificationService.sendSMS(user.getPhone(), 
    "Mật khẩu của bạn đã được thay đổi. Nếu không phải bạn, vui lòng liên hệ ngay.");
    
emailService.sendEmail(user.getEmail(),
    "Thông báo thay đổi mật khẩu",
    "Mật khẩu tài khoản của bạn đã được thay đổi lúc " + LocalDateTime.now());
```

## Troubleshooting

### Lỗi: "Không tìm thấy người dùng với số điện thoại"

**Nguyên nhân:** Số điện thoại không tồn tại trong database

**Giải pháp:** 
- Kiểm tra số điện thoại đã đúng chưa
- Kiểm tra user đã đăng ký chưa

### Lỗi: "Tài khoản đã bị khóa"

**Nguyên nhân:** Tài khoản bị khóa (isLocked = true)

**Giải pháp:**
- Liên hệ admin để mở khóa tài khoản
- Kiểm tra lý do bị khóa

### Mật khẩu mới không hoạt động

**Nguyên nhân:** 
- Mật khẩu chưa được cập nhật
- Cache vẫn giữ mật khẩu cũ

**Giải pháp:**
- Kiểm tra database xem passwordHash đã update chưa
- Clear cache nếu có
- Đợi vài giây rồi thử lại

        try {
            const response = await axios.post('http://localhost:8089/api/password/change', {
                phone: phone,
                newPassword: newPassword
            });

            if (response.data.success) {
                setMessage('Đổi mật khẩu thành công!');
                // Clear form
                setPhone('');
                setNewPassword('');
                // Redirect after 2 seconds
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            }
        } catch (error) {
            if (error.response) {
                setMessage('Lỗi: ' + error.response.data.message);
            } else {
                setMessage('Lỗi kết nối: ' + error.message);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>Số điện thoại:</label>
                <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    required
                    maxLength={20}
                />
            </div>
            <div>
                <label>Mật khẩu mới:</label>
                <input
                    type="password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                    minLength={6}
                />
            </div>
            <button type="submit" disabled={loading}>
                {loading ? 'Đang xử lý...' : 'Đổi mật khẩu'}
            </button>
            {message && <p>{message}</p>}
        </form>
    );
}

export default ChangePasswordForm;
```

## Flow hoạt động

1. **FE gửi request:**
   - User nhập số điện thoại và mật khẩu mới
   - FE gửi POST request với JSON body

2. **BE validate:**
   - Kiểm tra phone không trống
   - Kiểm tra newPassword không trống
   - Kiểm tra newPassword có ít nhất 6 ký tự

3. **BE tìm user:**
   - Tìm user theo phone trong database
   - Nếu không tìm thấy → trả về lỗi

# Change Password API Guide

## Mô tả
API này cho phép đổi mật khẩu user dựa vào số điện thoại. Frontend gửi số điện thoại và mật khẩu mới, backend sẽ tìm user theo phone, hash mật khẩu mới bằng BCrypt và cập nhật vào database.

## Endpoint

**URL:** `POST /api/password/change`

**Content-Type:** `application/json`

## Request Body

```json
{
    "phone": "0123456789",
    "newPassword": "newPassword123"
}
```

### Request Parameters

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| phone | String | Yes | Max 20 ký tự, không được trống | Số điện thoại của user |
| newPassword | String | Yes | Min 6 ký tự, không được trống | Mật khẩu mới |

## Response

### Success Response (200 OK)

```json
{
    "success": true,
    "message": "Đổi mật khẩu thành công"
}
```

### Error Responses

#### 1. Số điện thoại trống (400 Bad Request)

```json
{
    "success": false,
    "message": "Số điện thoại không được để trống"
}
```

#### 2. Mật khẩu mới trống (400 Bad Request)

```json
{
    "success": false,
    "message": "Mật khẩu mới không được để trống"
}
```

#### 3. Mật khẩu quá ngắn (400 Bad Request)

```json
{
    "success": false,
    "message": "Mật khẩu phải có ít nhất 6 ký tự"
}
```

#### 4. Không tìm thấy user (400 Bad Request)

```json
{
    "success": false,
    "message": "Không tìm thấy người dùng với số điện thoại: 0123456789"
}
```

#### 5. Tài khoản bị khóa (400 Bad Request)

```json
{
    "success": false,
    "message": "Tài khoản đã bị khóa, không thể đổi mật khẩu"
}
```

#### 6. Lỗi server (500 Internal Server Error)

```json
{
    "success": false,
    "message": "Lỗi khi đổi mật khẩu: [error details]"
}
```

## Testing với Postman

### Bước 1: Tạo request mới

1. Mở Postman
2. Tạo request mới với method **POST**
3. URL: `http://localhost:8089/api/password/change`

### Bước 2: Cấu hình Headers

1. Chọn tab **Headers**
2. Thêm header:
   - Key: `Content-Type`
   - Value: `application/json`

### Bước 3: Cấu hình Body

1. Chọn tab **Body**
2. Chọn **raw**
3. Chọn **JSON** từ dropdown
4. Nhập JSON:

```json
{
    "phone": "0987654321",
    "newPassword": "newPassword123"
}
```

### Bước 4: Gửi request

Click **Send** và xem kết quả

## Testing với cURL

```bash
curl -X POST http://localhost:8089/api/password/change \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "0987654321",
    "newPassword": "newPassword123"
  }'
```

## Frontend Integration

### JavaScript/Fetch Example

```javascript
async function changePassword(phone, newPassword) {
    try {
        const response = await fetch('http://localhost:8089/api/password/change', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                phone: phone,
                newPassword: newPassword
            })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            console.log('Success:', data.message);
            alert('Đổi mật khẩu thành công!');
            // Redirect to login page
            window.location.href = '/login';
        } else {
            console.error('Error:', data.message);
            alert('Lỗi: ' + data.message);
        }
    } catch (error) {
        console.error('Network error:', error);
        alert('Lỗi kết nối: ' + error.message);
    }
}

// Usage
changePassword('0987654321', 'newPassword123');
```

### Axios Example

```javascript
import axios from 'axios';

async function changePassword(phone, newPassword) {
    try {
        const response = await axios.post('http://localhost:8089/api/password/change', {
            phone: phone,
            newPassword: newPassword
        });

        if (response.data.success) {
            console.log('Success:', response.data.message);
            alert('Đổi mật khẩu thành công!');
            // Redirect to login page
            window.location.href = '/login';
        }
    } catch (error) {
        if (error.response) {
            // Server responded with error
            console.error('Error:', error.response.data.message);
            alert('Lỗi: ' + error.response.data.message);
        } else {
            // Network error
            console.error('Network error:', error.message);
            alert('Lỗi kết nối: ' + error.message);
        }
    }
}

// Usage
changePassword('0987654321', 'newPassword123');
```

### React Example with Form

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function ChangePasswordForm() {
    const [phone, setPhone] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage('');

