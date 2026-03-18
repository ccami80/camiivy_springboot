package com.cami.cami_springboot.api.image.service;

import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.util.SupabaseStorageClient;
import com.cami.cami_springboot.api.image.code.ImageStatus;
import com.cami.cami_springboot.api.image.code.ImageType;
import com.cami.cami_springboot.api.image.entity.Image;
import com.cami.cami_springboot.api.image.repository.ImageRepository;
import com.cami.cami_springboot.api.image.request.ImageDeleteRequest;
import com.cami.cami_springboot.api.image.request.ImageListSearchRequest;
import com.cami.cami_springboot.api.image.request.ImageUpdateRequest;
import com.cami.cami_springboot.api.image.response.ImageDeleteResponse;
import com.cami.cami_springboot.api.image.response.ImageDetailResponse;
import com.cami.cami_springboot.api.image.response.ImageResponse;
import com.cami.cami_springboot.api.image.response.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 이미지 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService
{
    
    private final ImageRepository imageRepository;
    private final SupabaseStorageClient supabaseStorageClient;
    
    /**
     * 이미지 업로드
     */
    @Transactional
    public ImageUploadResponse uploadImage(
        MultipartFile file, 
        ImageType imageType,
        String relatedEntityId,
        String relatedEntityType,
        String userId
    )
    {
        try
        {
            log.info("이미지 업로드 시작 - 사용자: {}, 타입: {}, 파일명: {}", 
                userId, imageType, file.getOriginalFilename());
            
            // 파일 유효성 검사
            if (file.isEmpty())
            {
                throw new CustomException(ErrorCode.EMPTY_IMAGE_FILE);
            }
            
            // MIME 타입 추출
            String mimeType = file.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/"))
            {
                throw new CustomException(ErrorCode.NOT_IMAGE_FILE);
            }
            
            // 파일 크기 확인
            long fileSize = file.getSize();
            
            // 파일 데이터 추출
            byte[] fileData = file.getBytes();
            
            // 폴더 경로 생성 (imageType/year/month)
            String folderPath = imageType.getPath();
            
            // Supabase Storage에 업로드
            String storagePath = supabaseStorageClient.uploadFile(
                fileData,
                file.getOriginalFilename(),
                mimeType,
                folderPath
            );
            
            // URL 생성 (Public 또는 Signed)
            String publicUrl = imageType.isSigned() 
                ? supabaseStorageClient.getSignedUrl(storagePath)
                : supabaseStorageClient.getPublicUrl(storagePath);
            
            // 이미지 ID 생성
            String imageId = generateImageId();
            
            // 이미지 엔티티 생성
            Image image = Image.builder()
                .imageId(imageId)
                .imageType(imageType)
                .imageStatus(ImageStatus.ACTIVE)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storagePath)
                .storagePath(storagePath)
                .publicUrl(publicUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .uploadedBy(userId)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .createdId(userId)
                .updatedId(userId)
                .build();
            
            // 저장
            imageRepository.save(Objects.requireNonNull(image));
            
            log.info("이미지 업로드 성공 - imageId: {}, publicUrl: {}", imageId, publicUrl);
            
            return ImageUploadResponse.builder()
                .imageId(imageId)
                .publicUrl(publicUrl)
                .storagePath(storagePath)
                .fileSize(fileSize)
                .message("이미지 업로드에 성공했습니다")
                .build();
        }
        catch (IOException e)
        {
            log.error("파일 읽기 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_READ_FAILED);
        }
        catch (CustomException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("이미지 업로드 중 오류 발생", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
    
    /**
     * 이미지 목록 검색
     */
    @Transactional(readOnly = true)
    public Page<ImageResponse> searchImageList(ImageListSearchRequest request)
    {
        Sort.Direction direction = request.getSortDirection().equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(direction, request.getSortBy())
        );
        
        List<Image> images = imageRepository.customSearchImageList(
            request.getImageType(),
            request.getImageStatus(),
            request.getUploadedBy(),
            request.getRelatedEntityId(),
            request.getRelatedEntityType(),
            pageable
        );
        
        long total = imageRepository.customSearchImageCount(
            request.getImageType(),
            request.getImageStatus(),
            request.getUploadedBy(),
            request.getRelatedEntityId(),
            request.getRelatedEntityType()
        );
        
        List<ImageResponse> content = images.stream()
            .map(this::convertToImageResponse)
            .collect(Collectors.toList());
        
        return new PageImpl<>(Objects.requireNonNull(content), pageable, total);
    }
    
    /**
     * 이미지 상세 조회
     */
    @Transactional(readOnly = true)
    public ImageDetailResponse getImageDetail(String imageId)
    {
        Image image = imageRepository.findById(Objects.requireNonNull(imageId))
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        return convertToImageDetailResponse(image);
    }
    
    /**
     * 이미지 정보 수정
     */
    @Transactional
    public ImageDetailResponse updateImage(ImageUpdateRequest request, String userId)
    {
        Image image = imageRepository.findById(Objects.requireNonNull(request.getImageId()))
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        image.updateInfo(null, null, null, userId);
        
        if (request.getRelatedEntityId() != null)
        {
            image.setRelatedEntityId(request.getRelatedEntityId());
        }
        
        if (request.getRelatedEntityType() != null)
        {
            image.setRelatedEntityType(request.getRelatedEntityType());
        }
        
        imageRepository.save(image);
        
        return convertToImageDetailResponse(image);
    }
    
    /**
     * 이미지 삭제
     */
    @Transactional
    public ImageDeleteResponse deleteImage(ImageDeleteRequest request, String userId)
    {
        try
        {
            Image image = imageRepository.findById(Objects.requireNonNull(request.getImageId()))
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
            
            // Supabase Storage에서 파일 삭제
            supabaseStorageClient.deleteFile(image.getStoragePath());
            
            // 이미지 상태를 DELETED로 변경
            image.delete();
            image.setUpdatedId(userId);
            imageRepository.save(image);
            
            log.info("이미지 삭제 성공 - imageId: {}", request.getImageId());
            
            return ImageDeleteResponse.builder()
                .imageId(request.getImageId())
                .message("이미지가 삭제되었습니다")
                .success(true)
                .build();
        }
        catch (Exception e)
        {
            log.error("이미지 삭제 중 오류 발생", e);
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
    
    // ========================================
    // 변환 메서드
    // ========================================
    
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
    
    private ImageDetailResponse convertToImageDetailResponse(Image image)
    {
        return ImageDetailResponse.builder()
            .imageId(image.getImageId())
            .imageType(image.getImageType())
            .imageStatus(image.getImageStatus())
            .originalFileName(image.getOriginalFileName())
            .storedFileName(image.getStoredFileName())
            .storagePath(image.getStoragePath())
            .publicUrl(image.getPublicUrl())
            .fileSize(image.getFileSize())
            .mimeType(image.getMimeType())
            .width(image.getWidth())
            .height(image.getHeight())
            .uploadedBy(image.getUploadedBy())
            .relatedEntityId(image.getRelatedEntityId())
            .relatedEntityType(image.getRelatedEntityType())
            .createdId(image.getCreatedId())
            .createdAt(image.getCreatedAt())
            .updatedId(image.getUpdatedId())
            .updatedAt(image.getUpdatedAt())
            .build();
    }
    
    // ========================================
    // 유틸리티 메서드
    // ========================================
    
    private String generateImageId()
    {
        return "img_" + UUID.randomUUID().toString().substring(0, 8);
    }
}

