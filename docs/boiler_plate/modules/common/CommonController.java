package com.culwonder.leeds_profile_springboot_core.api.common;

import com.culwonder.leeds_profile_springboot_core.api.common.code.ErrorCode;
import com.culwonder.leeds_profile_springboot_core.api.common.response.CommonResponse;
import com.culwonder.leeds_profile_springboot_core.api.common.response.ErrorCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common API controller.
 * Profile, error code list, and other shared endpoints.
 */
@Tag(name = "Common API", description = "Profile, error codes, and shared utilities")
@RestController
@RequestMapping("/api/common")
public class CommonController
{
    
    private final Environment environment;
    
    public CommonController(Environment environment)
    {
        this.environment = environment;
    }
    
    /**
     * 현재 사용 중인 프로파일 확인
     */
    @Operation(summary = "Profile", description = "Get current Spring profile (e.g. dev, prod)")
    @GetMapping("/profile")
    public CommonResponse getProfile()
    {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        Map<String, Object> profileInfo = new HashMap<>();
        profileInfo.put("activeProfile", activeProfile);
        profileInfo.put("profiles", activeProfiles);
        
        return new CommonResponse(true, "프로파일 조회 성공", profileInfo);
    }
    
    /**
     * 전체 에러 코드 목록 조회
     */
    @Operation(summary = "Error codes", description = "List all error codes by category (Common, Auth, User, Image, File)")
    @GetMapping("/error-codes")
    public CommonResponse getErrorCodes()
    {
        Map<String, List<ErrorCodeResponse>> categorizedErrors = new HashMap<>();
        
        categorizedErrors.put("Common", getCategoryErrors(1000, 1999));
        categorizedErrors.put("Auth", getCategoryErrors(2000, 2999));
        categorizedErrors.put("User", getCategoryErrors(3000, 3999));
        categorizedErrors.put("Image", getCategoryErrors(4000, 4999));
        categorizedErrors.put("File", getCategoryErrors(5000, 5999));
        
        return new CommonResponse(true, "에러 코드 목록 조회 성공", categorizedErrors);
    }
    
    /**
     * 카테고리별 에러 코드 필터링
     */
    private List<ErrorCodeResponse> getCategoryErrors(int rangeStart, int rangeEnd)
    {
        List<ErrorCodeResponse> errors = new ArrayList<>();
        
        for (ErrorCode errorCode : ErrorCode.values())
        {
            int codeNumber = Integer.parseInt(errorCode.getCode().substring(1));
            
            if (codeNumber >= rangeStart && codeNumber <= rangeEnd)
            {
                errors.add(ErrorCodeResponse.builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .httpStatus(errorCode.getHttpStatus().value())
                    .category(getCategoryName(codeNumber))
                    .build());
            }
        }
        
        return errors;
    }
    
    /**
     * 카테고리 이름 반환
     */
    private String getCategoryName(int codeNumber)
    {
        if (codeNumber >= 1000 && codeNumber < 2000) return "Common";
        if (codeNumber >= 2000 && codeNumber < 3000) return "Auth";
        if (codeNumber >= 3000 && codeNumber < 4000) return "User";
        if (codeNumber >= 4000 && codeNumber < 5000) return "Image";
        if (codeNumber >= 5000 && codeNumber < 6000) return "File";
        return "Other";
    }
}

