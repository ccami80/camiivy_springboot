package com.culwonder.leeds_profile_springboot_core.api.user.response;

import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 프로필 응답 DTO
 * User 정보 + 연결된 계정 리스트
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse 
{
    private String userId;
    private String phone;
    private String email;
    private String name;
    private String address;
    private UserStatus status;
    private LocalDateTime createdAt;
    
    // 권한 목록
    private List<UserPermissionResponse> permissions;
    
    // 연결된 계정 목록 (간단 정보)
    private List<UsersAccountSimpleResponse> accountList;
}

