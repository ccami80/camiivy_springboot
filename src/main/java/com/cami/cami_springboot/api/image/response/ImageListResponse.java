package com.cami.cami_springboot.api.image.response;

import com.cami.cami_springboot.api.common.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 이미지 목록 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageListResponse
{
    
    private List<ImageResponse> content;
    private PageResponse.PaginationInfo pagination;
}

