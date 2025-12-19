package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.FaceComparisonResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FaceComparisonService {
    /**
     * So sánh ảnh upload với ảnh trong photoURL của user
     * @param userId ID của user cần so sánh
     * @param uploadedImage Ảnh khuôn mặt upload từ FE
     * @return FaceComparisonResponse chứa kết quả so sánh
     * @throws Exception nếu có lỗi
     */
    FaceComparisonResponse compareFaceWithUser(Long userId, MultipartFile uploadedImage) throws Exception;
}

