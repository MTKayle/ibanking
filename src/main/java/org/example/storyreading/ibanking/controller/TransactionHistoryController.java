package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.TransactionHistoryDTO;
import org.example.storyreading.ibanking.service.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    /**
     * API lấy tất cả giao dịch theo userId (dành cho Officer)
     * GET /api/transactions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<Map<String, Object>> getAllTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<TransactionHistoryDTO> transactions = transactionHistoryService.getAllTransactionsByUserId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử giao dịch thành công");
            response.put("data", transactions);
            response.put("total", transactions.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API lấy giao dịch thành công của user hiện tại
     * GET /api/transactions/my-transactions
     */
    @GetMapping("/my-transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMySuccessfulTransactions() {
        try {
            List<TransactionHistoryDTO> transactions = transactionHistoryService.getMySuccessfulTransactions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử giao dịch thành công");
            response.put("data", transactions);
            response.put("total", transactions.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

