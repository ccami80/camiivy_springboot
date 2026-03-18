package com.culwonder.leeds_profile_springboot_core.api.user.code;

/**
 * 사용자 권한 타입
 * API 접두사와 연결된 권한 체계
 */
public enum PermissionType
{
    USER("일반 사용자 권한", "회원가입 시 자동 부여 (기본 권한)"),
    BUSINESS("비즈니스 권한", "/api-business/ 접근 가능 (BUSINESS 권한 필요)"),
    ADMIN("관리자 권한", "/api-admin/ 접근 가능 (ADMIN 권한 필요)");
    
    private final String displayName;
    private final String description;
    
        PermissionType(String displayName, String description)
    {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public String getDescription()
    {
        return description;
    }
}
