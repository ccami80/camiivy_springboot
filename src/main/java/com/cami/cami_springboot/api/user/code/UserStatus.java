package com.cami.cami_springboot.api.user.code;

public enum UserStatus
{
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지"),
    DELETED("탈퇴");

    private final String description;

        UserStatus(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
