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

    /**
     * Detect và encode ảnh khuôn mặt thành embedding vector (mã hóa đặc trưng khuôn mặt)
     * @param faceImage Ảnh khuôn mặt cần encode
     * @return Chuỗi JSON chứa embedding vector
     * @throws Exception nếu có lỗi khi gọi API
     */
    String detectAndEncodeFace(MultipartFile faceImage) throws Exception;

    /**
     * So sánh embedding của ảnh upload với embedding đã lưu trong DB
     * @param uploadedFaceImage Ảnh khuôn mặt người dùng upload
     * @param storedEmbedding Embedding đã lưu trong database (dạng JSON string)
     * @return Độ tương đồng (0-100)
     * @throws Exception nếu có lỗi khi gọi API
     */


    /**
     * So sánh 2 face tokens (embeddings) trực tiếp mà không cần encode lại
     * @param faceToken1 Face token đầu tiên (từ detectAndEncodeFace)
     * @param faceToken2 Face token thứ hai (đã lưu trong database)
     * @return Độ tương đồng (0-100)
     * @throws Exception nếu có lỗi khi gọi API
     */
    double compareFaceTokens(String faceToken1, String faceToken2) throws Exception;
}
