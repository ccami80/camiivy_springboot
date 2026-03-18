package com.cami.cami_springboot.api.common.interceptor;

import com.cami.cami_springboot.api.auth.code.TokenType;
import com.cami.cami_springboot.api.auth.response.LoginResponse;
import com.cami.cami_springboot.api.auth.response.LoginStatusResponse;
import com.cami.cami_springboot.api.auth.service.AuthService;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.util.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Authentication and authorization interceptor.
 * Validates JWT and checks path-based access (guest / logined / business / admin).
 *
 * Path rules:
 * - /api/, /api-guest/**: guest allowed
 * - /api-logined/**: login required
 * - /api-business/**: BUSINESS role required
 * - /api-admin/**: ADMIN role required
 *
 * Exception: throw CustomException; GlobalExceptionHandler handles response.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor
{

    private final AuthService authService;
    private final TokenUtil tokenUtil;

    @Value("${app.auth.token.access.expiration-seconds}")
    private int accessTokenExpirationSeconds;

    @Value("${app.auth.token.secure:false}")
    private boolean secureCookie;

    @Value("${app.auth.token.domain:}")
    private String cookieDomain;

    @Value("${app.auth.token.cookie.access-name:accessToken}")
    private String accessTokenCookieName;

    @Value("${app.auth.token.cookie.refresh-name:refreshToken}")
    private String refreshTokenCookieName;

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, 
                           @org.springframework.lang.NonNull HttpServletResponse response, 
                           @org.springframework.lang.NonNull Object handler) throws Exception 
    {

        String accessToken = extractToken(request, TokenType.ACCESS);
        String refreshToken = extractToken(request, TokenType.REFRESH);
        request.setAttribute("accessToken", accessToken);
        request.setAttribute("refreshToken", refreshToken);
        boolean isLogined = false;

        
        log.debug("AuthInterceptor - Request URI: {}", request.getRequestURI());
        log.debug("AuthInterceptor - Request Method: {}", request.getMethod());
        
        // OPTIONS 요청은 통과 (CORS)
        if ("OPTIONS".equals(request.getMethod())) 
        {
            log.debug("AuthInterceptor - OPTIONS request, allowing");
            return true;
        }
        
        // Swagger 관련 경로는 통과
        String requestURI = request.getRequestURI();
        if (isSwaggerPath(requestURI)) 
        {
            log.debug("AuthInterceptor - Swagger path, allowing: {}", requestURI);
            return true;
        }
        
        // 게스트 API 경로는 통과 (/api-guest/)
        if (requestURI.startsWith("/api-guest/"))
        {
            log.debug("AuthInterceptor - Guest path, allowing: {}", requestURI);
            return true;
        }
        
        // 로그인 필요한 /api/ 하위 경로 (Bearer 토큰 필수)
        if (requiresAuth(requestURI, request.getMethod()))
        {
            // 아래에서 isLogined 체크 후 통과 또는 예외
        }
        else if (requestURI.startsWith("/api/"))
        {
            // 공개 API (categories, brands, products, banners, auth 등)
            log.debug("AuthInterceptor - Public API path, allowing: {}", requestURI);
            return true;
        }

        if (accessToken != null && !accessToken.isEmpty()) 
        {    
            // TokenUtil을 통한 토큰 형식 검증
            Map<String, Object> validationResult = tokenUtil.validateToken(accessToken);
            boolean result = (boolean) validationResult.get("result");
            String message = (String) validationResult.get("message");

            if (result) 
            {
                Map<String, String> decodedToken = tokenUtil.decodeToken(accessToken);
                String userId = decodedToken.get("userId");
                String socialProvider = decodedToken.get("socialProvider");
                String socialId = decodedToken.get("socialId");

                request.setAttribute("userId", userId);
                request.setAttribute("socialProvider", socialProvider);
                request.setAttribute("socialId", socialId);

                // 로그인 상태 확인 (AuthService 사용)
                LoginStatusResponse loginStatus = authService.checkLoginStatusToken(accessToken);
                if (loginStatus.isLoggedIn()) 
                {
                    isLogined = true;
                }
                else
                {
                    log.warn("AuthInterceptor - Invalid token for URI: {}, attempting refresh", requestURI);
                    
                    // Access Token이 만료되었거나 유효하지 않으면 Refresh Token으로 재발급 시도
                    if (refreshToken != null && !refreshToken.isEmpty())
                    {
                        try
                        {
                            LoginResponse refreshResponse = authService.refreshToken(refreshToken);
                            String newAccessToken = refreshResponse.getAccessToken();
                            
                            // 새로운 Access Token을 쿠키에 설정
                            setAccessTokenCookie(response, newAccessToken);
                            
                            // request attribute 업데이트
                            request.setAttribute("accessToken", newAccessToken);
                            
                            // 토큰 정보 다시 디코딩하여 attribute 설정
                            Map<String, String> newDecodedToken = tokenUtil.decodeToken(newAccessToken);
                            if (newDecodedToken != null)
                            {
                                request.setAttribute("userId", newDecodedToken.get("userId"));
                                request.setAttribute("socialProvider", newDecodedToken.get("socialProvider"));
                                request.setAttribute("socialId", newDecodedToken.get("socialId"));
                            }
                            
                            isLogined = true;
                            log.info("AuthInterceptor - Access token refreshed successfully for URI: {}", requestURI);
                        }
                        catch (CustomException e)
                        {
                            log.warn("AuthInterceptor - Token refresh failed: {}", e.getMessage());
                            // 재발급 실패 시 예외 그대로 전달
                            throw e;
                        }
                    }
                    else
                    {
                        log.warn("AuthInterceptor - Refresh token not available for URI: {}", requestURI);
                    }
                }
            } 
            // 유효하지 않은 토큰 형식인 경우
            else
            {
                log.warn("AuthInterceptor - Invalid token format: {}", message);
                
                // Refresh Token이 있으면 재발급 시도
                if (refreshToken != null && !refreshToken.isEmpty())
                {
                    try
                    {
                        LoginResponse refreshResponse = authService.refreshToken(refreshToken);
                        String newAccessToken = refreshResponse.getAccessToken();
                        
                        // 새로운 Access Token을 쿠키에 설정
                        setAccessTokenCookie(response, newAccessToken);
                        
                        // request attribute 업데이트
                        request.setAttribute("accessToken", newAccessToken);
                        
                        // 토큰 정보 다시 디코딩하여 attribute 설정
                        Map<String, String> newDecodedToken = tokenUtil.decodeToken(newAccessToken);
                        if (newDecodedToken != null)
                        {
                            request.setAttribute("userId", newDecodedToken.get("userId"));
                            request.setAttribute("socialProvider", newDecodedToken.get("socialProvider"));
                            request.setAttribute("socialId", newDecodedToken.get("socialId"));
                        }
                        
                        isLogined = true;
                        log.info("AuthInterceptor - Access token refreshed successfully (invalid format) for URI: {}", requestURI);
                    }
                    catch (CustomException e)
                    {
                        log.warn("AuthInterceptor - Token refresh failed: {}", e.getMessage());
                        // 재발급 실패 시 예외 그대로 전달
                        throw e;
                    }
                }
            }
        }
        else
        {
            // Access Token이 없는 경우 Refresh Token으로 재발급 시도
            if (refreshToken != null && !refreshToken.isEmpty())
            {
                try
                {
                    LoginResponse refreshResponse = authService.refreshToken(refreshToken);
                    String newAccessToken = refreshResponse.getAccessToken();
                    
                    // 새로운 Access Token을 쿠키에 설정
                    setAccessTokenCookie(response, newAccessToken);
                    
                    // request attribute 업데이트
                    request.setAttribute("accessToken", newAccessToken);
                    
                    // 토큰 정보 디코딩하여 attribute 설정
                    Map<String, String> newDecodedToken = tokenUtil.decodeToken(newAccessToken);
                    if (newDecodedToken != null)
                    {
                        request.setAttribute("userId", newDecodedToken.get("userId"));
                        request.setAttribute("socialProvider", newDecodedToken.get("socialProvider"));
                        request.setAttribute("socialId", newDecodedToken.get("socialId"));
                    }
                    
                    isLogined = true;
                    log.info("AuthInterceptor - Access token refreshed successfully (no access token) for URI: {}", requestURI);
                }
                catch (CustomException e)
                {
                    log.warn("AuthInterceptor - Token refresh failed: {}", e.getMessage());
                    // 재발급 실패 시 예외 그대로 전달
                    throw e;
                }
            }
        }
        
        if (isLogined) 
        {
            // 관리자 API(/api/admin/)는 userId=admin 또는 socialProvider=ADMIN만 접근 가능
            if (requestURI.startsWith("/api/admin/"))
            {
                String reqUserId = (String) request.getAttribute("userId");
                String reqSocialProvider = (String) request.getAttribute("socialProvider");
                if (!"admin".equals(reqUserId) && !"ADMIN".equals(reqSocialProvider))
                {
                    throw new CustomException(ErrorCode.FORBIDDEN);
                }
            }
        }
        else
        {   
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }
        
        
        log.debug("AuthInterceptor - Request allowed");
        return true;
    }
    
    /**
     * 로그인(인증)이 필요한 API 경로인지 확인
     */
    private boolean requiresAuth(String requestURI, String method)
    {
        if ("POST".equals(method) && "/api/orders".equals(requestURI)) return false; // guest checkout
        if ("GET".equals(method) && requestURI.startsWith("/api/orders/lookup")) return false; // order lookup by number+phone
        return requestURI.startsWith("/api/user/") ||
               requestURI.startsWith("/api/cart") ||
               requestURI.startsWith("/api/orders") ||
               requestURI.startsWith("/api/partner/") ||
               requestURI.startsWith("/api/admin/") ||
               requestURI.equals("/api/inquiry/my") ||
               requestURI.startsWith("/api-logined/") ||
               requestURI.startsWith("/api-business/") ||
               requestURI.startsWith("/api-admin/");
    }

    /**
     * Swagger 관련 경로인지 확인
     */
    private boolean isSwaggerPath(String requestURI) 
    {
        return requestURI.startsWith("/swagger-ui") || 
               requestURI.startsWith("/api-docs") ||
               requestURI.startsWith("/actuator") ||
               requestURI.equals("/") ||
               requestURI.equals("/swagger-ui");
    }
    

    /**
     * Extract token: Authorization header first, then cookie (generic cookie names from config).
     */
    private String extractToken(HttpServletRequest request, TokenType tokenType)
    {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            String name = tokenType == TokenType.ACCESS ? accessTokenCookieName : refreshTokenCookieName;
            for (Cookie cookie : cookies)
            {
                if (name.equals(cookie.getName()))
                {
                    String token = cookie.getValue();
                    if (token != null && !token.isEmpty())
                    {
                        return token;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Set access token in HttpOnly cookie (cookie name from config).
     */
    private void setAccessTokenCookie(HttpServletResponse response, String accessToken)
    {
        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, accessToken)
                .httpOnly(true)        // JavaScript 접근 금지 (XSS 방지)
                .secure(secureCookie)  // 환경변수로 제어 (프로덕션: true, 개발: false)
                .path("/")             // 모든 경로에서 사용 가능
                .maxAge(accessTokenExpirationSeconds)  // 환경변수 (초 단위)
                .domain(cookieDomain.isEmpty() ? null : cookieDomain)
                .sameSite(secureCookie ? "None" : "Lax")  // 크로스 사이트 요청 허용 (Secure=true일 때만 None)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        log.debug("AuthInterceptor - Access token cookie set successfully");
    }
}

