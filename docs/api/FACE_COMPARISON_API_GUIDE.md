# Face Comparison API Guide

## Mô tả
API này cho phép frontend gửi một ảnh khuôn mặt để so sánh với ảnh đã lưu trong `photoURL` của user trong database. Hệ thống sẽ sử dụng Face++ API để so sánh và trả về kết quả dựa trên ngưỡng `confidence.threshold` đã cấu hình.

## Configuration

Trong file `application.properties`, đảm bảo đã cấu hình:

```properties
# Face++ API Configuration
faceplus.api.key=d-QiKNTAM2_wslV2YGWCnULZD-tWdpao
faceplus.api.secret=TbPVJBDBZc959HRCn4t-htl3ezYWugg2
faceplus.api.url=https://api-us.faceplusplus.com/facepp/v3/compare
faceplus.confidence.threshold=70.0
```

**Ngưỡng confidence**: 
- Mặc định là 70.0
- Nếu độ tương đồng >= 70.0% → Xác thực thành công
- Nếu độ tương đồng < 70.0% → Xác thực thất bại

## API Endpoints

### 1. So sánh ảnh với user cụ thể (khuyến nghị)

**Endpoint:** `POST /api/face/compare/{userId}`

**Description:** So sánh ảnh khuôn mặt upload với ảnh đã lưu trong photoURL của user

**Parameters:**
- `userId` (path): ID của user cần so sánh
- `faceImage` (form-data): File ảnh khuôn mặt cần so sánh

**Request:**

```http
POST /api/face/compare/1
Content-Type: multipart/form-data

Form Data:
- faceImage: [File ảnh khuôn mặt]
```

**Response Success (200 OK):**

```json
{
    "success": true,
    "message": "Xác thực khuôn mặt thành công. Độ tương đồng: 85.50%",
    "confidence": 85.50,
    "matched": true
}
```

**Response Failed (400 Bad Request):**

```json
{
    "success": false,
    "message": "Xác thực khuôn mặt thất bại. Độ tương đồng: 45.30% (yêu cầu tối thiểu: 70.0%)",
    "confidence": 45.30,
    "matched": false
}
```

**Error Responses:**

1. User không tồn tại:
```json
{
    "success": false,
    "message": "Không tìm thấy người dùng với ID: 999"
}
```

2. User chưa có ảnh trong hệ thống:
```json
{
    "success": false,
    "message": "Người dùng chưa có ảnh khuôn mặt trong hệ thống"
}
```

3. Ảnh không hợp lệ:
```json
{
    "success": false,
    "message": "Ảnh không được để trống"
}
```

### 2. So sánh ảnh với user hiện tại (từ JWT token)

**Endpoint:** `POST /api/face/compare`

**Description:** So sánh ảnh với user hiện tại (lấy từ JWT token)

**Note:** Endpoint này hiện tại yêu cầu custom implementation để lấy userId từ JWT. Khuyến nghị sử dụng endpoint trên với userId cụ thể.

## Testing với Postman

### Bước 1: Tạo request mới

1. Mở Postman
2. Tạo request mới với method `POST`
3. URL: `http://localhost:8089/api/face/compare/1` (thay 1 bằng userId thực tế)

### Bước 2: Cấu hình Body

1. Chọn tab **Body**
2. Chọn **form-data**
3. Thêm key:
   - Key: `faceImage`
   - Type: **File** (chọn từ dropdown)
   - Value: Chọn file ảnh khuôn mặt từ máy tính

### Bước 3: Gửi request

Click **Send** và xem kết quả

## Testing với cURL

```bash
curl -X POST http://localhost:8089/api/face/compare/1 \
  -F "faceImage=@/path/to/your/face-image.jpg"
```

## Flow hoạt động

1. **FE gửi request:**
   - FE upload ảnh khuôn mặt người dùng
   - Kèm theo userId của người cần xác thực

2. **BE xử lý:**
   - Tìm user theo userId
   - Kiểm tra user có photoUrl không
   - Gọi Face++ API so sánh ảnh upload với photoUrl
   - Nhận về độ confidence (0-100)

3. **So sánh với threshold:**
   - Nếu confidence >= threshold (70.0) → matched = true
   - Nếu confidence < threshold → matched = false

4. **Trả về kết quả:**
   - Success: HTTP 200 với matched = true
   - Failed: HTTP 400 với matched = false

## Use Cases

### 1. Xác thực khuôn mặt khi đăng nhập

```javascript
// Frontend code example
const formData = new FormData();
formData.append('faceImage', faceImageFile);

const response = await fetch(`http://localhost:8089/api/face/compare/${userId}`, {
    method: 'POST',
    body: formData
});

const result = await response.json();

if (result.matched) {
    // Cho phép đăng nhập
    console.log('Face verified successfully');
} else {
    // Từ chối đăng nhập
    console.log('Face verification failed');
}
```

### 2. Xác thực trước khi thực hiện giao dịch nhạy cảm

```javascript
// Trước khi chuyển tiền số lượng lớn
const verifyFace = async (userId, faceImage) => {
    const formData = new FormData();
    formData.append('faceImage', faceImage);
    
    const response = await fetch(`/api/face/compare/${userId}`, {
        method: 'POST',
        body: formData
    });
    
    const result = await response.json();
    
    if (!result.matched) {
        throw new Error('Vui lòng xác thực khuôn mặt trước khi thực hiện giao dịch');
    }
    
    // Tiếp tục với giao dịch...
};
```

## Notes

1. **Yêu cầu về ảnh:**
   - Format: JPG, JPEG, PNG
   - Kích thước tối đa: 10MB (cấu hình trong application.properties)
   - Ảnh phải rõ nét, có ánh sáng tốt
   - Khuôn mặt phải nhìn thẳng vào camera

2. **Performance:**
   - Thời gian xử lý phụ thuộc vào Face++ API
   - Thường mất 1-3 giây cho mỗi lần so sánh

3. **Security:**
   - Nên thêm rate limiting để tránh abuse
   - Có thể thêm JWT authentication cho endpoint này
   - Log tất cả các lần xác thực thất bại

4. **Threshold tuning:**
   - Threshold 70.0 là mức cân bằng giữa security và user experience
   - Có thể điều chỉnh trong `application.properties`
   - Threshold cao hơn = an toàn hơn nhưng dễ reject người dùng thật
   - Threshold thấp hơn = dễ pass hơn nhưng có thể cho phép người giả mạo

## Troubleshooting

### Lỗi: "Người dùng chưa có ảnh khuôn mặt trong hệ thống"

**Nguyên nhân:** User chưa upload ảnh hoặc photoUrl null

**Giải pháp:** 
- Yêu cầu user upload ảnh qua API đăng ký hoặc cập nhật profile
- Sử dụng endpoint `/api/auth/register-with-face` để đăng ký với ảnh

### Lỗi: "Face++ API error"

**Nguyên nhân:** Lỗi từ Face++ API (API key sai, quota hết, ảnh không hợp lệ)

**Giải pháp:**
- Kiểm tra API key và secret
- Kiểm tra quota của Face++ account
- Đảm bảo ảnh có chứa khuôn mặt rõ ràng

### Confidence thấp bất thường

**Nguyên nhân:** 
- Ảnh bị mờ hoặc góc chụp khác nhau
- Ánh sáng không tốt
- Người dùng thay đổi ngoại hình (râu, kính, tóc...)

**Giải pháp:**
- Yêu cầu chụp lại ảnh với điều kiện tốt hơn
- Cân nhắc giảm threshold nếu cần thiết
- Cập nhật lại ảnh trong photoUrl

