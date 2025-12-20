-- ============================================
-- MOVIE TICKET BOOKING SYSTEM - SAMPLE DATA
-- ============================================

-- 1. INSERT MOVIES DATA
-- ============================================
INSERT INTO movies (
    age_rating,
    movie_cast,
    country,
    description,
    director,
    duration_minutes,
    genre,
    is_showing,
    movie_language,
    poster_url,
    release_date,
    title,
    trailer_url
) VALUES
      (
          0,
          'Ginnifer Goodwin, Jason Bateman, Ke Huy Quan',
          'USA',
          'Judy Hopps và Nick Wilde tiếp tục hợp tác để điều tra một vụ án mới có nguy cơ làm đảo lộn trật tự của thành phố Zootopia.',
          'Jared Bush, Byron Howard',
          108,
          'ANIMATION',
          TRUE,
          'ENGLISH',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766058655/dAJAUq3WO5w5QUK2YLJTqxTfxio_qrswrn.webp',
          '2025-11-28',
          'Zootopia 2: Phi Vụ Động Trời',
          'https://www.youtube.com/watch?v=BjkIOU5PhyQ'
      ),
      (
          13,
          'Sam Worthington, Zoe Saldaña, Sigourney Weaver',
          'USA',
          'Gia đình Jake Sully phải đối mặt với bộ tộc Na''vi mới tại vùng núi lửa Pandora, nơi những xung đột dữ dội và bí ẩn cổ xưa dần được hé lộ.',
          'James Cameron',
          197,
          'SCI_FI',
          TRUE,
          'ENGLISH',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766058819/2adf92bb487c1da7e51553e9dec319ed_rrxvtv.jpg',
          '2025-12-19',
          'Avatar 3: Lửa Và Tro Tàn',
          'https://www.youtube.com/watch?v=rZXmSgjxpdQ'
      ),
      (
          13,
          'Quang Tuấn, Ma Ran Đô',
          'VIETNAM',
          'Một hành trình phiêu lưu truy tìm kho báu quý hiếm gắn liền với lịch sử và những âm mưu nguy hiểm, pha trộn hành động và yếu tố hài hước.',
          'Victor Vũ',
          125,
          'ADVENTURE',
          TRUE,
          'VIETNAMESE',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766058902/truy_t_m_long_di_n_h_ng_-_payoff_poster_kc_14112025_cuuzjs.jpg',
          '2025-10-18',
          'Truy Tìm Long Diên Hương',
          'https://www.youtube.com/watch?v=-q1FYNMQBeU'
      ),
      (
          18,
          'Anh Tú Atus, Lương Thế Thành',
          'VIETNAM',
          'Câu chuyện kinh dị xoay quanh một lời nguyền cổ xưa và những thế lực tà ác đe dọa sự bình yên của một ngôi làng.',
          'Hàm Trần',
          112,
          'HORROR',
          TRUE,
          'VIETNAMESE',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766058965/htq_special_poster_mzifea.jpg',
          '2025-11-22',
          'Hoàng Tử Quỷ',
          'https://www.youtube.com/watch?v=Rc-0s7oeON8'
      ),
      (
          0,
          'Cynthia Erivo, Ariana Grande',
          'USA',
          'Câu chuyện về tình bạn, số phận và sự lựa chọn của hai phù thủy Elphaba và Glinda trước khi trở thành huyền thoại xứ Oz.',
          'Jon M. Chu',
          160,
          'MUSICAL',
          TRUE,
          'ENGLISH',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766059022/wicked_for_good_zuuuff.jpg',
          '2025-12-05',
          'Wicked: For Good',
          'https://www.youtube.com/results?search_query=trailer+wicked+for+good'
      ),
      (
          16,
          'Phương Anh Đào, Tuấn Trần, Hồng Đào',
          'VIETNAM',
          'Câu chuyện tình yêu và gia đình qua góc nhìn của người phụ nữ hiện đại, tìm về cội nguồn và những giá trị truyền thống.',
          'Trấn Thành',
          131,
          'DRAMA',
          TRUE,
          'VIETNAMESE',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766059067/Mai_2024_poster_jguxsi.jpg',
          '2025-02-10',
          'Mai',
          'https://www.youtube.com/watch?v=HXWRTGbhb4U'
      ),
      (
          18,
          'Ryan Reynolds, Hugh Jackman, Emma Corrin',
          'USA',
          'Cuộc hội ngộ bất đắc dĩ giữa Deadpool và Wolverine trong một nhiệm vụ cứu thế giới đầy hành động và hài hước.',
          'Shawn Levy',
          128,
          'ACTION',
          TRUE,
          'ENGLISH',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766059133/dF3tFilSlwdjuWXQPYvYV6pCAT2_g00e80.webp',
          '2025-07-26',
          'Deadpool & Wolverine',
          'https://www.youtube.com/watch?v=73_1biulkYk'
      ),
      (
          0,
          'Aaron Pierre, Kelvin Harrison Jr., John Kani',
          'USA',
          'Câu chuyện về tuổi thơ của Mufasa, hành trình từ một chú sư tử mồ côi trở thành vị vua vĩ đại của Pride Rock.',
          'Barry Jenkins',
          118,
          'ANIMATION',
          TRUE,
          'ENGLISH',
          'https://res.cloudinary.com/dcyqkvgol/image/upload/v1766059173/k5w1IZ5EzR6qx4oXNAby0X3dqgU_ixkvj8.webp',
          '2025-12-20',
          'Mufasa: The Lion King',
          'https://www.youtube.com/watch?v=o17MF9vnabg'
      );


