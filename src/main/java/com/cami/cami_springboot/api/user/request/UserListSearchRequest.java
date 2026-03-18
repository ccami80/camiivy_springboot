package com.cami.cami_springboot.api.user.request;

import com.cami.cami_springboot.api.user.code.UserStatus;
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
