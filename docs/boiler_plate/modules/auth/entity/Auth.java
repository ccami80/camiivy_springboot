package com.culwonder.leeds_profile_springboot_core.api.auth.entity;

import com.culwonder.leeds_profile_springboot_core.api.auth.code.AuthEventType;
import com.culwonder.leeds_profile_springboot_core.api.auth.code.TokenType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Auth 엔티티 (Aggregate Root)
 * 
 * 테이블명 규칙: {도메인}_module (Aggregate Root)
 * 
 * 개선 사항:
 * - auth_module 테이블에는 활성 토큰만 저장
 * - 만료/무효화된 토큰은 AuthHistory로 이동 후 삭제
 * - status 필드 제거 (모든 토큰이 활성 상태)
 */
@Entity
@Table(name = "auth_module")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Auth
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(length = 50)
    private String socialProvider;

    @Column(length = 100)
    private String socialId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(length = 200)
    private String deviceInfo;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    // ========================================
    // Audit 컬럼 (필수)
    // ========================================
    
    @Column(length = 50)
    private String createdId;  // 생성자 ID (nullable = true)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 일시 (필수)

    @Column(length = 50)
    private String updatedId;  // 수정자 ID (nullable = true)

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 수정 일시 (nullable = false)

    // ========================================
    // Aggregate Root: AuthHistory 관리
    // ========================================
    
    @OneToMany(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<AuthHistory> histories = new ArrayList<>();

    @Builder
    public Auth(String token, TokenType tokenType, String userId, String socialProvider, String socialId, LocalDateTime expiresAt, String deviceInfo, String ipAddress, String userAgent, String createdId, String updatedId)
    {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.expiresAt = expiresAt;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdId = createdId;
        this.updatedId = updatedId;
        // createdAt, updatedAt은 @CreatedDate, @LastModifiedDate가 자동 설정
    }

    // ========================================
    // 비즈니스 메서드
    // ========================================

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired()
    {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * 로그인 히스토리 추가 (DDD)
     */
    public void addLoginHistory()
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.LOGIN)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage("로그인 성공")
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 토큰 갱신 히스토리 추가 (DDD)
     */
    public void addRefreshHistory()
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.TOKEN_REFRESH)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage("토큰 갱신 성공")
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 로그아웃 히스토리 추가 (DDD)
     */
    public void addLogoutHistory(String message)
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.LOGOUT)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage(message)
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 강제 로그아웃 히스토리 추가 (DDD)
     */
    public void addForceSignOutHistory(String message)
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.FORCE_SIGN_OUT)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage(message)
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 토큰 만료 히스토리 추가 (DDD)
     */
    public void addExpiredHistory(String message)
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.TOKEN_EXPIRED)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage(message)
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 토큰 교체 히스토리 추가 (DDD)
     */
    public void addReplacedHistory(String message)
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.TOKEN_REPLACED)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage(message)
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }

    /**
     * 회원 탈퇴 히스토리 추가 (DDD)
     */
    public void addSignOutHistory(String message)
    {
        AuthHistory history = AuthHistory.builder()
            .auth(this)
            .eventType(AuthEventType.SIGN_OUT)
            .userId(this.userId)
            .socialProvider(this.socialProvider)
            .socialId(this.socialId)
            .tokenType(this.tokenType)
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .eventMessage(message)
            .eventAt(LocalDateTime.now())
            .createdId("system")
            .build();
        this.histories.add(history);
    }
}