-- 2. INSERT CINEMAS DATA
-- ============================================
INSERT INTO cinemas (name, address, phone, city, latitude, longitude) VALUES
('CGV Vincom Mega Mall', '159 Xa lộ Hà Nội, Phường Thảo Điền, TP. Thủ Đức', '02873008881', 'Hồ Chí Minh', 10.8022, 106.7303),
('CGV Parkson Hùng Vương', '126 Hùng Vương, Phường An Lạc, Quận Bình Tân', '02862968623', 'Hồ Chí Minh', 10.7447, 106.6177),
('Galaxy Nguyễn Du', '116 Nguyễn Du, Phường Bến Thành, Quận 1', '0963778898', 'Hồ Chí Minh', 10.7713, 106.6992),
('Galaxy Kinh Dương Vương', '718bis Kinh Dương Vương, Phường 12, Quận 6', '0967007567', 'Hồ Chí Minh', 10.7468, 106.6291),
('Lotte Cinema Cộng Hòa', '180 Cộng Hòa, Phường 12, Quận Tân Bình', '18006799', 'Hồ Chí Minh', 10.8011, 106.6445),
('BHD Star Bitexco', 'Tầng 3, Bitexco Financial Tower, 2 Hải Triều, Quận 1', '02839142894', 'Hồ Chí Minh', 10.7718, 106.7044),
('CGV Vincom Center Landmark 81', 'Tầng B1, Vincom Center Landmark 81, 772 Điện Biên Phủ, Quận Bình Thạnh', '19006017', 'Hồ Chí Minh', 10.7944, 106.7217),
('Galaxy Tân Bình', '246 Nguyễn Hồng Đào, Phường 14, Quận Tân Bình', '02838484828', 'Hồ Chí Minh', 10.7990, 106.6520);

-- 3. INSERT CINEMA HALLS DATA
-- ============================================
-- CGV Vincom Mega Mall (cinema_id = 1)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(1, 'Phòng 1 - Standard', 150),
(1, 'Phòng 2 - Standard', 150),
(1, 'Phòng 3 - VIP', 120),
(1, 'Phòng 4 - IMAX', 200);

-- CGV Parkson Hùng Vương (cinema_id = 2)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(2, 'Phòng 1', 130),
(2, 'Phòng 2', 130),
(2, 'Phòng 3 - VIP', 100);

-- Galaxy Nguyễn Du (cinema_id = 3)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(3, 'Phòng 1', 140),
(3, 'Phòng 2', 140),
(3, 'Phòng 3 - Premium', 110);

-- Galaxy Kinh Dương Vương (cinema_id = 4)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(4, 'Phòng 1', 135),
(4, 'Phòng 2', 135);

-- Lotte Cinema Cộng Hòa (cinema_id = 5)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(5, 'Phòng 1', 145),
(5, 'Phòng 2 - VIP', 115),
(5, 'Phòng 3 - 4DX', 90);

-- BHD Star Bitexco (cinema_id = 6)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(6, 'Phòng Gold Class 1', 80),
(6, 'Phòng Gold Class 2', 80);

-- CGV Vincom Center Landmark 81 (cinema_id = 7)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(7, 'Phòng 1', 160),
(7, 'Phòng 2 - Starium', 100),
(7, 'Phòng 3 - IMAX', 220);

