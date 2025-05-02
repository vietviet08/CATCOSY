package com.catcosy.customer.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.catcosy.library.dto.FileMetadata;
import com.catcosy.library.service.S3StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class S3 {
    private final S3StorageService s3StorageService;

    @PostMapping("/s3/upload")
    public FileMetadata uploadImage(@RequestPart(value = "image") MultipartFile file) {
        try {
            return s3StorageService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
