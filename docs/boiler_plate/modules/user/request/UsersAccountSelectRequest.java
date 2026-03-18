package com.culwonder.leeds_profile_springboot_core.api.user.request;

import jakarta.validation.constraints.NotNull;

/**
 * 사용자 계정 조회 요청 DTO
 */
public record UsersAccountSelectRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String userId,
    @NotNull(message = "소셜 제공자는 필수입니다")
    String socialProvider,
    @NotNull(message = "소셜 ID는 필수입니다")
    String socialId
) {}