-- Galaxy Tân Bình (cinema_id = 8)
INSERT INTO cinema_halls (cinema_id, hall_name, total_seats) VALUES
(8, 'Phòng 1', 125),
(8, 'Phòng 2', 125),
(8, 'Phòng 3 - Premium', 95);

-- 4. INSERT SEATS DATA (Sample for Phòng 1 của CGV Vincom Mega Mall)
-- ============================================
-- Phòng 1 - Standard (hall_id = 1): 10 rows (A-J), 15 seats per row = 150 seats
-- Rows A-C: Standard (70,000 VND)
-- Rows D-G: VIP (100,000 VND)
-- Rows H-J: Couple seats (150,000 VND for 2 people)

INSERT INTO seats (hall_id, row_label, seat_number, seat_type, base_price) VALUES
-- Row A
(1,'A',1,'STANDARD',70000),(1,'A',2,'STANDARD',70000),(1,'A',3,'STANDARD',70000),
(1,'A',4,'STANDARD',70000),(1,'A',5,'STANDARD',70000),(1,'A',6,'STANDARD',70000),
(1,'A',7,'STANDARD',70000),(1,'A',8,'STANDARD',70000),(1,'A',9,'STANDARD',70000),
(1,'A',10,'STANDARD',70000),(1,'A',11,'STANDARD',70000),(1,'A',12,'STANDARD',70000),
(1,'A',13,'STANDARD',70000),(1,'A',14,'STANDARD',70000),(1,'A',15,'STANDARD',70000),

-- Row B
(1,'B',1,'STANDARD',70000),(1,'B',2,'STANDARD',70000),(1,'B',3,'STANDARD',70000),
(1,'B',4,'STANDARD',70000),(1,'B',5,'STANDARD',70000),(1,'B',6,'STANDARD',70000),
(1,'B',7,'STANDARD',70000),(1,'B',8,'STANDARD',70000),(1,'B',9,'STANDARD',70000),
(1,'B',10,'STANDARD',70000),(1,'B',11,'STANDARD',70000),(1,'B',12,'STANDARD',70000),
(1,'B',13,'STANDARD',70000),(1,'B',14,'STANDARD',70000),(1,'B',15,'STANDARD',70000),

-- Row C
(1,'C',1,'STANDARD',70000),(1,'C',2,'STANDARD',70000),(1,'C',3,'STANDARD',70000),
(1,'C',4,'STANDARD',70000),(1,'C',5,'STANDARD',70000),(1,'C',6,'STANDARD',70000),
(1,'C',7,'STANDARD',70000),(1,'C',8,'STANDARD',70000),(1,'C',9,'STANDARD',70000),
(1,'C',10,'STANDARD',70000),(1,'C',11,'STANDARD',70000),(1,'C',12,'STANDARD',70000),
(1,'C',13,'STANDARD',70000),(1,'C',14,'STANDARD',70000),(1,'C',15,'STANDARD',70000);




INSERT INTO seats (hall_id, row_label, seat_number, seat_type, base_price) VALUES
-- Row D
(1,'D',1,'VIP',100000),(1,'D',2,'VIP',100000),(1,'D',3,'VIP',100000),
(1,'D',4,'VIP',100000),(1,'D',5,'VIP',100000),(1,'D',6,'VIP',100000),
(1,'D',7,'VIP',100000),(1,'D',8,'VIP',100000),(1,'D',9,'VIP',100000),
(1,'D',10,'VIP',100000),(1,'D',11,'VIP',100000),(1,'D',12,'VIP',100000),
(1,'D',13,'VIP',100000),(1,'D',14,'VIP',100000),(1,'D',15,'VIP',100000),

-- Row E
(1,'E',1,'VIP',100000),(1,'E',2,'VIP',100000),(1,'E',3,'VIP',100000),
(1,'E',4,'VIP',100000),(1,'E',5,'VIP',100000),(1,'E',6,'VIP',100000),
(1,'E',7,'VIP',100000),(1,'E',8,'VIP',100000),(1,'E',9,'VIP',100000),
(1,'E',10,'VIP',100000),(1,'E',11,'VIP',100000),(1,'E',12,'VIP',100000),
(1,'E',13,'VIP',100000),(1,'E',14,'VIP',100000),(1,'E',15,'VIP',100000),

