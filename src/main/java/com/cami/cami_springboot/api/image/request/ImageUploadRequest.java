package com.cami.cami_springboot.api.image.request;

import com.cami.cami_springboot.api.image.code.ImageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 업로드 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest
{
    
    /**
     * 이미지 타입
     */
    @NotNull(message = "이미지 타입은 필수입니다")
    private ImageType imageType;
    
    /**
     * 관련 엔티티 ID (선택)
     */
    private String relatedEntityId;
    
    /**
     * 관련 엔티티 타입 (선택)
     */
    private String relatedEntityType;
}

