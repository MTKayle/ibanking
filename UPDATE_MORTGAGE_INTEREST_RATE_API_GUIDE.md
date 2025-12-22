# API Cập Nhật Lãi Suất Vay Thế Chấp (Update Mortgage Interest Rate)

## Mô tả
API này cho phép nhân viên ngân hàng (OFFICER) cập nhật lãi suất vay thế chấp theo kỳ hạn. **Lưu ý:** API chỉ cập nhật lãi suất và mô tả, không thay đổi kỳ hạn (minMonths, maxMonths).

## Endpoint
```
PUT /api/mortgage/interest-rates/update
```

## Phân quyền
- **Role required:** `OFFICER`
- Chỉ nhân viên ngân hàng mới có quyền cập nhật lãi suất

## Request Body
```json
{
    "rateId": 1,
    "interestRate": 7.5,
    "description": "Lãi suất vay 12-24 tháng (đã điều chỉnh)"
}
```

### Các trường dữ liệu:

| Trường | Kiểu | Bắt buộc | Mô tả |
|--------|------|----------|-------|
| `rateId` | Long | Có | ID của lãi suất cần cập nhật |
| `interestRate` | BigDecimal | Có | Lãi suất năm (%) (phải > 0.0001) |
| `description` | String | Không | Mô tả về lãi suất |

## Response

### Success Response (200 OK)
```json
{
    "success": true,
    "message": "Cập nhật lãi suất thành công",
    "data": {
        "rateId": 1,
        "minMonths": 12,
        "maxMonths": 24,
        "interestRate": 7.5,
        "description": "Lãi suất vay 12-24 tháng (đã điều chỉnh)"
    }
}
```

### Error Response (400 Bad Request)
```json
{
    "success": false,
    "message": "Lỗi khi cập nhật lãi suất: Không tìm thấy lãi suất với ID: 999"
}
```

## Các trường hợp lỗi

### 1. Không tìm thấy lãi suất
**Lỗi:** `Không tìm thấy lãi suất với ID: {rateId}`
- Xảy ra khi: ID lãi suất không tồn tại trong hệ thống

### 2. Validation errors
- `Rate ID không được để trống`
- `Lãi suất không được để trống`
- `Lãi suất phải lớn hơn 0`

### 3. Không có quyền truy cập (403 Forbidden)
**Lỗi:** `Access Denied`
- Xảy ra khi: User không có role OFFICER

## Ví dụ sử dụng

### 1. Cập nhật lãi suất vay 12-24 tháng
```bash
curl -X PUT http://localhost:8080/api/mortgage/interest-rates/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "rateId": 1,
    "interestRate": 7.5,
    "description": "Lãi suất vay 12-24 tháng (đã điều chỉnh)"
  }'
```

### 2. Cập nhật lãi suất vay trên 120 tháng
```bash
curl -X PUT http://localhost:8080/api/mortgage/interest-rates/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "rateId": 4,
    "interestRate": 10.0,
    "description": "Lãi suất vay trên 120 tháng (đã điều chỉnh)"
  }'
```

### 3. Postman Request
1. **Method:** PUT
2. **URL:** `http://localhost:8080/api/mortgage/interest-rates/update`
3. **Headers:**
   - `Content-Type: application/json`
   - `Authorization: Bearer YOUR_JWT_TOKEN`
4. **Body (raw JSON):**
```json
{
    "rateId": 2,
    "interestRate": 8.5,
    "description": "Lãi suất vay 24-60 tháng (đã điều chỉnh)"
}
```

## Lưu ý quan trọng

1. **Phân quyền:** Chỉ nhân viên (OFFICER) mới có quyền cập nhật lãi suất
2. **Chỉ cập nhật lãi suất:** API này chỉ cho phép cập nhật `interestRate` và `description`, không thay đổi kỳ hạn (`minMonths`, `maxMonths`)
3. **Lãi suất:** Nhập theo đơn vị phần trăm năm (ví dụ: 7.5 = 7.5%/năm)
4. **Ảnh hưởng:** Chỉ ảnh hưởng đến các khoản vay mới được tạo sau khi cập nhật, không ảnh hưởng đến các khoản vay đã tồn tại
5. **Kỳ hạn không đổi:** Nếu cần thay đổi kỳ hạn (minMonths, maxMonths), cần tạo một record lãi suất mới thay vì cập nhật

## API liên quan

### Lấy danh sách tất cả lãi suất
```
GET /api/mortgage/interest-rates
```
Dùng để xem danh sách các lãi suất hiện có trước khi cập nhật.

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "rateId": 1,
            "minMonths": 12,
            "maxMonths": 24,
            "interestRate": 7.5,
            "description": "Lãi suất vay 12-24 tháng"
        },
        {
            "rateId": 2,
            "minMonths": 24,
            "maxMonths": 60,
            "interestRate": 8.5,
            "description": "Lãi suất vay 24-60 tháng"
        }
    ]
}
```

## Quy trình nghiệp vụ

1. **Nhân viên đăng nhập** với role OFFICER
2. **Lấy danh sách lãi suất** hiện có (GET `/api/mortgage/interest-rates`)
3. **Chọn lãi suất** cần cập nhật (lấy `rateId`)
4. **Cập nhật lãi suất mới** (chỉ thay đổi interestRate và description)
5. **Hệ thống kiểm tra** validation và quyền truy cập
6. **Lưu thông tin** vào database
7. **Trả về kết quả** cập nhật

## Database Table
```sql
CREATE TABLE mortgage_interest_rates (
    rate_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    min_months INT NOT NULL,
    max_months INT,
    interest_rate DECIMAL(10,4) NOT NULL,
    description VARCHAR(100)
);
```

## Change Log
- **v1.0.0** (2025-12-23): Tạo API cập nhật lãi suất vay thế chấp cho OFFICER (chỉ cập nhật interestRate và description)
