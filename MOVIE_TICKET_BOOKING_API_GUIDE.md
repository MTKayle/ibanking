# MOVIE TICKET BOOKING SYSTEM - API GUIDE

## Tổng quan hệ thống

Hệ thống đặt vé xem phim cho phép người dùng:
- Xem danh sách phim đang chiếu
- Xem chi tiết phim
- Chọn suất chiếu theo ngày và rạp
- Chọn ghế ngồi
- Thanh toán và nhận vé

## Database Schema

### Bảng dữ liệu

#### 1. **movies** - Thông tin phim
- `movie_id`: ID phim (Primary Key)
- `title`: Tên phim
- `description`: Nội dung mô tả phim
- `duration_minutes`: Thời lượng phim (phút)
- `genre`: Thể loại phim (ACTION, COMEDY, DRAMA, HORROR, ROMANCE, SCI_FI, THRILLER, ANIMATION, ADVENTURE, FANTASY, CRIME, DOCUMENTARY, MYSTERY, WAR, MUSICAL, FAMILY)
- `release_date`: Ngày công chiếu
- `age_rating`: Độ tuổi giới hạn (0, 13, 16, 18)
- `director`: Đạo diễn
- `cast`: Các diễn viên chính
- `country`: Quốc gia sản xuất
- `language`: Ngôn ngữ (VIETNAMESE, ENGLISH, CHINESE, KOREAN, JAPANESE, THAI, FRENCH, SPANISH)
- `poster_url`: URL ảnh poster
- `trailer_url`: URL trailer
- `is_showing`: Đang chiếu hay không

#### 2. **cinemas** - Rạp chiếu phim
- `cinema_id`: ID rạp (Primary Key)
- `name`: Tên rạp
- `address`: Địa chỉ rạp
- `phone`: Số điện thoại
- `city`: Thành phố
- `latitude`: Vĩ độ
- `longitude`: Kinh độ

#### 3. **cinema_halls** - Phòng chiếu
- `hall_id`: ID phòng (Primary Key)
- `cinema_id`: ID rạp (Foreign Key)
- `hall_name`: Tên phòng (VD: "Phòng 1", "Phòng VIP 2")
- `total_seats`: Tổng số ghế

#### 4. **seats** - Ghế ngồi
- `seat_id`: ID ghế (Primary Key)
- `hall_id`: ID phòng (Foreign Key)
- `row_label`: Hàng ghế (A, B, C...)
- `seat_number`: Số ghế trong hàng (1, 2, 3...)
- `seat_type`: Loại ghế (STANDARD, VIP, COUPLE)
- `base_price`: Giá cơ bản

#### 5. **movie_screenings** - Suất chiếu
- `screening_id`: ID suất chiếu (Primary Key)
- `movie_id`: ID phim (Foreign Key)
- `hall_id`: ID phòng (Foreign Key)
- `screening_date`: Ngày chiếu
- `start_time`: Giờ bắt đầu
- `end_time`: Giờ kết thúc
- `screening_type`: Loại chiếu (TWO_D, THREE_D, IMAX, IMAX_3D, FOUR_DX, SCREEN_X)
- `price_multiplier`: Hệ số nhân giá (VD: 3D = 1.5, IMAX = 2.0)
- `available_seats`: Số ghế còn trống

#### 6. **bookings** - Đơn đặt vé
- `booking_id`: ID đặt vé (Primary Key)
- `user_id`: ID người dùng (Foreign Key)
- `screening_id`: ID suất chiếu (Foreign Key)
- `customer_name`: Họ tên người nhận vé
- `customer_phone`: Số điện thoại
- `customer_email`: Email
- `total_seats`: Tổng số ghế đặt
- `total_amount`: Tổng tiền
- `status`: Trạng thái (PENDING, CONFIRMED, CANCELLED, EXPIRED, COMPLETED)
- `booking_time`: Thời gian đặt
- `expiry_time`: Thời gian hết hạn giữ chỗ
- `booking_code`: Mã đặt vé

#### 7. **booking_seats** - Ghế đã đặt
- `booking_seat_id`: ID (Primary Key)
- `booking_id`: ID đặt vé (Foreign Key)
- `seat_id`: ID ghế (Foreign Key)
- `screening_id`: ID suất chiếu (Foreign Key)
- `price`: Giá ghế cho suất chiếu này
- `status`: Trạng thái (AVAILABLE, BOOKED, RESERVED, MAINTENANCE)

