package com.cami.cami_springboot.api.image.entity;

import com.cami.cami_springboot.api.image.code.ImageStatus;
import com.cami.cami_springboot.api.image.code.ImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 이미지 엔티티
 * Supabase Storage에 저장된 이미지의 메타데이터를 관리
 */
@Entity
@Table(name = "image_module")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image
{
    
    /**
     * 이미지 ID (기본키)
     * 형식: img_xxxxx
     */
    @Id
    @Column(length = 50)
    private String imageId;
    
    /**
     * 이미지 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImageType imageType;
    
    /**
     * 이미지 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImageStatus imageStatus;
    
    /**
     * 원본 파일명
     */
    @Column(nullable = false, length = 255)
    private String originalFileName;
    
    /**
     * Supabase Storage에 저장된 파일명
     */
    @Column(nullable = false, length = 255)
    private String storedFileName;
    
    /**
     * Supabase Storage 경로
     */
    @Column(nullable = false, length = 500)
    private String storagePath;
    
    /**
     * Supabase Storage 공개 URL
     */
    @Column(length = 1000)
    private String publicUrl;
    
    /**
     * 파일 크기 (bytes)
     */
    @Column(nullable = false)
    private Long fileSize;
    
    /**
     * MIME 타입 (예: image/jpeg, image/png)
     */
    @Column(length = 100)
    private String mimeType;
    
    /**
     * 이미지 너비 (픽셀)
     */
    private Integer width;
    
    /**
     * 이미지 높이 (픽셀)
     */
    private Integer height;
    
    /**
     * 업로드한 사용자 ID
     */
    @Column(length = 50)
    private String uploadedBy;
    
    /**
     * 관련 엔티티 ID (예: 사용자 ID, 게시글 ID 등)
     */
    @Column(length = 50)
    private String relatedEntityId;
    
    /**
     * 관련 엔티티 타입 (예: USER, POST, PRODUCT 등)
     */
    @Column(length = 50)
    private String relatedEntityType;
    
    // ========================================
    // Audit 컬럼 (필수)
    // ========================================
    
    @Column(length = 50)
    private String createdId;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(length = 50)
    private String updatedId;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // ========================================
    // 비즈니스 메서드
    // ========================================
    
    /**
     * 이미지를 활성 상태로 변경
     */
    public void activate()
    {
        this.imageStatus = ImageStatus.ACTIVE;
    }
    
    /**
     * 이미지를 삭제 상태로 변경
     */
    public void delete()
    {
        this.imageStatus = ImageStatus.DELETED;
    }
    
    /**
     * 이미지 정보 업데이트
     */
    public void updateInfo(String publicUrl, Integer width, Integer height, String updatedId)
    {
        if (publicUrl != null)
        {
            this.publicUrl = publicUrl;
        }
        if (width != null)
        {
            this.width = width;
        }
        if (height != null)
        {
            this.height = height;
        }
        this.updatedId = updatedId;
    }
}