-- Row F
(1,'F',1,'VIP',100000),(1,'F',2,'VIP',100000),(1,'F',3,'VIP',100000),
(1,'F',4,'VIP',100000),(1,'F',5,'VIP',100000),(1,'F',6,'VIP',100000),
(1,'F',7,'VIP',100000),(1,'F',8,'VIP',100000),(1,'F',9,'VIP',100000),
(1,'F',10,'VIP',100000),(1,'F',11,'VIP',100000),(1,'F',12,'VIP',100000),
(1,'F',13,'VIP',100000),(1,'F',14,'VIP',100000),(1,'F',15,'VIP',100000),

-- Row G
(1,'G',1,'VIP',100000),(1,'G',2,'VIP',100000),(1,'G',3,'VIP',100000),
(1,'G',4,'VIP',100000),(1,'G',5,'VIP',100000),(1,'G',6,'VIP',100000),
(1,'G',7,'VIP',100000),(1,'G',8,'VIP',100000),(1,'G',9,'VIP',100000),
(1,'G',10,'VIP',100000),(1,'G',11,'VIP',100000),(1,'G',12,'VIP',100000),
(1,'G',13,'VIP',100000),(1,'G',14,'VIP',100000),(1,'G',15,'VIP',100000),

-- Row H
(1,'H',1,'VIP',100000),(1,'H',2,'VIP',100000),(1,'H',3,'VIP',100000),
(1,'H',4,'VIP',100000),(1,'H',5,'VIP',100000),(1,'H',6,'VIP',100000),
(1,'H',7,'VIP',100000),(1,'H',8,'VIP',100000),(1,'H',9,'VIP',100000),
(1,'H',10,'VIP',100000),(1,'H',11,'VIP',100000),(1,'H',12,'VIP',100000),
(1,'H',13,'VIP',100000),(1,'H',14,'VIP',100000),(1,'H',15,'VIP',100000),

-- Row I
(1,'I',1,'VIP',100000),(1,'I',2,'VIP',100000),(1,'I',3,'VIP',100000),
(1,'I',4,'VIP',100000),(1,'I',5,'VIP',100000),(1,'I',6,'VIP',100000),
(1,'I',7,'VIP',100000),(1,'I',8,'VIP',100000),(1,'I',9,'VIP',100000),
(1,'I',10,'VIP',100000),(1,'I',11,'VIP',100000),(1,'I',12,'VIP',100000),
(1,'I',13,'VIP',100000),(1,'I',14,'VIP',100000),(1,'I',15,'VIP',100000);

INSERT INTO seats (hall_id, row_label, seat_number, seat_type, base_price) VALUES
                                                                               (1,'J',1,'COUPLE',150000),(1,'J',2,'COUPLE',150000),(1,'J',3,'COUPLE',150000),
                                                                               (1,'J',4,'COUPLE',150000),(1,'J',5,'COUPLE',150000),(1,'J',6,'COUPLE',150000),
                                                                               (1,'J',7,'COUPLE',150000),(1,'J',8,'COUPLE',150000),(1,'K',9,'COUPLE',150000),
                                                                               (1,'K',10,'COUPLE',150000),(1,'K',11,'COUPLE',150000),(1,'K',12,'COUPLE',150000),
                                                                               (1,'K',13,'COUPLE',150000),(1,'K',14,'COUPLE',150000),(1,'K',15,'COUPLE',150000);

-- NOTE: Tương tự cho các phòng chiếu khác (hall_id 2-23), bạn có thể tạo thêm seats với cùng pattern
-- Để tiết kiệm thời gian, tôi sẽ tạo sample cho một vài phòng quan trọng

-- 5. INSERT MOVIE SCREENINGS DATA
-- ============================================
-- Ngày 18/12/2025 - 25/12/2025 (1 tuần)

