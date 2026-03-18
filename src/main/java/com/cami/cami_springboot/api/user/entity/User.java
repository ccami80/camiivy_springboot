package com.cami.cami_springboot.api.user.entity;

import com.cami.cami_springboot.api.user.code.PermissionType;
import com.cami.cami_springboot.api.user.code.UserStatus;
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

@Entity
@Table(name = "user_module")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User
{

    @Id
    @Column(length = 50)
    private String userId;

    @Column(unique = true, length = 50)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(length = 20, nullable = true)
    private String phone;

    @Column(length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAccount> usersAccounts = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPermission> permissions = new ArrayList<>();

    @Builder
    public User(String userId, String email, String name, String phone, String address, UserStatus status, String createdId, String updatedId)
    {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.createdId = createdId;
        this.updatedId = updatedId;
        // createdAt, updatedAt은 @CreatedDate, @LastModifiedDate가 자동 설정
    }

    public void updateUser(String name, String phone, String address)
    {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public void deactivate()
    {
        this.status = UserStatus.INACTIVE;
    }

    public void activate()
    {
        this.status = UserStatus.ACTIVE;
    }
    
    public void delete()
    {
        this.status = UserStatus.DELETED;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public List<UserAccount> getUsersAccounts()
    {
        return this.usersAccounts;
    }

    public void addUsersAccount(UserAccount usersAccount)
    {
        this.usersAccounts.add(usersAccount);
        usersAccount.setUser(this);
    }

    public void removeUsersAccount(UserAccount usersAccount)
    {
        this.usersAccounts.remove(usersAccount);
        usersAccount.setUser(null);
    }
    
    /**
     * 권한 목록 반환
     */
    public List<UserPermission> getPermissions()
    {
        return this.permissions;
    }
    
    /**
     * 권한 추가
     */
    public void addPermission(PermissionType permissionType)
    {
        UserPermission permission = UserPermission.builder()
            .userId(this.userId)
            .permissionType(permissionType)
            .createdId("system")  // 시스템 생성
            .build();
        this.permissions.add(permission);
        permission.setUser(this);
    }
    
    /**
     * 권한 제거
     */
    public void removePermission(UserPermission permission)
    {
        this.permissions.remove(permission);
        permission.setUser(null);
    }
    
    /**
     * 특정 권한 보유 여부 확인
     */
    public boolean hasPermission(PermissionType permissionType)
    {
        return permissions.stream()
            .anyMatch(permission -> permission.getPermissionType() == permissionType);
    }
    
    /**
     * 관리자 권한 보유 여부 확인
     */
    public boolean isAdmin()
    {
        return hasPermission(PermissionType.ADMIN);
    }
    
    /**
     * 비즈니스 권한 보유 여부 확인
     */
    public boolean hasBusiness()
    {
        return hasPermission(PermissionType.BUSINESS);
    }
}
