# API Lấy Tất Cả Tài Khoản Tiết Kiệm (Get All Saving Accounts for Officer)

## Mô tả
API này cho phép nhân viên ngân hàng (OFFICER) xem danh sách tất cả tài khoản tiết kiệm của toàn bộ khách hàng, bao gồm thông tin chi tiết về khách hàng (họ tên, số điện thoại).

## Endpoint
```
GET /api/saving/all
```

## Phân quyền
- **Role required:** `OFFICER`
- Chỉ nhân viên ngân hàng mới có quyền xem toàn bộ tài khoản tiết kiệm

## Request
- **Method:** GET
- **Headers:**
  - `Authorization: Bearer <JWT_TOKEN>`
  - `Content-Type: application/json`
- **Body:** Không có

## Response

### Success Response (200 OK)
```json
[
    {
        "savingId": 1,
        "savingBookNumber": "STK-2025122301",
        "accountNumber": "SAV0123456789",
        "balance": 50000000.00,
        "term": "6 tháng",
        "termMonths": 6,
        "interestRate": 5.5,
        "openedDate": "2025-06-23",
        "maturityDate": "2025-12-23",
        "status": "ACTIVE",
        "userId": 10,
        "userFullName": "Nguyễn Văn A",
        "userPhone": "0901234567"
    },
    {
        "savingId": 2,
        "savingBookNumber": "STK-2025122302",
        "accountNumber": "SAV0987654321",
        "balance": 100000000.00,
        "term": "12 tháng",
        "termMonths": 12,
        "interestRate": 6.5,
        "openedDate": "2025-01-15",
        "maturityDate": "2026-01-15",
        "status": "ACTIVE",
        "userId": 15,
        "userFullName": "Trần Thị B",
        "userPhone": "0912345678"
    },
    {
        "savingId": 3,
        "savingBookNumber": "STK-2024112303",
        "accountNumber": "SAV1122334455",
        "balance": 0.00,
        "term": "3 tháng",
        "termMonths": 3,
        "interestRate": 4.5,
        "openedDate": "2024-11-23",
        "maturityDate": "2025-02-23",
        "status": "CLOSED",
        "userId": 20,
        "userFullName": "Lê Văn C",
        "userPhone": "0923456789"
    }
]
```

### Response Fields

| Trường | Kiểu | Mô tả |
|--------|------|-------|
| `savingId` | Long | ID của tài khoản tiết kiệm |
| `savingBookNumber` | String | Số sổ tiết kiệm (STK-yyyyMMddXXX) |
| `accountNumber` | String | Số tài khoản (SAV...) |
| `balance` | BigDecimal | Số dư hiện tại (VND) |
| `term` | String | Kỳ hạn (dạng text hiển thị) |
| `termMonths` | Integer | Số tháng của kỳ hạn |
| `interestRate` | BigDecimal | Lãi suất (%/năm) |
| `openedDate` | LocalDate | Ngày mở sổ (yyyy-MM-dd) |
| `maturityDate` | LocalDate | Ngày đáo hạn (yyyy-MM-dd) |
| `status` | String | Trạng thái (ACTIVE/CLOSED) |
| `userId` | Long | ID của khách hàng |
| `userFullName` | String | **Họ tên khách hàng** |
| `userPhone` | String | **Số điện thoại khách hàng** |

### Status Values
- **ACTIVE**: Sổ tiết kiệm đang hoạt động
- **CLOSED**: Sổ tiết kiệm đã tất toán

## Error Responses

### 1. Không có quyền truy cập (403 Forbidden)
```json
{
    "timestamp": "2025-12-23T10:30:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/api/saving/all"
}
```
**Nguyên nhân:** User không có role OFFICER

### 2. Token không hợp lệ (401 Unauthorized)
```json
{
    "timestamp": "2025-12-23T10:30:00.000+00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource",
    "path": "/api/saving/all"
}
```
**Nguyên nhân:** JWT token không hợp lệ hoặc đã hết hạn

## Ví dụ sử dụng

### 1. cURL
```bash
curl -X GET http://localhost:8080/api/saving/all \
  -H "Authorization: Bearer YOUR_OFFICER_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2. Postman
1. **Method:** GET
2. **URL:** `http://localhost:8080/api/saving/all`
3. **Headers:**
   - `Authorization: Bearer YOUR_OFFICER_JWT_TOKEN`
   - `Content-Type: application/json`
4. **Body:** None

### 3. JavaScript (Fetch API)
```javascript
const token = 'YOUR_OFFICER_JWT_TOKEN';

fetch('http://localhost:8080/api/saving/all', {
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => {
    console.log('Danh sách tài khoản tiết kiệm:', data);
    data.forEach(account => {
        console.log(`${account.userFullName} (${account.userPhone}): ${account.balance} VND`);
    });
})
.catch(error => console.error('Error:', error));
```

### 4. React/Vue Example
```javascript
const getAllSavingAccounts = async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/saving/all', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('officerToken')}`
            }
        });
        
        console.log('Total accounts:', response.data.length);
        return response.data;
    } catch (error) {
        console.error('Error fetching saving accounts:', error);
        throw error;
    }
};
```

## Use Cases

### 1. Hiển thị danh sách tất cả sổ tiết kiệm
```javascript
// Lấy và hiển thị tất cả tài khoản
const accounts = await getAllSavingAccounts();

