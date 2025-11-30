package org.example.storyreading.ibanking.service;

import org.springframework.web.multipart.MultipartFile;

public interface FaceRecognitionService {
    /**
     * So sánh 2 ảnh khuôn mặt và trả về độ tương đồng (0-100)
     * @param image1 Ảnh CCCD
     * @param image2 Ảnh selfie
     * @return Độ confidence (0-100)
     * @throws Exception nếu có lỗi khi gọi API
     */
    double compareFaces(MultipartFile image1, MultipartFile image2) throws Exception;

    /**
     * So sánh ảnh khuôn mặt upload với ảnh đã lưu trên URL (Cloudinary)
     * @param uploadedFace Ảnh khuôn mặt người dùng upload
     * @param storedPhotoUrl URL ảnh đã lưu (photoUrl trong database)
     * @return Độ confidence (0-100)
     * @throws Exception nếu có lỗi khi gọi API
     */
    double compareFaceWithUrl(MultipartFile uploadedFace, String storedPhotoUrl) throws Exception;
}
