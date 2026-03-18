package com.culwonder.leeds_profile_springboot_core.api.user.service;

import com.culwonder.leeds_profile_springboot_core.api.user.response.UserProviderResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountCheckResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountCreateRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 타 모듈에게 User 정보를 제공하는 서비스
 * 
 * 역할:
 * - Auth 모듈 등 타 모듈에게 사용자 정보 제공
 * - UserService를 활용하여 구현
 * - UserCallService 호출 금지
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserProviderService
{
    
    private final UserService userService;
    
    /**
     * 소셜 계정 존재 여부 확인
     * Auth 모듈에서 로그인 시 호출
     * 
     * @param socialProvider 소셜 제공자 (KAKAO, NAVER, GOOGLE)
     * @param socialId 소셜 ID
     * @return 계정 존재 여부 및 사용자 정보
     */
    public UserProviderResponse checkAccountExists(String socialProvider, String socialId)
    {
        log.info("[UserProviderService] 계정 존재 확인 요청: socialProvider={}, socialId={}", 
                socialProvider, socialId);
        
        try
        {
            // UserService의 checkAccountExists 활용
            UsersAccountCheckResponse checkResponse = userService.checkAccountExists(
                new com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountCheckRequest(
                    socialProvider, socialId
                )
            );
            
            log.info("[UserProviderService] 계정 존재 확인 결과: exists={}, userId={}", 
                    checkResponse.exists(), checkResponse.userId());
            
            // 규칙: 필요한 필드만 포함
            return UserProviderResponse.builder()
                .exists(checkResponse.exists())
                .userId(checkResponse.userId())
                .message(checkResponse.message())
                .build();
        }
        catch (Exception e)
        {
            log.error("[UserProviderService] 계정 존재 확인 실패: {}", e.getMessage());
            throw new RuntimeException("계정 존재 확인 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * 사용자 존재 여부 확인 (userId 기반)
     * 
     * @param userId 사용자 ID
     * @return 사용자 존재 여부
     */
    public UserProviderResponse checkUserExists(String userId)
    {
        log.info("[UserProviderService] 사용자 존재 확인 요청: userId={}", userId);
        
        try
        {
            // UserService의 사용자 조회 활용
            boolean exists = userService.checkUserExists(userId);
            
            log.info("[UserProviderService] 사용자 존재 확인 결과: exists={}, userId={}", exists, userId);
            
            return UserProviderResponse.builder()
                .exists(exists)
                .userId(exists ? userId : null)
                .message(exists ? "사용자가 존재합니다" : "사용자를 찾을 수 없습니다")
                .build();
        }
        catch (Exception e)
        {
            log.error("[UserProviderService] 사용자 존재 확인 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 존재 확인 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * 사용자 계정 생성
     * Auth 모듈에서 자동 회원가입 시 호출
     * 
     * @param request 사용자 계정 생성 요청
     * @return 생성된 사용자 계정
     */
    public UserAccount createUserAccount(UsersAccountCreateRequest request)
    {
        log.info("[UserProviderService] 사용자 계정 생성 요청: socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        
        try
        {
            // UserService의 userSignIn 활용
            UserAccount userAccount = userService.userSignIn(request);
            
            log.info("[UserProviderService] 사용자 계정 생성 완료: userId={}, socialProvider={}, socialId={}", 
                    userAccount.getUserId(), userAccount.getSocialProvider(), userAccount.getSocialId());
            
            return userAccount;
        }
        catch (Exception e)
        {
            log.error("[UserProviderService] 사용자 계정 생성 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 계정 생성 중 오류가 발생했습니다", e);
        }
    }
}
