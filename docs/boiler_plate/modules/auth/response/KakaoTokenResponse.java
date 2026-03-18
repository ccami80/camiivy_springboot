package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오 토큰 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTokenResponse
{
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;
    
    @JsonProperty("scope")
    private String scope;
}
