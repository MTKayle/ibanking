package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.CreateSavingAccountRequest;
import org.example.storyreading.ibanking.dto.SavingAccountDetailResponse;
import org.example.storyreading.ibanking.dto.SavingAccountResponse;
import org.example.storyreading.ibanking.dto.WithdrawPreviewResponse;
import org.example.storyreading.ibanking.dto.WithdrawSavingResponse;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SavingAccountService {

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionCreationService transactionCreationService;

    @Autowired
    private TransactionFailureService transactionFailureService;

    @Autowired
    private SavingTermConfigRepository savingTermConfigRepository;

    /**
     * Lấy lãi suất từ database, fallback về enum nếu không tìm thấy
     */
    private BigDecimal getInterestRateForTerm(SavingTerm term) {
        return savingTermConfigRepository.findByTermType(term)
                .map(SavingTermConfig::getInterestRate)
                .orElse(BigDecimal.valueOf(term.getInterestRate()));
    }

    /**
     * Tạo tài khoản tiết kiệm mới
     * - Trừ tiền từ tài khoản checking
     * - Tạo tài khoản tiết kiệm với số tiền và kỳ hạn
     * - Ghi transaction
     */
    @Transactional
    public SavingAccountResponse createSavingAccount(CreateSavingAccountRequest request) {
        String transactionCode = generateSavingTransactionCode();

        try {
            // Bước 1: Kiểm tra quyền sở hữu tài khoản checking
            CheckingAccount checkingAccount = checkingAccountRepository
                    .findByAccountNumber(request.getSenderAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản checking: " + request.getSenderAccountNumber()));

            Account senderAccount = checkingAccount.getAccount();

            // Kiểm tra quyền sở hữu
            verifyAccountOwnership(senderAccount);

            // Bước 2: Kiểm tra trạng thái tài khoản
            if (senderAccount.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản không hoạt động");
            }

            // Bước 3: Kiểm tra số dư
            if (checkingAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new IllegalArgumentException("Số dư không đủ để gửi tiết kiệm");
            }

            // Bước 4: Trừ tiền từ checking account với lock
            CheckingAccount lockedChecking = checkingAccountRepository
                    .findByAccountNumberForUpdate(request.getSenderAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            BigDecimal newCheckingBalance = lockedChecking.getBalance().subtract(request.getAmount());
            lockedChecking.setBalance(newCheckingBalance);
            checkingAccountRepository.save(lockedChecking);

            // Bước 5: Tạo Account cho saving
            Account savingAccountEntity = new Account();
            savingAccountEntity.setUser(senderAccount.getUser());
            savingAccountEntity.setAccountType(Account.AccountType.saving);
            savingAccountEntity.setAccountNumber(generateSavingAccountNumber());
            savingAccountEntity.setStatus(Account.Status.active);
            Account savedAccount = accountRepository.save(savingAccountEntity);
            accountRepository.flush();

            // Bước 6: Tạo SavingAccount
            // Lấy lãi suất từ database trước
            BigDecimal interestRate = getInterestRateForTerm(request.getTerm());

            SavingAccount savingAccount = new SavingAccount();
            savingAccount.setAccount(savedAccount);
            savingAccount.setBalance(request.getAmount());
            savingAccount.setSavingBookNumber(generateSavingBookNumber());
            savingAccount.setOpenedDate(LocalDate.now());

            // Set term sau khi set các giá trị khác
            savingAccount.setTerm(request.getTerm());
            // Set interest rate AFTER setTerm to ensure it's not overwritten
            savingAccount.setInterestRate(interestRate);


            // Tính ngày đáo hạn
            if (request.getTerm().getMonths() > 0) {
                savingAccount.setMaturityDate(LocalDate.now().plusMonths(request.getTerm().getMonths()));
            }

            savingAccount.setStatus(SavingAccount.Status.ACTIVE);
            SavingAccount savedSaving = savingAccountRepository.save(savingAccount);
            savingAccountRepository.flush();




            // Bước 7: Tạo transaction DEPOSIT cho saving account
            Transaction newTransaction = new Transaction();
            newTransaction.setSenderAccount(senderAccount);
            newTransaction.setReceiverAccount(savedAccount);
            newTransaction.setAmount(request.getAmount());
            newTransaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
            newTransaction.setDescription(String.format("Gửi tiết kiệm %s - Kỳ hạn %s",
                    savedSaving.getSavingBookNumber(),
                    request.getTerm().getDisplayName()));
            newTransaction.setCode(transactionCode);
            newTransaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            transactionRepository.save(newTransaction);

            // Bước 9: Tạo response
            return mapToResponse(savedSaving);

        } catch (Exception e) {
            throw new RuntimeException("Tạo tài khoản tiết kiệm thất bại: " + e.getMessage(), e);
        }
    }

    /**
     * Tất toán tiết kiệm
     * - Tính lãi theo kỳ hạn hoặc không kỳ hạn (nếu rút trước hạn)
     * - Chuyển tiền gốc + lãi về tài khoản checking
     * - Đánh dấu tài khoản tiết kiệm là CLOSED
     */
    @Transactional
    public WithdrawSavingResponse withdrawSaving(String savingBookNumber) {
        String transactionCode = generateWithdrawTransactionCode();

        try {
            // Bước 1: Tìm tài khoản tiết kiệm
            SavingAccount savingAccount = savingAccountRepository.findBySavingBookNumber(savingBookNumber)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sổ tiết kiệm: " + savingBookNumber));

            // Kiểm tra quyền sở hữu
            verifyAccountOwnership(savingAccount.getAccount());

            // Bước 2: Kiểm tra trạng thái
            if (savingAccount.getStatus() == SavingAccount.Status.CLOSED) {
                throw new IllegalStateException("Sổ tiết kiệm đã được tất toán");
            }

            // Bước 3: Tìm tài khoản checking của user
            List<CheckingAccount> checkingAccounts = checkingAccountRepository
                    .findByUser(savingAccount.getAccount().getUser());

            if (checkingAccounts.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy tài khoản checking để nhận tiền");
            }

            CheckingAccount checkingAccount = checkingAccounts.get(0);

            // Bước 4: Tính lãi
            LocalDate openedDate = savingAccount.getOpenedDate();
            LocalDate closedDate = LocalDate.now();
            long daysHeld = ChronoUnit.DAYS.between(openedDate, closedDate);

            BigDecimal principalAmount = savingAccount.getBalance();
            BigDecimal appliedInterestRate;
            BigDecimal interestEarned;

            // Kiểm tra rút trước hạn hay đúng hạn
            boolean isEarlyWithdrawal = savingAccount.getMaturityDate() != null
                    && closedDate.isBefore(savingAccount.getMaturityDate());

            if (isEarlyWithdrawal) {
                // Rút trước hạn: áp dụng lãi suất không kỳ hạn
                appliedInterestRate = BigDecimal.valueOf(SavingTerm.NON_TERM.getInterestRate());
            } else {
                // Rút đúng hạn hoặc sau đáo hạn: áp dụng lãi suất theo kỳ hạn
                appliedInterestRate = savingAccount.getInterestRate();
            }

            // Tính lãi: Principal * Rate% * Days / 365
            interestEarned = principalAmount
                    .multiply(appliedInterestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(daysHeld))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

            BigDecimal totalAmount = principalAmount.add(interestEarned);

            // Bước 5: Lock checking account và cộng tiền
            CheckingAccount lockedChecking = checkingAccountRepository
                    .findByAccountNumberForUpdate(checkingAccount.getAccount().getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            BigDecimal newCheckingBalance = lockedChecking.getBalance().add(totalAmount);
            lockedChecking.setBalance(newCheckingBalance);
            checkingAccountRepository.save(lockedChecking);

            // Bước 6: Cập nhật trạng thái saving account
            savingAccount.setStatus(SavingAccount.Status.CLOSED);
            savingAccount.setBalance(BigDecimal.ZERO);
            savingAccountRepository.save(savingAccount);

            // Bước 7: Tạo transaction WITHDRAW
//            Transaction transaction = transactionCreationService.createPendingTransaction(
//                    savingAccount.getAccount(),
//                    checkingAccount.getAccount(),
//                    totalAmount,
//                    Transaction.TransactionType.WITHDRAW,
//                    String.format("Tất toán sổ tiết kiệm %s - Lãi: %.2f%%",
//                            savingBookNumber,
//                            appliedInterestRate),
//                    transactionCode
//            );

//            // Bước 8: Đánh dấu transaction thành công
//            transactionFailureService.markSuccess(transactionCode);

            Transaction newTransaction = new Transaction();
            newTransaction.setSenderAccount(savingAccount.getAccount());
            newTransaction.setReceiverAccount(checkingAccount.getAccount());
            newTransaction.setAmount(totalAmount);
            newTransaction.setTransactionType(Transaction.TransactionType.WITHDRAW);
            newTransaction.setDescription(isEarlyWithdrawal
                    ? "Tất toán trước hạn thành công cho sổ tiết kiệm" + savingBookNumber + ". Lãi suất áp dụng: " + appliedInterestRate + "% (không kỳ hạn)"
                    : "Tất toán thành công cho sổ tiết kiệm" + savingBookNumber + ". Lãi suất áp dụng: " + appliedInterestRate + "%");
            newTransaction.setCode(transactionCode);
            newTransaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            transactionRepository.save(newTransaction);

            // Bước 9: Tạo response
            WithdrawSavingResponse response = new WithdrawSavingResponse();
            response.setSavingBookNumber(savingBookNumber);
            response.setPrincipalAmount(principalAmount);
            response.setAppliedInterestRate(appliedInterestRate);
            response.setInterestEarned(interestEarned);
            response.setTotalAmount(totalAmount);
            response.setCheckingAccountNumber(checkingAccount.getAccount().getAccountNumber());
            response.setNewCheckingBalance(newCheckingBalance);
            response.setOpenedDate(openedDate);
            response.setClosedDate(closedDate);
            response.setDaysHeld((int) daysHeld);
            response.setTransactionCode(transactionCode);
            response.setMessage(isEarlyWithdrawal
                    ? "Tất toán trước hạn thành công. Lãi suất áp dụng: " + appliedInterestRate + "% (không kỳ hạn)"
                    : "Tất toán thành công. Lãi suất áp dụng: " + appliedInterestRate + "%");

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Tất toán sổ tiết kiệm thất bại: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách tài khoản tiết kiệm của user
     */
    @Transactional(readOnly = true)
    public List<SavingAccountResponse> getMySavingAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new AccessDeniedException("Unauthorized");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        List<SavingAccount> savingAccounts = savingAccountRepository.findByUserId(userId);
        return savingAccounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết tài khoản tiết kiệm
     */
    @Transactional(readOnly = true)
    public SavingAccountResponse getSavingAccountDetail(String savingBookNumber) {
        SavingAccount savingAccount = savingAccountRepository.findBySavingBookNumber(savingBookNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sổ tiết kiệm: " + savingBookNumber));

        // Kiểm tra quyền sở hữu
        verifyAccountOwnership(savingAccount.getAccount());

        return mapToResponse(savingAccount);
    }

    /**
     * Lấy chi tiết tài khoản tiết kiệm với tính toán lãi ước tính khi đáo hạn
     */
    @Transactional(readOnly = true)
    public SavingAccountDetailResponse getSavingAccountDetailWithEstimate(String savingBookNumber) {
        SavingAccount savingAccount = savingAccountRepository.findBySavingBookNumber(savingBookNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sổ tiết kiệm: " + savingBookNumber));

        // Kiểm tra quyền sở hữu
        verifyAccountOwnership(savingAccount.getAccount());

        SavingAccountDetailResponse response = new SavingAccountDetailResponse();
        response.setSavingId(savingAccount.getSavingId());
        response.setSavingBookNumber(savingAccount.getSavingBookNumber());
        response.setAccountNumber(savingAccount.getAccount().getAccountNumber());
        response.setBalance(savingAccount.getBalance());
        response.setTerm(savingAccount.getTerm().getDisplayName());
        response.setTermMonths(savingAccount.getTerm().getMonths());
        response.setInterestRate(savingAccount.getInterestRate());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        response.setOpenedDate(savingAccount.getOpenedDate().format(formatter));
        response.setMaturityDate(savingAccount.getMaturityDate() != null
                ? savingAccount.getMaturityDate().format(formatter)
                : null);
        response.setStatus(savingAccount.getStatus().name());
        response.setUserId(savingAccount.getAccount().getUser().getUserId());
        response.setUserFullName(savingAccount.getAccount().getUser().getFullName());

        // Tính lãi ước tính khi đáo hạn (nếu chưa tất toán)
        if (savingAccount.getStatus() == SavingAccount.Status.ACTIVE && savingAccount.getMaturityDate() != null) {
            LocalDate maturityDate = savingAccount.getMaturityDate();
            LocalDate openedDate = savingAccount.getOpenedDate();
            long totalDays = ChronoUnit.DAYS.between(openedDate, maturityDate);
            long daysUntilMaturity = ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);

            // Tính lãi ước tính khi đáo hạn
            BigDecimal estimatedInterest = savingAccount.getBalance()
                    .multiply(savingAccount.getInterestRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(totalDays))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

            BigDecimal estimatedTotal = savingAccount.getBalance().add(estimatedInterest);

            response.setEstimatedInterestAtMaturity(estimatedInterest);
            response.setEstimatedTotalAtMaturity(estimatedTotal);
            response.setDaysUntilMaturity((int) daysUntilMaturity);
            response.setTotalDaysOfTerm((int) totalDays);
        }

        return response;
    }

    /**
     * Preview tất toán - Xem trước thông tin tất toán mà chưa thực hiện
     */
    @Transactional(readOnly = true)
    public WithdrawPreviewResponse previewWithdraw(String savingBookNumber) {
        SavingAccount savingAccount = savingAccountRepository.findBySavingBookNumber(savingBookNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sổ tiết kiệm: " + savingBookNumber));

        // Kiểm tra quyền sở hữu
        verifyAccountOwnership(savingAccount.getAccount());

        // Kiểm tra trạng thái
        if (savingAccount.getStatus() == SavingAccount.Status.CLOSED) {
            throw new IllegalStateException("Sổ tiết kiệm đã được tất toán");
        }

        LocalDate openedDate = savingAccount.getOpenedDate();
        LocalDate withdrawDate = LocalDate.now();
        long daysHeld = ChronoUnit.DAYS.between(openedDate, withdrawDate);

        BigDecimal principalAmount = savingAccount.getBalance();
        BigDecimal appliedInterestRate;

        // Kiểm tra rút trước hạn hay đúng hạn
        boolean isEarlyWithdrawal = savingAccount.getMaturityDate() != null
                && withdrawDate.isBefore(savingAccount.getMaturityDate());

        if (isEarlyWithdrawal) {
            // Rút trước hạn: áp dụng lãi suất không kỳ hạn từ DB
            appliedInterestRate = getInterestRateForTerm(SavingTerm.NON_TERM);
        } else {
            // Rút đúng hạn: áp dụng lãi suất theo kỳ hạn
            appliedInterestRate = savingAccount.getInterestRate();
        }

        // Tính lãi
        BigDecimal interestEarned = principalAmount
                .multiply(appliedInterestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(daysHeld))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = principalAmount.add(interestEarned);

        WithdrawPreviewResponse response = new WithdrawPreviewResponse();
        response.setSavingBookNumber(savingBookNumber);
        response.setPrincipalAmount(principalAmount);
        response.setAppliedInterestRate(appliedInterestRate);
        response.setInterestEarned(interestEarned);
        response.setTotalAmount(totalAmount);
        response.setOpenedDate(openedDate);
        response.setWithdrawDate(withdrawDate);
        response.setDaysHeld((int) daysHeld);
        response.setEarlyWithdrawal(isEarlyWithdrawal);

        if (isEarlyWithdrawal) {
            response.setMessage(String.format(
                    "Bạn đang tất toán trước hạn nên sẽ áp dụng lãi suất không kỳ hạn là %.2f%%/năm",
                    appliedInterestRate));
        } else {
            response.setMessage("Tất toán đúng hạn/sau đáo hạn. Lãi suất áp dụng: " + appliedInterestRate + "%/năm");
        }

        return response;
    }

    /**
     * Confirm withdraw - Thực hiện tất toán sau khi user đã xem preview và xác nhận
     */
    @Transactional
    public WithdrawSavingResponse confirmWithdraw(String savingBookNumber) {
        return withdrawSaving(savingBookNumber);
    }

    /**
     * Lấy danh sách tất cả các kỳ hạn với lãi suất hiện tại
     */
    @Transactional(readOnly = true)
    public List<SavingTermConfig> getAllSavingTerms() {
        return savingTermConfigRepository.findAll();
    }

    /**
     * Cập nhật lãi suất cho một kỳ hạn (chỉ OFFICER)
     */
    @Transactional
    public SavingTermConfig updateTermInterestRate(SavingTerm termType, BigDecimal newRate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = "system";

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            updatedBy = userDetails.getUsername();
        }

        SavingTermConfig config = savingTermConfigRepository.findByTermType(termType)
                .orElseGet(() -> {
                    SavingTermConfig newConfig = new SavingTermConfig();
                    newConfig.setTermType(termType);
                    return newConfig;
                });

        config.setInterestRate(newRate);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(Instant.now());

        return savingTermConfigRepository.save(config);
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

    private String generateSavingAccountNumber() {
        return String.format("SAV%010d", Math.abs(UUID.randomUUID().hashCode()) % 10000000000L);
    }

    private String generateSavingBookNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = LocalDate.now().format(formatter);
        int randomNum = (int) (Math.random() * 999) + 1;
        return String.format("STK-%s%03d", dateStr, randomNum);
    }

    private String generateSavingTransactionCode() {
        return String.format("SAV-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    private String generateWithdrawTransactionCode() {
        return String.format("WDR-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    private SavingAccountResponse mapToResponse(SavingAccount savingAccount) {
        SavingAccountResponse response = new SavingAccountResponse();
        response.setSavingId(savingAccount.getSavingId());
        response.setSavingBookNumber(savingAccount.getSavingBookNumber());
        response.setAccountNumber(savingAccount.getAccount().getAccountNumber());
        response.setBalance(savingAccount.getBalance());
        response.setTerm(savingAccount.getTerm().getDisplayName());
        response.setTermMonths(savingAccount.getTerm().getMonths());
        response.setInterestRate(savingAccount.getInterestRate());
        response.setOpenedDate(savingAccount.getOpenedDate());
        response.setMaturityDate(savingAccount.getMaturityDate());
        response.setStatus(savingAccount.getStatus().name());
        response.setUserId(savingAccount.getAccount().getUser().getUserId());
        response.setUserFullName(savingAccount.getAccount().getUser().getFullName());
        return response;
    }
}
