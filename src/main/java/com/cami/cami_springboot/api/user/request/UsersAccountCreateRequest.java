package com.cami.cami_springboot.api.user.request;

import com.cami.cami_springboot.api.user.code.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 사용자 계정 생성 요청 DTO
 */
@Schema(description = "사용자 계정 생성 요청", example = """
    {
      "phone": "010-1234-5678",
      "socialProvider": "KAKAO",
      "socialId": "kakao_user_123"
    }
    """)
public record UsersAccountCreateRequest(
    @Schema(description = "핸드폰 번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 20, message = "핸드폰 번호는 20자 이하여야 합니다")
    String phone,
    
    @Schema(description = "소셜 제공자", example = "KAKAO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "소셜 제공자는 필수입니다")
    SocialProvider socialProvider,
    
    @Schema(description = "소셜 ID", example = "kakao_user_123")
    @Size(max = 100, message = "소셜 ID는 100자 이하여야 합니다")
    String socialId
) {}
