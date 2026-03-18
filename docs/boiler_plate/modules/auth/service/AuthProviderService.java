package com.culwonder.leeds_profile_springboot_core.api.auth.service;

import com.culwonder.leeds_profile_springboot_core.api.auth.entity.Auth;
import com.culwonder.leeds_profile_springboot_core.api.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Auth 모듈의 Provider 서비스 (개선됨)
 * 타 모듈에게 인증/인가 관련 서비스를 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthProviderService {

    private final AuthRepository authRepository;

    /**
     * 사용자 회원 탈퇴 처리 (모든 토큰 삭제)
     * ACCESS 토큰과 REFRESH 토큰을 모두 히스토리로 이동 후 삭제합니다.
     * (회원 탈퇴 시 사용)
     * 
     * @param userId 사용자 ID
     */
    @Transactional
    public void logoutSignOut(String userId) {
        log.info("회원 탈퇴 처리 요청 (Provider): userId={}", userId);

        // 사용자의 모든 토큰 조회
        List<Auth> userTokens = authRepository.findByUserId(userId);

        if (userTokens.isEmpty()) {
            log.info("삭제할 토큰 없음: userId={}", userId);
            return;
        }

        // 히스토리 저장 (회원 탈퇴 이벤트) - DDD 방식
        for (Auth auth : userTokens) {
            auth.addSignOutHistory("회원 탈퇴로 인한 로그아웃");
            authRepository.save(auth);  // cascade로 히스토리 저장
        }

        // 토큰 삭제
        authRepository.deleteAll(userTokens);

        log.info("회원 탈퇴 처리 완료 (Provider): userId={}, deletedCount={}",
                userId, userTokens.size());
    }
}
