# QR Scan API Documentation

## Tổng quan

API này cho phép quét mã QR VietQR và lấy thông tin tài khoản người nhận để thực hiện chuyển tiền. API sẽ:
- Parse nội dung QR code VietQR
- Validate account number có tồn tại trong hệ thống
- Kiểm tra account type phải là checking
- Lấy thông tin ngân hàng của user
- Trả về đầy đủ thông tin để thực hiện chuyển tiền

## Database Schema

### Bảng Banks

```sql
CREATE TABLE banks (
    bank_id BIGSERIAL PRIMARY KEY,
    bank_bin VARCHAR(10) NOT NULL UNIQUE,
    bank_code VARCHAR(20) NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Dữ liệu mẫu:**
| bank_bin | bank_code | bank_name |
|----------|-----------|-----------|
| 970405 | AGRIBANK | Ngan hang Nong nghiep va Phat trien Nong thon Viet Nam |
| 970422 | MBBANK | Ngan hang Quan doi |
| 970436 | VIETCOMBANK | Ngan hang Ngoai thuong Viet Nam |

### User - Bank Relationship

- Mỗi user chỉ có **1 ngân hàng**
- User có thêm field `bank_id` (Foreign Key → banks.bank_id)
- Khi user đăng ký, cần chọn ngân hàng của mình

## API Endpoint

### Quét QR Code

**Endpoint:** `POST /api/qr/scan`

**Authorization:** Required (Bearer Token)

**Roles:** CUSTOMER, OFFICER, ADMIN

**Description:** Quét mã QR VietQR và lấy thông tin tài khoản người nhận. API sẽ validate và trả về thông tin đầy đủ để thực hiện chuyển tiền.

#### Request Body

```json
{
  "qrContent": "00020101021238570010A000000727011497042201021234567890530370454065000005802VN5913NGUYEN VAN A6006HA NOI6304XXXX"
}
```

**Fields:**
- `qrContent` (String, required): Nội dung QR code VietQR đã quét được

#### Response

**Success - 200 OK:**
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

**Response Fields:**
- `accountNumber`: Số tài khoản người nhận
- `accountHolderName`: Tên chủ tài khoản
- `bankBin`: Mã BIN ngân hàng
- `bankCode`: Mã ngân hàng (viết tắt)
- `bankName`: Tên đầy đủ ngân hàng
- `amount`: Số tiền (null nếu QR tĩnh)
- `description`: Nội dung chuyển khoản (null nếu không có)
- `userId`: ID của user nhận tiền
- `accountType`: Loại tài khoản (luôn là "checking")

#### Error Responses

**400 Bad Request - Nội dung QR không hợp lệ:**
```json
{
  "message": "Nội dung QR không hợp lệ"
}
```

**400 Bad Request - Không phải tài khoản checking:**
```json
{
  "message": "Tài khoản không phải là tài khoản checking. Chỉ hỗ trợ chuyển tiền vào tài khoản checking."
}
```

**400 Bad Request - Tài khoản không hoạt động:**
```json
{
  "message": "Tài khoản không hoạt động"
}
```

**400 Bad Request - Bank BIN không khớp:**
```json
{
  "message": "Mã ngân hàng trong QR không khớp với ngân hàng của tài khoản"
}
```

**404 Not Found - Không tìm thấy tài khoản:**
```json
{
  "message": "Không tìm thấy tài khoản: 1234567890"
}
```

**404 Not Found - Không tìm thấy ngân hàng:**
```json
{
  "message": "Không tìm thấy thông tin ngân hàng với BIN: 970422"
}
```

**401 Unauthorized:**
```json
{
  "message": "Unauthorized"
}
```

## Quy trình sử dụng

### 1. User A tạo QR code (người nhận tiền)

```bash
POST /api/accounts/checking/qr-code
Authorization: Bearer {token_user_A}
Content-Type: application/json

{
  "amount": 500000,
  "description": "Thanh toan hoa don"
}

# Response: PNG image của QR code
```

### 2. User B quét QR code (người chuyển tiền)

**a) Quét QR bằng camera (mobile app)**
- Sử dụng thư viện QR scanner (react-native-camera, expo-barcode-scanner, etc.)
- Lấy được chuỗi QR content

**b) Gọi API scan để lấy thông tin:**

```bash
POST /api/qr/scan
Authorization: Bearer {token_user_B}
Content-Type: application/json

{
  "qrContent": "00020101021238570010A000000727011497042201021234567890530370454065000005802VN5913NGUYEN VAN A6006HA NOI62200816Thanh toan hoa don6304XXXX"
}

# Response:
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

### 3. User B xác nhận và thực hiện chuyển tiền

Sử dụng thông tin từ response để điền vào form chuyển tiền:

```bash
POST /api/payment/transfer
Authorization: Bearer {token_user_B}
Content-Type: application/json

{
  "fromAccountNumber": "{user_B_account}",
  "toAccountNumber": "1234567890",
  "amount": 500000,
  "description": "Thanh toan hoa don"
}
```

## Ví dụ sử dụng

### React Native Example - Quét QR và hiển thị thông tin

