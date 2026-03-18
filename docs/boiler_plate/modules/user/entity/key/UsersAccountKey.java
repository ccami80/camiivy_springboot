package com.culwonder.leeds_profile_springboot_core.api.user.entity.key;

import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UsersAccount 복합키 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersAccountKey implements Serializable
{
    
    private String userId;
    private SocialProvider socialProvider;
    private String socialId;
}
