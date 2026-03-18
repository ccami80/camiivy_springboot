package com.cami.cami_springboot.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 카카오 로그인 요청 DTO
 */
@Schema(description = "카카오 로그인 요청")
public record KakaoLoginRequest(
    @Schema(description = "카카오 인가 코드", example = "authorization_code_here", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "카카오 인가 코드는 필수입니다")
    String code
) {}
