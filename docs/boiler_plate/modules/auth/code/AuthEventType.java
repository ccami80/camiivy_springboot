package com.culwonder.leeds_profile_springboot_core.api.auth.code;

/**
 * 인증 이벤트 타입 (개선됨)
 */
public enum AuthEventType
{
    LOGIN,            // 로그인 (새 토큰 생성)
    LOGOUT,           // 일반 로그아웃
    TOKEN_REFRESH,    // 토큰 갱신 (새 토큰 생성)
    TOKEN_REPLACED,   // 토큰 교체로 인한 기존 토큰 삭제 ⭐
    TOKEN_EXPIRED,    // 토큰 만료 (스케줄러 자동 정리)
    FORCE_SIGN_OUT,   // 관리자 강제 로그아웃
    SIGN_OUT          // 회원 탈퇴
}
