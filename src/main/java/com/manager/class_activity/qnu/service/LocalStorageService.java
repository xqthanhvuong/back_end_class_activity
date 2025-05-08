package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class LocalStorageService {

    @Value("${local.storage.path}")
    String storagePath;  // Đường dẫn đến thư mục lưu trữ cục bộ

    @Value("${server.host}")
    String serverHost;  // Địa chỉ của server, ví dụ: "http://localhost:8080"

    public CompletableFuture<String> uploadPdfAsync(MultipartFile file) {
        if (file == null || file.isEmpty() || !file.getOriginalFilename().endsWith(".pdf")) {
            throw new BadException(ErrorCode.INVALID_FILE);
        }

        File pdfFile = null;
        try {
            // Chuyển MultipartFile sang File
            pdfFile = convertToFile(file);

            // Đường dẫn lưu trữ tệp PDF
            String fileName = file.getOriginalFilename();
            String filePath = storagePath + "/" + fileName;

            // Kiểm tra xem tệp đã tồn tại chưa, nếu có thì đổi tên tệp
            File destinationFile = new File(filePath);
            int counter = 1;
            while (destinationFile.exists()) {
                String newFileName = getNewFileName(fileName, counter);
                filePath = storagePath + "/" + newFileName;
                destinationFile = new File(filePath);
                counter++;
            }

            // Lưu tệp vào hệ thống cục bộ
            try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
                fos.write(file.getBytes());
            }

            // Trả về URL đầy đủ của tệp đã lưu
            String fileUrl = serverHost + "/files/" + destinationFile.getName();
            log.info("Upload successful! File saved at: {}", fileUrl);

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

    private String getNewFileName(String originalFileName, int counter) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        return baseName + "_" + counter + extension;
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
