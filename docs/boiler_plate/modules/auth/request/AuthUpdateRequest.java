package com.culwonder.leeds_profile_springboot_core.api.auth.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AuthUpdateRequest(
    @NotNull(message = "인증 ID는 필수입니다")
    Long id,
    
    LocalDateTime expiresAt,
    
    @Size(max = 200, message = "디바이스 정보는 200자 이하여야 합니다")
    String deviceInfo,
    
    @Size(max = 45, message = "IP 주소는 45자 이하여야 합니다")
    String ipAddress,
    
    @Size(max = 500, message = "User Agent는 500자 이하여야 합니다")
    String userAgent
) {}
