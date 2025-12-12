# Get Account Info API Documentation

## Tổng quan

API này cho phép lấy thông tin chi tiết của tài khoản dựa trên account number, bao gồm thông tin người dùng, ngân hàng và loại tài khoản.

## API Endpoint

### Lấy thông tin tài khoản theo Account Number

**Endpoint:** `GET /api/accounts/info/{accountNumber}`

**Authorization:** Required (Bearer Token)

**Roles:** CUSTOMER, OFFICER, ADMIN

**Description:** Lấy thông tin đầy đủ của tài khoản bao gồm tên chủ tài khoản, thông tin ngân hàng và loại tài khoản.

#### Request

**Path Parameter:**
- `accountNumber` (String, required): Số tài khoản cần tra cứu

**Example:**
```
GET /api/accounts/info/5329812059
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Response

**Success - 200 OK:**
```json
{
  "accountNumber": "5329812059",
  "accountHolderName": "Nguyễn Thế An",
  "bankBin": "770717",
  "bankCode": "HATBANK",
  "bankName": "Ngan hang thuong mai HAT",
  "userId": 21,
  "accountType": "checking"
}
```

**Response Fields:**
- `accountNumber` (String): Số tài khoản
- `accountHolderName` (String): Tên chủ tài khoản
- `bankBin` (String): Mã BIN của ngân hàng (Bank Identification Number)
- `bankCode` (String): Mã ngân hàng (viết tắt)
- `bankName` (String): Tên đầy đủ ngân hàng
- `userId` (Long): ID của user sở hữu tài khoản
- `accountType` (String): Loại tài khoản (checking, saving, mortgage)

#### Error Responses

**404 Not Found - Không tìm thấy tài khoản:**
```json
{
  "message": "Không tìm thấy tài khoản với số: 5329812059"
}
```

**404 Not Found - Không tìm thấy user:**
```json
{
  "message": "Không tìm thấy thông tin người dùng cho tài khoản: 5329812059"
}
```

**401 Unauthorized:**
```json
{
  "message": "Unauthorized"
}
```

## Use Cases

### 1. Xác minh tài khoản trước khi chuyển tiền

Trước khi thực hiện chuyển tiền, người dùng có thể kiểm tra thông tin tài khoản người nhận:

```javascript
// Step 1: User nhập account number
const recipientAccountNumber = "5329812059";

// Step 2: Gọi API lấy thông tin
const response = await axios.get(
  `http://localhost:8089/api/accounts/info/${recipientAccountNumber}`,
  {
    headers: {
      'Authorization': `Bearer ${yourToken}`
    }
  }
);

// Step 3: Hiển thị thông tin xác nhận
console.log(`Người nhận: ${response.data.accountHolderName}`);
console.log(`Ngân hàng: ${response.data.bankName}`);
console.log(`Loại TK: ${response.data.accountType}`);

// Step 4: User xác nhận và thực hiện chuyển tiền
```

### 2. Hiển thị danh bạ người nhận

Lưu và hiển thị thông tin người nhận đã chuyển tiền trước đó:

```javascript
const savedRecipients = [
  { accountNumber: "5329812059", name: "Nguyễn Thế An" },
  { accountNumber: "1234567890", name: "Trần Văn B" }
];

// Lấy thông tin đầy đủ khi cần
for (const recipient of savedRecipients) {
  const info = await getAccountInfo(recipient.accountNumber);
  console.log(`${info.accountHolderName} - ${info.bankName}`);
}
```

### 3. Validate account number trước khi tạo giao dịch

```javascript
async function validateAndTransfer(toAccountNumber, amount) {
  try {
    // Validate account exists
    const accountInfo = await axios.get(
      `http://localhost:8089/api/accounts/info/${toAccountNumber}`,
      { headers: { 'Authorization': `Bearer ${token}` } }
    );
    
    // Check account type
    if (accountInfo.data.accountType !== 'checking') {
      alert('Chỉ có thể chuyển tiền vào tài khoản checking');
      return;
    }
    
    // Proceed with transfer
    await transfer(toAccountNumber, amount);
    
  } catch (error) {
    if (error.response?.status === 404) {
      alert('Số tài khoản không tồn tại');
    }
  }
}
```

## Example - React Native

```javascript
import React, { useState } from 'react';
import { View, TextInput, Button, Text, Alert } from 'react-native';
import axios from 'axios';

