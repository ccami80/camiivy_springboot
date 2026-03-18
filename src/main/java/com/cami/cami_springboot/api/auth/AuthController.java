package com.cami.cami_springboot.api.auth;

import com.cami.cami_springboot.api.auth.request.AuthTokenCreateRequest;
import com.cami.cami_springboot.api.auth.request.KakaoLoginRequest;
import com.cami.cami_springboot.api.auth.request.LoginRequest;
import com.cami.cami_springboot.api.auth.request.LogoutRequest;
import com.cami.cami_springboot.api.auth.request.RefreshTokenRequest;
import com.cami.cami_springboot.api.auth.response.LoginResponse;
import com.cami.cami_springboot.api.auth.response.LogoutResponse;
import com.cami.cami_springboot.api.auth.response.LoginStatusResponse;
import com.cami.cami_springboot.api.auth.response.TokenValidationResponse;
import com.cami.cami_springboot.api.auth.service.AuthService;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.common.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.cami.cami_springboot.api.user.code.SocialProvider;
import com.cami.cami_springboot.api.auth.response.KakaoUserInfoResponse;

import java.util.Map;

/**
 * Authentication guest API controller.
 * APIs that can be accessed without login (guest).
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
@Tag(name = "Auth Guest API", description = "Authentication guest API (no login required)")
public class AuthController
{

    private final AuthService authService;
    private final TokenUtil tokenUtil;

    @Value("${app.auth.token.access.expiration-seconds}")
    private int accessTokenExpirationSeconds;

    @Value("${app.auth.token.refresh.expiration-seconds}")
    private int refreshTokenExpirationSeconds;

    /** Cookie name for access token (generic: set per platform, e.g. accessToken) */
    @Value("${app.auth.token.cookie.access-name:accessToken}")
    private String accessTokenCookieName;

    /** Cookie name for refresh token (generic: set per platform, e.g. refreshToken) */
    @Value("${app.auth.token.cookie.refresh-name:refreshToken}")
    private String refreshTokenCookieName;

    /**
     * Cookie secure flag.
     * true: HTTPS only (production), false: allow HTTP (development)
     */
    @Value("${app.auth.token.secure:false}")
    private boolean secureCookie;

    @Value("${app.auth.token.domain:}")
    private String cookieDomain;

    /**
     * 인증 토큰 생성 (REST: POST /auth/tokens = 로그인 후 access/refresh 토큰 발급)
     * provider로 소셜 구분 (kakao, google)
     */
    @PostMapping("/api-guest/auth/tokens")
    @Operation(
        summary = "인증 토큰 생성 (소셜 로그인)",
        description = "소셜 인가 코드로 로그인하여 access/refresh 토큰을 발급합니다. (비로그인 접근 가능)\n\n" +
                     "**REST**: 리소스는 토큰(tokens), 동작은 POST(생성). provider는 body로 전달.\n\n" +
                     "**주의**: 먼저 회원가입(POST /api-guest/users/accounts)이 완료되어야 합니다.\n\n" +
                     "**절차**:\n" +
                     "1. 소셜 인가 코드로 액세스 토큰 발급\n" +
                     "2. 소셜 사용자 정보 조회 (ID 추출)\n" +
                     "3. 소셜 정보로 회원가입 여부 확인\n" +
                     "4. 회원가입되어 있으면 → 토큰 발급 (로그인 성공)\n" +
                     "5. 회원가입되어 있지 않으면 → 에러 반환 (회원가입 필요)\n\n" +
                     "**토큰 반환**:\n" +
                     "- accessToken, refreshToken은 HttpOnly 쿠키로 반환됩니다"
    )
    public ResponseEntity<CommonResponse> createToken(@Valid @RequestBody AuthTokenCreateRequest request)
    {
        String provider = request.provider().trim().toLowerCase();
        log.info("인증 토큰 생성 API 호출: provider={}, code={}", provider, request.code());

        LoginResponse response;
        if ("kakao".equals(provider))
        {
            KakaoUserInfoResponse kakaoUserInfo = authService.kakaoLogin(request.code());
            LoginRequest loginRequest = new LoginRequest(SocialProvider.KAKAO.name(), String.valueOf(kakaoUserInfo.getId()), null, null, null);
            response = authService.login(loginRequest);
        }
        else if ("google".equals(provider))
        {
            // Google: AuthService.googleLogin(), GoogleUserInfoResponse 구현 필요 (src/.../api/auth 참고)
            throw new UnsupportedOperationException("Google 로그인: AuthService.googleLogin() 구현 필요");
        }
        else
        {
            throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }

        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, response.getAccessToken())
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(accessTokenExpirationSeconds)
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieName, response.getRefreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(refreshTokenExpirationSeconds)
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .sameSite("Lax")
                .build();

        CommonResponse commonResponse = new CommonResponse(true, "로그인이 성공적으로 완료되었습니다", response);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(commonResponse);
    }
    
    @PostMapping("/api-guest/auth/login")
    @Operation(
        summary = "로그인 (소셜)", 
        description = "소셜 계정으로 로그인합니다. (비로그인 접근 가능)\n\n" +
                     "**주의**: 먼저 회원가입(POST /api-guest/users/accounts)이 완료되어야 합니다.\n\n" +
                     "**절차**:\n" +
                     "1. 소셜 정보로 회원가입 여부 확인\n" +
                     "2. 회원가입되어 있으면 → 토큰 발급 (로그인 성공)\n" +
                     "3. 회원가입되어 있지 않으면 → 에러 반환 (회원가입 필요)\n\n" +
                     "**토큰 반환**:\n" +
                     "- accessToken, refreshToken은 HttpOnly 쿠키로 반환됩니다\n" +
                     "- 쿠키는 자동으로 HTTP 요청에 포함됩니다"
    )
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginRequest request)
    {
        log.info("로그인 API 호출: socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        LoginResponse response = authService.login(request);
        
        // HttpOnly 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, response.getAccessToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .maxAge(accessTokenExpirationSeconds)  // 환경변수 (초 단위)
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .sameSite("Lax")       // CSRF 방지
                .build();
        
        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieName, response.getRefreshToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .maxAge(refreshTokenExpirationSeconds)  // 환경변수 (초 단위)
                .sameSite("Lax")       // CSRF 방지
                .build();
        
        CommonResponse commonResponse = new CommonResponse(true, "로그인이 성공적으로 완료되었습니다", response);
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(commonResponse);
    }
    
    @PostMapping("/api-guest/auth/login/kakao")
    @Operation(
        summary = "카카오 로그인", 
        description = "카카오 인가 코드로 로그인합니다. (비로그인 접근 가능)\n\n" +
                     "**주의**: 먼저 회원가입(POST /api-guest/users/accounts)이 완료되어야 합니다.\n\n" +
                     "**절차**:\n" +
                     "1. 카카오 인가 코드로 액세스 토큰 발급\n" +
                     "2. 카카오 사용자 정보 조회 (ID 추출)\n" +
                     "3. 소셜 정보로 회원가입 여부 확인\n" +
                     "4. 회원가입되어 있으면 → 토큰 발급 (로그인 성공)\n" +
                     "5. 회원가입되어 있지 않으면 → 에러 반환 (회원가입 필요)\n\n" +
                     "**토큰 반환**:\n" +
                     "- accessToken, refreshToken은 HttpOnly 쿠키로 반환됩니다\n" +
                     "- 쿠키는 자동으로 HTTP 요청에 포함됩니다"
    )
    public ResponseEntity<CommonResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request)
    {


        log.info("카카오 로그인 API 호출: code={}", request.code());
        KakaoUserInfoResponse kakaoUserInfo = authService.kakaoLogin(request.code());
        LoginRequest loginRequest = new LoginRequest(SocialProvider.KAKAO.name(), String.valueOf(kakaoUserInfo.getId()), null, null, null);
        LoginResponse response = authService.login(loginRequest);

        // HttpOnly 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, response.getAccessToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .maxAge(accessTokenExpirationSeconds)  // 환경변수 (초 단위)
                .sameSite("Lax")       // CSRF 방지
                .build();
        
        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieName, response.getRefreshToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .maxAge(refreshTokenExpirationSeconds)  // 환경변수 (초 단위)
                .sameSite("Lax")       // CSRF 방지
                .build();
        
        CommonResponse commonResponse = new CommonResponse(true, "로그인이 성공적으로 완료되었습니다", response);
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(commonResponse);
    }


    
    @PostMapping("/api-guest/auth/login-signin/kakao")
    @Operation(
        summary = "카카오 로그인", 
        description = "카카오 인가 코드로 로그인합니다. (비로그인 접근 가능)\n\n" +
                     "**주의**: 먼저 회원가입(POST /api-guest/users/accounts)이 완료되어야 합니다.\n\n" +
                     "**절차**:\n" +
                     "1. 카카오 인가 코드로 액세스 토큰 발급\n" +
                     "2. 카카오 사용자 정보 조회 (ID 추출)\n" +
                     "3. 소셜 정보로 회원가입 여부 확인\n" +
                     "4. 회원가입되어 있으면 → 토큰 발급 (로그인 성공)\n" +
                     "5. 회원가입되어 있지 않으면 → 에러 반환 (회원가입 필요)\n\n" +
                     "**토큰 반환**:\n" +
                     "- accessToken, refreshToken은 HttpOnly 쿠키로 반환됩니다\n" +
                     "- 쿠키는 자동으로 HTTP 요청에 포함됩니다"
    )
    public ResponseEntity<CommonResponse> kakaoLoginSignIn(@Valid @RequestBody KakaoLoginRequest request)
    {


        log.info("카카오 로그인 API 호출: code={}", request.code());
        KakaoUserInfoResponse kakaoUserInfo = authService.kakaoLogin(request.code());
        LoginRequest loginRequest = new LoginRequest(SocialProvider.KAKAO.name(), String.valueOf(kakaoUserInfo.getId()), null, null, null);
        LoginResponse response = authService.loginSignIn(loginRequest);

        // HttpOnly 쿠키 생성
        ResponseCookie.ResponseCookieBuilder accessTokenBuilder = ResponseCookie.from(accessTokenCookieName, response.getAccessToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .maxAge(accessTokenExpirationSeconds);  // 환경변수 (초 단위)
        
        
        ResponseCookie accessTokenCookie = accessTokenBuilder.build();
        
        ResponseCookie.ResponseCookieBuilder refreshTokenBuilder = ResponseCookie.from(refreshTokenCookieName, response.getRefreshToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .maxAge(refreshTokenExpirationSeconds);  // 환경변수 (초 단위)
        
        
        ResponseCookie refreshTokenCookie = refreshTokenBuilder.build();
        
        CommonResponse commonResponse = new CommonResponse(true, "로그인이 성공적으로 완료되었습니다", response);
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(commonResponse);
    }
    
    @PostMapping("/api-guest/auth/refresh")
    @Operation(
        summary = "토큰 갱신", 
        description = "리프레시 토큰으로 액세스 토큰을 갱신합니다. (비로그인 접근 가능)\n\n" +
                     "**토큰 반환**:\n" +
                     "- 새로운 accessToken이 HttpOnly 쿠키로 반환됩니다"
    )
    public ResponseEntity<CommonResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request, @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String refreshToken)
    {
        String valueRefreshToken = null;
        if(refreshToken == null || refreshToken.isEmpty())
        {
            valueRefreshToken = request.refreshToken();
        }
        else
        {
            valueRefreshToken = refreshToken;
        }   


        log.info("토큰 갱신 API 호출");
        LoginResponse response = authService.refreshToken(valueRefreshToken);
        
        // 새로운 Access Token을 HttpOnly 쿠키로 반환
        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, response.getAccessToken())
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .maxAge(accessTokenExpirationSeconds)  // 환경변수 (초 단위)
                .sameSite("Lax")       // CSRF 방지
                .build();
        
        CommonResponse commonResponse = new CommonResponse(true, "토큰이 성공적으로 갱신되었습니다", response);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(commonResponse);
    }
    
    @PostMapping("/api/auth/admin-login")
    @Operation(
        summary = "관리자 로그인",
        description = "환경설정(app.admin.username, app.admin.password)에 설정된 ID/비밀번호로 로그인합니다.\n\n" +
                     "**환경변수**: ADMIN_USERNAME, ADMIN_PASSWORD (application.properties 기본값: admin, admin123!)"
    )
    public ResponseEntity<CommonResponse> adminLogin(@RequestBody Map<String, String> body)
    {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null)
        {
            return ResponseEntity.badRequest().body(new CommonResponse(false, "username, password 필수", null));
        }
        log.info("관리자 로그인 API 호출: username={}", username);
        LoginResponse response = authService.adminLogin(username, password);
        return ResponseEntity.ok(new CommonResponse(true, "관리자 로그인 성공", Map.of("token", response.getAccessToken(), "userId", response.getUserId())));
    }

    @GetMapping("/api/auth/validate")
    @Operation(summary = "토큰 검증", description = "토큰의 유효성을 검증합니다. (비로그인 접근 가능)")
    public ResponseEntity<CommonResponse> validateToken(
            @Parameter(description = "검증할 토큰", required = true)
            @RequestParam String token) {
        log.info("토큰 검증 API 호출");
        TokenValidationResponse response = authService.validateToken(token);
        
        CommonResponse commonResponse = new CommonResponse(true, "토큰 검증이 완료되었습니다", response);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @GetMapping("/api/auth/check-login")
    @Operation(
        summary = "로그인 상태 확인", 
        description = "현재 로그인되어 있는지 확인합니다. (토큰 + 사용자 상태 확인)\n\n" +
                     "**확인 항목**:\n" +
                     "1. 토큰 유효성 (ACTIVE 상태, 만료 여부)\n" +
                     "2. 사용자 존재 여부\n" +
                     "3. 사용자 탈퇴 여부\n\n" +
                     "**응답**:\n" +
                     "- `loggedIn: true`: 로그인 중 (정상)\n" +
                     "- `loggedIn: false`: 로그아웃 상태 (토큰 무효, 사용자 탈퇴 등)\n\n" +
                     "**참고**: 쿠키는 자동으로 전송되므로 별도 파라미터 불필요"
    )
    public ResponseEntity<CommonResponse> checkLogin(
            @Parameter(hidden = true)
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        log.info("로그인 상태 확인 API 호출");
        
        // 토큰이 없는 경우
        if (accessToken == null || accessToken.isEmpty())
        {
            log.info("로그인 상태 확인 결과: 비로그인 (토큰 없음)");
            LoginStatusResponse loginStatus = LoginStatusResponse.loggedOut("로그인되어 있지 않습니다");
            
            CommonResponse commonResponse = new CommonResponse(
                true, 
                "로그인 상태 확인 완료", 
                loginStatus
            );
            return ResponseEntity.ok(commonResponse);
        }
        
        // 토큰에서 사용자 정보 추출
        Map<String, String> tokenInfo = tokenUtil.decodeToken(accessToken);
        if (tokenInfo == null)
        {
            log.info("로그인 상태 확인 결과: 비로그인 (토큰 디코딩 실패)");
            LoginStatusResponse loginStatus = LoginStatusResponse.loggedOut("유효하지 않은 토큰입니다");
            
            CommonResponse commonResponse = new CommonResponse(
                true, 
                "로그인 상태 확인 완료", 
                loginStatus
            );
            return ResponseEntity.ok(commonResponse);
        }
        
        
        // 로그인 상태 확인 (DB 기반)
        LoginStatusResponse loginStatus = authService.checkLoginStatusToken(accessToken);
        
        log.info("로그인 상태 확인 결과: loggedIn={}, userId={}", 
                loginStatus.isLoggedIn(), loginStatus.getUserId());
        
        CommonResponse commonResponse = new CommonResponse(
            true, 
            "로그인 상태 확인 완료", 
            loginStatus
        );
        
        return ResponseEntity.ok(commonResponse);
    }


    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃", 
        description = "로그아웃하여 토큰을 무효화합니다. (로그인 필요)\n\n" +
                     "**처리 내용**:\n" +
                     "1. 사용자의 모든 활성 토큰 만료 처리\n" +
                     "2. Clear HttpOnly cookies (access/refresh token cookie names from config)\n\n" +
                     "**반환 정보**:\n" +
                     "- message: 로그아웃 완료 메시지\n" +
                     "- revokedTokenCount: 만료 처리된 토큰 개수"
    )
        public ResponseEntity<CommonResponse> logout(@RequestAttribute("userId") String userId)
    {
        log.info("로그아웃 API 호출 (로그인 필요): userId={}", userId);
        
        // LogoutRequest 생성
        LogoutRequest request = new LogoutRequest(userId);
        
        // 로그아웃 처리 (모든 활성 토큰 만료)
        LogoutResponse response = authService.logout(request);
        
        // HttpOnly 쿠키 삭제 (토큰 기반)
        ResponseCookie deleteAccessToken = ResponseCookie.from(accessTokenCookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .build();
        
        ResponseCookie deleteRefreshToken = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .build();
        
        log.info("로그아웃 완료: userId={}, revokedTokenCount={}", userId, response.getRevokedTokenCount());
        
        CommonResponse commonResponse = new CommonResponse(
            true, 
            "로그아웃이 성공적으로 완료되었습니다", 
            response
        );
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString())
                .body(commonResponse);
    }
    
}
