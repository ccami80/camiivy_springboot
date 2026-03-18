package com.culwonder.leeds_profile_springboot_core.api.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 계정 삭제 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersAccountDeleteResponse
{
    private String userId;
    private String socialProvider;
    private String socialId;
    private String message;
}
