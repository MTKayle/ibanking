package org.example.storyreading.ibanking.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Upload ảnh lên Cloudinary và trả về URL công khai
     * @param file File ảnh cần upload
     * @param userId ID của user (dùng để tạo tên file duy nhất)
     * @return URL công khai của ảnh
     * @throws Exception nếu có lỗi khi upload
     */
    String uploadImage(MultipartFile file, Long userId) throws Exception;

    /**
     * Xóa ảnh từ Cloudinary
     * @param publicId Public ID của ảnh trên Cloudinary
     * @throws Exception nếu có lỗi khi xóa
     */
    void deleteImage(String publicId) throws Exception;
}

