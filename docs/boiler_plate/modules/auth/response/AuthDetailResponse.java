package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import com.culwonder.leeds_profile_springboot_core.api.auth.code.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Auth 상세 응답 (개선됨)
 * 
 * status 필드 제거: auth_module에는 활성 토큰만 저장되므로 모두 ACTIVE
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDetailResponse
{
    private Long id;
    private String token;
    private TokenType tokenType;
    private String userId;
    private LocalDateTime expiresAt;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
