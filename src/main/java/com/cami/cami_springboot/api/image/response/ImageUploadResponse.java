package com.cami.cami_springboot.api.image.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 업로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse
{
    
    private String imageId;
    private String publicUrl;
    private String storagePath;
    private Long fileSize;
    private String message;
}

