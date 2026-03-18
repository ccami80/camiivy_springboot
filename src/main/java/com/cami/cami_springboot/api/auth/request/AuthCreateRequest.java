package com.cami.cami_springboot.api.auth.request;

import com.cami.cami_springboot.api.auth.code.TokenType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AuthCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String userId,
    
    String socialProvider,
    
    String socialId,
    
    @NotNull(message = "토큰 타입은 필수입니다")
    TokenType tokenType,
    
    @NotNull(message = "만료 시간은 필수입니다")
    LocalDateTime expiresAt,
    
    @Size(max = 200, message = "디바이스 정보는 200자 이하여야 합니다")
    String deviceInfo,
    
    @Size(max = 45, message = "IP 주소는 45자 이하여야 합니다")
    String ipAddress,
    
    @Size(max = 500, message = "User Agent는 500자 이하여야 합니다")
    String userAgent
) {}
