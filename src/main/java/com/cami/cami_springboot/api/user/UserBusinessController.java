package com.cami.cami_springboot.api.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 비즈니스 API 컨트롤러
 * BUSINESS 권한이 필요한 사용자 관리 API
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api-business/users")
@Tag(name = "User Business API", description = "사용자 비즈니스 API (BUSINESS 권한 필요)")
public class UserBusinessController
{
    
    // BUSINESS 권한이 필요한 API 구현
    
}
