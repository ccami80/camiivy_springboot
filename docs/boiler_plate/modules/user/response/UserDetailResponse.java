package com.culwonder.leeds_profile_springboot_core.api.user.response;

import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse
{
    private String userId;
    private String email;
    private String name;
    private String phone;
    private String address;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
