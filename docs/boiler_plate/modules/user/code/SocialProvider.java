package com.culwonder.leeds_profile_springboot_core.api.user.code;

/**
 * 소셜 로그인 제공자 열거형
 */
public enum SocialProvider
{
    KAKAO("카카오"),
    GOOGLE("구글"),
    NAVER("네이버"),
    FACEBOOK("페이스북"),
    APPLE("애플"),
    LOCAL("로컬");
    
    private final String displayName;
    
        SocialProvider(String displayName)
    {
        this.displayName = displayName;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
}
