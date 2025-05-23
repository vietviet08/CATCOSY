package com.catcosy.library.service;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.catcosy.library.dto.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface S3StorageService {
    Bucket createBucket(String bucketName);

    FileMetadata uploadFile(MultipartFile file) throws IOException;

    List<FileMetadata> uploadFiles(List<MultipartFile> files) throws IOException;

    void deleteFile(String fileUrl);

    FileMetadata uploadBase64Image(String base64Image, String folder) throws IOException;

    FileMetadata uploadByUrl(String url, String folder) throws IOException;

    String getPublicUrl(String s3Url);

    List<FileMetadata> updateBase64ByProductId(Long productId);

    List<FileMetadata> updateBase64ByCommentId();

}