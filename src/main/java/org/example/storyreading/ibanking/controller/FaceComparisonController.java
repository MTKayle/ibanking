package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.FaceComparisonResponse;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.example.storyreading.ibanking.service.FaceComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/face")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FaceComparisonController {

    @Autowired
    private FaceComparisonService faceComparisonService;

    /**
     * API so sánh ảnh khuôn mặt với ảnh đã lưu của user
     * FE gửi ảnh lên, BE sẽ lấy ảnh đó so sánh với photoURL trong database
     *
     * @param faceImage Ảnh khuôn mặt cần so sánh
     * @return Response chứa kết quả so sánh (matched/not matched)
     */
    @PostMapping("/compare")
    public ResponseEntity<?> compareFace(@RequestPart("faceImage") MultipartFile faceImage) {
        try {
            // Lấy thông tin user hiện tại từ JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long currentUserId = null;
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                currentUserId = userDetails.getUserId();
            }

            // Parse userId từ phone (giả sử phone được lưu trong principal)
            // Hoặc có thể lấy userId trực tiếp từ custom UserDetails


            // Gọi service để so sánh ảnh
            FaceComparisonResponse response = faceComparisonService.compareFaceWithUser(currentUserId, faceImage);

            if (response.isMatched()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(400).body(response);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi so sánh khuôn mặt: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * API so sánh ảnh khuôn mặt với user cụ thể (dành cho admin hoặc test)
     *
     * @param userId ID của user cần so sánh
     * @param faceImage Ảnh khuôn mặt cần so sánh
     * @return Response chứa kết quả so sánh
     */
    @PostMapping("/compare/{userId}")
    public ResponseEntity<?> compareFaceWithUserId(
            @PathVariable Long userId,
            @RequestPart("faceImage") MultipartFile faceImage) {
        try {
            // Gọi service để so sánh ảnh
            FaceComparisonResponse response = faceComparisonService.compareFaceWithUser(userId, faceImage);

            if (response.isMatched()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(400).body(response);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi so sánh khuôn mặt: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Helper method để lấy userId từ Authentication object
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Implement logic để lấy userId từ authentication
        // Có thể cần custom UserDetails để lưu userId
        // Tạm thời return null, cần implement dựa vào cấu trúc UserDetails hiện tại

        // Check if principal has userId property
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Need to cast to custom UserDetails that has userId
            // For now, we'll need to query by phone
            String phone = authentication.getName();
            // This is not optimal, but works for now
            // Better to store userId in JWT claims or custom UserDetails
            throw new UnsupportedOperationException(
                "Vui lòng sử dụng endpoint /api/face/compare/{userId} và truyền userId trực tiếp");
        }
        return null;
    }
}

