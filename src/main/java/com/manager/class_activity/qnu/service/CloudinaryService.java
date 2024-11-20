package com.manager.class_activity.qnu.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryService {
    @Value("${cloudinary.cloud_name}")
    String cloudName;

    @Value("${cloudinary.api_key}")
    String apiKey;

    @Value("${cloudinary.api_secret}")
    String apiSecret;

    Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
    @Async
    public CompletableFuture<String> uploadPdfAsync(MultipartFile file) {
        if (file == null || file.isEmpty() || !file.getOriginalFilename().endsWith(".pdf")) {
            throw new BadException(ErrorCode.INVALID_FILE);
        }

        File pdfFile = null;
        try {
            // Chuyển MultipartFile sang File
            pdfFile = convertToFile(file);

            // Thiết lập tham số upload
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "folder", "pdf_files",
                    "type", "upload"
            );

            // Upload file lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(pdfFile, params);
            String fileUrl = (String) uploadResult.get("url");

            log.info("Upload successful! File URL: {}", fileUrl);
            return CompletableFuture.completedFuture(fileUrl);

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new BadException(ErrorCode.UPLOAD_ERROR);
        } finally {
            if (pdfFile != null && pdfFile.exists()) {
                if (!pdfFile.delete()) {
                    log.warn("Temporary file deletion failed: {}", pdfFile.getAbsolutePath());
                }
            }
        }
    }

    private File convertToFile(MultipartFile file) throws IOException {
        // Chuyển đổi MultipartFile sang File
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

}