#### 8. **payments** - Thanh toán
- `payment_id`: ID thanh toán (Primary Key)
- `booking_id`: ID đặt vé (Foreign Key)
- `amount`: Số tiền
- `payment_method`: Phương thức thanh toán
- `payment_status`: Trạng thái thanh toán
- `payment_time`: Thời gian thanh toán
- `transaction_id`: ID giao dịch
- `payment_details`: Chi tiết thanh toán

## API Endpoints

### 1. Danh sách phim đang chiếu

**GET** `/api/movies/now-showing`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "movieId": 1,
      "title": "Avengers: Endgame",
      "genre": "ACTION",
      "genreDisplay": "Hành động",
      "releaseDate": "2024-04-26",
      "durationMinutes": 181,
      "ageRating": 13,
      "posterUrl": "https://example.com/poster.jpg",
      "isShowing": true
    }
  ]
}
```

### 2. Chi tiết phim

**GET** `/api/movies/{movieId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "movieId": 1,
    "title": "Avengers: Endgame",
    "description": "Sau sự kiện Infinity War...",
    "durationMinutes": 181,
    "genre": "ACTION",
    "genreDisplay": "Hành động",
    "releaseDate": "2024-04-26",
    "ageRating": 13,
    "director": "Anthony Russo, Joe Russo",
    "cast": "Robert Downey Jr., Chris Evans, Mark Ruffalo",
    "country": "Mỹ",
    "language": "ENGLISH",
    "languageDisplay": "Tiếng Anh",
    "posterUrl": "https://example.com/poster.jpg",
    "trailerUrl": "https://example.com/trailer.mp4",
    "screeningTypes": ["TWO_D", "THREE_D", "IMAX"],
    "isShowing": true
  }
}
```

### 3. Danh sách rạp chiếu phim

**GET** `/api/cinemas`

**Query Parameters:**
- `city` (optional): Lọc theo thành phố

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "cinemaId": 1,
      "name": "CGV Vincom Mega Mall",
      "address": "123 Đường ABC, Quận 1",
      "phone": "0281234567",
      "city": "Hồ Chí Minh",
      "latitude": 10.7769,
      "longitude": 106.7009
    }
  ]
}
```

### 4. Lấy suất chiếu theo ngày và rạp

**GET** `/api/screenings`

