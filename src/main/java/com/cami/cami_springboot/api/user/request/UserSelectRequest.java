package com.cami.cami_springboot.api.user.request;

import jakarta.validation.constraints.NotNull;

public record UserSelectRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String id
) {}
