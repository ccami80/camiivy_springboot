package com.culwonder.leeds_profile_springboot_core.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 타 모듈에게 제공하는 사용자 정보 응답 DTO
 * UserProviderService에서 사용
 * 
 * 규칙: 필요한 필드만 포함
 */
@Schema(description = "사용자 제공 응답 (타 모듈용)")
@Builder
public record UserProviderResponse(
    @Schema(description = "계정 존재 여부", example = "true")
    boolean exists,
    
    @Schema(description = "사용자 ID", example = "usr_2bG7_1")
    String userId,
    
    @Schema(description = "메시지", example = "계정이 존재합니다")
    String message
) {}