**Query Parameters:**
- `movieId` (required): ID phim
- `screeningDate` (required): Ngày chiếu (format: YYYY-MM-DD)
- `cinemaId` (optional): ID rạp

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "cinemaId": 1,
      "cinemaName": "CGV Vincom Mega Mall",
      "cinemaAddress": "123 Đường ABC, Quận 1",
      "screenings": [
        {
          "screeningId": 1,
          "hallId": 1,
          "hallName": "Phòng 1",
          "startTime": "09:00",
          "endTime": "11:30",
          "screeningType": "TWO_D",
          "screeningTypeDisplay": "2D",
          "availableSeats": 120,
          "totalSeats": 150,
          "priceMultiplier": 1.00
        },
        {
          "screeningId": 2,
          "hallId": 2,
          "hallName": "Phòng IMAX",
          "startTime": "14:00",
          "endTime": "16:30",
          "screeningType": "IMAX_3D",
          "screeningTypeDisplay": "IMAX 3D",
          "availableSeats": 80,
          "totalSeats": 100,
          "priceMultiplier": 2.50
        }
      ]
    }
  ]
}
```

### 5. Lấy thông tin ghế của suất chiếu

**GET** `/api/screenings/{screeningId}/seats`

**Response:**
```json
{
  "success": true,
  "data": {
    "screeningId": 1,
    "movieTitle": "Avengers: Endgame",
    "cinemaName": "CGV Vincom Mega Mall",
    "hallName": "Phòng 1",
    "screeningDate": "2024-12-20",
    "startTime": "09:00",
    "screeningType": "TWO_D",
    "priceMultiplier": 1.00,
    "seats": [
      {
        "seatId": 1,
        "rowLabel": "A",
        "seatNumber": 1,
        "seatLabel": "A1",
        "seatType": "STANDARD",
        "seatTypeDisplay": "Ghế thường",
        "basePrice": 70000,
        "finalPrice": 70000,
        "status": "AVAILABLE"
      },
      {
        "seatId": 15,
        "rowLabel": "B",
        "seatNumber": 5,
        "seatLabel": "B5",
        "seatType": "VIP",
        "seatTypeDisplay": "Ghế VIP",
        "basePrice": 100000,
        "finalPrice": 100000,
        "status": "BOOKED"
      },
      {
        "seatId": 25,
        "rowLabel": "C",
        "seatNumber": 3,
        "seatLabel": "C3",
        "seatType": "COUPLE",
        "seatTypeDisplay": "Ghế đôi",
        "basePrice": 150000,
        "finalPrice": 150000,
        "status": "AVAILABLE"
      }
    ]
  }
}
```

### 6. Tạo đơn đặt vé (giữ chỗ)

**POST** `/api/bookings/reserve`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "screeningId": 1,
  "seatIds": [1, 2, 3],
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0901234567",
  "customerEmail": "nguyenvana@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đặt chỗ thành công. Vui lòng thanh toán trong 10 phút.",
  "data": {
    "bookingId": 1,
    "bookingCode": "BK20241220001",
    "screeningId": 1,
    "movieTitle": "Avengers: Endgame",
    "cinemaName": "CGV Vincom Mega Mall",
    "hallName": "Phòng 1",
    "screeningDate": "2024-12-20",
    "startTime": "09:00",
    "seats": [
      {
        "seatLabel": "A1",
        "seatType": "STANDARD",
        "price": 70000
      },
      {
        "seatLabel": "A2",
        "seatType": "STANDARD",
        "price": 70000
      },
      {
        "seatLabel": "A3",
        "seatType": "STANDARD",
        "price": 70000
      }
    ],
    "totalSeats": 3,
    "totalAmount": 210000,
    "status": "PENDING",
    "bookingTime": "2024-12-20T08:30:00",
    "expiryTime": "2024-12-20T08:40:00"
  }
}
```

### 7. Thanh toán đặt vé

**POST** `/api/bookings/{bookingId}/payment`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "paymentMethod": "ACCOUNT_BALANCE",
  "accountNumber": "1234567890"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Thanh toán thành công",
  "data": {
    "bookingId": 1,
    "bookingCode": "BK20241220001",
    "paymentId": 1,
    "amount": 210000,
    "paymentMethod": "ACCOUNT_BALANCE",
    "paymentStatus": "SUCCESS",
    "paymentTime": "2024-12-20T08:35:00",
    "transactionId": "TXN20241220001",
    "bookingStatus": "CONFIRMED"
  }
}
```

### 8. Lấy thông tin đơn đặt vé

**GET** `/api/bookings/{bookingId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "bookingId": 1,
    "bookingCode": "BK20241220001",
    "movieTitle": "Avengers: Endgame",
    "durationMinutes": 181,
    "screeningDate": "2024-12-20",
    "startTime": "09:00",
    "screeningType": "2D",
    "hallName": "Phòng 1",
    "cinemaName": "CGV Vincom Mega Mall",
    "cinemaAddress": "123 Đường ABC, Quận 1",
    "customerName": "Nguyễn Văn A",
    "customerPhone": "0901234567",
    "customerEmail": "nguyenvana@example.com",
    "seats": ["A1", "A2", "A3"],
    "totalSeats": 3,
    "pricePerSeat": 70000,
    "totalAmount": 210000,
    "status": "CONFIRMED",
    "bookingTime": "2024-12-20T08:30:00"
  }
}
```

### 9. Lịch sử đặt vé của người dùng

**GET** `/api/bookings/my-bookings`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `status` (optional): Lọc theo trạng thái (PENDING, CONFIRMED, CANCELLED, EXPIRED, COMPLETED)
- `page` (optional): Số trang (default: 0)
- `size` (optional): Số items mỗi trang (default: 10)

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "bookingId": 1,
        "bookingCode": "BK20241220001",
        "movieTitle": "Avengers: Endgame",
        "posterUrl": "https://example.com/poster.jpg",
        "cinemaName": "CGV Vincom Mega Mall",
        "screeningDate": "2024-12-20",
        "startTime": "09:00",
        "totalSeats": 3,
        "totalAmount": 210000,
        "status": "CONFIRMED",
        "bookingTime": "2024-12-20T08:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 10. Hủy đặt vé

**POST** `/api/bookings/{bookingId}/cancel`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Hủy đặt vé thành công. Tiền sẽ được hoàn lại vào tài khoản.",
  "data": {
    "bookingId": 1,
    "status": "CANCELLED",
    "refundAmount": 210000
  }
}
```

