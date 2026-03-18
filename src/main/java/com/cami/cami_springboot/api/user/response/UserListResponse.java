package com.cami.cami_springboot.api.user.response;

import com.cami.cami_springboot.api.user.code.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse
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
