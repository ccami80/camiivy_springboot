package com.cami.cami_springboot.api.image.repository;

import com.cami.cami_springboot.api.image.code.ImageStatus;
import com.cami.cami_springboot.api.image.code.ImageType;
import com.cami.cami_springboot.api.image.entity.Image;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 이미지 커스텀 리포지토리 인터페이스
 */
public interface ImageRepositoryCustom
{
    
    /**
     * 이미지 목록 검색
     */
    List<Image> customSearchImageList(
        ImageType imageType,
        ImageStatus imageStatus,
        String uploadedBy,
        String relatedEntityId,
        String relatedEntityType,
        Pageable pageable
    );
    
    /**
     * 이미지 목록 카운트
     */
    long customSearchImageCount(
        ImageType imageType,
        ImageStatus imageStatus,
        String uploadedBy,
        String relatedEntityId,
        String relatedEntityType
    );
    
    /**
     * 특정 사용자가 업로드한 이미지 목록 조회
     */
    List<Image> customSearchImageListByUploader(String uploadedBy, Pageable pageable);
    
    /**
     * 특정 엔티티와 연관된 이미지 목록 조회
     */
    List<Image> customSearchImageListByRelatedEntity(
        String relatedEntityId,
        String relatedEntityType,
        ImageStatus imageStatus
    );
}