## Luồng đặt vé

1. **Xem danh sách phim đang chiếu**
   - GET `/api/movies/now-showing`
   
2. **Xem chi tiết phim**
   - GET `/api/movies/{movieId}`
   
3. **Chọn ngày chiếu và rạp**
   - GET `/api/screenings?movieId={movieId}&screeningDate={date}&cinemaId={cinemaId}`
   
4. **Chọn suất chiếu và xem ghế**
   - GET `/api/screenings/{screeningId}/seats`
   
5. **Đặt chỗ (giữ ghế trong 10 phút)**
   - POST `/api/bookings/reserve`
   
6. **Thanh toán**
   - POST `/api/bookings/{bookingId}/payment`
   
7. **Nhận thông tin vé**
   - GET `/api/bookings/{bookingId}`

## Quy tắc nghiệp vụ

### Giữ chỗ
- Khi người dùng chọn ghế và bấm đặt vé, ghế sẽ được giữ trong 10 phút
- Sau 10 phút nếu không thanh toán, booking sẽ tự động hủy và ghế được giải phóng
- Trạng thái booking: `PENDING`

### Thanh toán
- Người dùng phải thanh toán trong thời gian giữ chỗ
- Thanh toán thành công: Booking chuyển sang `CONFIRMED`
- Thanh toán thất bại: Booking vẫn ở `PENDING` và có thể thử lại

### Hủy vé
- Có thể hủy vé trước giờ chiếu ít nhất 2 giờ
- Tiền sẽ được hoàn lại 100% vào tài khoản

### Tính giá vé
```
Giá cuối = Giá cơ bản ghế × Hệ số loại chiếu

Ví dụ:
- Ghế thường: 70,000 VND
- Suất chiếu 3D (hệ số 1.5): 70,000 × 1.5 = 105,000 VND
- Suất chiếu IMAX 3D (hệ số 2.5): 70,000 × 2.5 = 175,000 VND
```

## Enum Values

### MovieGenre
- ACTION - Hành động
- COMEDY - Hài
- DRAMA - Chính kịch
- HORROR - Kinh dị
- ROMANCE - Lãng mạn
- SCI_FI - Khoa học viễn tưởng
- THRILLER - Ly kỳ
- ANIMATION - Hoạt hình
- ADVENTURE - Phiêu lưu
- FANTASY - Giả tưởng
- CRIME - Tội phạm
- DOCUMENTARY - Tài liệu
- MYSTERY - Bí ẩn
- WAR - Chiến tranh
- MUSICAL - Nhạc kịch
- FAMILY - Gia đình

### ScreeningType
- TWO_D - 2D
- THREE_D - 3D
- IMAX - IMAX
- IMAX_3D - IMAX 3D
- FOUR_DX - 4DX
- SCREEN_X - ScreenX

### SeatType
- STANDARD - Ghế thường
- VIP - Ghế VIP
- COUPLE - Ghế đôi

### SeatStatus
- AVAILABLE - Còn trống
- BOOKED - Đã đặt
- RESERVED - Đang giữ chỗ
- MAINTENANCE - Bảo trì

### BookingStatus
- PENDING - Chờ thanh toán
- CONFIRMED - Đã xác nhận
- CANCELLED - Đã hủy
- EXPIRED - Đã hết hạn
- COMPLETED - Hoàn thành

### Language
- VIETNAMESE - Tiếng Việt
- ENGLISH - Tiếng Anh
- CHINESE - Tiếng Trung
- KOREAN - Tiếng Hàn
- JAPANESE - Tiếng Nhật
- THAI - Tiếng Thái
- FRENCH - Tiếng Pháp
- SPANISH - Tiếng Tây Ban Nha

## Notes
- Tất cả API cần authentication đều yêu cầu JWT token trong header
- Giá tiền tính bằng VND
- Thời gian theo múi giờ Việt Nam (GMT+7)
- Booking code được generate tự động theo format: BK{YYYYMMDD}{sequence}

