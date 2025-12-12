# VietQR Code API Documentation

## Tổng quan

API này cho phép tạo mã QR theo chuẩn VietQR (Vietnam QR Payment Standard) cho tài khoản checking của người dùng. Mã QR được tạo tuân thủ chuẩn EMVCo và sử dụng đúng BIN code của ngân hàng theo quy định của Ngân hàng Nhà nước Việt Nam.

**⚠️ LƯU Ý QUAN TRỌNG:** API này trả về **ảnh PNG trực tiếp**, KHÔNG phải JSON. Thông tin tài khoản được gửi qua response headers.

## Tính năng

- Tạo mã QR tĩnh (Static QR): Không có số tiền cố định, người quét có thể nhập số tiền tùy ý
- Tạo mã QR động (Dynamic QR): Có số tiền và nội dung chuyển khoản được định sẵn
- Mã QR trả về dạng Base64 PNG image, dễ dàng hiển thị trên mobile app
- Tuân thủ chuẩn VietQR của Việt Nam
- Sử dụng đúng BIN code của ngân hàng

## Cấu hình

Trong file `application.properties`, bạn cần cấu hình thông tin ngân hàng:

```properties
# VietQR Configuration
# BIN code của ngân hàng theo quy định của Ngân hàng Nhà nước
# Ví dụ: 970422 = MB Bank, 970415 = Vietinbank, 970436 = Vietcombank
vietqr.bank.bin=970422
vietqr.bank.name=My iBank
vietqr.bank.code=MYIBANK
```

### Danh sách BIN code các ngân hàng Việt Nam

| BIN Code | Ngân hàng |
|----------|-----------|
| 970405 | Agribank |
| 970422 | MB Bank (Military Bank) |
| 970407 | Techcombank |
| 970415 | Vietinbank |
| 970436 | Vietcombank |
| 970418 | BIDV |
| 970403 | Sacombank |
| 970416 | ACB |
| 970432 | VPBank |
| 970423 | TPBank |
| 970441 | VIB |
| 970448 | OCB |
| 970426 | MSB |
| 970414 | Ocean Bank |
| 970409 | BAB |
| 970438 | BaoViet Bank |
| 970433 | VietBank |
| 970434 | Indovina Bank |

## API Endpoint

### Tạo mã QR cho tài khoản checking

**Endpoint:** `POST /api/accounts/checking/qr-code`

**Authorization:** Required (Bearer Token)

**Roles:** CUSTOMER, OFFICER, ADMIN

**Content-Type Response:** `image/png`

**Description:** Tạo mã QR VietQR cho tài khoản checking của user đang đăng nhập. User chỉ có thể tạo QR cho tài khoản của chính mình. API trả về ảnh PNG trực tiếp.

#### Request Body (Optional)

```json
{
  "amount": 500000,
  "description": "Thanh toan hoa don"
}
```

**Fields:**
- `amount` (BigDecimal, optional): Số tiền chuyển khoản. Nếu không có, tạo Static QR
- `description` (String, optional, max 500 chars): Nội dung chuyển khoản

**Lưu ý:** Request body có thể để trống hoặc null để tạo Static QR code.

#### Response

**Content-Type:** `image/png`

**Body:** Binary PNG image data (300x300 pixels)

**Response Headers:**
- `Content-Type`: `image/png`
- `Content-Length`: Size của ảnh (bytes)
- `X-Account-Number`: Số tài khoản (ví dụ: "1234567890")
- `X-Account-Holder`: Tên chủ tài khoản đã chuẩn hóa (ví dụ: "NGUYEN VAN A")
- `X-Amount`: Số tiền (chỉ có khi request có amount)

**Ví dụ Response Headers:**
```
Content-Type: image/png
Content-Length: 8543
X-Account-Number: 1234567890
X-Account-Holder: NGUYEN VAN A
X-Amount: 500000
```

#### Error Responses

**400 Bad Request:**
```json
{
  "message": "Tài khoản không hoạt động, không thể tạo mã QR"
}
```

