# QR Code System - Complete Guide

## Tổng quan hệ thống

Hệ thống VietQR bao gồm 2 phần chính:

### 1. **Tạo mã QR** (User A - người nhận tiền)
- API: `POST /api/accounts/checking/qr-code`
- Tạo mã QR theo chuẩn VietQR
- Trả về ảnh PNG trực tiếp
- Có thể tạo Static QR (không số tiền) hoặc Dynamic QR (có số tiền)

### 2. **Quét mã QR** (User B - người chuyển tiền)
- API: `POST /api/qr/scan`
- Parse nội dung QR
- Validate account và bank
- Trả về thông tin để thực hiện chuyển tiền

## Quy trình hoàn chỉnh

```
┌─────────────┐                    ┌─────────────┐
│   User A    │                    │   User B    │
│ (Nhận tiền) │                    │(Chuyển tiền)│
└──────┬──────┘                    └──────┬──────┘
       │                                  │
       │ 1. Tạo QR code                   │
       │ POST /api/accounts/checking/qr-code
       │ {"amount": 500000}               │
       │                                  │
       ├──────────►PNG Image              │
       │           QR Code                │
       │                                  │
       │         2. Hiển thị QR           │
       │         trên màn hình            │
       │                                  │
       │                          3. Quét QR
       │                          (Camera)│
       │                                  │
       │                          4. Parse QR content
       │                          POST /api/qr/scan
       │                          {"qrContent": "..."}
       │                                  │
       │              5. Response         │
       │◄─────────────────────────────────┤
       │              {                   │
       │                accountNumber,    │
       │                bankName,         │
       │                amount,           │
       │                ...               │
       │              }                   │
       │                                  │
       │                          6. Hiển thị form
       │                          chuyển tiền      │
       │                                  │
       │                          7. Xác nhận
       │                          POST /api/payment/transfer
       │                                  │
       ├──────────────────────────────────┤
       │         8. Chuyển tiền           │
       │         thành công               │
       └──────────────────────────────────┘
```

## Database Schema

### Banks Table
```sql
CREATE TABLE banks (
    bank_id BIGSERIAL PRIMARY KEY,
    bank_bin VARCHAR(10) UNIQUE NOT NULL,
    bank_code VARCHAR(20) UNIQUE NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Users Table (Updated)
```sql
ALTER TABLE users ADD COLUMN bank_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_bank 
    FOREIGN KEY (bank_id) REFERENCES banks(bank_id);
```

**Quan hệ:** Mỗi user chỉ có **1 ngân hàng**

## API Endpoints

### 1. Tạo QR Code

**Endpoint:** `POST /api/accounts/checking/qr-code`

**Request:**
```json
{
  "amount": 500000,
  "description": "Thanh toan hoa don"
}
```

**Response:** PNG image (binary)

**Headers:**
- `X-Account-Number`: Số tài khoản
- `X-Account-Holder`: Tên chủ tài khoản
- `X-Amount`: Số tiền

### 2. Quét QR Code

**Endpoint:** `POST /api/qr/scan`

**Request:**
```json
{
  "qrContent": "00020101021238570010A00000072701149704220102123456789053037045406500000..."
}
```

**Response:**
```json
{
  "accountNumber": "1234567890",
  "accountHolderName": "Nguyen Van A",
  "bankBin": "970422",
  "bankCode": "MBBANK",
  "bankName": "Ngan hang Quan doi",
  "amount": 500000,
  "description": "Thanh toan hoa don",
  "userId": 1,
  "accountType": "checking"
}
```

## Setup Instructions

### 1. Run Database Migrations

```bash
# Enable Flyway in application.properties
spring.flyway.enabled=true

# Restart application to run migrations
# V13__create_banks_table.sql
# V14__add_bank_id_to_users.sql
```

### 2. Seed Bank Data

Banks table đã được seed tự động với 18 ngân hàng Việt Nam khi chạy migration V13.

### 3. Assign Bank to Users

Khi user đăng ký hoặc cập nhật profile, cần gán bank_id:

```sql
-- Ví dụ: Gán MB Bank cho user
UPDATE users 
SET bank_id = (SELECT bank_id FROM banks WHERE bank_code = 'MBBANK')
WHERE user_id = 1;
```

### 4. Configure VietQR

Trong `application.properties`:

```properties
# VietQR Configuration
vietqr.bank.bin=970422
vietqr.bank.name=My iBank
vietqr.bank.code=MYIBANK
```

## Code Examples

### React Native - Complete Flow

```javascript
import React, { useState, useEffect } from 'react';
import { View, Text, Button, Image, Alert } from 'react-native';
import { BarCodeScanner } from 'expo-barcode-scanner';
import axios from 'axios';

