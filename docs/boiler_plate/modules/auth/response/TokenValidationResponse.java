package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse
{
    private boolean valid;
    private String userId;
    private String socialProvider;
    private String socialId;
    private String createdAt;
    private String token;
    private String message;
    private Long expiresIn;
}
