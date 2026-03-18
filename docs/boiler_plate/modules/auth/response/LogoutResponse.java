package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse
{
    private String message;
    private Long revokedTokenCount;
}
