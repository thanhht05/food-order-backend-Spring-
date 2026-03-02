package com.thanh.foodOrder.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thanh.foodOrder.service.UploadFileService;

@RestController
@RequestMapping("/api/v1")

public class UploadController {
    private final UploadFileService uploadFileService;

    public UploadController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String path = uploadFileService.uploadFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("fileName", path);

        return ResponseEntity.ok(response);
    }

}
