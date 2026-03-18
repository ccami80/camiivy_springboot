package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import lombok.Builder;
import lombok.Data;

/**
 * 로그인 상태 확인 응답
 */
@Data
@Builder
public class LoginStatusResponse
{
    private boolean loggedIn;       // 로그인 여부
    private String userId;          // 사용자 ID
    private String socialProvider;  // 소셜 제공자
    private String socialId;        // 소셜 ID
    private String message;         // 메시지
    
    /**
     * 로그인 중 응답 생성
     */
        public static LoginStatusResponse loggedIn(String userId, String socialProvider, String socialId)
    {
        return LoginStatusResponse.builder()
            .loggedIn(true)
            .userId(userId)
            .socialProvider(socialProvider)
            .socialId(socialId)
            .message("로그인 중입니다")
            .build();
    }
    
    /**
     * 로그아웃 상태 응답 생성
     */
        public static LoginStatusResponse loggedOut(String message)
    {
        return LoginStatusResponse.builder()
            .loggedIn(false)
            .message(message)
            .build();
    }
}
