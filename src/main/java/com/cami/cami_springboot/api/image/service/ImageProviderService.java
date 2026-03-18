package com.cami.cami_springboot.api.image.service;

import com.cami.cami_springboot.api.image.code.ImageStatus;
import com.cami.cami_springboot.api.image.entity.Image;
import com.cami.cami_springboot.api.image.repository.ImageRepository;
import com.cami.cami_springboot.api.image.response.ImageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 이미지 Provider 서비스
 * 타 모듈에게 제공하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProviderService
{
    
    private final ImageRepository imageRepository;
    
    /**
     * 특정 엔티티와 연관된 이미지 목록 조회
     * 
     * @param relatedEntityId 연관 엔티티 ID
     * @param relatedEntityType 연관 엔티티 타입
     * @return 이미지 응답 목록
     */
    @Transactional(readOnly = true)
    public List<ImageResponse> getImagesByRelatedEntity(String relatedEntityId, String relatedEntityType)
    {
        List<Image> images = imageRepository.customSearchImageListByRelatedEntity(
            relatedEntityId,
            relatedEntityType,
            ImageStatus.ACTIVE
        );
        
        return images.stream()
            .map(this::convertToImageResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 사용자가 업로드한 이미지 개수 조회
     * 
     * @param userId 사용자 ID
     * @return 이미지 개수
     */
    @Transactional(readOnly = true)
    public long getImageCountByUser(String userId)
    {
        return imageRepository.customSearchImageCount(
            null,
            ImageStatus.ACTIVE,
            userId,
            null,
            null
        );
    }
    
    /**
     * 이미지 응답 변환
     */
    private ImageResponse convertToImageResponse(Image image)
    {
        return ImageResponse.builder()
            .imageId(image.getImageId())
            .imageType(image.getImageType())
            .imageStatus(image.getImageStatus())
            .originalFileName(image.getOriginalFileName())
            .storedFileName(image.getStoredFileName())
            .publicUrl(image.getPublicUrl())
            .fileSize(image.getFileSize())
            .mimeType(image.getMimeType())
            .width(image.getWidth())
            .height(image.getHeight())
            .uploadedBy(image.getUploadedBy())
            .relatedEntityId(image.getRelatedEntityId())
            .relatedEntityType(image.getRelatedEntityType())
            .createdAt(image.getCreatedAt())
            .updatedAt(image.getUpdatedAt())
            .build();
    }
}

