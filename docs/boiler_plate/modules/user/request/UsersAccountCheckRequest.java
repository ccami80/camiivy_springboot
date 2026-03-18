package com.culwonder.leeds_profile_springboot_core.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 계정 존재 확인 요청 DTO
 */
@Schema(description = "사용자 계정 존재 확인 요청")
public record UsersAccountCheckRequest(
    @Schema(description = "소셜 제공자", example = "KAKAO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소셜 제공자는 필수입니다")
    String socialProvider,
    
    @Schema(description = "소셜 ID", example = "kakao_user_123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소셜 ID는 필수입니다")
    String socialId
) {}

