# Face Recognition Login - API Documentation

## Tổng quan
API login-with-face đã được cập nhật để hoạt động theo luồng mới:
- **Frontend**: Chỉ cần gửi ảnh khuôn mặt từ camera
- **Backend**: Tự động detect, encode và so khớp với tất cả users trong DB
- **Kết quả**: Đăng nhập vào user có độ tương đồng cao nhất

## Thay đổi chính

### 1. Thêm field `face_embedding` vào User Entity
- Lưu trữ face_token từ Face++ API
- Được tạo tự động khi đăng ký với register-with-face

### 2. API Register With Face (Đã cập nhật)
**Endpoint**: `POST /api/auth/register-with-face`

**Luồng xử lý**:
1. So sánh ảnh CCCD và selfie
2. Upload ảnh selfie lên Cloudinary
3. **[MỚI]** Encode ảnh thành embedding và lưu vào DB
4. Trả về JWT token

**Request** (multipart/form-data):
```
phone: String
email: String
password: String
fullName: String
cccdNumber: String
dateOfBirth: String (optional, format: YYYY-MM-DD)
permanentAddress: String (optional)
temporaryAddress: String (optional)
cccdPhoto: File
selfiePhoto: File
```

### 3. API Login With Face (Đã sửa hoàn toàn)
**Endpoint**: `POST /api/auth/login-with-face`

**Thay đổi**:
- ❌ **Trước**: Cần `phone` + `facePhoto`
- ✅ **Bây giờ**: Chỉ cần `facePhoto`

**Luồng xử lý mới**:
1. Frontend gửi ảnh từ camera → Backend
2. Backend detect + encode ảnh → embedding
3. Lấy tất cả users có face_embedding trong DB
4. So khớp embedding với từng user (chỉ user đã bật face_recognition_enabled)
5. Tìm user có độ tương đồng cao nhất
6. Kiểm tra threshold (mặc định >= 70%)
7. Đăng nhập vào user đó

**Request** (multipart/form-data):
```
facePhoto: File (JPG, JPEG, PNG, max 5MB)
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "role": "customer"
}
```

**Error Response**:
```json
{
  "message": "Xác thực khuôn mặt thất bại. Độ tương đồng cao nhất: 65.50% (yêu cầu >= 70.00%)",
  "status": 401
}
```

## Cấu hình

### Application Properties
Thêm cấu hình cho Face++ Detect API:
```properties
# Face++ API Configuration
faceplus.api.key=YOUR_API_KEY
faceplus.api.secret=YOUR_API_SECRET
faceplus.api.url=https://api-us.faceplusplus.com/facepp/v3/compare
faceplus.api.detect.url=https://api-us.faceplusplus.com/facepp/v3/detect
faceplus.confidence.threshold=70.0
```

### Database Migration
Chạy migration để thêm column `face_embedding`:
```sql
-- V4__add_face_embedding_column.sql
ALTER TABLE users
ADD COLUMN face_embedding TEXT NULL;
```

## Testing với Postman/cURL

### 1. Đăng ký với Face Recognition
```bash
curl -X POST "http://localhost:8080/api/auth/register-with-face" ^
  -F "phone=0987654321" ^
  -F "email=test@example.com" ^
  -F "password=password123" ^
  -F "fullName=Nguyen Van B" ^
  -F "cccdNumber=001234567890" ^
  -F "dateOfBirth=1990-01-01" ^
  -F "cccdPhoto=@path/to/cccd.jpg" ^
  -F "selfiePhoto=@path/to/selfie.jpg"
```

### 2. Kích hoạt Face Recognition (quan trọng!)
User phải bật face_recognition_enabled = true:
```bash
curl -X PUT "http://localhost:8080/api/user-management/users/1/features" ^
  -H "Authorization: Bearer YOUR_TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"faceRecognitionEnabled\": true}"
```

### 3. Login với Face (không cần phone!)
```bash
curl -X POST "http://localhost:8080/api/auth/login-with-face" ^
  -F "facePhoto=@path/to/login-selfie.jpg"
```

## Lưu ý quan trọng

### Điều kiện để Login với Face thành công:
1. ✅ User đã đăng ký bằng `register-with-face`
2. ✅ User có `face_embedding` trong DB
3. ✅ User đã bật `face_recognition_enabled = true`
4. ✅ User không bị khóa (`is_locked = false`)
5. ✅ Ảnh upload có độ tương đồng >= threshold (70%)

### Xử lý nhiều users:
- Backend tự động so khớp với TẤT CẢ users có face_embedding
- Chỉ xét users đã bật face_recognition_enabled
- Tìm user có confidence cao nhất
- Log ra console để debug: `User <name> (<phone>) - Confidence: XX.XX%`

### Performance:
- Mỗi lần so khớp gọi Face++ API → có thể chậm nếu nhiều users
- Recommend: Cache embeddings hoặc dùng local face recognition model
- Có thể tối ưu bằng cách lọc users theo region/branch trước

## Troubleshooting

### "Không phát hiện được khuôn mặt trong ảnh"
- Đảm bảo ảnh có khuôn mặt rõ ràng
- Ảnh không bị mờ, tối hoặc nghiêng quá nhiều

### "Không tìm thấy người dùng nào đã đăng ký nhận diện khuôn mặt"
- Kiểm tra DB có users với face_embedding IS NOT NULL
- Kiểm tra face_recognition_enabled = true

### "Xác thực khuôn mặt thất bại"
- Confidence < 70%: Ảnh không khớp hoặc lighting khác biệt
- Thử chụp lại ảnh trong điều kiện ánh sáng tốt hơn
- Có thể giảm threshold trong application.properties (không khuyến khích)

## Face++ API Calls

### detectAndEncodeFace()
- **Endpoint**: `/facepp/v3/detect`
- **Input**: MultipartFile (image)
- **Output**: face_token (String)
- **Purpose**: Encode ảnh thành embedding để lưu DB

### compareEmbeddings()
- **Endpoint**: `/facepp/v3/compare`
- **Input**: face_token1, face_token2
- **Output**: confidence (0-100)
- **Purpose**: So sánh 2 embeddings

## Database Schema

```sql
CREATE TABLE users (
  user_id BIGSERIAL PRIMARY KEY,
  phone VARCHAR(20) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  cccd_number VARCHAR(50) UNIQUE NOT NULL,
  date_of_birth DATE,
  permanent_address VARCHAR(255),
  temporary_address VARCHAR(255),
  photo_url TEXT,
  face_embedding TEXT,  -- NEW FIELD
  role VARCHAR(20) NOT NULL,
  is_locked BOOLEAN DEFAULT FALSE,
  face_recognition_enabled BOOLEAN DEFAULT FALSE,
  smart_ekyc_enabled BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

## Security Notes
- Face token chỉ valid trong 24h (theo Face++ policy)
- Nên re-encode face_embedding định kỳ
- Implement rate limiting cho login-with-face API
- Log tất cả face login attempts để audit

