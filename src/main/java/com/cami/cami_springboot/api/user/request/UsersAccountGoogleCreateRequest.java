package com.cami.cami_springboot.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User account sign-in request (Google OAuth)
 */
@Schema(description = "Google sign-in request")
public record UsersAccountGoogleCreateRequest(
    @Schema(description = "Phone number", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone max 20 characters")
    String phone,

    @Schema(description = "Google authorization code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Authorization code is required")
    @Size(max = 500, message = "Code max 500 characters")
    String code
) {}
