# API Lấy Danh Sách Vé Đã Đặt

## Mô tả
API này cho phép user lấy tất cả các vé xem phim đã đặt của mình, bao gồm đầy đủ thông tin về phim, suất chiếu, rạp, ghế ngồi và giá tiền.

## Endpoint
```
GET /api/bookings/my-bookings
```

## Authentication
- **Yêu cầu**: Bearer Token (JWT)
- **Header**: `Authorization: Bearer {access_token}`

## Request
Không cần body hoặc parameters. API tự động lấy thông tin user từ JWT token.

## Response

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Lấy danh sách vé thành công",
  "total": 2,
  "data": [
    {
      "bookingId": 1,
      "bookingCode": "BK202412180001",
      
      // Thông tin phim
      "movieId": 1,
      "movieTitle": "The Avengers: Endgame",
      "posterUrl": "https://res.cloudinary.com/...",
      "durationMinutes": 181,
      
      // Thông tin suất chiếu
      "screeningId": 5,
      "screeningDate": "2024-12-20",
      "startTime": "19:00:00",
      "endTime": "22:01:00",
      "screeningType": "IMAX_3D",
      "screeningTypeDisplay": "IMAX 3D",
      
      // Thông tin rạp
      "cinemaName": "CGV Vincom",
      "cinemaAddress": "123 Nguyễn Huệ, Q.1, TP.HCM",
      "hallName": "Rạp 1",
      
      // Thông tin khách hàng
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0901234567",
      "customerEmail": "nguyenvana@gmail.com",
      
      // Thông tin ghế đã đặt
      "seats": [
        {
          "seatId": 25,
          "seatLabel": "A5",
          "seatType": "VIP",
          "seatTypeDisplay": "Ghế VIP",
          "price": 150000
        },
        {
          "seatId": 26,
          "seatLabel": "A6",
          "seatType": "VIP",
          "seatTypeDisplay": "Ghế VIP",
          "price": 150000
        }
      ],
      "totalSeats": 2,
      
      // Thông tin thanh toán
      "totalAmount": 300000,
      "status": "CONFIRMED",
      "bookingTime": "2024-12-18T14:30:00"
    },
    {
      "bookingId": 2,
      "bookingCode": "BK202412180002",
      "movieId": 2,
      "movieTitle": "Spider-Man: No Way Home",
      "posterUrl": "https://res.cloudinary.com/...",
      "durationMinutes": 148,
      "screeningId": 8,
      "screeningDate": "2024-12-22",
      "startTime": "21:00:00",
      "endTime": "23:28:00",
      "screeningType": "STANDARD_2D",
      "screeningTypeDisplay": "2D Phổ Thông",
      "cinemaName": "Galaxy Cinema",
      "cinemaAddress": "456 Lê Lợi, Q.1, TP.HCM",
      "hallName": "Rạp 3",
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0901234567",
      "customerEmail": "nguyenvana@gmail.com",
      "seats": [
        {
          "seatId": 45,
          "seatLabel": "C10",
          "seatType": "STANDARD",
          "seatTypeDisplay": "Ghế Thường",
          "price": 80000
        }
      ],
      "totalSeats": 1,
      "totalAmount": 80000,
      "status": "CONFIRMED",
      "bookingTime": "2024-12-18T16:45:00"
    }
  ]
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Không tìm thấy user"
}
```

### Error Response (401 Unauthorized)
```json
{
  "success": false,
  "message": "Unauthorized"
}
```

## Thông tin trả về

### Booking Information
| Field | Type | Description |
|-------|------|-------------|
| bookingId | Long | ID của booking |
| bookingCode | String | Mã booking (VD: BK202412180001) |

### Movie Information
| Field | Type | Description |
|-------|------|-------------|
| movieId | Long | ID của phim |
| movieTitle | String | Tên phim |
| posterUrl | String | URL poster phim |
| durationMinutes | Integer | Thời lượng phim (phút) |

### Screening Information
| Field | Type | Description |
|-------|------|-------------|
| screeningId | Long | ID của suất chiếu |
| screeningDate | LocalDate | Ngày chiếu (YYYY-MM-DD) |
| startTime | LocalTime | Giờ bắt đầu (HH:mm:ss) |
| endTime | LocalTime | Giờ kết thúc (HH:mm:ss) |
| screeningType | String | Loại suất chiếu (STANDARD_2D, IMAX_3D, v.v.) |
| screeningTypeDisplay | String | Tên hiển thị của loại suất chiếu |

### Cinema Information
| Field | Type | Description |
|-------|------|-------------|
| cinemaName | String | Tên rạp chiếu phim |
| cinemaAddress | String | Địa chỉ rạp |
| hallName | String | Tên phòng chiếu |

### Customer Information
| Field | Type | Description |
|-------|------|-------------|
| customerName | String | Tên khách hàng |
| customerPhone | String | Số điện thoại |
| customerEmail | String | Email |

### Seat Information
| Field | Type | Description |
|-------|------|-------------|
| seats | List | Danh sách ghế đã đặt |
| seats[].seatId | Long | ID của ghế |
| seats[].seatLabel | String | Nhãn ghế (VD: A5, B10) |
| seats[].seatType | String | Loại ghế (STANDARD, VIP, COUPLE) |
| seats[].seatTypeDisplay | String | Tên hiển thị loại ghế |
| seats[].price | BigDecimal | Giá tiền đã thanh toán cho ghế này |
| totalSeats | Integer | Tổng số ghế đã đặt |

### Payment Information
| Field | Type | Description |
|-------|------|-------------|
| totalAmount | BigDecimal | Tổng số tiền đã thanh toán (VND) |
| status | String | Trạng thái booking (CONFIRMED, CANCELLED) |
| bookingTime | LocalDateTime | Thời gian đặt vé |

## Ví dụ sử dụng

### cURL
```bash
curl -X GET "http://localhost:8080/api/bookings/my-bookings" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### JavaScript (Fetch API)
```javascript
const getMyBookings = async () => {
  const token = localStorage.getItem('access_token');
  
  try {
    const response = await fetch('http://localhost:8080/api/bookings/my-bookings', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('Tổng số vé:', result.total);
      console.log('Danh sách vé:', result.data);
      
      // Hiển thị từng booking
      result.data.forEach(booking => {
        console.log('-------------------');
        console.log('Mã booking:', booking.bookingCode);
        console.log('Phim:', booking.movieTitle);
        console.log('Rạp:', booking.cinemaName);
        console.log('Ngày chiếu:', booking.screeningDate);
        console.log('Giờ chiếu:', booking.startTime);
        console.log('Số ghế:', booking.totalSeats);
        console.log('Tổng tiền:', booking.totalAmount.toLocaleString(), 'VND');
        
        // Hiển thị chi tiết ghế
        booking.seats.forEach(seat => {
          console.log(`  - Ghế ${seat.seatLabel} (${seat.seatTypeDisplay}): ${seat.price.toLocaleString()} VND`);
        });
      });
    } else {
      console.error('Lỗi:', result.message);
    }
  } catch (error) {
    console.error('Lỗi kết nối:', error);
  }
};

getMyBookings();
```

