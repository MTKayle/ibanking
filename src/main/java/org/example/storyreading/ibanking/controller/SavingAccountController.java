package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.*;
import org.example.storyreading.ibanking.entity.SavingTermConfig;
import org.example.storyreading.ibanking.service.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saving")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavingAccountController {

    @Autowired
    private SavingAccountService savingAccountService;

    /**
     * Tạo tài khoản tiết kiệm mới
     * - Trừ tiền từ tài khoản checking
     * - Tạo sổ tiết kiệm với kỳ hạn và lãi suất
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER')")
    public ResponseEntity<SavingAccountResponse> createSavingAccount(
            @Valid @RequestBody CreateSavingAccountRequest request) {
        SavingAccountResponse response = savingAccountService.createSavingAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách tài khoản tiết kiệm của user hiện tại
     */
    @GetMapping("/my-accounts")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER')")
    public ResponseEntity<List<SavingAccountResponse>> getMySavingAccounts() {
        List<SavingAccountResponse> accounts = savingAccountService.getMySavingAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Xem chi tiết sổ tiết kiệm theo số sổ
     * Bao gồm tính toán lãi ước tính khi đáo hạn
     */
    @GetMapping("/{savingBookNumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER')")
    public ResponseEntity<SavingAccountDetailResponse> getSavingAccountDetail(
            @PathVariable String savingBookNumber) {
        SavingAccountDetailResponse response = savingAccountService.getSavingAccountDetailWithEstimate(savingBookNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Preview tất toán sổ tiết kiệm
     * Hiển thị thông tin chi tiết về số tiền sẽ nhận được khi tất toán
     * Bao gồm cảnh báo nếu rút trước hạn
     */
    @GetMapping("/{savingBookNumber}/withdraw-preview")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER')")
    public ResponseEntity<WithdrawPreviewResponse> previewWithdraw(
            @PathVariable String savingBookNumber) {
        WithdrawPreviewResponse response = savingAccountService.previewWithdraw(savingBookNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Xác nhận tất toán sổ tiết kiệm
     * Thực hiện sau khi user đã xem preview và xác nhận
     */
    @PostMapping("/{savingBookNumber}/withdraw-confirm")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER')")
    public ResponseEntity<WithdrawSavingResponse> confirmWithdraw(
            @PathVariable String savingBookNumber) {
        WithdrawSavingResponse response = savingAccountService.confirmWithdraw(savingBookNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách tất cả kỳ hạn với lãi suất hiện tại (public API)
     */
    @GetMapping("/terms")
    public ResponseEntity<?> getAllSavingTerms() {
        try {
            List<SavingTermConfig> terms = savingAccountService.getAllSavingTerms();

            // Convert sang DTO response với thông tin đầy đủ hơn
            List<SavingTermResponse> responses = terms.stream()
                    .map(term -> {
                        SavingTermResponse response = new SavingTermResponse();
                        response.setTermId(term.getTermId());
                        response.setTermType(term.getTermType().name());
                        response.setMonths(term.getTermType().getMonths());
                        response.setDisplayName(term.getTermType().getDisplayName());
                        response.setInterestRate(term.getInterestRate());
                        response.setUpdatedBy(term.getUpdatedBy());
                        response.setUpdatedAt(term.getUpdatedAt() != null ? term.getUpdatedAt().toString() : null);
                        return response;
                    })
                    .collect(java.util.stream.Collectors.toList());

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy danh sách kỳ hạn thành công");
            result.put("data", responses);
            result.put("total", responses.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Cập nhật lãi suất cho một kỳ hạn (chỉ OFFICER)
     */
    @PutMapping("/terms/update-rate")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<SavingTermConfig> updateTermRate(
            @Valid @RequestBody UpdateSavingTermRateRequest request) {
        SavingTermConfig updated = savingAccountService.updateTermInterestRate(
                request.getTermType(),
                request.getInterestRate());
        return ResponseEntity.ok(updated);
    }
}
