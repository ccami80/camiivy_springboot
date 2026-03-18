package com.culwonder.leeds_profile_springboot_core.api.image.request;

import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageStatus;
import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이미지 목록 검색 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageListSearchRequest
{
    
    /**
     * 이미지 타입 (선택)
     */
    private ImageType imageType;
    
    /**
     * 이미지 상태 (선택)
     */
    private ImageStatus imageStatus;
    
    /**
     * 업로드한 사용자 ID (선택)
     */
    private String uploadedBy;
    
    /**
     * 관련 엔티티 ID (선택)
     */
    private String relatedEntityId;
    
    /**
     * 관련 엔티티 타입 (선택)
     */
    private String relatedEntityType;
    
    /**
     * 페이지 번호 (0부터 시작)
     */
    @Builder.Default
    private int page = 0;
    
    /**
     * 페이지 크기
     */
    @Builder.Default
    private int size = 20;
    
    /**
     * 정렬 기준 (createdAt, updatedAt, fileSize 등)
     */
    @Builder.Default
    private String sortBy = "createdAt";
    
    /**
     * 정렬 방향 (asc, desc)
     */
    @Builder.Default
    private String sortDirection = "desc";
}

