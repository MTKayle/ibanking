# Hướng dẫn cấu hình Cloudinary cho iBanking

## 1. Đăng ký tài khoản Cloudinary

1. Truy cập: https://cloudinary.com/users/register_free
2. Điền thông tin đăng ký:
   - Email
   - Password
   - Cloud name (tên duy nhất cho tài khoản của bạn)
3. Xác nhận email
4. Đăng nhập vào Dashboard

## 2. Lấy thông tin API credentials

1. Sau khi đăng nhập, bạn sẽ thấy Dashboard với thông tin:

```
Account Details
├── Cloud name: dxyz123abc
├── API Key: 123456789012345
└── API Secret: AbcDefGhiJklMnoPqrStuvWxyz (Click "Reveal" để xem)
```

2. Click vào icon "eye" bên cạnh API Secret để hiển thị

## 3. Cấu hình trong project

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

**Ví dụ:**
```properties
cloudinary.cloud-name=dxyz123abc
cloudinary.api-key=123456789012345
cloudinary.api-secret=AbcDefGhiJklMnoPqrStuvWxyz
```

## 4. Giới hạn của Free tier

- **Storage**: 25 GB
- **Bandwidth**: 25 GB/tháng
- **Transformations**: 25,000/tháng
- **Requests**: Không giới hạn

## 5. Cấu trúc lưu trữ ảnh

Ảnh sẽ được lưu theo cấu trúc:

```
cloudinary://
└── ibanking/
    └── users/
        ├── 1/
        │   ├── {uuid}.jpg
        │   └── {uuid}.jpg
        ├── 2/
        │   └── {uuid}.jpg
        └── 3/
            └── {uuid}.jpg
```

- Mỗi user có folder riêng theo `userId`
- Mỗi ảnh có tên duy nhất (UUID)
- Format mặc định: JPG

## 6. Quản lý ảnh trên Cloudinary

1. Truy cập: https://console.cloudinary.com/console/media_library
2. Tìm folder `ibanking/users/`
3. Có thể:
   - Xem ảnh
   - Download ảnh
   - Xóa ảnh
   - Transform ảnh (resize, crop, filter...)

## 7. Test API Upload

Sau khi cấu hình xong, test API đăng ký với xác thực khuôn mặt:

**URL**: `POST http://localhost:8089/api/auth/register-with-face`

**Body (form-data)**:
- `registerRequest`: JSON string với thông tin đăng ký
- `cccdPhoto`: File ảnh CCCD
- `selfiePhoto`: File ảnh selfie

Nếu thành công, ảnh selfie sẽ được upload lên Cloudinary và URL sẽ được lưu vào database.

## 8. URL ảnh trả về

URL ảnh có dạng:
```
https://res.cloudinary.com/dxyz123abc/image/upload/v1234567890/ibanking/users/1/abc-def-ghi.jpg
```

Có thể transform ảnh bằng cách thêm parameters:
- Resize: `.../w_200,h_200/...`
- Crop: `.../c_fill/...`
- Quality: `.../q_auto/...`

## 9. Bảo mật

**Lưu ý quan trọng:**
- ⚠️ KHÔNG commit API Secret lên Git
- ⚠️ Sử dụng environment variables cho production
- ⚠️ Có thể restrict upload từ specific domains

**Cách sử dụng environment variables:**

```properties
# application.properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

Sau đó set environment variables:
```bash
# Windows
set CLOUDINARY_CLOUD_NAME=dxyz123abc
set CLOUDINARY_API_KEY=123456789012345
set CLOUDINARY_API_SECRET=AbcDefGhiJklMnoPqrStuvWxyz

# Linux/Mac
export CLOUDINARY_CLOUD_NAME=dxyz123abc
export CLOUDINARY_API_KEY=123456789012345
export CLOUDINARY_API_SECRET=AbcDefGhiJklMnoPqrStuvWxyz
```

## 10. Troubleshooting

### Lỗi: "Invalid cloud_name"
- Kiểm tra lại cloud name có đúng không
- Cloud name phân biệt hoa thường

### Lỗi: "Invalid API Key"
- Kiểm tra lại API Key
- Đảm bảo không có khoảng trắng thừa

### Lỗi: "Upload failed"
- Kiểm tra kích thước file (max 10MB)
- Kiểm tra format file (jpg, png, gif...)
- Kiểm tra quota còn lại

### Lỗi: "Cloudinary service chưa được cấu hình"
- Đảm bảo đã cấu hình đầy đủ 3 properties
- Restart lại Spring Boot application

## 11. Monitoring

Theo dõi usage tại: https://console.cloudinary.com/console/usage

Bạn có thể xem:
- Storage used
- Bandwidth used
- Transformations used
- Credits remaining

---

**Hỗ trợ thêm:** https://cloudinary.com/documentation