-- Movie 1: Zootopia 2 (movie_id=1, duration=108 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(1, 1, '2025-12-18', '09:00:00', '11:00:00', 'TWO_D', 1.00, 150),
(1, 1, '2025-12-18', '14:00:00', '16:00:00', 'TWO_D', 1.00, 150),
(1, 3, '2025-12-18', '18:30:00', '20:30:00', 'THREE_D', 1.50, 120),
(1, 4, '2025-12-18', '21:00:00', '23:00:00', 'IMAX_3D', 2.50, 200),
-- 19/12/2025
(1, 1, '2025-12-19', '10:00:00', '12:00:00', 'TWO_D', 1.00, 150),
(1, 2, '2025-12-19', '15:00:00', '17:00:00', 'THREE_D', 1.50, 150),
(1, 4, '2025-12-19', '20:00:00', '22:00:00', 'IMAX_3D', 2.50, 200),
-- 20/12/2025
(1, 1, '2025-12-20', '09:30:00', '11:30:00', 'TWO_D', 1.00, 150),
(1, 3, '2025-12-20', '16:00:00', '18:00:00', 'THREE_D', 1.50, 120),
(1, 4, '2025-12-20', '21:30:00', '23:30:00', 'IMAX_3D', 2.50, 200);

-- Movie 2: Avatar 3 (movie_id=2, duration=197 min ≈ 3h17m)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 19/12/2025 (Ngày ra mắt)
(2, 4, '2025-12-19', '09:00:00', '12:30:00', 'IMAX_3D', 2.50, 200),
(2, 4, '2025-12-19', '13:00:00', '16:30:00', 'IMAX_3D', 2.50, 200),
(2, 7, '2025-12-19', '17:00:00', '20:30:00', 'IMAX', 2.00, 220),
(2, 7, '2025-12-19', '21:00:00', '00:30:00', 'IMAX_3D', 2.50, 220),
-- 20/12/2025
(2, 4, '2025-12-20', '08:30:00', '12:00:00', 'IMAX_3D', 2.50, 200),
(2, 4, '2025-12-20', '14:00:00', '17:30:00', 'IMAX_3D', 2.50, 200),
(2, 7, '2025-12-20', '18:00:00', '21:30:00', 'IMAX_3D', 2.50, 220),
(2, 2, '2025-12-20', '10:00:00', '13:30:00', 'THREE_D', 1.50, 150),
(2, 2, '2025-12-20', '15:00:00', '18:30:00', 'THREE_D', 1.50, 150),
-- 21/12/2025
(2, 4, '2025-12-21', '09:00:00', '12:30:00', 'IMAX_3D', 2.50, 200),
(2, 7, '2025-12-21', '13:00:00', '16:30:00', 'IMAX_3D', 2.50, 220),
(2, 7, '2025-12-21', '19:00:00', '22:30:00', 'IMAX', 2.00, 220);

-- Movie 3: Truy Tìm Long Diên Hương (movie_id=3, duration=125 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(3, 5, '2025-12-18', '11:00:00', '13:15:00', 'TWO_D', 1.00, 130),
(3, 5, '2025-12-18', '16:00:00', '18:15:00', 'TWO_D', 1.00, 130),
(3, 8, '2025-12-18', '19:30:00', '21:45:00', 'TWO_D', 1.00, 140),
-- 19/12/2025
(3, 5, '2025-12-19', '10:30:00', '12:45:00', 'TWO_D', 1.00, 130),
(3, 6, '2025-12-19', '14:00:00', '16:15:00', 'TWO_D', 1.00, 130),
(3, 8, '2025-12-19', '20:00:00', '22:15:00', 'TWO_D', 1.00, 140),
-- 20/12/2025
(3, 5, '2025-12-20', '11:30:00', '13:45:00', 'TWO_D', 1.00, 130),
(3, 8, '2025-12-20', '17:00:00', '19:15:00', 'TWO_D', 1.00, 140);

-- Movie 4: Hoàng Tử Quỷ (movie_id=4, duration=112 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(4, 8, '2025-12-18', '21:00:00', '23:00:00', 'TWO_D', 1.00, 140),
(4, 10, '2025-12-18', '22:00:00', '00:00:00', 'TWO_D', 1.00, 140),
-- 19/12/2025
(4, 9, '2025-12-19', '20:30:00', '22:30:00', 'TWO_D', 1.00, 110),
(4, 10, '2025-12-19', '21:45:00', '23:45:00', 'TWO_D', 1.00, 140),
-- 20/12/2025 (Cuối tuần - nhiều suất)
(4, 8, '2025-12-20', '19:00:00', '21:00:00', 'TWO_D', 1.00, 140),
(4, 8, '2025-12-20', '22:00:00', '00:00:00', 'TWO_D', 1.00, 140),
(4, 10, '2025-12-20', '20:00:00', '22:00:00', 'TWO_D', 1.00, 140);

-- Movie 5: Wicked: For Good (movie_id=5, duration=160 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(5, 11, '2025-12-18', '10:00:00', '12:50:00', 'TWO_D', 1.00, 145),
(5, 11, '2025-12-18', '14:00:00', '16:50:00', 'TWO_D', 1.00, 145),
(5, 13, '2025-12-18', '18:00:00', '20:50:00', 'FOUR_DX', 2.00, 90),
-- 19/12/2025
(5, 11, '2025-12-19', '11:00:00', '13:50:00', 'TWO_D', 1.00, 145),
(5, 12, '2025-12-19', '15:30:00', '18:20:00', 'THREE_D', 1.50, 115),
(5, 13, '2025-12-19', '19:00:00', '21:50:00', 'FOUR_DX', 2.00, 90),
-- 20/12/2025
(5, 11, '2025-12-20', '09:00:00', '11:50:00', 'TWO_D', 1.00, 145),
(5, 11, '2025-12-20', '13:00:00', '15:50:00', 'TWO_D', 1.00, 145),
(5, 12, '2025-12-20', '17:00:00', '19:50:00', 'THREE_D', 1.50, 115);

-- Movie 6: Mai (movie_id=6, duration=131 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(6, 14, '2025-12-18', '10:30:00', '12:50:00', 'TWO_D', 1.00, 80),
(6, 15, '2025-12-18', '14:30:00', '16:50:00', 'TWO_D', 1.00, 80),
(6, 16, '2025-12-18', '18:30:00', '20:50:00', 'TWO_D', 1.00, 160),
-- 19/12/2025
(6, 14, '2025-12-19', '11:00:00', '13:20:00', 'TWO_D', 1.00, 80),
(6, 16, '2025-12-19', '15:00:00', '17:20:00', 'TWO_D', 1.00, 160),
(6, 17, '2025-12-19', '19:30:00', '21:50:00', 'TWO_D', 1.00, 100),
-- 20/12/2025
(6, 15, '2025-12-20', '10:00:00', '12:20:00', 'TWO_D', 1.00, 80),
(6, 16, '2025-12-20', '14:00:00', '16:20:00', 'TWO_D', 1.00, 160),
(6, 17, '2025-12-20', '18:00:00', '20:20:00', 'TWO_D', 1.00, 100);

-- Movie 7: Deadpool & Wolverine (movie_id=7, duration=128 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 18/12/2025
(7, 19, '2025-12-18', '12:00:00', '14:15:00', 'TWO_D', 1.00, 160),
(7, 19, '2025-12-18', '16:30:00', '18:45:00', 'TWO_D', 1.00, 160),
(7, 21, '2025-12-18', '20:00:00', '22:15:00', 'IMAX', 2.00, 220),
-- 19/12/2025
(7, 19, '2025-12-19', '13:00:00', '15:15:00', 'TWO_D', 1.00, 160),
(7, 20, '2025-12-19', '17:00:00', '19:15:00', 'THREE_D', 1.50, 100),
(7, 21, '2025-12-19', '21:00:00', '23:15:00', 'IMAX_3D', 2.50, 220),
-- 20/12/2025
(7, 19, '2025-12-20', '11:00:00', '13:15:00', 'TWO_D', 1.00, 160),
(7, 19, '2025-12-20', '15:00:00', '17:15:00', 'TWO_D', 1.00, 160),
(7, 21, '2025-12-20', '19:00:00', '21:15:00', 'IMAX_3D', 2.50, 220);

-- Movie 8: Mufasa: The Lion King (movie_id=8, duration=118 min)
INSERT INTO movie_screenings (movie_id, hall_id, screening_date, start_time, end_time, screening_type, price_multiplier, available_seats) VALUES
-- 20/12/2025 (Ngày ra mắt)
(8, 22, '2025-12-20', '09:00:00', '11:00:00', 'TWO_D', 1.00, 125),
(8, 22, '2025-12-20', '13:00:00', '15:00:00', 'THREE_D', 1.50, 125),
(8, 23, '2025-12-20', '16:00:00', '18:00:00', 'TWO_D', 1.00, 125),

-- 21/12/2025
(8, 22, '2025-12-21', '10:00:00', '12:00:00', 'TWO_D', 1.00, 125),
(8, 22, '2025-12-21', '14:00:00', '16:00:00', 'THREE_D', 1.50, 125),
(8, 23, '2025-12-21', '17:00:00', '19:00:00', 'TWO_D', 1.00, 125);


-- 6. SAMPLE BOOKINGS DATA (Một vài booking mẫu)
-- ============================================
-- NOTE: Giả sử có user_id = 1 trong bảng users

-- Booking 1: User đặt 3 ghế xem Zootopia 2
INSERT INTO bookings (user_id, screening_id, customer_name, customer_phone, customer_email, total_seats, total_amount, status, booking_time, expiry_time, booking_code) VALUES
(1, 1, 'Nguyễn Văn A', '0901234567', 'nguyenvana@example.com', 3, 210000, 'CONFIRMED', '2025-12-17 14:30:00', '2025-12-17 14:40:00', 'BK20251217001');

-- Booking 2: User đặt 2 ghế VIP xem Avatar 3
INSERT INTO bookings (user_id, screening_id, customer_name, customer_phone, customer_email, total_seats, total_amount, status, booking_time, expiry_time, booking_code) VALUES
(1, 11, 'Trần Thị B', '0912345678', 'tranthib@example.com', 2, 500000, 'CONFIRMED', '2025-12-18 10:00:00', '2025-12-18 10:10:00', 'BK20251218001');

-- Booking 3: User đặt 4 ghế xem Wicked (Pending - chưa thanh toán)
INSERT INTO bookings (user_id, screening_id, customer_name, customer_phone, customer_email, total_seats, total_amount, status, booking_time, expiry_time, booking_code) VALUES
(1, 29, 'Lê Văn C', '0923456789', 'levanc@example.com', 4, 280000, 'PENDING', '2025-12-18 11:00:00', '2025-12-18 11:10:00', 'BK20251218002');

-- 7. INSERT BOOKING SEATS DATA
-- ============================================
-- Booking 1: 3 ghế A1, A2, A3 của screening_id=1
INSERT INTO booking_seats (booking_id, seat_id, screening_id, price, status) VALUES
(1, 1, 1, 70000, 'BOOKED'),
(1, 2, 1, 70000, 'BOOKED'),
(1, 3, 1, 70000, 'BOOKED');

-- Booking 2: 2 ghế VIP D5, D6 (IMAX 3D - hệ số 2.5) của screening_id=11
-- Giả sử seat_id D5=50, D6=51 (VIP seats với base_price=100000)
-- Final price = 100000 * 2.5 = 250000
INSERT INTO booking_seats (booking_id, seat_id, screening_id, price, status) VALUES
(2, 50, 11, 250000, 'BOOKED'),
(2, 51, 11, 250000, 'BOOKED');

-- Booking 3: 4 ghế A10, A11, A12, A13 (RESERVED - đang giữ chỗ)
INSERT INTO booking_seats (booking_id, seat_id, screening_id, price, status) VALUES
(3, 10, 29, 70000, 'RESERVED'),
(3, 11, 29, 70000, 'RESERVED'),
(3, 12, 29, 70000, 'RESERVED'),
(3, 13, 29, 70000, 'RESERVED');

-- 8. INSERT PAYMENTS DATA
-- ============================================
-- Payment cho Booking 1
INSERT INTO payments (booking_id, amount, payment_method, payment_status, payment_time, transaction_id, payment_details) VALUES
(1, 210000, 'ACCOUNT_BALANCE', 'SUCCESS', '2025-12-17 14:35:00', 'TXN20251217001', '{"account_number": "1234567890", "balance_after": 9790000}');

-- Payment cho Booking 2
INSERT INTO payments (booking_id, amount, payment_method, payment_status, payment_time, transaction_id, payment_details) VALUES
(2, 500000, 'ACCOUNT_BALANCE', 'SUCCESS', '2025-12-18 10:05:00', 'TXN20251218001', '{"account_number": "1234567890", "balance_after": 9290000}');

-- ============================================
-- END OF SAMPLE DATA
-- ============================================

-- NOTES:
-- 1. Để có đầy đủ data cho tất cả seats, bạn cần tạo thêm INSERT cho các hall_id từ 2-24
-- 2. Pattern tạo seats tương tự như hall_id=1 (150 ghế = 10 rows x 15 seats)
-- 3. Điều chỉnh seat_type và base_price theo từng loại phòng (Standard, VIP, IMAX, Premium...)
-- 4. Bạn có thể tạo thêm bookings và payments để test đầy đủ các tính năng
-- 5. Remember: Final price = base_price × price_multiplier

-- ============================================
-- UTILITY BILLS SAMPLE DATA
-- ============================================

-- Sample Electricity Bills (EVN - Điện lực)
INSERT INTO utility_bills (
    bill_code, bill_type, customer_name, customer_address, customer_phone,
    period, usage_amount, old_index, new_index, unit_price,
    amount, vat, total_amount,
    issue_date, due_date, status,
    provider_name, provider_code, notes,
    created_at
) VALUES
      (
          'EVN202411001',
          'ELECTRICITY',
          'Nguyễn Văn A',
          '123 Nguyễn Huệ, Quận 1, TP.HCM',
          '0901234567',
          '2024-11',
          250,
          1000,
          1250,
          2500,
          625000,
          62500,
          687500,
          '2024-11-25',
          '2024-12-20',
          'UNPAID',
          'Tổng Công ty Điện lực TP.HCM',
          'EVNHCMC',
          'Hóa đơn tiền điện tháng 11/2024',
          NOW()
      ),
      (
          'EVN202412001',
          'ELECTRICITY',
          'Trần Thị B',
          '456 Lê Lợi, Quận 3, TP.HCM',
          '0912345678',
          '2024-12',
          180,
          1250,
          1430,
          2500,
          450000,
          45000,
          495000,
          '2024-12-15',
          '2025-01-10',
          'UNPAID',
          'Tổng Công ty Điện lực TP.HCM',
          'EVNHCMC',
          'Hóa đơn tiền điện tháng 12/2024',
          NOW()
      ),
      (
          'EVN202410001',
          'ELECTRICITY',
          'Lê Văn C',
          '789 Võ Văn Tần, Quận 3, TP.HCM',
          '0923456789',
          '2024-10',
          320,
          800,
          1120,
          2500,
          800000,
          80000,
          880000,
          '2024-10-25',
          '2024-11-20',
          'OVERDUE',
          'Tổng Công ty Điện lực TP.HCM',
          'EVNHCMC',
          'Hóa đơn tiền điện tháng 10/2024 - Quá hạn',
          NOW()
      );


INSERT INTO utility_bills (
    bill_code, bill_type, customer_name, customer_address, customer_phone,
    period, usage_amount, old_index, new_index, unit_price,
    amount, vat, total_amount,
    issue_date, due_date, status,
    provider_name, provider_code, notes,
    created_at
) VALUES
      (
          'VNW202411001',
          'WATER',
          'Phạm Thị D',
          '321 Pasteur, Quận 1, TP.HCM',
          '0934567890',
          '2024-11',
          15,
          100,
          115,
          8000,
          120000,
          12000,
          132000,
          '2024-11-28',
          '2024-12-25',
          'UNPAID',
          'Công ty Cấp nước Sài Gòn',
          'SAWACO',
          'Hóa đơn tiền nước tháng 11/2024',
          NOW()
      ),
      (
          'VNW202412001',
          'WATER',
          'Hoàng Văn E',
          '654 Điện Biên Phủ, Quận 3, TP.HCM',
          '0945678901',
          '2024-12',
          20,
          115,
          135,
          8000,
          160000,
          16000,
          176000,
          '2024-12-15',
          '2025-01-12',
          'UNPAID',
          'Công ty Cấp nước Sài Gòn',
          'SAWACO',
          'Hóa đơn tiền nước tháng 12/2024',
          NOW()
      );


-- Sample Internet Bills
INSERT INTO utility_bills (
    bill_code, bill_type, customer_name, customer_address, customer_phone,
    period, usage_amount, old_index, new_index, unit_price,
    amount, vat, total_amount,
    issue_date, due_date, status,
    provider_name, provider_code, notes,
    created_at
) VALUES
    (
        'VNP202412001',
        'INTERNET',
        'Vũ Thị F',
        '147 Hai Bà Trưng, Quận 1, TP.HCM',
        '0956789012',
        '2024-12',
        NULL,
        NULL,
        NULL,
        NULL,
        200000,
        20000,
        220000,
        '2024-12-01',
        '2024-12-31',
        'UNPAID',
        'VNPT TP.HCM',
        'VNPT',
        'Gói cước Home 100Mbps',
        NOW()
    );


-- Sample Phone Bills
INSERT INTO utility_bills (
    bill_code, bill_type, customer_name, customer_address, customer_phone,
    period, usage_amount, old_index, new_index, unit_price, amount, vat, total_amount,
    issue_date, due_date, status, provider_name, provider_code, notes
) VALUES
-- Hóa đơn điện thoại tháng 12/2024
(
    'VTL202412001',
    'PHONE',
    'Đỗ Văn G',
    '258 Lý Tự Trọng, Quận 1, TP.HCM',
    '0967890123',
    '2024-12',
    NULL,
    NULL,
    NULL,
    NULL,
    150000,
    15000,
    165000,
    '2024-12-05',
    '2025-01-05',
    'UNPAID',
    'Viettel TP.HCM',
    'VIETTEL',
    'Gói cước VD149 - Data không giới hạn'
);
