package com.cami.cami_springboot.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    String refreshToken
) {}
