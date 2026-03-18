package com.cami.cami_springboot.api.common.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Application error codes (generic, reusable across platforms).
 * Categories: 1xxx Common, 2xxx Auth, 3xxx User, 4xxx Image, 5xxx File.
 */
@Getter
public enum ErrorCode
{
    // ========================================
    // 공통 에러 (1000-1999)
    // ========================================
    INVALID_INPUT("E1001", "잘못된 입력값입니다", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("E1002", "요청한 리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("E1003", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("E1004", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("E1005", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    
    // ========================================
    // 인증 관련 에러 (2000-2999)
    // ========================================
    INVALID_TOKEN("E2001", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("E2002", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("E2003", "유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("E2004", "Session expired. Please sign in again", HttpStatus.UNAUTHORIZED),
    LOGIN_REQUIRED("E2005", "Login required", HttpStatus.UNAUTHORIZED),
    ALREADY_LOGGED_IN("E2006", "이미 로그인되어 있습니다", HttpStatus.BAD_REQUEST),
    
    // ========================================
    // 사용자 관련 에러 (3000-3999)
    // ========================================
    USER_NOT_FOUND("E3001", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("E3002", "이미 존재하는 사용자입니다", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("E3003", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    DUPLICATE_PHONE("E3004", "이미 사용 중인 전화번호입니다", HttpStatus.CONFLICT),
    DUPLICATE_SOCIAL_ACCOUNT("E3007", "이미 등록된 소셜 계정입니다", HttpStatus.CONFLICT),
    USER_WITHDRAWN("E3005", "탈퇴한 사용자입니다", HttpStatus.GONE),
    USER_SUSPENDED("E3006", "정지된 사용자입니다", HttpStatus.FORBIDDEN),
    
    // ========================================
    // 이미지 관련 에러 (4000-4999)
    // ========================================
    IMAGE_NOT_FOUND("E4001", "이미지를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_IMAGE_FORMAT("E4002", "잘못된 이미지 형식입니다", HttpStatus.BAD_REQUEST),
    IMAGE_TOO_LARGE("E4003", "이미지 파일 크기가 너무 큽니다", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED("E4004", "이미지 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_DELETE_FAILED("E4005", "이미지 삭제에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    EMPTY_IMAGE_FILE("E4006", "파일이 비어있습니다", HttpStatus.BAD_REQUEST),
    NOT_IMAGE_FILE("E4007", "이미지 파일만 업로드 가능합니다", HttpStatus.BAD_REQUEST),
    
    // ========================================
    // 파일 관련 에러 (5000-5999)
    // ========================================
    FILE_UPLOAD_FAILED("E5001", "파일 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("E5002", "파일 삭제에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_READ_FAILED("E5003", "파일 읽기에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("E5004", "지원하지 않는 파일 형식입니다", HttpStatus.BAD_REQUEST);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    
    ErrorCode(String code, String message, HttpStatus httpStatus)
    {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

