package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse
{
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String userId;
    private String message;
}