**404 Not Found:**
```json
{
  "message": "Không tìm thấy tài khoản checking"
}
```

**401 Unauthorized:**
```json
{
  "message": "Unauthorized"
}
```

## Ví dụ sử dụng

### 1. Tạo Static QR Code (không có số tiền cố định)

**Request:**
```bash
curl -X POST http://localhost:8089/api/accounts/checking/qr-code \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' \
  --output qr-code.png
```

**Response:**
- Ảnh PNG được lưu vào file `qr-code.png`
- Thông tin tài khoản trong headers

### 2. Tạo Dynamic QR Code (có số tiền và nội dung)

**Request:**
```bash
curl -X POST http://localhost:8089/api/accounts/checking/qr-code \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000000,
    "description": "Thanh toan tien dien thang 12"
  }' \
  --output qr-code-1m.png
```

**Response:**
- Ảnh PNG được lưu vào file `qr-code-1m.png`
- Headers chứa thông tin số tiền và tài khoản

### 3. Hiển thị QR Code trên Mobile App (React Native example)

```javascript
import React, { useState } from 'react';
import { View, Image, Button, Text, Alert } from 'react-native';
import axios from 'axios';

const QRCodeScreen = () => {
  const [qrImageUri, setQrImageUri] = useState(null);
  const [accountInfo, setAccountInfo] = useState({});
  
  const generateQR = async (amount, description) => {
    try {
      const response = await axios.post(
        'http://localhost:8089/api/accounts/checking/qr-code',
        { amount, description },
        {
          headers: {
            'Authorization': `Bearer ${yourJwtToken}`,
            'Content-Type': 'application/json'
          },
          responseType: 'arraybuffer' // Quan trọng: để nhận binary data
        }
      );
      
      // Lấy thông tin từ headers
      const accountNumber = response.headers['x-account-number'];
      const accountHolder = response.headers['x-account-holder'];
      const responseAmount = response.headers['x-amount'];
      
      setAccountInfo({
        accountNumber,
        accountHolder,
        amount: responseAmount
      });
      
      // Convert binary data to base64 để hiển thị
      const base64 = btoa(
        new Uint8Array(response.data)
          .reduce((data, byte) => data + String.fromCharCode(byte), '')
      );
      
      setQrImageUri(`data:image/png;base64,${base64}`);
      
    } catch (error) {
      console.error('Error generating QR code:', error);
      Alert.alert('Lỗi', 'Không thể tạo mã QR');
    }
  };
  
  return (
    <View style={{ padding: 20 }}>
      <Button 
        title="Tạo QR tĩnh" 
        onPress={() => generateQR(null, null)} 
      />
      <Button 
        title="Tạo QR 500,000 VND" 
        onPress={() => generateQR(500000, "Thanh toan")} 
      />
      
      {qrImageUri && (
        <View style={{ marginTop: 20, alignItems: 'center' }}>
          <Image 
            source={{ uri: qrImageUri }}
            style={{ width: 300, height: 300 }}
          />
          <Text style={{ marginTop: 10, fontSize: 16 }}>
            Số TK: {accountInfo.accountNumber}
          </Text>
          <Text style={{ fontSize: 16 }}>
            Tên: {accountInfo.accountHolder}
          </Text>
          {accountInfo.amount && (
            <Text style={{ fontSize: 16, fontWeight: 'bold' }}>
              Số tiền: {parseInt(accountInfo.amount).toLocaleString()} VND
            </Text>
          )}
        </View>
      )}
    </View>
  );
};

export default QRCodeScreen;
```

### 4. Hiển thị QR Code trên Web (HTML/JavaScript)

