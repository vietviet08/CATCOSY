package com.catcosy.library.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.catcosy.library.dto.FileMetadata;
import com.catcosy.library.model.ProductImage;
import com.catcosy.library.repository.ProductImageRepository;
import com.catcosy.library.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    @Value("${aws.s3.endpoint}")
    private String urlStorage;

    private final AmazonS3 amazonS3;

    private final Tika tika = new Tika();

    private final ProductImageRepository productImageRepository;

    @Override
    public Bucket createBucket(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            return amazonS3.createBucket(bucketName);
        }
        return amazonS3.createBucket(bucketName);
    }

    @Override
    public FileMetadata uploadFile(MultipartFile image) {
        if (image == null) {
            return null;
        }

        String fileKey = "catcosy-file-" + image.getOriginalFilename();
        if (Objects.requireNonNull(image.getOriginalFilename()).contains(fileKey)) {
            fileKey = image.getOriginalFilename();
        }

        return putByMultipartFile(BUCKET_NAME, fileKey, image, true);
    }

    @Override
    public List<FileMetadata> uploadFiles(List<MultipartFile> files) throws IOException {
        List<FileMetadata> fileMetadata = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = "catcosy-file-" + file.getOriginalFilename();
            if (Objects.requireNonNull(file.getOriginalFilename()).contains(fileKey)) {
                fileKey = file.getOriginalFilename();
            }
            fileMetadata.add(putByMultipartFile(BUCKET_NAME, fileKey, file, true));
        }

        return fileMetadata;
    }

    public FileMetadata putByMultipartFile(String bucket, String key, MultipartFile file, Boolean publicAccess) {
        FileMetadata metadata = FileMetadata.builder()
                .bucket(bucket)
                .key(key)
                .name(file.getOriginalFilename())
                .extension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                .mime(tika.detect(file.getOriginalFilename()))
                .size(file.getSize())
                .build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(metadata.getSize());
        objectMetadata.setContentType(metadata.getMime());

        log.info("Uploading file to S3: {}", metadata.getName());

        try {
            InputStream stream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, objectMetadata);
            PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
            metadata.setUrl(amazonS3.getUrl(bucket, key).toString());
            metadata.setHash(putObjectResult.getContentMd5());
            metadata.setEtag(putObjectResult.getETag());
            metadata.setPublicAccess(publicAccess);
            stream.close();
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
        }
        return metadata;
    }

    @Override
    public void deleteFile(String keyName) {
        try {
            log.info("Deleting file from S3: {}", keyName);

            if (keyName == null) {
                return;
            } else if (keyName.contains(urlStorage)) {
                keyName = keyName.replace(urlStorage, "");
            }

            log.info("Deleting file from S3: {}", keyName);

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET_NAME, keyName);
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException e) {

            log.error("Error deleting file from S3", e);

        }

        log.info("File deleted from S3: {}", keyName);
    }

    @Override
    public FileMetadata uploadBase64Image(String base64Image, String folder) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        // Validate and preprocess the input
        if (base64Image.startsWith("{") || base64Image.startsWith("[")) {
            throw new IOException("Invalid input: Received JSON or array data instead of base64 string");
        }

        // Xử lý base64 string
        String imageData = base64Image;
        String contentType = "image/jpeg"; // Mặc định là JPEG
        String extension = "jpg";          // Mặc định là jpg

        // Kiểm tra và loại bỏ header nếu có
        if (base64Image.contains(",")) {
            // Trích xuất header để xác định loại hình ảnh
            String header = base64Image.substring(0, base64Image.indexOf(","));
            imageData = base64Image.substring(base64Image.indexOf(",") + 1);

            log.debug("Base64 header found: {}", header);

            // Xác định loại ảnh từ header
            if (header.contains("image/png")) {
                contentType = "image/png";
                extension = "png";
            } else if (header.contains("image/jpeg") || header.contains("image/jpg")) {
                contentType = "image/jpeg";
                extension = "jpg";
            } else if (header.contains("image/gif")) {
                contentType = "image/gif";
                extension = "gif";
            } else if (header.contains("image/webp")) {
                contentType = "image/webp";
                extension = "webp";
            } else if (header.contains("image/svg+xml")) {
                contentType = "image/svg+xml";
                extension = "svg";
            }
        }

        try {
            // Directly attempt to decode - Base64.Decoder handles validation internally
            byte[] decodedImage;
            try {
                decodedImage = Base64.getDecoder().decode(imageData);
            } catch (IllegalArgumentException e) {
                log.error("Error decoding base64 string: {}", e.getMessage());
                throw new IOException("Invalid Base64 string: " + e.getMessage(), e);
            }

            // Additional check for empty decoded data
            if (decodedImage.length == 0) {
                throw new IOException("Base64 decoding produced empty result");
            }

            // Tạo key duy nhất cho file
            String key = generateKey(folder, extension);

            // Cấu hình request để upload lên S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(decodedImage.length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key,
                    new ByteArrayInputStream(decodedImage), metadata);

            // Upload to S3
            PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);

            // Log việc upload thành công
            log.info("Successfully uploaded base64 image to S3: {}", key);

            // Create and return FileMetadata object
            FileMetadata fileMetadata = FileMetadata.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .name(key.substring(key.lastIndexOf('/') + 1))
                    .extension(extension)
                    .mime(contentType)
                    .size((long) decodedImage.length)
                    .url(amazonS3.getUrl(BUCKET_NAME, key).toString())
                    .hash(putObjectResult.getContentMd5())
                    .etag(putObjectResult.getETag())
                    .publicAccess(true)
                    .build();

            return fileMetadata;
        } catch (Exception e) {
            log.error("Error uploading base64 image to S3: {}", e.getMessage());
            throw new IOException("Failed to upload base64 image to S3: " + e.getMessage(), e);
        }
    }

    //upload all base64 image to s3
    @Override
    public List<FileMetadata> updateBase64ByProductId(Long productId) {

        List<FileMetadata> fileMetadataList = new ArrayList<>();
        for (long i = 39; i >= 1; i--) {
            List<ProductImage> productImages = productImageRepository.findByIdProduct(i);
            if(productImages == null || productImages.isEmpty()) {
                continue;
            }
            for (ProductImage productImage : productImages) {
                String base64 = productImage.getImage();
                if (base64 != null && !base64.isEmpty()) {
                    try {
                        FileMetadata fileMetadata = uploadBase64Image(base64, "product/" + productImage.getProduct().getId());
                        productImage.setS3Url(fileMetadata.getUrl());
                        productImage.setImage(null);
                        productImage.setUsingS3(true);
                        productImageRepository.save(productImage);
                        fileMetadataList.add(fileMetadata);
                    } catch (IOException e) {
                        log.error("Error uploading base64 image for product ID {}: {}", productId, e.getMessage());
                    }
                }
            }
        }
        return fileMetadataList;
    }

    // Phương thức hỗ trợ xác định định dạng ảnh từ signature bytes
    private boolean isPNG(byte[] bytes) {
        return bytes.length > 8 &&
                bytes[0] == (byte) 0x89 &&
                bytes[1] == (byte) 0x50 &&
                bytes[2] == (byte) 0x4E &&
                bytes[3] == (byte) 0x47;
    }

    private boolean isJPEG(byte[] bytes) {
        return bytes.length > 3 &&
                bytes[0] == (byte) 0xFF &&
                bytes[1] == (byte) 0xD8 &&
                bytes[bytes.length - 2] == (byte) 0xFF &&
                bytes[bytes.length - 1] == (byte) 0xD9;
    }

    private boolean isGIF(byte[] bytes) {
        return bytes.length > 6 &&
                bytes[0] == (byte) 'G' &&
                bytes[1] == (byte) 'I' &&
                bytes[2] == (byte) 'F' &&
                bytes[3] == (byte) '8';
    }

    private boolean isWebP(byte[] bytes) {
        return bytes.length > 12 &&
                bytes[8] == (byte) 'W' &&
                bytes[9] == (byte) 'E' &&
                bytes[10] == (byte) 'B' &&
                bytes[11] == (byte) 'P';
    }

    @Override
    public String getPublicUrl(String s3Url) {
        if (s3Url == null || s3Url.trim().isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ, trả về nguyên trạng
        if (s3Url.startsWith("http://") || s3Url.startsWith("https://")) {
            return s3Url;
        }

        // Xử lý các trường hợp s3Url có thể là key hoặc đường dẫn đầy đủ
        String key = s3Url;

        // Nếu URL chứa tên bucket, trích xuất phần key
        if (s3Url.contains(BUCKET_NAME)) {
            // Trích xuất phần key từ URL chứa tên bucket
            int bucketIndex = s3Url.indexOf(BUCKET_NAME);
            if (bucketIndex + BUCKET_NAME.length() < s3Url.length()) {
                // +1 để bỏ qua dấu '/' sau tên bucket
                key = s3Url.substring(bucketIndex + BUCKET_NAME.length() + 1);
            }
        }

        try {
            // Kiểm tra xem key đã được chuẩn hóa chưa
            if (key.startsWith("/")) {
                key = key.substring(1);
            }

            // Tạo URL công khai
            String publicUrl;
            if (urlStorage.endsWith("/")) {
                publicUrl = urlStorage + key;
            } else {
                publicUrl = urlStorage + "/" + key;
            }

            // Kiểm tra và ghi log URL được tạo
            log.info("Generated public URL for key {}: {}", key, publicUrl);

            return publicUrl;
        } catch (Exception e) {
            log.error("Error generating public URL for key {}: {}", key, e.getMessage());
            // Trả về URL mặc định trong trường hợp lỗi
            return urlStorage + "/" + key;
        }
    }

    private String generateKey(String folder, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        return folder + "/" + timestamp + "-" + randomId + "." + extension;
    }
}