const QRPaymentScreen = ({ navigation }) => {
  const [hasPermission, setHasPermission] = useState(null);
  const [scanned, setScanned] = useState(false);

  useEffect(() => {
    (async () => {
      const { status } = await BarCodeScanner.requestPermissionsAsync();
      setHasPermission(status === 'granted');
    })();
  }, []);

  const handleQRScanned = async ({ data }) => {
    setScanned(true);
    
    try {
      // Step 1: Scan QR and get account info
      const response = await axios.post(
        'http://localhost:8089/api/qr/scan',
        { qrContent: data },
        {
          headers: {
            'Authorization': `Bearer ${yourToken}`,
            'Content-Type': 'application/json'
          }
        }
      );
      
      const recipientInfo = response.data;
      
      // Step 2: Show confirmation dialog
      Alert.alert(
        'Xác nhận chuyển tiền',
        `Người nhận: ${recipientInfo.accountHolderName}\n` +
        `Ngân hàng: ${recipientInfo.bankName}\n` +
        `Số tiền: ${recipientInfo.amount?.toLocaleString()} VND\n` +
        `Nội dung: ${recipientInfo.description || ''}`,
        [
          { text: 'Hủy', style: 'cancel', onPress: () => setScanned(false) },
          {
            text: 'Xác nhận',
            onPress: () => handleTransfer(recipientInfo)
          }
        ]
      );
      
    } catch (error) {
      Alert.alert('Lỗi', error.response?.data?.message || 'Không thể quét QR');
      setScanned(false);
    }
  };

  const handleTransfer = async (recipientInfo) => {
    try {
      // Step 3: Execute transfer
      await axios.post(
        'http://localhost:8089/api/payment/transfer',
        {
          fromAccountNumber: yourAccountNumber,
          toAccountNumber: recipientInfo.accountNumber,
          amount: recipientInfo.amount,
          description: recipientInfo.description
        },
        {
          headers: {
            'Authorization': `Bearer ${yourToken}`,
            'Content-Type': 'application/json'
          }
        }
      );
      
      Alert.alert('Thành công', 'Chuyển tiền thành công!');
      navigation.goBack();
      
    } catch (error) {
      Alert.alert('Lỗi', error.response?.data?.message || 'Chuyển tiền thất bại');
    }
  };

  if (hasPermission === null) {
    return <Text>Đang yêu cầu quyền camera...</Text>;
  }

  if (hasPermission === false) {
    return <Text>Không có quyền truy cập camera</Text>;
  }

  return (
    <View style={{ flex: 1 }}>
      <BarCodeScanner
        onBarCodeScanned={scanned ? undefined : handleQRScanned}
        style={{ flex: 1 }}
      />
      {scanned && (
        <Button title="Quét lại" onPress={() => setScanned(false)} />
      )}
    </View>
  );
};

export default QRPaymentScreen;
```

## Validation Rules

### QR Scan API

✅ **Account Validation:**
- Account number phải tồn tại trong database
- Account type phải là `checking`
- Account status phải là `active`

✅ **Bank Validation:**
- Bank BIN trong QR phải khớp với bank của user (nếu user đã có bank)
- Bank BIN phải tồn tại trong bảng banks

✅ **QR Content Validation:**
- Phải parse được theo chuẩn VietQR
- Phải có account number trong QR

## Error Handling

| Error Code | Message | Giải pháp |
|------------|---------|-----------|
| 400 | Nội dung QR không hợp lệ | Quét lại QR hoặc check format |
| 400 | Không phải tài khoản checking | Chỉ hỗ trợ checking account |
| 400 | Tài khoản không hoạt động | Liên hệ hỗ trợ để kích hoạt |
| 400 | Mã ngân hàng không khớp | QR không đúng ngân hàng |
| 404 | Không tìm thấy tài khoản | Account number không tồn tại |
| 404 | Không tìm thấy ngân hàng | Bank BIN không hợp lệ |

## Testing

### Postman Testing

1. **Login** để lấy token
2. **Generate QR** (User A):
   ```
   POST /api/accounts/checking/qr-code
   Body: {"amount": 500000, "description": "Test"}
   ```
3. **Decode QR** để lấy qrContent (dùng online tool hoặc mobile)
4. **Scan QR** (User B):
   ```
   POST /api/qr/scan
   Body: {"qrContent": "..."}
   ```
5. **Verify** response chứa đúng thông tin

### Unit Test Example

```java
@Test
void testScanQRCode_ValidQR_ReturnsAccountInfo() {
    // Arrange
    QRScanRequest request = new QRScanRequest();
    request.setQrContent("00020101021238570010A000000727...");
    
    // Act
    QRScanResponse response = qrScanService.scanQRCode(request);
    
    // Assert
    assertNotNull(response);
    assertEquals("1234567890", response.getAccountNumber());
    assertEquals("checking", response.getAccountType());
}
```

## Security Considerations

🔒 **Authentication:** Tất cả API đều yêu cầu JWT token  
🔒 **Authorization:** Chỉ CUSTOMER/OFFICER/ADMIN mới access được  
🔒 **Validation:** Validate account type, status, bank info  
🔒 **Transaction Safety:** Sử dụng pessimistic lock khi transfer  

## Files Created

### Database Migrations
- ✅ `V13__create_banks_table.sql`
- ✅ `V14__add_bank_id_to_users.sql`

### Entities
- ✅ `Bank.java`
- ✅ `User.java` (updated with bank relationship)

### Repositories
- ✅ `BankRepository.java`

### DTOs
- ✅ `QRCodeRequest.java`
- ✅ `QRCodeResponse.java`
- ✅ `QRScanRequest.java`
- ✅ `QRScanResponse.java`

### Services
- ✅ `QRCodeService.java` & `QRCodeServiceImpl.java`
- ✅ `QRScanService.java` & `QRScanServiceImpl.java`

### Controllers
- ✅ `AccountController.java` (updated)
- ✅ `QRController.java`

### Utilities
- ✅ `VietQRUtils.java` - Generate VietQR content
- ✅ `VietQRParser.java` - Parse VietQR content

### Documentation
- ✅ `VIETQR_API_GUIDE.md`
- ✅ `QR_SCAN_API_GUIDE.md`
- ✅ `VIETQR_POSTMAN_COLLECTION.json`

## Next Steps

1. ✅ Enable Flyway và chạy migrations
2. ✅ Gán bank_id cho users hiện có
3. ✅ Test tạo QR code
4. ✅ Test quét QR code
5. ✅ Integrate với transfer API
6. ✅ Test end-to-end flow trên mobile app

