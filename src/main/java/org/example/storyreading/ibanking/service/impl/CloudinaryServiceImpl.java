package org.example.storyreading.ibanking.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.example.storyreading.ibanking.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public String uploadImage(MultipartFile file, Long userId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }

        try {
            // Tạo public_id duy nhất cho ảnh: ibanking/users/{userId}/{uuid}
            String publicId = String.format("ibanking/users/%d/%s", userId, UUID.randomUUID().toString());

            // Upload lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "ibanking/users/" + userId,
                    "resource_type", "image",
                    "format", "jpg"
            ));

            // Trả về secure URL
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new Exception("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String publicId) throws Exception {
        if (publicId == null || publicId.isEmpty()) {
            throw new IllegalArgumentException("Public ID không được rỗng");
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");

            if (!"ok".equals(resultStatus) && !"not found".equals(resultStatus)) {
                throw new Exception("Không thể xóa ảnh từ Cloudinary. Result: " + resultStatus);
            }
        } catch (IOException e) {
            throw new Exception("Lỗi khi xóa ảnh từ Cloudinary: " + e.getMessage(), e);
        }
    }
}

