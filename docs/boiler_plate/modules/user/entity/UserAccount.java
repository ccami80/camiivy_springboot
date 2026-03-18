package com.culwonder.leeds_profile_springboot_core.api.user.entity;

import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import com.culwonder.leeds_profile_springboot_core.api.user.entity.key.UsersAccountKey;
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
 * 사용자 계정 엔티티
 * 핸드폰 번호와 소셜 계정 정보를 관리
 */
@Entity
@Table(name = "user_module_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@IdClass(UsersAccountKey.class)
public class UserAccount
{

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false)
    private SocialProvider socialProvider;

    @Id
    @Column(name = "social_id", length = 100, nullable = false)
    private String socialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private User user;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // ========================================
    // Audit 컬럼 (필수)
    // ========================================
    
    @Column(name = "created_id", length = 50)
    private String createdId;  // 생성자 ID (nullable = true)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 일시 (필수)

    @Column(name = "updated_id", length = 50)
    private String updatedId;  // 수정자 ID (nullable = true)

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정 일시 (nullable = false)

    @Builder
    public UserAccount(String userId, SocialProvider socialProvider, String socialId, String phone, String email, String name, UserStatus status, String createdId, String updatedId)
    {
        this.userId = userId;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.createdId = createdId;
        this.updatedId = updatedId;
        // createdAt, updatedAt은 @CreatedDate, @LastModifiedDate가 자동 설정
    }

    /**
     * 사용자 정보 업데이트
     */
    public void updateUser(String name, String email)
    {
        this.name = name;
        this.email = email;
    }

    /**
     * 핸드폰 번호 업데이트
     */
    public void updatePhone(String phone)
    {
        this.phone = phone;
    }

    /**
     * 계정 비활성화
     */
    public void deactivate()
    {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 계정 활성화
     */
    public void activate()
    {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 사용자 ID 반환
     */
    public String getUserId()
    {
        return this.userId;
    }

    /**
     * 핸드폰 번호 반환
     */
    public String getPhone()
    {
        return this.phone;
    }

    /**
     * 이메일 반환
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * 소셜 제공자 반환
     */
    public SocialProvider getSocialProvider()
    {
        return this.socialProvider;
    }

    /**
     * 소셜 ID 반환
     */
    public String getSocialId()
    {
        return this.socialId;
    }

    /**
     * User 엔티티 반환
     */
    public User getUser()
    {
        return this.user;
    }

    /**
     * User 엔티티 설정
     */
    public void setUser(User user)
    {
        this.user = user;
    }
}
