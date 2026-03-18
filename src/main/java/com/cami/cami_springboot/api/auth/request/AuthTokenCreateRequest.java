package com.cami.cami_springboot.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 인증 토큰 생성 요청 DTO (REST: 리소스 = tokens, 동작 = POST)
 * 소셜 인가 코드로 access/refresh 토큰 발급(로그인) 시 사용
 */
@Schema(description = "인증 토큰 생성 요청 (소셜 로그인)", example = """
    {
      "provider": "kakao",
      "code": "authorization_code_here"
    }
    """)
public record AuthTokenCreateRequest(
    @Schema(description = "소셜 제공자 (kakao, google)", example = "kakao", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소셜 제공자는 필수입니다")
    @Pattern(regexp = "^(?i)kakao|google$", message = "provider는 kakao 또는 google 이어야 합니다")
    String provider,

    @Schema(description = "소셜 인가 코드", example = "authorization_code_here", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "인가 코드는 필수입니다")
    @Size(max = 500, message = "인가 코드는 500자 이하여야 합니다")
    String code
) {}
