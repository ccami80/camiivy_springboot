package com.culwonder.leeds_profile_springboot_core.api.user.entity;

import com.culwonder.leeds_profile_springboot_core.api.user.code.PermissionType;
import com.culwonder.leeds_profile_springboot_core.api.user.entity.key.UserPermissionKey;
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
 * 사용자 권한 엔티티
 * User의 하위 엔티티로 권한 정보를 관리
 */
@Entity
@Table(name = "user_module_permission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@IdClass(UserPermissionKey.class)
public class UserPermission
{
    
    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;
    
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false)
    private PermissionType permissionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private User user;
    
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
    public UserPermission(String userId, PermissionType permissionType, String createdId, String updatedId)
    {
        this.userId = userId;
        this.permissionType = permissionType;
        this.createdId = createdId;
        this.updatedId = updatedId;
        // createdAt, updatedAt은 @CreatedDate, @LastModifiedDate가 자동 설정
    }
    
    /**
     * User 엔티티 설정
     */
    public void setUser(User user)
    {
        this.user = user;
    }
    
    /**
     * 권한 타입 반환
     */
    public PermissionType getPermissionType()
    {
        return this.permissionType;
    }
    
    /**
     * 사용자 ID 반환
     */
    public String getUserId()
    {
        return this.userId;
    }
}
