package com.cami.cami_springboot.api.auth.request;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String userId
) {}
