package com.culwonder.leeds_profile_springboot_core.api.user.response;

import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 계정 간단 응답 DTO (accountList용)
 * email, name, address, updatedAt 제외
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersAccountSimpleResponse 
{
    private String userId;
    private SocialProvider socialProvider;
    private String socialId;
    private String phone;
    private UserStatus status;
    private LocalDateTime createdAt;
}