```html
<!DOCTYPE html>
<html>
<head>
    <title>VietQR Generator</title>
</head>
<body>
    <button onclick="generateQR(null, null)">Tạo QR tĩnh</button>
    <button onclick="generateQR(500000, 'Thanh toan')">Tạo QR 500k</button>
    
    <div id="qr-container" style="margin-top: 20px;">
        <img id="qr-image" style="width: 300px; height: 300px; display: none;" />
        <div id="account-info"></div>
    </div>

    <script>
        async function generateQR(amount, description) {
            const token = localStorage.getItem('accessToken');
            
            const response = await fetch('http://localhost:8089/api/accounts/checking/qr-code', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ amount, description })
            });
            
            // Lấy thông tin từ headers
            const accountNumber = response.headers.get('X-Account-Number');
            const accountHolder = response.headers.get('X-Account-Holder');
            const responseAmount = response.headers.get('X-Amount');
            
            // Hiển thị ảnh QR
            const blob = await response.blob();
            const imageUrl = URL.createObjectURL(blob);
            
            document.getElementById('qr-image').src = imageUrl;
            document.getElementById('qr-image').style.display = 'block';
            
            // Hiển thị thông tin
            let info = `<p>Số TK: ${accountNumber}</p>`;
            info += `<p>Tên: ${accountHolder}</p>`;
            if (responseAmount) {
                info += `<p>Số tiền: ${parseInt(responseAmount).toLocaleString()} VND</p>`;
            }
            document.getElementById('account-info').innerHTML = info;
        }
    </script>
</body>
</html>
```

### 5. Sử dụng với Postman

1. Gọi API Login để lấy token
2. Copy accessToken vào biến `{{accessToken}}`
3. Gọi endpoint `POST /api/accounts/checking/qr-code`
4. Chọn tab "Send and Download" hoặc "Preview" để xem ảnh
5. Xem thông tin tài khoản trong tab "Headers" của response

## Quy trình sử dụng QR Code

1. **User tạo mã QR:**
   - User đăng nhập vào app
   - Gọi API `POST /api/accounts/checking/qr-code` với hoặc không có amount/description
   - Nhận về mã QR dạng Base64 và hiển thị trên màn hình

2. **Người khác quét mã QR:**
   - Người chuyển tiền sử dụng app ngân hàng (VietQR-compatible app)
   - Quét mã QR bằng camera
   - App sẽ tự động điền:
     - Số tài khoản người nhận
     - Tên người nhận
     - Số tiền (nếu là Dynamic QR)
     - Nội dung chuyển khoản (nếu có)
   - Người chuyển xác nhận và thực hiện giao dịch

3. **Xử lý giao dịch:**
   - Sau khi chuyển tiền thành công, có thể sử dụng API `/api/payment/transfer` để ghi nhận giao dịch trong hệ thống

## Lưu ý kỹ thuật

### Response Format

- **Content-Type:** `image/png`
- **Body:** Binary PNG image (300x300 pixels)
- **Thông tin bổ sung:** Được gửi qua Custom Headers (X-Account-Number, X-Account-Holder, X-Amount)

### Xử lý Response

**Trong Mobile App:**
```javascript
// responseType phải là 'arraybuffer' hoặc 'blob'
axios.post(url, data, { 
  responseType: 'arraybuffer',
  headers: { Authorization: `Bearer ${token}` }
})
```

**Trong Browser:**
```javascript
// Sử dụng Blob để tạo Object URL
const blob = await response.blob();
const imageUrl = URL.createObjectURL(blob);
```

**Lưu vào file:**
```bash
curl ... --output qr-code.png
```

## Troubleshooting

**Lỗi: Response không phải là ảnh**
- Kiểm tra `Content-Type` header có phải `image/png` không
- Đảm bảo không có lỗi authentication (401)
- Kiểm tra request có đúng format không

**Ảnh bị lỗi hoặc không hiển thị:**
- Kiểm tra responseType trong request (phải là 'arraybuffer' hoặc 'blob')
- Đảm bảo đã convert binary đúng cách sang base64 hoặc blob URL

**Không lấy được thông tin tài khoản:**
- Thông tin nằm trong response headers (X-Account-Number, X-Account-Holder, X-Amount)
- Kiểm tra CORS settings nếu gọi từ browser
