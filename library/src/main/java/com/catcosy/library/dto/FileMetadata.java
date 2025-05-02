package com.catcosy.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {
    private String bucket;
    private String key;
    private String name;
    private String mime;
    private String hash;
    private String etag;
    private Long size;
    private String extension;
    private String url;
    private Boolean publicAccess = true;
}
