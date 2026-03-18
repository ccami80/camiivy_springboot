package com.culwonder.leeds_profile_springboot_core.api.image.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 삭제 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDeleteRequest
{
    
    /**
     * 이미지 ID
     */
    @NotBlank(message = "이미지 ID는 필수입니다")
    private String imageId;
}

