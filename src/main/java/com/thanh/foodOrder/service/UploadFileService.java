package com.thanh.foodOrder.service;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thanh.foodOrder.util.exception.CommonException;

@Service
public class UploadFileService {
    @Value("${thanh.upload-file.base-uri}")
    private String baseUri;

    public String uploadFile(String category, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new CommonException("File is empty");

            }
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                throw new CommonException("Only image files are allowed");
            }

            Path root = Paths.get(URI.create(baseUri)); // D:/upload
            Path dir = root.resolve(category); // D:/upload/beverages

            Files.createDirectories(dir); // auto-create folder

            String fileName = UUID.randomUUID().toString();
            Path filePath = dir.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // return relative path for DB
            return category + "/" + fileName;

        } catch (Exception e) {
            throw new CommonException("Failed to store file: " + e.getMessage());
        }
    }
}
