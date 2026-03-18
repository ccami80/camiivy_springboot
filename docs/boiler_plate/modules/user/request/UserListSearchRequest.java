package com.culwonder.leeds_profile_springboot_core.api.user.request;

import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import lombok.Data;

@Data
public class UserListSearchRequest
{
    private String searchKeyword;
    private String email;
    private String name;
    private String phone;
    private UserStatus status;
    private String[] sort;
}
