package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.BankResponse;
import org.example.storyreading.ibanking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BankController {

    @Autowired
    private BankService bankService;

    /**
     * Lấy danh sách tất cả ngân hàng
     * GET /api/banks
     */
    @GetMapping
    public ResponseEntity<?> getAllBanks() {
        try {
            List<BankResponse> banks = bankService.getAllBanks();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách ngân hàng thành công");
            response.put("data", banks);
            response.put("total", banks.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy thông tin ngân hàng theo ID
     * GET /api/banks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBankById(@PathVariable("id") Long bankId) {
        try {
            BankResponse bank = bankService.getBankById(bankId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin ngân hàng thành công");
            response.put("data", bank);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy thông tin ngân hàng theo BIN
     * GET /api/banks/by-bin/{bin}
     */
    @GetMapping("/by-bin/{bin}")
    public ResponseEntity<?> getBankByBin(@PathVariable("bin") String bankBin) {
        try {
            BankResponse bank = bankService.getBankByBin(bankBin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin ngân hàng thành công");
            response.put("data", bank);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

