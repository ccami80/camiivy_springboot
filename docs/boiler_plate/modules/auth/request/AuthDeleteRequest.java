package com.culwonder.leeds_profile_springboot_core.api.auth.request;

import jakarta.validation.constraints.NotNull;

public record AuthDeleteRequest(
    @NotNull(message = "인증 ID는 필수입니다")
    Long id
) {}
