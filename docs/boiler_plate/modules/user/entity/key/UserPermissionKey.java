package com.culwonder.leeds_profile_springboot_core.api.user.entity.key;

import com.culwonder.leeds_profile_springboot_core.api.user.code.PermissionType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 사용자 권한 복합키
 * userId + permissionType
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserPermissionKey implements Serializable
{
    private String userId;
    private PermissionType permissionType;
}
