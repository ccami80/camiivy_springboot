package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import com.culwonder.leeds_profile_springboot_core.api.auth.code.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse
{
    private Long id;
    private String token;
    private TokenType tokenType;
    private String userId;
    private LocalDateTime expiresAt;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
