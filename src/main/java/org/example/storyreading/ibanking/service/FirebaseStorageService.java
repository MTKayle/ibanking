package org.example.storyreading.ibanking.service;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseStorageService {
    /**
     * Upload ảnh lên Firebase Storage và trả về URL công khai
     * @param file File ảnh cần upload
     * @param userId ID của user (dùng để tạo path)
     * @return URL công khai của ảnh
     * @throws Exception nếu có lỗi khi upload
     */
    String uploadImage(MultipartFile file, Long userId) throws Exception;
}

