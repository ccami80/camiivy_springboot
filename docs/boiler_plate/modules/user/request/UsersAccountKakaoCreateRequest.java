package com.culwonder.leeds_profile_springboot_core.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 계정 생성 요청 DTO
 */
@Schema(description = "사용자 계정 생성 요청", example = """
    {
      "phone": "010-1234-5678",
      "socialProvider": "KAKAO",
      "code": "authorization_code_123"
    }
    """)
public record UsersAccountKakaoCreateRequest(
    @Schema(description = "핸드폰 번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핸드폰 번호는 필수입니다")
    @Size(max = 20, message = "핸드폰 번호는 20자 이하여야 합니다")
    String phone,
    
    @Schema(description = "카카오 인가 코드", example = "authorization_code_123")
    @NotBlank(message = "카카오 인가 코드는 필수입니다")
    @Size(max = 100, message = "카카오 인가 코드는 100자 이하여야 합니다")
    String code
) {}
