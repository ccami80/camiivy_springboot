package com.culwonder.leeds_profile_springboot_core.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    String id,
    
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    String name,
    
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    String phone,
    
    @Size(max = 200, message = "주소는 200자 이하여야 합니다")
    String address
) {}
