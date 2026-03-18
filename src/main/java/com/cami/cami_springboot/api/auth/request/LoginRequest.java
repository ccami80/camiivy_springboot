package com.cami.cami_springboot.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 요청 DTO (소셜 로그인)
 */
@Schema(description = "로그인 요청 (소셜 로그인)")
public record LoginRequest(
    @Schema(description = "소셜 제공자", example = "KAKAO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소셜 제공자는 필수입니다")
    String socialProvider,
    
    @Schema(description = "소셜 ID", example = "kakao_user_123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소셜 ID는 필수입니다")
    String socialId,
    
    @Schema(description = "디바이스 정보", example = "iPhone 14 Pro")
    @Size(max = 200, message = "디바이스 정보는 200자 이하여야 합니다")
    String deviceInfo,
    
    @Schema(description = "IP 주소", example = "192.168.1.1")
    @Size(max = 45, message = "IP 주소는 45자 이하여야 합니다")
    String ipAddress,
    
    @Schema(description = "User Agent", example = "Mozilla/5.0...")
    @Size(max = 500, message = "User Agent는 500자 이하여야 합니다")
    String userAgent
) {}
