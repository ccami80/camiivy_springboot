package com.cami.cami_springboot.api.auth.request;

import com.cami.cami_springboot.api.auth.code.TokenType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Auth 목록 검색 요청 (개선됨)
 * 
 * status 필드 제거: auth_module에는 활성 토큰만 저장되므로 불필요
 */
@Data
public class AuthListSearchRequest
{
    private String userId;
    private TokenType tokenType;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime expiresAtFrom;
    private LocalDateTime expiresAtTo;
    private String[] sort;
}