const TransferScreen = () => {
  const [accountNumber, setAccountNumber] = useState('');
  const [accountInfo, setAccountInfo] = useState(null);
  const [amount, setAmount] = useState('');

  const lookupAccount = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8089/api/accounts/info/${accountNumber}`,
        {
          headers: {
            'Authorization': `Bearer ${yourToken}`
          }
        }
      );
      
      setAccountInfo(response.data);
      
    } catch (error) {
      if (error.response?.status === 404) {
        Alert.alert('Lỗi', 'Không tìm thấy tài khoản');
      } else {
        Alert.alert('Lỗi', 'Không thể tra cứu tài khoản');
      }
    }
  };

  const handleTransfer = async () => {
    if (!accountInfo) {
      Alert.alert('Lỗi', 'Vui lòng tra cứu tài khoản trước');
      return;
    }
    
    try {
      await axios.post(
        'http://localhost:8089/api/payment/transfer',
        {
          fromAccountNumber: yourAccountNumber,
          toAccountNumber: accountNumber,
          amount: parseFloat(amount),
          description: 'Chuyển tiền'
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
      Alert.alert('Lỗi', 'Chuyển tiền thất bại');
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Số tài khoản người nhận:</Text>
      <TextInput
        value={accountNumber}
        onChangeText={setAccountNumber}
        placeholder="Nhập số tài khoản"
        keyboardType="numeric"
      />
      
      <Button title="Tra cứu" onPress={lookupAccount} />
      
      {accountInfo && (
        <View style={{ marginTop: 20, padding: 10, backgroundColor: '#f0f0f0' }}>
          <Text style={{ fontWeight: 'bold' }}>
            Tên: {accountInfo.accountHolderName}
          </Text>
          <Text>Ngân hàng: {accountInfo.bankName}</Text>
          <Text>Loại TK: {accountInfo.accountType}</Text>
          <Text>Số TK: {accountInfo.accountNumber}</Text>
        </View>
      )}
      
      {accountInfo && (
        <>
          <TextInput
            value={amount}
            onChangeText={setAmount}
            placeholder="Số tiền"
            keyboardType="numeric"
            style={{ marginTop: 10 }}
          />
          <Button 
            title="Xác nhận chuyển tiền" 
            onPress={handleTransfer}
            color="green"
          />
        </>
      )}
    </View>
  );
};

export default TransferScreen;
```

## Testing với Postman

### Request
```
GET http://localhost:8089/api/accounts/info/5329812059
Headers:
  Authorization: Bearer YOUR_JWT_TOKEN
```

### Expected Response
```json
{
  "accountNumber": "5329812059",
  "accountHolderName": "Nguyễn Thế An",
  "bankBin": "770717",
  "bankCode": "HATBANK",
  "bankName": "Ngan hang thuong mai HAT",
  "userId": 21,
  "accountType": "checking"
}
```

## Security

✅ **Authentication Required** - Phải đăng nhập mới có thể tra cứu  
✅ **Authorization** - CUSTOMER, OFFICER, ADMIN đều có thể tra cứu  
⚠️ **Privacy Note** - API này public thông tin tên và ngân hàng, cân nhắc thêm rate limiting để tránh brute force  

## Integration với các API khác

### 1. Kết hợp với Transfer API

```javascript
// Tra cứu tài khoản
const accountInfo = await getAccountInfo(accountNumber);

// Thực hiện chuyển tiền
await transfer({
  toAccountNumber: accountInfo.accountNumber,
  amount: 1000000,
  description: `Chuyển tiền cho ${accountInfo.accountHolderName}`
});
```

### 2. Kết hợp với QR Scan API

```javascript
// Quét QR code
const qrData = await scanQR();

// Parse account number từ QR
const accountNumber = parseAccountNumberFromQR(qrData);

// Lấy thông tin chi tiết
const accountInfo = await getAccountInfo(accountNumber);

// Hiển thị xác nhận
showConfirmation(accountInfo);
```

## Notes

- API này không trả về số dư tài khoản (balance) vì lý do bảo mật
- Chỉ trả về thông tin cơ bản: tên, ngân hàng, loại tài khoản
- Nếu user chưa được gán bank, các field bank sẽ là chuỗi rỗng
- Account type có thể là: `checking`, `saving`, hoặc `mortgage`

## Error Handling

```javascript
async function getAccountInfoSafely(accountNumber) {
  try {
    const response = await axios.get(
      `http://localhost:8089/api/accounts/info/${accountNumber}`,
      { headers: { 'Authorization': `Bearer ${token}` } }
    );
    return response.data;
    
  } catch (error) {
    if (error.response?.status === 404) {
      throw new Error('Tài khoản không tồn tại');
    } else if (error.response?.status === 401) {
      throw new Error('Chưa đăng nhập');
    } else {
      throw new Error('Không thể tra cứu tài khoản');
    }
  }
}
```

