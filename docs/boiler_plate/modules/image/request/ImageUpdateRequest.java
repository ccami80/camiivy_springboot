package com.culwonder.leeds_profile_springboot_core.api.image.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUpdateRequest
{
    
    /**
     * 이미지 ID
     */
    @NotBlank(message = "이미지 ID는 필수입니다")
    private String imageId;
    
    /**
     * 관련 엔티티 ID (선택)
     */
    private String relatedEntityId;
    
    /**
     * 관련 엔티티 타입 (선택)
     */
    private String relatedEntityType;
}

