package com.cami.cami_springboot.api.auth;

import com.cami.cami_springboot.api.auth.request.LogoutRequest;
import com.cami.cami_springboot.api.auth.response.LogoutResponse;
import com.cami.cami_springboot.api.auth.service.AuthService;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 로그인 API 컨트롤러
 * 로그인한 사용자를 위한 API (권한 무관, 로그인만 필요)
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Logined API", description = "인증 로그인 API (로그인 필요)")
public class AuthLoginedController
{
    
    private final AuthService authService;
    
    /**
     * 쿠키의 secure 플래그 설정
     * true: HTTPS에서만 쿠키 저장 (프로덕션)
     * false: HTTP에서도 쿠키 저장 (개발)
     */
    @Value("${app.auth.token.secure:false}")
    private boolean secureCookie;
    
    @Value("${app.auth.token.domain:}")
    private String cookieDomain;
    
}
