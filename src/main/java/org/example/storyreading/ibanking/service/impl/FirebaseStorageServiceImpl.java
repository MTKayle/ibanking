package org.example.storyreading.ibanking.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.example.storyreading.ibanking.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    private Storage storage;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ClassPathResource(credentialsPath.replace("classpath:", "")).getInputStream()
            );
            storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (Exception e) {
            System.err.println("Warning: Firebase initialization failed. Upload will not work: " + e.getMessage());
            // Không throw exception để app vẫn chạy được khi develop
        }
    }

    @Override
    public String uploadImage(MultipartFile file, Long userId) throws Exception {
        if (storage == null) {
            throw new RuntimeException("Firebase Storage chưa được khởi tạo. Vui lòng kiểm tra cấu hình.");
        }

        // Tạo tên file unique
        String fileName = "users/" + userId + "/selfie_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload lên Firebase Storage
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // Tạo signed URL (public URL có thời hạn 100 năm)
        Blob blob = storage.get(blobId);
        return blob.signUrl(365 * 100, TimeUnit.DAYS).toString();
    }
}

