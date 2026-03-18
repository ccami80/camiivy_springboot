package com.cami.cami_springboot.api.image.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 삭제 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDeleteResponse
{
    
    private String imageId;
    private String message;
    private boolean success;
}

