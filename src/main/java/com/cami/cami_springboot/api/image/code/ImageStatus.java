package com.cami.cami_springboot.api.image.code;

/**
 * 이미지 상태 열거형
 */
public enum ImageStatus
{
    ACTIVE,      // 활성
    DELETED,     // 삭제됨
    PROCESSING,  // 처리 중
    FAILED       // 실패
}

