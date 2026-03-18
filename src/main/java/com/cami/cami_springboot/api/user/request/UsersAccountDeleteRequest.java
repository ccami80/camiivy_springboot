package com.cami.cami_springboot.api.user.request;

import jakarta.validation.constraints.NotNull;

/**
 * 사용자 계정 삭제 요청 DTO
 */
public record UsersAccountDeleteRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String userId,
    
    @NotNull(message = "소셜 제공자는 필수입니다")
    String socialProvider,
    
    @NotNull(message = "소셜 ID는 필수입니다")
    String socialId
) {}
