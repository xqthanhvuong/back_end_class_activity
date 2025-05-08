package com.manager.class_activity.qnu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Value("${local.storage.path}")
    String storagePath;

    @GetMapping("/files/{filename}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String filename) {
        FileSystemResource file = new FileSystemResource(storagePath + "/" + filename);
        if (file.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

