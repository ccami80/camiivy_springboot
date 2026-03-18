package com.cami.cami_springboot.api.user.request;

import com.cami.cami_springboot.api.user.code.SocialProvider;
import com.cami.cami_springboot.api.user.code.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 사용자 계정 목록 검색 요청 DTO
 */
@Data
@Schema(description = "사용자 계정 목록 검색 요청")
public class UsersAccountListSearchRequest
{
    
    @Schema(description = "사용자 ID", example = "usr_20241201_001")
    private String userId;
    
    @Schema(description = "소셜 제공자", example = "KAKAO")
    private SocialProvider socialProvider;
    
    @Schema(description = "소셜 ID", example = "kakao_user_123")
    private String socialId;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "계정 상태", example = "ACTIVE")
    private UserStatus status;
}
