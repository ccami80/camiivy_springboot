package com.culwonder.leeds_profile_springboot_core.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 사용자 계정 존재 확인 응답 DTO
 */
@Schema(description = "사용자 계정 존재 확인 응답")
@Builder
public record UsersAccountCheckResponse(
    @Schema(description = "계정 존재 여부", example = "true")
    boolean exists,
    
    @Schema(description = "사용자 ID (계정이 존재하는 경우)", example = "usr_20241201_001")
    String userId,
    
    @Schema(description = "소셜 제공자", example = "KAKAO")
    String socialProvider,
    
    @Schema(description = "소셜 ID", example = "kakao_user_123")
    String socialId,
    
    @Schema(description = "메시지", example = "계정이 존재합니다")
    String message
) {}

