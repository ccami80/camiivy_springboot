package com.culwonder.leeds_profile_springboot_core.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    String refreshToken
) {}
