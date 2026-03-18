package com.culwonder.leeds_profile_springboot_core.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 에러 코드 응답 DTO
 * 에러 코드 목록 API에서 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "에러 코드 정보")
public class ErrorCodeResponse
{
    
    @Schema(description = "에러 코드", example = "E2001")
    private String code;
    
    @Schema(description = "에러 메시지", example = "유효하지 않은 토큰입니다")
    private String message;
    
    @Schema(description = "HTTP 상태 코드", example = "401")
    private int httpStatus;
    
    @Schema(description = "에러 카테고리", example = "인증 관련 에러")
    private String category;
}