### React Native
```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';

const getMyBookings = async () => {
  try {
    const token = await AsyncStorage.getItem('access_token');
    
    const response = await fetch('http://localhost:8080/api/bookings/my-bookings', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      // result.data chứa danh sách tất cả vé đã đặt
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Lỗi:', error);
    throw error;
  }
};
```

## Lưu ý
- API trả về danh sách vé được sắp xếp theo thời gian đặt mới nhất
- Mỗi vé bao gồm thông tin chi tiết về:
  - **Phim**: Tên, poster, thời lượng
  - **Suất chiếu**: Ngày, giờ, loại suất chiếu (2D, 3D, IMAX)
  - **Rạp**: Tên rạp, địa chỉ, phòng chiếu
  - **Ghế**: Danh sách ghế đã đặt với giá tiền từng ghế
  - **Thanh toán**: Tổng tiền, trạng thái, thời gian đặt
- Giá tiền mỗi ghế đã được tính với hệ số nhân của suất chiếu
- Status có thể là: CONFIRMED, CANCELLED
- Tất cả số tiền đều tính bằng VND (Việt Nam Đồng)

## Test với Postman
1. Import collection vào Postman
2. Đăng nhập để lấy access token
3. Thêm token vào header: `Authorization: Bearer {token}`
4. Gửi GET request đến `/api/bookings/my-bookings`
5. Kiểm tra response trả về danh sách vé đã đặt

## Kết quả mong đợi
- Trả về tất cả booking của user hiện tại
- Mỗi booking có đầy đủ thông tin: phim, suất chiếu, rạp, ghế, giá tiền
- Dữ liệu được sắp xếp theo thời gian đặt vé mới nhất
- Response bao gồm tổng số vé đã đặt