```javascript
import React, { useState } from 'react';
import { View, Text, Button, Alert } from 'react-native';
import { BarCodeScanner } from 'expo-barcode-scanner';
import axios from 'axios';

const QRScanScreen = ({ navigation }) => {
  const [hasPermission, setHasPermission] = useState(null);
  const [scanned, setScanned] = useState(false);
  const [recipientInfo, setRecipientInfo] = useState(null);

  // Request camera permission
  useEffect(() => {
    (async () => {
      const { status } = await BarCodeScanner.requestPermissionsAsync();
      setHasPermission(status === 'granted');
    })();
  }, []);

  const handleBarCodeScanned = async ({ type, data }) => {
    setScanned(true);
    
    try {
      // Gọi API scan QR
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
      
      setRecipientInfo(response.data);
      
      // Hiển thị thông tin và cho phép chuyển tiền
      Alert.alert(
        'Thông tin người nhận',
        `Tên: ${response.data.accountHolderName}\n` +
        `Số TK: ${response.data.accountNumber}\n` +
        `Ngân hàng: ${response.data.bankName}\n` +
        `Số tiền: ${response.data.amount ? response.data.amount.toLocaleString() + ' VND' : 'Nhập số tiền'}`,
        [
          { text: 'Hủy', style: 'cancel' },
          {
            text: 'Chuyển tiền',
            onPress: () => navigation.navigate('Transfer', { recipientInfo: response.data })
          }
        ]
      );
      
    } catch (error) {
      Alert.alert('Lỗi', error.response?.data?.message || 'Không thể quét QR code');
      setScanned(false);
    }
  };

  if (hasPermission === null) {
    return <Text>Đang yêu cầu quyền truy cập camera...</Text>;
  }
  
  if (hasPermission === false) {
    return <Text>Không có quyền truy cập camera</Text>;
  }

  return (
    <View style={{ flex: 1 }}>
      <BarCodeScanner
        onBarCodeScanned={scanned ? undefined : handleBarCodeScanned}
        style={{ flex: 1 }}
      />
      {scanned && (
        <Button title="Quét lại" onPress={() => setScanned(false)} />
      )}
    </View>
  );
};

export default QRScanScreen;
```

### Transfer Screen Example - Sử dụng thông tin từ QR scan

```javascript
import React, { useState } from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import axios from 'axios';

const TransferScreen = ({ route }) => {
  const { recipientInfo } = route.params;
  const [amount, setAmount] = useState(recipientInfo.amount?.toString() || '');
  const [description, setDescription] = useState(recipientInfo.description || '');

  const handleTransfer = async () => {
    try {
      const response = await axios.post(
        'http://localhost:8089/api/payment/transfer',
        {
          fromAccountNumber: yourAccountNumber,
          toAccountNumber: recipientInfo.accountNumber,
          amount: parseFloat(amount),
          description: description
        },
        {
          headers: {
            'Authorization': `Bearer ${yourToken}`,
            'Content-Type': 'application/json'
          }
        }
      );
      
      Alert.alert('Thành công', 'Chuyển tiền thành công!');
    } catch (error) {
      Alert.alert('Lỗi', error.response?.data?.message || 'Chuyển tiền thất bại');
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Người nhận: {recipientInfo.accountHolderName}</Text>
      <Text>Số TK: {recipientInfo.accountNumber}</Text>
      <Text>Ngân hàng: {recipientInfo.bankName}</Text>
      
      <TextInput
        placeholder="Số tiền"
        value={amount}
        onChangeText={setAmount}
        keyboardType="numeric"
        editable={!recipientInfo.amount} // Disable nếu QR có sẵn số tiền
      />
      
      <TextInput
        placeholder="Nội dung chuyển khoản"
        value={description}
        onChangeText={setDescription}
        editable={!recipientInfo.description}
      />
      
      <Button title="Xác nhận chuyển tiền" onPress={handleTransfer} />
    </View>
  );
};
```

## Logic xử lý

### 1. Parse QR Content

API sử dụng `VietQRParser` để parse chuỗi QR theo chuẩn EMVCo:
- Tag 38: Merchant Account Info (chứa Bank BIN và Account Number)
- Tag 54: Transaction Amount
- Tag 59: Merchant Name
- Tag 62: Additional Data (chứa description)

### 2. Validate Account

- Tìm account theo account number
- Kiểm tra account type = checking
- Kiểm tra account status = active

### 3. Validate Bank

- Lấy bank từ user.bank (nếu user đã có bank)
- Hoặc tìm bank theo BIN trong QR
- Validate BIN trong QR phải khớp với bank của user (nếu có cả 2)

### 4. Return Response

Trả về đầy đủ thông tin để FE hiển thị và thực hiện chuyển tiền.

## Security & Validation

✅ **Authentication required** - Chỉ user đã đăng nhập mới quét QR  
✅ **Account validation** - Chỉ cho phép chuyển vào tài khoản checking đang active  
✅ **Bank validation** - Kiểm tra bank BIN hợp lệ  
✅ **QR format validation** - Parse và validate theo chuẩn VietQR  

## Testing với Postman

1. **Tạo QR code** (User A):
   ```
   POST /api/accounts/checking/qr-code
   Body: { "amount": 500000, "description": "Test" }
   Save response image
   ```

2. **Decode QR image** để lấy qrContent:
   - Sử dụng online QR decoder hoặc mobile app
   - Hoặc giả lập: Copy qrContent từ log/debug

3. **Scan QR** (User B):
   ```
   POST /api/qr/scan
   Body: { "qrContent": "..." }
   ```

4. **Verify response** chứa đúng thông tin User A

## Troubleshooting

**Lỗi: "Nội dung QR không hợp lệ"**
- QR content không đúng format VietQR
- Kiểm tra lại chuỗi QR có đầy đủ tags không

**Lỗi: "Không tìm thấy tài khoản"**
- Account number trong QR không tồn tại trong DB
- Kiểm tra user đã tạo checking account chưa

**Lỗi: "Tài khoản không phải là tài khoản checking"**
- QR chứa account number của saving/mortgage account
- Chỉ hỗ trợ chuyển vào checking account

**Lỗi: "Không tìm thấy thông tin ngân hàng"**
- User chưa được gán bank
- Bank BIN trong QR không có trong bảng banks
- Cần seed data banks table

