package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.*;
import org.example.storyreading.ibanking.entity.*;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.*;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MortgageAccountService {

    @Autowired
    private MortgageAccountRepository mortgageAccountRepository;

    @Autowired
    private MortgagePaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MortgageInterestRateRepository mortgageInterestRateRepository;

    private static final BigDecimal PENALTY_RATE_MULTIPLIER = new BigDecimal("1.5"); // 150% lãi suất
    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");

    /**
     * Tạo tài khoản vay thế chấp mới - Nhân viên tạo cho khách hàng
     */
    @Transactional
    public MortgageAccountResponse createMortgageAccount(
            CreateMortgageAccountRequest request,
            MultipartFile cccdFront,
            MultipartFile cccdBack,
            List<MultipartFile> collateralDocuments) throws Exception {

        // 1. Tìm user theo số điện thoại
        User user = userRepository.findByPhone(request.getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với số điện thoại: " + request.getPhoneNumber()));

        // 2. Upload ảnh lên Cloudinary
        String cccdFrontUrl = null;
        String cccdBackUrl = null;
        String collateralDocUrls = null;

        if (cccdFront != null && !cccdFront.isEmpty()) {
            cccdFrontUrl = cloudinaryService.uploadImage(cccdFront, user.getUserId());
        }

        if (cccdBack != null && !cccdBack.isEmpty()) {
            cccdBackUrl = cloudinaryService.uploadImage(cccdBack, user.getUserId());
        }

        if (collateralDocuments != null && !collateralDocuments.isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile doc : collateralDocuments) {
                if (doc != null && !doc.isEmpty()) {
                    urls.add(cloudinaryService.uploadImage(doc, user.getUserId()));
                }
            }
            collateralDocUrls = String.join(",", urls);
        }

        // 3. Tạo Account cho mortgage
        Account mortgageAccountEntity = new Account();
        mortgageAccountEntity.setUser(user);
        mortgageAccountEntity.setAccountType(Account.AccountType.mortgage);
        mortgageAccountEntity.setAccountNumber(generateMortgageAccountNumber());
        mortgageAccountEntity.setStatus(Account.Status.active);
        Account savedAccount = accountRepository.save(mortgageAccountEntity);
        accountRepository.flush();

        // 4. Tạo MortgageAccount với trạng thái PENDING_APPRAISAL
        MortgageAccount mortgageAccount = new MortgageAccount();
        mortgageAccount.setAccount(savedAccount);
        mortgageAccount.setStatus(MortgageAccount.MortgageStatus.PENDING_APPRAISAL);
        mortgageAccount.setCollateralType(request.getCollateralType());
        mortgageAccount.setCollateralDescription(request.getCollateralDescription());
        mortgageAccount.setCccdFrontUrl(cccdFrontUrl);
        mortgageAccount.setCccdBackUrl(cccdBackUrl);
        mortgageAccount.setCollateralDocumentUrls(collateralDocUrls);
        mortgageAccount.setPaymentFrequency(request.getPaymentFrequency());
        mortgageAccount.setCreatedDate(LocalDate.now());
        mortgageAccount.setPrincipalAmount(BigDecimal.ZERO);
        mortgageAccount.setInterestRate(BigDecimal.ZERO);

        MortgageAccount savedMortgage = mortgageAccountRepository.save(mortgageAccount);
        mortgageAccountRepository.flush();

        return convertToResponse(savedMortgage);
    }

    /**
     * Phê duyệt tài khoản vay - Nhân viên thẩm định
     */
    @Transactional
    public MortgageAccountResponse approveMortgage(ApproveMortgageRequest request) {
        // 1. Tìm mortgage account
        MortgageAccount mortgageAccount = mortgageAccountRepository.findById(request.getMortgageId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản vay"));

        // 2. Kiểm tra trạng thái
        if (mortgageAccount.getStatus() != MortgageAccount.MortgageStatus.PENDING_APPRAISAL) {
            throw new IllegalStateException("Tài khoản vay không ở trạng thái chờ thẩm định");
        }

        // 3. Tự động lấy lãi suất từ bảng lãi suất theo kỳ hạn
        BigDecimal autoInterestRate = getInterestRateByTermMonths(request.getTermMonths());

        // Nếu request có lãi suất thì dùng request, không thì dùng auto
        BigDecimal finalInterestRate = (request.getInterestRate() != null && request.getInterestRate().compareTo(BigDecimal.ZERO) > 0)
                ? request.getInterestRate()
                : autoInterestRate;

        //

        // 4. Cập nhật thông tin
        mortgageAccount.setPrincipalAmount(request.getPrincipalAmount());
        mortgageAccount.setInterestRate(finalInterestRate.divide(new BigDecimal(12), 10, RoundingMode.HALF_UP)); // Lưu lãi suất ở dạng thập phân
        mortgageAccount.setTermMonths(request.getTermMonths());
        mortgageAccount.setStartDate(LocalDate.now());
        mortgageAccount.setStatus(MortgageAccount.MortgageStatus.ACTIVE);
        mortgageAccount.setApprovalDate(LocalDate.now());

        MortgageAccount savedMortgage = mortgageAccountRepository.save(mortgageAccount);
        mortgageAccountRepository.flush();

        // 5. Tạo payment schedule
        generatePaymentSchedule(savedMortgage);

        // 6. Giải ngân khoản vay vào tài khoản checking đầu tiên của khách hàng

        CheckingAccount customerCheckingAccount = checkingAccountRepository
                .findFirstByUserId(
                        mortgageAccount.getAccount().getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không có tài khoản thanh toán hợp lệ"));

        customerCheckingAccount.setBalance(
                customerCheckingAccount.getBalance().add(mortgageAccount.getPrincipalAmount()));
        checkingAccountRepository.save(customerCheckingAccount);

        // 7. Tạo transaction giải ngân
        String code = generateMortgageTransactionCode();
        Transaction transaction = new Transaction();
        transaction.setCode(code);
        transaction.setSenderAccount(null); // Nguồn từ ngân hàng
        transaction.setReceiverAccount(mortgageAccount.getAccount());
        transaction.setAmount(mortgageAccount.getPrincipalAmount());
        transaction.setTransactionType(Transaction.TransactionType.LOAN_PAYMENT);
        transaction.setDescription("Giải ngân khoản vay thế chấp - " + mortgageAccount.getAccount().getAccountNumber());
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);




        return convertToResponse(savedMortgage);
    }

    /**
     * Lấy lãi suất theo kỳ hạn từ bảng lãi suất
     */
    private BigDecimal getInterestRateByTermMonths(Integer termMonths) {
        return mortgageInterestRateRepository.findByTermMonths(termMonths)
                .map(MortgageInterestRate::getInterestRate)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy lãi suất cho kỳ hạn " + termMonths + " tháng"));
    }

    /**
     * Từ chối tài khoản vay
     */
    @Transactional
    public MortgageAccountResponse rejectMortgage(RejectMortgageRequest request) {
        MortgageAccount mortgageAccount = mortgageAccountRepository.findById(request.getMortgageId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản vay"));

        if (mortgageAccount.getStatus() != MortgageAccount.MortgageStatus.PENDING_APPRAISAL) {
            throw new IllegalStateException("Tài khoản vay không ở trạng thái chờ thẩm định");
        }

        mortgageAccount.setStatus(MortgageAccount.MortgageStatus.REJECTED);
        mortgageAccount.setRejectionReason(request.getRejectionReason());
        mortgageAccount.setApprovalDate(LocalDate.now());

        MortgageAccount savedMortgage = mortgageAccountRepository.save(mortgageAccount);

        return convertToResponse(savedMortgage);
    }

    /**
     * Tạo lịch thanh toán - Áp dụng phương pháp trả đều
     */
    private void generatePaymentSchedule(MortgageAccount mortgageAccount) {
        BigDecimal principal = mortgageAccount.getPrincipalAmount();
        BigDecimal monthlyRate = mortgageAccount.getInterestRate().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

        // Tính số kỳ dựa trên payment frequency
        int totalPeriods;
        if (mortgageAccount.getPaymentFrequency() == MortgageAccount.PaymentFrequency.MONTHLY) {
            totalPeriods = mortgageAccount.getTermMonths();
        } else {
            // BI_WEEKLY: 1 tháng = 2 kỳ
            totalPeriods = mortgageAccount.getTermMonths() * 2;
            monthlyRate = monthlyRate.divide(new BigDecimal("2"), 10, RoundingMode.HALF_UP);
        }

        // Tính số tiền trả đều mỗi kỳ (gốc + lãi)
        // PMT = P * r * (1 + r)^n / ((1 + r)^n - 1)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(totalPeriods);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRatePowN);
        BigDecimal denominator = onePlusRatePowN.subtract(BigDecimal.ONE);
        BigDecimal periodicPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        BigDecimal remainingBalance = principal;
        LocalDate dueDate = mortgageAccount.getStartDate();

        for (int i = 1; i <= totalPeriods; i++) {
            // Tính lãi cho kỳ này
            BigDecimal interestAmount = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            // Tính gốc cho kỳ này
            BigDecimal principalAmount = periodicPayment.subtract(interestAmount).setScale(2, RoundingMode.HALF_UP);

            // Đảm bảo kỳ cuối trả hết
            if (i == totalPeriods) {
                principalAmount = remainingBalance;
                periodicPayment = principalAmount.add(interestAmount);
            }

            // Cập nhật dư nợ
            remainingBalance = remainingBalance.subtract(principalAmount);

            // Tính ngày đến hạn
            if (mortgageAccount.getPaymentFrequency() == MortgageAccount.PaymentFrequency.MONTHLY) {
                dueDate = mortgageAccount.getStartDate().plusMonths(i);
            } else {
                dueDate = mortgageAccount.getStartDate().plusWeeks(i * 2L);
            }

            // Tạo payment schedule
            MortgagePaymentSchedule schedule = new MortgagePaymentSchedule();
            schedule.setMortgageAccount(mortgageAccount);
            schedule.setPeriodNumber(i);
            schedule.setDueDate(dueDate);
            schedule.setPrincipalAmount(principalAmount);
            schedule.setInterestAmount(interestAmount);
            schedule.setTotalAmount(periodicPayment);
            schedule.setPenaltyAmount(BigDecimal.ZERO);
            schedule.setRemainingBalance(remainingBalance);
            schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.PENDING);
            schedule.setPaidAmount(BigDecimal.ZERO);
            schedule.setOverdueDays(0);

            paymentScheduleRepository.save(schedule);
        }
    }

    /**
     * Thanh toán khoản vay - TẤT TOÁN (thanh toán tất cả các kỳ chưa thanh toán)
     */
    @Transactional
    public MortgageAccountResponse makePayment(MortgagePaymentRequest request) {
        // 1. Tìm mortgage account
        MortgageAccount mortgageAccount = mortgageAccountRepository.findById(request.getMortgageId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản vay"));

        if (mortgageAccount.getStatus() != MortgageAccount.MortgageStatus.ACTIVE) {
            throw new IllegalStateException("Tài khoản vay không ở trạng thái hoạt động");
        }

        // 2. Tìm checking account để trừ tiền
        CheckingAccount checkingAccount = checkingAccountRepository.findByAccountNumber(request.getPaymentAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản thanh toán"));

        if (checkingAccount.getBalance().compareTo(request.getPaymentAmount()) < 0) {
            throw new IllegalArgumentException("Số dư tài khoản không đủ để thanh toán");
        }

        // 3. Lấy danh sách các kỳ chưa thanh toán từ DB (đã có penalty, flags)
        List<MortgagePaymentSchedule> unpaidSchedules = paymentScheduleRepository.findUnpaidSchedules(mortgageAccount);

        if (unpaidSchedules.isEmpty()) {
            throw new IllegalStateException("Không có kỳ nào cần thanh toán");
        }

        // 4. Tính tổng số tiền cần thanh toán từ dữ liệu DB (đã bao gồm lãi phạt)
        BigDecimal totalDue = unpaidSchedules.stream()
                .map(s -> s.getTotalAmount().subtract(s.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.getPaymentAmount().compareTo(totalDue) < 0) {
            throw new IllegalArgumentException(
                    String.format("Số tiền thanh toán không đủ. Cần thanh toán: %s VND", totalDue));
        }

        // 5. Trừ tiền từ checking account
        checkingAccount.setBalance(checkingAccount.getBalance().subtract(request.getPaymentAmount()));
        checkingAccountRepository.save(checkingAccount);

        // 6. Cập nhật trạng thái các kỳ đã thanh toán
        BigDecimal remainingPayment = request.getPaymentAmount();
        for (MortgagePaymentSchedule schedule : unpaidSchedules) {
            BigDecimal amountDue = schedule.getTotalAmount().subtract(schedule.getPaidAmount());

            if (remainingPayment.compareTo(amountDue) >= 0) {
                schedule.setPaidAmount(schedule.getTotalAmount());
                schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.PAID);
                schedule.setPaidDate(LocalDate.now());
                schedule.setIsOverdue(false);
                schedule.setIsCurrentPeriod(false);
                remainingPayment = remainingPayment.subtract(amountDue);
            } else {
                schedule.setPaidAmount(schedule.getPaidAmount().add(remainingPayment));
                schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.PARTIAL);
                remainingPayment = BigDecimal.ZERO;
            }

            paymentScheduleRepository.save(schedule);

            if (remainingPayment.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        // 7. Kiểm tra xem đã thanh toán hết chưa
        List<MortgagePaymentSchedule> remainingSchedules = paymentScheduleRepository.findUnpaidSchedules(mortgageAccount);
        if (remainingSchedules.isEmpty()) {
            mortgageAccount.setStatus(MortgageAccount.MortgageStatus.COMPLETED);
            mortgageAccountRepository.save(mortgageAccount);
        } else {
            // Cập nhật lại flags cho các kỳ còn lại
            updatePaymentScheduleFlags(mortgageAccount);
        }

        String code = generateMortgageTransactionCode();

        // 8. Tạo transaction
        Transaction transaction = new Transaction();
        transaction.setCode(code);
        transaction.setSenderAccount(checkingAccount.getAccount());
        transaction.setReceiverAccount(mortgageAccount.getAccount());
        transaction.setAmount(request.getPaymentAmount());
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription("Tất toán khoản vay thế chấp - " + mortgageAccount.getAccount().getAccountNumber());
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return convertToResponse(mortgageAccount);
    }

    /**
     * Thanh toán kỳ hiện tại - Thanh toán kỳ tiếp theo và các kỳ quá hạn
     */
    @Transactional
    public MortgageAccountResponse makeCurrentPayment(MortgagePaymentRequest request) {
        // 1. Tìm mortgage account
        MortgageAccount mortgageAccount = mortgageAccountRepository.findById(request.getMortgageId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản vay"));

        if (mortgageAccount.getStatus() != MortgageAccount.MortgageStatus.ACTIVE) {
            throw new IllegalStateException("Tài khoản vay không ở trạng thái hoạt động");
        }

        // 2. Tìm checking account để trừ tiền
        CheckingAccount checkingAccount = checkingAccountRepository.findByAccountNumber(request.getPaymentAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản thanh toán"));

        if (checkingAccount.getBalance().compareTo(request.getPaymentAmount()) < 0) {
            throw new IllegalArgumentException("Số dư tài khoản không đủ để thanh toán");
        }

        // 3. Lấy danh sách các kỳ chưa thanh toán từ DB
        List<MortgagePaymentSchedule> unpaidSchedules = paymentScheduleRepository.findUnpaidSchedules(mortgageAccount);

        if (unpaidSchedules.isEmpty()) {
            throw new IllegalStateException("Không có kỳ nào cần thanh toán");
        }

        // 4. Lấy các kỳ cần thanh toán từ DB (dùng flags đã lưu sẵn)
        List<MortgagePaymentSchedule> payableSchedules = unpaidSchedules.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsCurrentPeriod()) || Boolean.TRUE.equals(s.getIsOverdue()))
                .collect(Collectors.toList());

        if (payableSchedules.isEmpty()) {
            throw new IllegalStateException("Không có kỳ nào cần thanh toán");
        }

        // 5. Tính tổng số tiền cần thanh toán từ dữ liệu DB (đã bao gồm penalty)
        BigDecimal totalDue = payableSchedules.stream()
                .map(s -> s.getTotalAmount().subtract(s.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đếm số kỳ quá hạn
        long overdueCount = payableSchedules.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsOverdue()))
                .count();

        if (request.getPaymentAmount().compareTo(totalDue) < 0) {
            String message = overdueCount > 0
                ? String.format("Số tiền thanh toán không đủ. Cần thanh toán kỳ hiện tại + %d kỳ quá hạn: %s VND",
                        overdueCount, totalDue)
                : String.format("Số tiền thanh toán không đủ. Cần thanh toán kỳ hiện tại: %s VND", totalDue);

            throw new IllegalArgumentException(message);
        }

        // 6. Trừ tiền từ checking account
        checkingAccount.setBalance(checkingAccount.getBalance().subtract(request.getPaymentAmount()));
        checkingAccountRepository.save(checkingAccount);

        // 7. Cập nhật trạng thái các kỳ đã thanh toán
        BigDecimal remainingPayment = request.getPaymentAmount();
        MortgagePaymentSchedule firstPaidPeriod = null;

        for (MortgagePaymentSchedule schedule : payableSchedules) {
            BigDecimal amountDue = schedule.getTotalAmount().subtract(schedule.getPaidAmount());

            if (remainingPayment.compareTo(amountDue) >= 0) {
                schedule.setPaidAmount(schedule.getTotalAmount());
                schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.PAID);
                schedule.setPaidDate(LocalDate.now());
                schedule.setIsOverdue(false);
                schedule.setIsCurrentPeriod(false);
                remainingPayment = remainingPayment.subtract(amountDue);

                if (firstPaidPeriod == null) {
                    firstPaidPeriod = schedule;
                }
            } else {
                schedule.setPaidAmount(schedule.getPaidAmount().add(remainingPayment));
                schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.PARTIAL);
                remainingPayment = BigDecimal.ZERO;
            }

            paymentScheduleRepository.save(schedule);

            if (remainingPayment.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        // 8. Kiểm tra xem đã thanh toán hết chưa
        List<MortgagePaymentSchedule> remainingSchedules = paymentScheduleRepository.findUnpaidSchedules(mortgageAccount);
        if (remainingSchedules.isEmpty()) {
            mortgageAccount.setStatus(MortgageAccount.MortgageStatus.COMPLETED);
            mortgageAccountRepository.save(mortgageAccount);
        } else {
            // Cập nhật lại flags cho các kỳ còn lại
            updatePaymentScheduleFlags(mortgageAccount);
        }

        // 9. Tạo transaction
        String code = generateMortgageTransactionCode();
        String description = firstPaidPeriod != null
            ? String.format("Thanh toán kỳ %d vay thế chấp - %s", firstPaidPeriod.getPeriodNumber(), mortgageAccount.getAccount().getAccountNumber())
            : "Thanh toán vay thế chấp - " + mortgageAccount.getAccount().getAccountNumber();

        Transaction transaction = new Transaction();
        transaction.setCode(code);
        transaction.setSenderAccount(checkingAccount.getAccount());
        transaction.setReceiverAccount(mortgageAccount.getAccount());
        transaction.setAmount(request.getPaymentAmount());
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return convertToResponse(mortgageAccount);
    }
    // ham tao transaction code tra tien cho vay the chap
    private String generateMortgageTransactionCode() {
        return String.format("MOR-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    /**
     * Cập nhật lãi phạt cho các kỳ quá hạn
     */
    private void updatePenaltyForOverdueSchedules(List<MortgagePaymentSchedule> schedules, MortgageAccount mortgageAccount) {
        LocalDate today = LocalDate.now();
        BigDecimal annualRate = mortgageAccount.getInterestRate().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        BigDecimal penaltyRate = annualRate.multiply(PENALTY_RATE_MULTIPLIER);

        for (MortgagePaymentSchedule schedule : schedules) {
            if (today.isAfter(schedule.getDueDate())) {
                long overdueDays = ChronoUnit.DAYS.between(schedule.getDueDate(), today);
                schedule.setOverdueDays((int) overdueDays);
                schedule.setStatus(MortgagePaymentSchedule.PaymentStatus.OVERDUE);

                // Tính lãi phạt: penalty = dư nợ * lãi suất phạt * số ngày quá hạn / 365
                BigDecimal penalty = schedule.getRemainingBalance()
                        .multiply(penaltyRate)
                        .multiply(new BigDecimal(overdueDays))
                        .divide(DAYS_IN_YEAR, 2, RoundingMode.HALF_UP);

                schedule.setPenaltyAmount(penalty);

                // Cập nhật lại totalAmount = gốc + lãi + lãi phạt
                BigDecimal newTotalAmount = schedule.getPrincipalAmount()
                        .add(schedule.getInterestAmount())
                        .add(penalty);
                schedule.setTotalAmount(newTotalAmount);

                // Đánh dấu là kỳ quá hạn
                schedule.setIsOverdue(true);

                paymentScheduleRepository.save(schedule);
            }
        }
    }

    /**
     * Cập nhật flags (isCurrentPeriod, isOverdue) cho tất cả các kỳ thanh toán
     */
    private void updatePaymentScheduleFlags(MortgageAccount mortgageAccount) {
        LocalDate today = LocalDate.now();
        List<MortgagePaymentSchedule> allSchedules = paymentScheduleRepository
                .findByMortgageAccountOrderByPeriodNumberAsc(mortgageAccount);

        // Tìm kỳ hiện tại: kỳ đầu tiên chưa thanh toán VÀ chưa quá hạn
        MortgagePaymentSchedule currentPeriod = allSchedules.stream()
                .filter(s -> s.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID)
                .filter(s -> !s.getDueDate().isBefore(today))
                .findFirst()
                .orElse(null);

        // Cập nhật flags cho tất cả các kỳ
        for (MortgagePaymentSchedule schedule : allSchedules) {
            // Reset flags trước
            schedule.setIsCurrentPeriod(false);
            schedule.setIsOverdue(false);

            // Đánh dấu kỳ hiện tại
            if (currentPeriod != null && schedule.getScheduleId().equals(currentPeriod.getScheduleId())) {
                schedule.setIsCurrentPeriod(true);
            }

            // Đánh dấu kỳ quá hạn
            if (schedule.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID
                    && schedule.getDueDate().isBefore(today)) {
                schedule.setIsOverdue(true);
            }

            paymentScheduleRepository.save(schedule);
        }
    }

    /**
     * Lấy thông tin chi tiết tài khoản vay
     */
    @Transactional
    public MortgageAccountResponse getMortgageAccountDetails(Long mortgageId) {

        //check xem user hien tai co phai chu tai khoan vay ko
        MortgageAccount mortgageAccount = mortgageAccountRepository.findById(mortgageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản vay"));

        Account account = mortgageAccount.getAccount();
        verifyAccountOwnership(account);

        // Cập nhật lãi phạt và flags cho các kỳ quá hạn trước khi trả về response
        if (mortgageAccount.getStatus() == MortgageAccount.MortgageStatus.ACTIVE) {
            List<MortgagePaymentSchedule> unpaidSchedules = paymentScheduleRepository
                    .findUnpaidSchedules(mortgageAccount);

            if (!unpaidSchedules.isEmpty()) {
                updatePenaltyForOverdueSchedules(unpaidSchedules, mortgageAccount);
            }

            // Cập nhật flags isCurrentPeriod và isOverdue vào DB
            updatePaymentScheduleFlags(mortgageAccount);
        }

        return convertToResponse(mortgageAccount);
    }

    /**
     * Lấy danh sách tài khoản vay theo user
     */
    public List<MortgageAccountResponse> getMortgageAccountsByUserId(Long userId) {
        List<MortgageAccount> accounts = mortgageAccountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tài khoản vay theo trạng thái
     */
    public List<MortgageAccountResponse> getMortgageAccountsByStatus(MortgageAccount.MortgageStatus status) {
        List<MortgageAccount> accounts = mortgageAccountRepository.findByStatus(status);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tài khoản vay theo trạng thái và số điện thoại (tìm kiếm) - Chỉ nhân viên
     */

    public List<MortgageAccountResponse> getMortgageAccountsByStatusAndPhone(
            MortgageAccount.MortgageStatus status,
            String phoneNumber) {
        List<MortgageAccount> accounts = mortgageAccountRepository
                .findByStatusAndPhoneNumber(status, phoneNumber);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity sang response
     */
    private MortgageAccountResponse convertToResponse(MortgageAccount mortgageAccount) {
        MortgageAccountResponse response = new MortgageAccountResponse();
        response.setMortgageId(mortgageAccount.getMortgageId());
        response.setAccountNumber(mortgageAccount.getAccount().getAccountNumber());
        response.setCustomerName(mortgageAccount.getAccount().getUser().getFullName());
        response.setCustomerPhone(mortgageAccount.getAccount().getUser().getPhone());
        response.setPrincipalAmount(mortgageAccount.getPrincipalAmount());
        response.setInterestRate(mortgageAccount.getInterestRate());
        response.setTermMonths(mortgageAccount.getTermMonths());
        response.setStartDate(mortgageAccount.getStartDate());
        response.setStatus(mortgageAccount.getStatus());
        response.setCollateralType(mortgageAccount.getCollateralType());
        response.setCollateralDescription(mortgageAccount.getCollateralDescription());
        response.setCccdFrontUrl(mortgageAccount.getCccdFrontUrl());
        response.setCccdBackUrl(mortgageAccount.getCccdBackUrl());
        response.setCollateralDocumentUrls(mortgageAccount.getCollateralDocumentUrls());
        response.setPaymentFrequency(mortgageAccount.getPaymentFrequency());
        response.setRejectionReason(mortgageAccount.getRejectionReason());
        response.setCreatedDate(mortgageAccount.getCreatedDate());
        response.setApprovalDate(mortgageAccount.getApprovalDate());

        // Lấy payment schedules
        List<MortgagePaymentSchedule> schedules = paymentScheduleRepository.findByMortgageAccountOrderByPeriodNumberAsc(mortgageAccount);

        // Tìm kỳ hiện tại: kỳ đầu tiên chưa thanh toán VÀ chưa quá hạn
        LocalDate today = LocalDate.now();
        MortgagePaymentSchedule currentPeriod = schedules.stream()
                .filter(s -> s.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID)
                .filter(s -> !s.getDueDate().isBefore(today)) // Bỏ qua các kỳ quá hạn
                .findFirst()
                .orElse(null);

        // Convert schedules sang response và đánh dấu
        List<PaymentScheduleResponse> scheduleResponses = schedules.stream()
                .map(schedule -> {
                    PaymentScheduleResponse scheduleResp = convertToScheduleResponse(schedule);

                    // Đánh dấu kỳ hiện tại (kỳ chưa trả đầu tiên KHÔNG quá hạn)
                    if (currentPeriod != null && schedule.getScheduleId().equals(currentPeriod.getScheduleId())) {
                        scheduleResp.setCurrentPeriod(true);
                    } else {
                        scheduleResp.setCurrentPeriod(false);
                    }

                    // Đánh dấu kỳ quá hạn (chưa trả và đã qua ngày đáo hạn)
                    if (schedule.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID
                        && schedule.getDueDate().isBefore(today)) {
                        scheduleResp.setOverdue(true);
                    } else {
                        scheduleResp.setOverdue(false);
                    }

                    return scheduleResp;
                })
                .collect(Collectors.toList());
        response.setPaymentSchedules(scheduleResponses);

        // Tính dư nợ còn lại ĐÚNG:
        // Dư nợ còn lại = remainingBalance của kỳ gần nhất chưa trả + gốc của kỳ đó
        // Phương án này đúng khi không có kỳ quá hạn
        BigDecimal remainingBalance = schedules.stream()
                .filter(s -> s.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID)
                .min((s1, s2) -> s1.getPeriodNumber().compareTo(s2.getPeriodNumber())) // Lấy kỳ đầu tiên chưa trả
                .map(schedule -> schedule.getRemainingBalance().add(schedule.getPrincipalAmount())) // remainingBalance + principalAmount
                .orElse(BigDecimal.ZERO); // Nếu đã trả hết thì = 0

        response.setRemainingBalance(remainingBalance);

        // Tính tổng tiền tất toán sớm:
        // earlySettlementAmount = tổng tiền (gốc + lãi + lãi phạt nếu có) của tất cả các kỳ chưa thanh toán
        BigDecimal earlySettlementAmount = schedules.stream()
                .filter(s -> s.getStatus() != MortgagePaymentSchedule.PaymentStatus.PAID)
                .map(s -> s.getPrincipalAmount()
                        .add(s.getInterestAmount())
                        .add(s.getPenaltyAmount() != null ? s.getPenaltyAmount() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setEarlySettlementAmount(earlySettlementAmount);

        return response;
    }

    /**
     * Convert payment schedule entity sang response
     */
    private PaymentScheduleResponse convertToScheduleResponse(MortgagePaymentSchedule schedule) {
        PaymentScheduleResponse response = new PaymentScheduleResponse();
        response.setScheduleId(schedule.getScheduleId());
        response.setPeriodNumber(schedule.getPeriodNumber());
        response.setDueDate(schedule.getDueDate());
        response.setPrincipalAmount(schedule.getPrincipalAmount());
        response.setInterestAmount(schedule.getInterestAmount());
        response.setTotalAmount(schedule.getTotalAmount());
        response.setPenaltyAmount(schedule.getPenaltyAmount());
        response.setRemainingBalance(schedule.getRemainingBalance());
        response.setStatus(schedule.getStatus());
        response.setPaidDate(schedule.getPaidDate());
        response.setPaidAmount(schedule.getPaidAmount());
        response.setOverdueDays(schedule.getOverdueDays());
        return response;
    }

    /**
     * Generate mortgage account number
     */
    private String generateMortgageAccountNumber() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "MTG" + timestamp + randomPart;
    }

    // Helper methods
    private void verifyAccountOwnership(Account account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long currentUserId = userDetails.getUserId();
            String currentUserRole = userDetails.getRole().name();

            // OFFICER không cần kiểm tra ownership
            if (!"officer".equalsIgnoreCase(currentUserRole)) {
                Long accountOwnerId = account.getUser().getUserId();
                if (!currentUserId.equals(accountOwnerId)) {
                    throw new AccessDeniedException("Bạn không có quyền truy cập tài khoản này");
                }
            }
        }
    }
}
