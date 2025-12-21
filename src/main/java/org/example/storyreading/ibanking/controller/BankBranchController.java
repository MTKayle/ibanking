package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.BankBranchResponse;
import org.example.storyreading.ibanking.dto.FindNearestBranchRequest;
import org.example.storyreading.ibanking.entity.BankBranch;
import org.example.storyreading.ibanking.service.BankBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bank-branches")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BankBranchController {

    @Autowired
    private BankBranchService bankBranchService;

    /**
     * Tìm chi nhánh gần nhất dựa trên tọa độ
     * POST /api/bank-branches/nearest
     */
    @PostMapping("/nearest")
    public ResponseEntity<?> findNearestBranches(@Valid @RequestBody FindNearestBranchRequest request) {
        try {
            List<BankBranchResponse> branches = bankBranchService.findNearestBranches(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getLimit()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tìm chi nhánh gần nhất thành công");
            response.put("data", branches);
            response.put("total", branches.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tìm chi nhánh: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy tất cả chi nhánh
     * GET /api/bank-branches
     */
    @GetMapping
    public ResponseEntity<?> getAllBranches() {
        try {
            List<BankBranch> branches = bankBranchService.getAllBranches();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách chi nhánh thành công");
            response.put("data", branches);
            response.put("total", branches.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách chi nhánh: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy chi nhánh theo ID
     * GET /api/bank-branches/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBranchById(@PathVariable("id") Long branchId) {
        try {
            BankBranch branch = bankBranchService.getBranchById(branchId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin chi nhánh thành công");
            response.put("data", branch);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thông tin chi nhánh: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

