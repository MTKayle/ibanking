package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.movie.BookingRequestDTO;
import org.example.storyreading.ibanking.dto.movie.BookingResponseDTO;
import org.example.storyreading.ibanking.dto.movie.BookedSeatDetailDTO;
import org.example.storyreading.ibanking.dto.movie.MyBookingResponseDTO;
import org.example.storyreading.ibanking.entity.*;
import org.example.storyreading.ibanking.repository.*;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieScreeningRepository movieScreeningRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Đặt vé xem phim
     */
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        // 1. Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUserId = userDetails.getUserId();
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 2. Lấy thông tin suất chiếu
        MovieScreening screening = movieScreeningRepository.findByIdWithDetails(request.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        // 3. Validate số lượng ghế
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 ghế");
        }

        // 4. Lấy thông tin các ghế được chọn
        List<Seat> selectedSeats = seatRepository.findAllById(request.getSeatIds());
        if (selectedSeats.size() != request.getSeatIds().size()) {
            throw new RuntimeException("Một số ghế không tồn tại");
        }

        // 5. Kiểm tra ghế đã được đặt chưa
        List<BookingSeat> bookedSeats = bookingSeatRepository.findBookedSeatsByScreeningId(request.getScreeningId());
        List<Long> bookedSeatIds = bookedSeats.stream()
                .map(bs -> bs.getSeat().getSeatId())
                .collect(Collectors.toList());

        for (Long seatId : request.getSeatIds()) {
            if (bookedSeatIds.contains(seatId)) {
                throw new RuntimeException("Ghế đã được đặt, vui lòng chọn ghế khác");
            }
        }

        // 6. Tính tổng tiền
        BigDecimal priceMultiplier = screening.getPriceMultiplier();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Seat seat : selectedSeats) {
            BigDecimal seatPrice = seat.getBasePrice().multiply(priceMultiplier);
            totalAmount = totalAmount.add(seatPrice);
        }

        // 7. Lấy tài khoản checking đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản thanh toán"));

        // 8. Kiểm tra số dư
        if (checkingAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Số dư tài khoản không đủ. Số dư hiện tại: "
                + checkingAccount.getBalance() + " VND. Cần thanh toán: " + totalAmount + " VND");
        }

        // 9. Trừ tiền trong tài khoản
        BigDecimal newBalance = checkingAccount.getBalance().subtract(totalAmount);
        checkingAccount.setBalance(newBalance);
        checkingAccountRepository.save(checkingAccount);

        // 10. Tạo booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScreening(screening);
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setCustomerEmail(request.getCustomerEmail());
        booking.setTotalSeats(selectedSeats.size());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        // Tạo booking code: BK + YYYYMMDD + sequence
        String bookingCode = generateBookingCode();
        booking.setBookingCode(bookingCode);

        booking = bookingRepository.save(booking);

        // 11. Tạo booking seats
        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setScreening(screening);
            bookingSeat.setPrice(seat.getBasePrice().multiply(priceMultiplier));
            bookingSeat.setStatus(SeatStatus.BOOKED);
            bookingSeats.add(bookingSeat);
        }
        bookingSeatRepository.saveAll(bookingSeats);

        // 12. Tạo transaction
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(checkingAccount.getAccount());
        transaction.setReceiverAccount(null); // Thanh toán cho hệ thống (không có receiver account)
        transaction.setAmount(totalAmount);
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAW);
        transaction.setDescription("Thanh toán vé xem phim " + screening.getMovie().getTitle()
            + " - " + selectedSeats.size() + " vé");
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        // Generate transaction code
        String transactionCode = "TXN" + System.currentTimeMillis();
        transaction.setCode(transactionCode);

        transaction = transactionRepository.save(transaction);

        // 13. Cập nhật số ghế còn trống của suất chiếu
        Integer availableSeats = screening.getAvailableSeats() - selectedSeats.size();
        screening.setAvailableSeats(availableSeats);
        movieScreeningRepository.save(screening);

        // 14. Tạo response
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(booking.getBookingId());
        response.setBookingCode(booking.getBookingCode());
        response.setScreeningId(screening.getScreeningId());
        response.setMovieTitle(screening.getMovie().getTitle());
        response.setCinemaName(screening.getCinemaHall().getCinema().getName());
        response.setHallName(screening.getCinemaHall().getHallName());
        response.setScreeningDate(screening.getScreeningDate());
        response.setStartTime(screening.getStartTime());
        response.setCustomerName(booking.getCustomerName());
        response.setCustomerPhone(booking.getCustomerPhone());
        response.setCustomerEmail(booking.getCustomerEmail());

        List<String> seatLabels = selectedSeats.stream()
                .map(seat -> seat.getRowLabel() + seat.getSeatNumber())
                .collect(Collectors.toList());
        response.setSeatLabels(seatLabels);

        response.setTotalSeats(booking.getTotalSeats());
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus().name());
        response.setBookingTime(booking.getBookingTime());
        response.setTransactionId(transaction.getTransactionId().toString());

        return response;
    }

    /**
     * Generate booking code: BK + YYYYMMDD + sequence
     */
    private String generateBookingCode() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = now.format(formatter);

        // Lấy số sequence (số booking trong ngày)
        long count = bookingRepository.count() + 1;
        String sequence = String.format("%03d", count % 1000);

        return "BK" + dateStr + sequence;
    }

    /**
     * Lấy tất cả booking của user hiện tại
     */
    @Transactional(readOnly = true)
    public List<MyBookingResponseDTO> getMyBookings() {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUserId = userDetails.getUserId();
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Lấy tất cả bookings của user
        List<Booking> bookings = bookingRepository.findByUserIdWithDetails(user.getUserId());

        // Convert sang DTO
        return bookings.stream()
                .map(this::convertToMyBookingDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Booking entity sang MyBookingResponseDTO
     */
    private MyBookingResponseDTO convertToMyBookingDTO(Booking booking) {
        MyBookingResponseDTO dto = new MyBookingResponseDTO();

        // Thông tin booking
        dto.setBookingId(booking.getBookingId());
        dto.setBookingCode(booking.getBookingCode());

        // Thông tin phim
        Movie movie = booking.getScreening().getMovie();
        dto.setMovieId(movie.getMovieId());
        dto.setMovieTitle(movie.getTitle());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setDurationMinutes(movie.getDurationMinutes());

        // Thông tin suất chiếu
        MovieScreening screening = booking.getScreening();
        dto.setScreeningId(screening.getScreeningId());
        dto.setScreeningDate(screening.getScreeningDate());
        dto.setStartTime(screening.getStartTime());
        dto.setEndTime(screening.getEndTime());
        dto.setScreeningType(screening.getScreeningType().name());
        dto.setScreeningTypeDisplay(screening.getScreeningType().getDisplayName());

        // Thông tin rạp
        Cinema cinema = screening.getCinemaHall().getCinema();
        dto.setCinemaName(cinema.getName());
        dto.setCinemaAddress(cinema.getAddress());
        dto.setHallName(screening.getCinemaHall().getHallName());

        // Thông tin khách hàng
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setCustomerEmail(booking.getCustomerEmail());

        // Thông tin ghế - lấy từ booking_seats
        List<BookingSeat> bookingSeats = bookingSeatRepository
                .findAll()
                .stream()
                .filter(bs -> bs.getBooking().getBookingId().equals(booking.getBookingId()))
                .collect(Collectors.toList());

        List<BookedSeatDetailDTO> seatDTOs = bookingSeats.stream()
                .map(this::convertToBookedSeatDTO)
                .collect(Collectors.toList());
        dto.setSeats(seatDTOs);

        // Thông tin thanh toán
        dto.setTotalSeats(booking.getTotalSeats());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus().name());
        dto.setBookingTime(booking.getBookingTime());

        return dto;
    }

    /**
     * Convert BookingSeat sang BookedSeatDetailDTO
     */
    private BookedSeatDetailDTO convertToBookedSeatDTO(BookingSeat bookingSeat) {
        Seat seat = bookingSeat.getSeat();

        BookedSeatDetailDTO dto = new BookedSeatDetailDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatLabel(seat.getRowLabel() + seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType().name());
        dto.setSeatTypeDisplay(seat.getSeatType().getDisplayName());
        dto.setPrice(bookingSeat.getPrice());

        return dto;
    }
}
