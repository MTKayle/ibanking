package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.FaceComparisonResponse;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.service.FaceComparisonService;
import org.example.storyreading.ibanking.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FaceComparisonServiceImpl implements FaceComparisonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Value("${faceplus.confidence.threshold}")
    private double confidenceThreshold;

    @Override
    public FaceComparisonResponse compareFaceWithUser(Long userId, MultipartFile uploadedImage) throws Exception {
        // Validate file
        if (uploadedImage == null || uploadedImage.isEmpty()) {
            throw new IllegalArgumentException("Ảnh không được để trống");
        }

        // Tìm user theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Kiểm tra user có photoUrl không
        if (user.getPhotoUrl() == null || user.getPhotoUrl().trim().isEmpty()) {
            throw new RuntimeException("Người dùng chưa có ảnh khuôn mặt trong hệ thống");
        }

        // So sánh ảnh upload với ảnh trong photoUrl
        double confidence = faceRecognitionService.compareFaceWithUrl(uploadedImage, user.getPhotoUrl());

        // Kiểm tra confidence với threshold
        boolean matched = confidence >= confidenceThreshold;

        String message;
        if (matched) {
            message = "Xác thực khuôn mặt thành công. Độ tương đồng: " + String.format("%.2f", confidence) + "%";
        } else {
            message = "Xác thực khuôn mặt thất bại. Độ tương đồng: " + String.format("%.2f", confidence) + "% (yêu cầu tối thiểu: " + confidenceThreshold + "%)";
        }

        return new FaceComparisonResponse(matched, message, confidence, matched);
    }
}

