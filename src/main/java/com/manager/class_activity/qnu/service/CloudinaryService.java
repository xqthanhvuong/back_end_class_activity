package com.manager.class_activity.qnu.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
        try {
            File pdfFile = convertToFile(file);
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "folder", "pdf_files",
                    "type", "upload"
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(pdfFile, params);
            String fileUrl = (String) uploadResult.get("url");
            log.info("Upload successful! File URL: " + fileUrl);
            pdfFile.delete();
            return CompletableFuture.completedFuture(fileUrl);
        } catch (Exception e) {
            log.error("Error uploading file: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