accounts.forEach(account => {
    console.log(`
        Số sổ: ${account.savingBookNumber}
        Khách hàng: ${account.userFullName}
        SĐT: ${account.userPhone}
        Số dư: ${account.balance.toLocaleString()} VND
        Kỳ hạn: ${account.term}
        Lãi suất: ${account.interestRate}%
        Trạng thái: ${account.status}
        Ngày mở: ${account.openedDate}
        Ngày đáo hạn: ${account.maturityDate}
    `);
});
```

### 2. Lọc tài khoản theo trạng thái
```javascript
const accounts = await getAllSavingAccounts();

// Chỉ lấy sổ đang hoạt động
const activeAccounts = accounts.filter(acc => acc.status === 'ACTIVE');
console.log('Số sổ đang hoạt động:', activeAccounts.length);

// Chỉ lấy sổ đã tất toán
const closedAccounts = accounts.filter(acc => acc.status === 'CLOSED');
console.log('Số sổ đã tất toán:', closedAccounts.length);
```

### 3. Tìm kiếm theo khách hàng
```javascript
const accounts = await getAllSavingAccounts();

// Tìm theo tên
const searchByName = (name) => {
    return accounts.filter(acc => 
        acc.userFullName.toLowerCase().includes(name.toLowerCase())
    );
};

// Tìm theo số điện thoại
const searchByPhone = (phone) => {
    return accounts.filter(acc => 
        acc.userPhone.includes(phone)
    );
};

const results = searchByName('Nguyễn');
console.log('Tìm thấy:', results.length, 'tài khoản');
```

### 4. Thống kê tổng quan
```javascript
const accounts = await getAllSavingAccounts();

// Tính tổng số dư tất cả sổ tiết kiệm
const totalBalance = accounts
    .filter(acc => acc.status === 'ACTIVE')
    .reduce((sum, acc) => sum + acc.balance, 0);

console.log('Tổng số dư:', totalBalance.toLocaleString(), 'VND');

// Thống kê theo kỳ hạn
const statsByTerm = accounts
    .filter(acc => acc.status === 'ACTIVE')
    .reduce((stats, acc) => {
        const term = acc.term;
        if (!stats[term]) {
            stats[term] = { count: 0, totalBalance: 0 };
        }
        stats[term].count++;
        stats[term].totalBalance += acc.balance;
        return stats;
    }, {});

console.log('Thống kê theo kỳ hạn:', statsByTerm);
```

## Lưu ý quan trọng

1. **Phân quyền:** Chỉ OFFICER mới có quyền truy cập API này
2. **Dữ liệu nhạy cảm:** API trả về thông tin khách hàng (họ tên, số điện thoại), cần bảo mật
3. **Hiệu suất:** API trả về toàn bộ tài khoản tiết kiệm, có thể tốn thời gian nếu có nhiều dữ liệu
4. **Số dư:** Sổ đã CLOSED sẽ có balance = 0
5. **Lọc client-side:** Nên thực hiện lọc và phân trang ở phía client để giảm tải server

## So sánh với API khác

| API | Endpoint | Role | Mô tả |
|-----|----------|------|-------|
| **Get All** | `/api/saving/all` | OFFICER | Lấy TẤT CẢ sổ tiết kiệm của mọi khách hàng |
| Get My Accounts | `/api/saving/my-accounts` | CUSTOMER/OFFICER | Chỉ lấy sổ của user hiện tại |
| Get Detail | `/api/saving/{savingBookNumber}` | CUSTOMER/OFFICER | Xem chi tiết 1 sổ cụ thể |

## API liên quan

### Xem chi tiết một sổ tiết kiệm
```
GET /api/saving/{savingBookNumber}
```

### Xem danh sách sổ của user hiện tại
```
GET /api/saving/my-accounts
```

### Tạo sổ tiết kiệm mới
```
POST /api/saving/create
```

### Tất toán sổ tiết kiệm
```
POST /api/saving/{savingBookNumber}/withdraw-confirm
```

## Quy trình nghiệp vụ

1. **Officer đăng nhập** với tài khoản có role OFFICER
2. **Lấy danh sách** tất cả tài khoản tiết kiệm
3. **Hiển thị thông tin** bao gồm:
   - Thông tin sổ tiết kiệm (số sổ, số dư, kỳ hạn, lãi suất)
   - Thông tin khách hàng (họ tên, số điện thoại)
   - Trạng thái sổ (đang hoạt động/đã tất toán)
4. **Officer có thể:**
   - Tìm kiếm theo tên/số điện thoại khách hàng
   - Lọc theo trạng thái/kỳ hạn
   - Xem chi tiết từng sổ
   - Thống kê tổng quan

## Security Notes

⚠️ **LƯU Ý BẢO MẬT:**
- API này trả về thông tin nhạy cảm của khách hàng
- Chỉ cấp quyền cho nhân viên đáng tin cậy
- Nên log lại các request để audit
- Không cache response này ở client
- Đảm bảo sử dụng HTTPS trong production

## Change Log
- **v1.0.0** (2025-12-23): Tạo API lấy tất cả tài khoản tiết kiệm cho OFFICER với thông tin khách hàng đầy đủ (họ tên, số điện thoại)

