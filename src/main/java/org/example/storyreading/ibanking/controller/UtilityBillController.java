package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.utility.UtilityBillPaymentRequestDTO;
import org.example.storyreading.ibanking.dto.utility.UtilityBillPaymentResponseDTO;
import org.example.storyreading.ibanking.dto.utility.UtilityBillResponseDTO;
import org.example.storyreading.ibanking.entity.UtilityBillType;
import org.example.storyreading.ibanking.service.UtilityBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utility-bills")
public class UtilityBillController {

    @Autowired
    private UtilityBillService utilityBillService;

    /**
     * API tìm kiếm hóa đơn theo mã hóa đơn
     * GET /api/utility-bills/search?billCode=EVN202412001
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> searchBillByCode(@RequestParam String billCode, @RequestParam String billType) {
        try {
            UtilityBillResponseDTO bill = utilityBillService.findByBillCodeAndBillType(billCode, billType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tìm thấy hóa đơn");
            response.put("data", bill);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API thanh toán hóa đơn
     * POST /api/utility-bills/pay
     */
    @PostMapping("/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> payBill(@Valid @RequestBody UtilityBillPaymentRequestDTO request) {
        try {
            UtilityBillPaymentResponseDTO payment = utilityBillService.payBill(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", payment.getMessage());
            response.put("data", payment);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi thanh toán hóa đơn: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API lấy danh sách hóa đơn đã thanh toán của user hiện tại
     * GET /api/utility-bills/my-paid-bills
     */
    @GetMapping("/my-paid-bills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyPaidBills() {
        try {
            List<UtilityBillResponseDTO> bills = utilityBillService.getMyPaidBills();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách hóa đơn đã thanh toán thành công");
            response.put("data", bills);
            response.put("total", bills.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API lấy tất cả hóa đơn chưa thanh toán (for testing/admin)
     * GET /api/utility-bills/unpaid
     */
    @GetMapping("/unpaid")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAllUnpaidBills() {
        try {
            List<UtilityBillResponseDTO> bills = utilityBillService.getAllUnpaidBills();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách hóa đơn chưa thanh toán thành công");
            response.put("data", bills);
            response.put("total", bills.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API lấy tất cả loại hóa đơn (BillType)
     * GET /api/utility-bills/bill-types
     */
    @GetMapping("/bill-types")
    public ResponseEntity<Map<String, Object>> getAllBillTypes() {
        List<Map<String, String>> billTypes = Arrays.stream(UtilityBillType.values())
                .map(type -> {
                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put("value", type.name());
                    typeMap.put("displayName", type.getDisplayName());
                    return typeMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lấy danh sách loại hóa đơn thành công");
        response.put("data", billTypes);

        return ResponseEntity.ok(response);
    }
}
