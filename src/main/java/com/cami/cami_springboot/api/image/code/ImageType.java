package com.cami.cami_springboot.api.image.code;

import lombok.Getter;

/**
 * 이미지 타입 열거형
 * 각 타입별로 Storage 폴더 경로와 Signed URL 사용 여부를 가짐
 */
@Getter
public enum ImageType
{
    TEMP("temp", true),                 // 임시 이미지 (Signed URL, 5분)
    PROFILE("profile", false),          // 프로필 이미지 (Public URL, 영구)
    CONTENT("content", false),          // 콘텐츠 이미지 (Public URL, 영구)
    THUMBNAIL("thumbnail", false),      // 썸네일 이미지 (Public URL, 영구)
    BANNER("banner", false),            // 배너 이미지 (Public URL, 영구)
    ATTACHMENT("attachment", false);     // 첨부 파일 이미지 (Public URL, 영구)
    
    private final String path;
    private final boolean isSigned;
    
    ImageType(String path, boolean isSigned)
    {
        this.path = path;
        this.isSigned = isSigned;
    }
}

