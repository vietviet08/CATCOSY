package com.catcosy.customer.controller;

import com.catcosy.library.dto.Base64Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.catcosy.library.dto.FileMetadata;
import com.catcosy.library.service.S3StorageService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class S3 {
    private static final Logger log = LoggerFactory.getLogger(S3.class);
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

    @PostMapping("/s3/uploads")
    public List<FileMetadata> uploadImage(@RequestPart(value = "images") List<MultipartFile> files) {
        try {
            return s3StorageService.uploadFiles(files);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/s3/base64")
    public FileMetadata uploadBase64(@RequestBody Base64Request base64) {
        try {
            return s3StorageService.uploadBase64Image(base64.getBase64(), "test");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    @PostMapping("/s3/base64")
//    public List<FileMetadata> uploadBase64() {
//        try {
//            return s3StorageService.updateBase64ByCommentId();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
