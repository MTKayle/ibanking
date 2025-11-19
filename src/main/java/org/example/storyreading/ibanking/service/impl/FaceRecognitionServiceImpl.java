package org.example.storyreading.ibanking.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.storyreading.ibanking.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    @Value("${faceplus.api.key}")
    private String apiKey;

    @Value("${faceplus.api.secret}")
    private String apiSecret;

    @Value("${faceplus.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public double compareFaces(MultipartFile image1, MultipartFile image2) throws Exception {
        // Tạo request body với multipart form data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);

        // Thêm file ảnh 1 (CCCD)
        body.add("image_file1", new ByteArrayResource(image1.getBytes()) {
            @Override
            public String getFilename() {
                return image1.getOriginalFilename();
            }
        });

        // Thêm file ảnh 2 (Selfie)
        body.add("image_file2", new ByteArrayResource(image2.getBytes()) {
            @Override
            public String getFilename() {
                return image2.getOriginalFilename();
            }
        });

        // Tạo headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gọi Face++ API
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Face++ API call failed: " + response.getStatusCode());
        }

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Kiểm tra lỗi từ Face++ API
        if (jsonNode.has("error_message")) {
            throw new RuntimeException("Face++ API error: " + jsonNode.get("error_message").asText());
        }

        // Lấy confidence score
        if (jsonNode.has("confidence")) {
            return jsonNode.get("confidence").asDouble();
        } else {
            throw new RuntimeException("Không tìm thấy confidence trong response từ Face++ API");
        }
    }
}
