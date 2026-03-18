package com.culwonder.leeds_profile_springboot_core.api.image.response;

import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageStatus;
import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이미지 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse
{
    
    private String imageId;
    private ImageType imageType;
    private ImageStatus imageStatus;
    private String originalFileName;
    private String storedFileName;
    private String publicUrl;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private String uploadedBy;
    private String relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

