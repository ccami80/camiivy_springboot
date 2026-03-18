package com.culwonder.leeds_profile_springboot_core.api.auth.repository;

import com.culwonder.leeds_profile_springboot_core.api.auth.entity.Auth;
import com.culwonder.leeds_profile_springboot_core.api.auth.code.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Auth 리포지토리 (개선됨)
 * 
 * 규칙:
 * - 기본 JPA 메서드만 정의 (findBy, findFirstBy, countBy, existsBy, deleteBy 패턴)
 * - 복잡한 조회/삭제는 AuthRepositoryCustom 사용
 * - auth_module 테이블에는 활성 토큰만 저장되므로 status 조건 불필요
 */
@Repository
public interface AuthRepository extends JpaRepository<Auth, Long>, AuthRepositoryCustom
{
    
    // ========================================
    // 토큰 조회 (status 조건 제거됨)
    // ========================================
    
    /**
     * 토큰으로 조회 (최신순)
     */
    Optional<Auth> findFirstByTokenOrderByCreatedAtDesc(String token);
    
    /**
     * 토큰과 토큰 타입으로 조회 (최신순)
     */
    Optional<Auth> findFirstByTokenAndTokenTypeOrderByCreatedAtDesc(String token, TokenType tokenType);
    
    /**
     * 사용자 ID로 조회 (모든 토큰)
     */
    List<Auth> findByUserId(String userId);
    
    /**
     * 사용자 ID와 토큰 타입으로 조회
     */
    List<Auth> findByUserIdAndTokenType(String userId, TokenType tokenType);
    
    /**
     * 사용자 ID와 토큰 타입으로 최신 토큰 조회
     */
    Optional<Auth> findFirstByUserIdAndTokenTypeOrderByCreatedAtDesc(
        String userId, TokenType tokenType);
    
    // ========================================
    // 만료된 토큰 조회 (스케줄러용)
    // ========================================
    
    /**
     * 만료 시간이 지난 토큰 조회
     */
    List<Auth> findByExpiresAtBefore(LocalDateTime expiresAt);
    
    // ========================================
    // 토큰 존재 여부 확인
    // ========================================
    
    /**
     * 토큰 존재 여부 확인
     */
    boolean existsByToken(String token);
}
