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

    /**
     * API lấy chi tiết giao dịch cùng ngân hàng (internal transaction) theo ID
     * GET /api/transactions/internal/{transactionId}
     */
    @GetMapping("/internal/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getInternalTransactionById(@PathVariable Long transactionId) {
        try {
            TransactionHistoryDTO transaction = transactionHistoryService.getInternalTransactionById(transactionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết giao dịch cùng ngân hàng thành công");
            response.put("data", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API lấy chi tiết giao dịch ngoài ngân hàng (external transaction) theo ID
     * GET /api/transactions/external/{externalTransferId}
     */
    @GetMapping("/external/{externalTransferId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getExternalTransactionById(@PathVariable Long externalTransferId) {
        try {
            TransactionHistoryDTO transaction = transactionHistoryService.getExternalTransactionById(externalTransferId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết giao dịch ngoài ngân hàng thành công");
            response.put("data", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API lấy lịch sử giao dịch tiền vào (DEPOSIT + nhận chuyển khoản)
     * GET /api/transactions/incoming
     */
    @GetMapping("/incoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyIncomingTransactions() {
        try {
            List<TransactionHistoryDTO> transactions = transactionHistoryService.getMyIncomingTransactions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử giao dịch tiền vào thành công");
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
     * API lấy lịch sử giao dịch tiền ra (WITHDRAW + chuyển khoản đi + chuyển ngoài ngân hàng)
     * GET /api/transactions/outgoing
     */
    @GetMapping("/outgoing")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyOutgoingTransactions() {
        try {
            List<TransactionHistoryDTO> transactions = transactionHistoryService.getMyOutgoingTransactions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử giao dịch tiền ra thành công");
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
     * API lấy chi tiết giao dịch theo mã code
     * GET /api/transactions/code/{code}
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getTransactionByCode(@PathVariable String code) {
        try {
            TransactionHistoryDTO transaction = transactionHistoryService.getTransactionByCode(code);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết giao dịch thành công");
            response.put("data", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
