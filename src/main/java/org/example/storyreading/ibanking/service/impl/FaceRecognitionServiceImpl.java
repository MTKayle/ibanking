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

    @Value("${faceplus.api.detect.url:https://api-us.faceplusplus.com/facepp/v3/detect}")
    private String detectApiUrl;

    @Value("${faceplus.api.search.url:https://api-us.faceplusplus.com/facepp/v3/search}")
    private String searchApiUrl;

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

    @Override
    public double compareFaceWithUrl(MultipartFile uploadedFace, String storedPhotoUrl) throws Exception {
        // Tạo request body với multipart form data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);

        // Thêm ảnh upload từ user (dạng file)
        body.add("image_file1", new ByteArrayResource(uploadedFace.getBytes()) {
            @Override
            public String getFilename() {
                return uploadedFace.getOriginalFilename();
            }
        });

        // Thêm ảnh đã lưu (dạng URL từ Cloudinary)
        body.add("image_url2", storedPhotoUrl);

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

    @Override
    public String detectAndEncodeFace(MultipartFile faceImage) throws Exception {
        // Tạo request body với multipart form data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);
        body.add("return_landmark", "0");
        body.add("return_attributes", "none");

        // Thêm file ảnh
        body.add("image_file", new ByteArrayResource(faceImage.getBytes()) {
            @Override
            public String getFilename() {
                return faceImage.getOriginalFilename();
            }
        });

        // Tạo headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gọi Face++ Detect API
        ResponseEntity<String> response = restTemplate.postForEntity(detectApiUrl, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Face++ Detect API call failed: " + response.getStatusCode());
        }

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Kiểm tra lỗi từ Face++ API
        if (jsonNode.has("error_message")) {
            throw new RuntimeException("Face++ API error: " + jsonNode.get("error_message").asText());
        }

        // Kiểm tra có phát hiện khuôn mặt không
        if (!jsonNode.has("faces") || jsonNode.get("faces").size() == 0) {
            throw new RuntimeException("Không phát hiện được khuôn mặt trong ảnh");
        }

        // Lấy face_token từ khuôn mặt đầu tiên
        String faceToken = jsonNode.get("faces").get(0).get("face_token").asText();

        // Trả về face_token (đây chính là embedding identifier của Face++)
        return faceToken;
    }

    @Override
    public double compareFaceTokens(String faceToken1, String faceToken2) throws Exception {
        // So sánh 2 face_token bằng Face++ Compare API
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);
        body.add("face_token1", faceToken1);
        body.add("face_token2", faceToken2);

        // Tạo headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gọi Face++ Compare API
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Face++ Compare API call failed: " + response.getStatusCode());
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
