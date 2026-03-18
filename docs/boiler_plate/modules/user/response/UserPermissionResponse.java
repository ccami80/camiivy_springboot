package com.culwonder.leeds_profile_springboot_core.api.user.response;

import com.culwonder.leeds_profile_springboot_core.api.user.code.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 사용자 권한 응답 DTO
 */
@Schema(description = "사용자 권한 응답")
@Builder
public record UserPermissionResponse(
    @Schema(description = "사용자 ID", example = "usr_2bG7_1")
    String userId,
    
    @Schema(description = "권한 타입", example = "USER")
    PermissionType permissionType,
    
    @Schema(description = "권한 부여 일시", example = "2024-10-08T10:00:00")
    LocalDateTime createdAt
) {}
