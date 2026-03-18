package com.cami.cami_springboot.api.auth.entity;

import com.cami.cami_springboot.api.auth.code.AuthEventType;
import com.cami.cami_springboot.api.auth.code.TokenType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Auth History 엔티티
 * 
 * 테이블명: auth_module_history
 * 모든 인증 이벤트 이력을 기록합니다.
 * (로그인, 로그아웃, 토큰 갱신, 토큰 무효화 등)
 */
@Entity
@Table(name = "auth_module_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AuthHistory
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // Aggregate Root 참조
    // ========================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuthEventType eventType;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(length = 50)
    private String socialProvider;

    @Column(length = 100)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TokenType tokenType;

    @Column(length = 500)
    private String token;

    @Column(length = 200)
    private String deviceInfo;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 500)
    private String eventMessage;

    private LocalDateTime eventAt;

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

    @Builder
    public AuthHistory(Auth auth, AuthEventType eventType, String userId, String socialProvider, String socialId, TokenType tokenType, String token, String deviceInfo, String ipAddress, String userAgent, String eventMessage, LocalDateTime eventAt, String createdId, String updatedId)
    {
        this.auth = auth;
        this.eventType = eventType;
        this.userId = userId;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.tokenType = tokenType;
        this.token = token;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.eventMessage = eventMessage;
        this.eventAt = eventAt != null ? eventAt : LocalDateTime.now();
        this.createdId = createdId;
        this.updatedId = updatedId;
        // createdAt, updatedAt은 @CreatedDate, @LastModifiedDate가 자동 설정
    }
}
