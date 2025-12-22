package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.*;
import org.example.storyreading.ibanking.entity.CollateralType;
import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.MortgageInterestRate;
import org.example.storyreading.ibanking.repository.MortgageInterestRateRepository;
import org.example.storyreading.ibanking.service.MortgageAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mortgage")
public class MortgageAccountController {

    @Autowired
    private MortgageAccountService mortgageAccountService;

    @Autowired
    private MortgageInterestRateRepository mortgageInterestRateRepository;

    /**
     * Tạo tài khoản vay thế chấp mới - Chỉ nhân viên
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<MortgageAccountResponse> createMortgageAccount(
            @Valid @RequestPart("request") CreateMortgageAccountRequest request,
            @RequestPart(value = "cccdFront", required = false) MultipartFile cccdFront,
            @RequestPart(value = "cccdBack", required = false) MultipartFile cccdBack,
            @RequestPart(value = "collateralDocuments", required = false) List<MultipartFile> collateralDocuments) {
        try {
            MortgageAccountResponse response = mortgageAccountService.createMortgageAccount(
                    request, cccdFront, cccdBack, collateralDocuments);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo tài khoản vay: " + e.getMessage(), e);
        }
    }

    /**
     * Phê duyệt tài khoản vay - Chỉ nhân viên
     */
    @PostMapping("/approve")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<MortgageAccountResponse> approveMortgage(
            @Valid @RequestBody ApproveMortgageRequest request) {
        MortgageAccountResponse response = mortgageAccountService.approveMortgage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Từ chối tài khoản vay - Chỉ nhân viên
     */
    @PostMapping("/reject")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<MortgageAccountResponse> rejectMortgage(
            @Valid @RequestBody RejectMortgageRequest request) {
        MortgageAccountResponse response = mortgageAccountService.rejectMortgage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Thanh toán khoản vay - TẤT TOÁN (thanh toán tất cả các kỳ chưa thanh toán)
     */
    @PostMapping("/payment")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'BANKING_OFFICER')")
    public ResponseEntity<MortgageAccountResponse> makePayment(
            @Valid @RequestBody MortgagePaymentRequest request) {
        MortgageAccountResponse response = mortgageAccountService.makePayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Thanh toán kỳ hiện tại - CHỈ thanh toán kỳ đã đến hạn và quá hạn
     */
    @PostMapping("/payment/current")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER')")
    public ResponseEntity<MortgageAccountResponse> makeCurrentPayment(
            @Valid @RequestBody MortgagePaymentRequest request) {
        MortgageAccountResponse response = mortgageAccountService.makeCurrentPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy thông tin chi tiết tài khoản vay
     */
    @GetMapping("/{mortgageId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER')")
    public ResponseEntity<MortgageAccountResponse> getMortgageDetails(
            @PathVariable Long mortgageId) {
        MortgageAccountResponse response = mortgageAccountService.getMortgageAccountDetails(mortgageId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách tài khoản vay theo user - Khách hàng xem của mình
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER')")
    public ResponseEntity<List<MortgageAccountResponse>> getMortgagesByUserId(
            @PathVariable Long userId) {
        List<MortgageAccountResponse> responses = mortgageAccountService.getMortgageAccountsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Lấy danh sách tài khoản vay theo trạng thái - Chỉ nhân viên
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<List<MortgageAccountResponse>> getMortgagesByStatus(
            @PathVariable MortgageAccount.MortgageStatus status) {
        List<MortgageAccountResponse> responses = mortgageAccountService.getMortgageAccountsByStatus(status);
        return ResponseEntity.ok(responses);
    }



    /**
     * Lấy danh sách tài khoản vay theo trạng thái và số điện thoại (tìm kiếm) - user
     */

    @GetMapping("/status/{status}/search")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER')")
    public ResponseEntity<List<MortgageAccountResponse>> searchMortgagesByStatusAndPhone(
            @PathVariable MortgageAccount.MortgageStatus status,
            @RequestParam("phone") String phoneNumber) {
        List<MortgageAccountResponse> responses = mortgageAccountService
                .getMortgageAccountsByStatusAndPhone(status, phoneNumber);
        return ResponseEntity.ok(responses);
    }

    /**
     * Lấy danh sách tất cả tài khoản vay chờ thẩm định - Nhân viên
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<List<MortgageAccountResponse>> getPendingMortgages() {
        List<MortgageAccountResponse> responses = mortgageAccountService
                .getMortgageAccountsByStatus(MortgageAccount.MortgageStatus.PENDING_APPRAISAL);
        return ResponseEntity.ok(responses);
    }

    /**
     * Lấy danh sách tất cả loại tài sản thế chấp (CollateralType)
     */
    @GetMapping("/collateral-types")
    public ResponseEntity<Map<String, Object>> getAllCollateralTypes() {
        List<Map<String, String>> collateralTypes = Arrays.stream(CollateralType.values())
                .map(type -> {
                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put("value", type.name());
                    typeMap.put("displayName", type.getDisplayName());
                    return typeMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", collateralTypes);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách tất cả lãi suất vay theo kỳ hạn (MortgageInterestRate)
     */
    @GetMapping("/interest-rates")
    public ResponseEntity<Map<String, Object>> getAllInterestRates() {
        List<MortgageInterestRate> interestRates = mortgageInterestRateRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", interestRates);
        return ResponseEntity.ok(response);
    }

}
