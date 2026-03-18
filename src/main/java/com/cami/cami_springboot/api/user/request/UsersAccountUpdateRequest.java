package com.cami.cami_springboot.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 사용자 계정 수정 요청 DTO
 */
public record UsersAccountUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String userId,
    
    @NotNull(message = "소셜 제공자는 필수입니다")
    String socialProvider,
    
    @NotNull(message = "소셜 ID는 필수입니다")
    String socialId,
    
    @Size(max = 20, message = "핸드폰 번호는 20자 이하여야 합니다")
    String phone,
    
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    String name,
    
    @Size(max = 200, message = "주소는 200자 이하여야 합니다")
    String address
) {}
