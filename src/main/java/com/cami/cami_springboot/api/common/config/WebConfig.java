package com.cami.cami_springboot.api.common.config;

import com.cami.cami_springboot.api.common.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * 웹 MVC 설정
 * 
 * 기능:
 * - 인터셉터 등록 (권한 체크)
 * - 루트 경로 리다이렉트 (Swagger UI)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer
{
    
    private final AuthInterceptor authInterceptor;
    
    /**
     * 인터셉터 등록
     * 
     * 권한별 경로:
     * - /api/, /api-guest/**: 게스트 허용 (비로그인 접근)
     * - /api-logined/**: 로그인 필요 (권한 무관)
     * - /api-business/**: BUSINESS 권한 필요
     * - /api-admin/**: ADMIN 권한 필요
     */
    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry)
    {
        registry.addInterceptor(Objects.requireNonNull(authInterceptor))
                .addPathPatterns(
                    "/api-guest/**",         // 게스트 허용
                    "/api-logined/**",       // 로그인 필요
                    "/api-business/**",      // BUSINESS 권한 필요
                    "/api-admin/**",         // ADMIN 권한 필요
                    "/api/user/**",          // 로그인 필요
                    "/api/cart/**",          // 로그인 필요
                    "/api/orders/**",        // 로그인 필요
                    "/api/partner/**",       // 파트너 권한 필요
                    "/api/admin/**",         // 관리자 권한 필요
                    "/api/inquiry/my"        // 로그인 필요
                )
                .excludePathPatterns(
                    "/swagger-ui/**",        // Swagger UI 제외
                    "/api-docs/**",          // API 문서 제외
                    "/actuator/**"           // Actuator 제외
                );
    }
    
    /**
     * 루트 경로 리다이렉트 설정
     * 
     * - / → /swagger-ui/index.html
     * - /swagger-ui → /swagger-ui/index.html
     */
    @Override
    public void addViewControllers(@org.springframework.lang.NonNull ViewControllerRegistry registry)
    {
        // 루트 경로(/) 접속 시 Swagger UI로 리다이렉트
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
        
        // /swagger-ui 접속 시 /swagger-ui/index.html로 리다이렉트
        registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
    }
    
    /**
     * CORS 설정
     * 
     * 프론트엔드에서 백엔드 API 호출 시 CORS 오류 방지
     */
    @Override
    public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry)
    {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",    // React 개발 서버
                    "http://localhost:3001",    // 추가 프론트엔드 포트
                    "http://127.0.0.1:3000",   // localhost 대체
                    "http://127.0.0.1:3001",   // localhost 대체
                    "https://platform.store",    // 프로덕션 도메인
                    "https://www.platform.store" // 프로덕션 도메인 (www)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)         // 쿠키 전송 허용 (HttpOnly 쿠키용)
                .maxAge(3600);                 // preflight 요청 캐시 시간 (1시간)
    }
}

