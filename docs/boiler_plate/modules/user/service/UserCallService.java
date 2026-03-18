package com.culwonder.leeds_profile_springboot_core.api.user.service;

import com.culwonder.leeds_profile_springboot_core.api.auth.service.AuthProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * User 모듈의 Call 서비스
 * 타 모듈의 서비스를 호출합니다.
 */
@Slf4j
@Service
public class UserCallService
{
    
    private final AuthProviderService authProviderService;
    
    /**
     * 생성자 주입
     * @Lazy: 순환 참조 방지 (AuthService ↔ UserService)
     */
    public UserCallService(@Lazy AuthProviderService authProviderService)
    {
        this.authProviderService = authProviderService;
    }
    
    /**
     * Auth 모듈을 통해 사용자 로그아웃 (모든 토큰 무효화)
     * 
     * @param userId 사용자 ID
     */
    public void logout(String userId)
    {
        log.info("Auth 모듈 호출: 사용자 로그아웃 요청 - userId={}", userId);
        
        authProviderService.logoutSignOut(userId);
        
        log.info("Auth 모듈 호출: 사용자 로그아웃 완료 - userId={}", userId);
    }
}
