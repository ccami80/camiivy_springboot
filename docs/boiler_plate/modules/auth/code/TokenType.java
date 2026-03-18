package com.culwonder.leeds_profile_springboot_core.api.auth.code;

public enum TokenType
{
    ACCESS("액세스 토큰"),
    REFRESH("리프레시 토큰"),
    VERIFICATION("인증 토큰"),
    RESET_PASSWORD("비밀번호 재설정 토큰");

    private final String description;

        TokenType(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
