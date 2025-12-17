package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Test endpoint để generate token và trả về cả token và secret key
     * Dùng để test trên jwt.io
     */
    @GetMapping("/jwt")
    public ResponseEntity<Map<String, Object>> testJwt(@RequestParam(defaultValue = "0912345678") String phone) {
        // Generate token
        String token = jwtTokenProvider.generateTokenFromPhone(phone);

        // Validate token
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Extract phone from token
        String extractedPhone = jwtTokenProvider.getPhoneFromToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("secretKey", jwtSecret);
        response.put("algorithm", "HS256");
        response.put("isValid", isValid);
        response.put("phone", phone);
        response.put("extractedPhone", extractedPhone);
        response.put("instruction", "Copy token và secretKey, paste vào https://jwt.io với algorithm HS256");

        return ResponseEntity.ok(response);
    }

    /**
     * Validate token từ client
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isValid = jwtTokenProvider.validateToken(token);
            response.put("valid", isValid);

            if (isValid) {
                String phone = jwtTokenProvider.getPhoneFromToken(token);
                response.put("phone", phone);
            }
        } catch (Exception e) {
            response.put("valid", false);
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

