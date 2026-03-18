package com.culwonder.leeds_profile_springboot_core.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * 
 * SpringDoc 2.7.0+ 사용
 * - displayName 기준 알파벳 정렬 (숫자 접두사 필수)
 * - 모듈별 그룹화 (User, Auth, Image)
 * - 권한별 분리 (게스트, 로그인, 비즈니스, 관리자)
 */
@Configuration
public class SwaggerConfig
{

    @Bean
    public OpenAPI openAPI()
    {
        return new OpenAPI()
            .info(new Info()
                .title("Platform API")
                .description("Platform Spring Boot API Documentation\n\n" +
                           "**📥 API 명세 파일**:\n" +
                           "- JSON: [/api-docs/00-all](/api-docs/00-all) (우클릭 → 다른 이름으로 저장)\n" +
                           "- YAML: [/api-docs.yaml](/api-docs.yaml) (우클릭 → 다른 이름으로 저장)\n" +
                           "- 터미널: `curl -o openapi.json http://localhost:8082/api-docs/00-all`")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Culwonder Team")))
            .servers(List.of(
                new Server()
                    .url("/")
                    .description("Current Server"),
                new Server()
                    .url("https://api.platform.store")
                    .description("Production Server"),
                new Server()
                    .url("http://localhost:8082")
                    .description("Local Development Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 토큰을 입력하세요. 예: Bearer valid-token-123")));
    }

    // ========================================
    // 전체 API
    // ========================================
    
    /**
     * 전체 API 그룹 (기본) - 맨 위 배치
     * displayName에 숫자 접두사 필수 (SpringDoc 2.7.0+)
     */
    @Bean
    public GroupedOpenApi allApi()
    {
        return GroupedOpenApi.builder()
                .group("00-all")
                .displayName("00. 전체 API")
                .pathsToMatch("/**")
                .build();
    }

    // ========================================
    // 공통 기능
    // ========================================
    
    /**
     * 공통 API 그룹
     */
    @Bean
    public GroupedOpenApi commonApi()
    {
        return GroupedOpenApi.builder()
                .group("00a-common")
                .displayName("00a. 공통 기능")
                .pathsToMatch("/api/common/**")
                .build();
    }

    // ========================================
    // User 도메인 API (게스트 → 로그인 → 비즈니스 → 관리자)
    // ========================================
    
    /**
     * User 게스트 API 그룹 (비로그인 접근 가능)
     * 경로: /api/users/** OR /api-guest/users/**
     * 
     * 규칙: 게스트 API는 두 경로 모두 포함 필수
     */
    @Bean
    public GroupedOpenApi userGuestApi()
    {
        return GroupedOpenApi.builder()
                .group("01-user-guest")
                .displayName("01. User 게스트 API")
                .pathsToMatch("/api/users/**", "/api-guest/users/**")
                .build();
    }

    /**
     * User 로그인 API 그룹 (로그인 필요)
     */
    @Bean
    public GroupedOpenApi userLoginedApi()
    {
        return GroupedOpenApi.builder()
                .group("02-user-logined")
                .displayName("02. User 로그인 API")
                .pathsToMatch("/api-logined/users/**")
                .build();
    }

    /**
     * User 비즈니스 API 그룹 (BUSINESS 권한 필요)
     */
    @Bean
    public GroupedOpenApi userBusinessApi()
    {
        return GroupedOpenApi.builder()
                .group("03-user-business")
                .displayName("03. User 비즈니스 API (BUSINESS 권한)")
                .pathsToMatch("/api-business/users/**")
                .build();
    }

    /**
     * User 관리자 API 그룹 (ADMIN 권한 필요)
     */
    @Bean
    public GroupedOpenApi userAdminApi()
    {
        return GroupedOpenApi.builder()
                .group("04-user-admin")
                .displayName("04. User 관리자 API (ADMIN 권한)")
                .pathsToMatch("/api-admin/users/**")
                .build();
    }

    // ========================================
    // Auth 도메인 API (게스트 → 로그인 → 비즈니스 → 관리자)
    // ========================================
    
    /**
     * Auth 게스트 API 그룹 (비로그인 접근 가능)
     * 경로: /api/auth/** OR /api-guest/auth/**
     * 
     * 규칙: 게스트 API는 두 경로 모두 포함 필수
     */
    @Bean
    public GroupedOpenApi authGuestApi()
    {
        return GroupedOpenApi.builder()
                .group("05-auth-guest")
                .displayName("05. Auth 게스트 API")
                .pathsToMatch("/api/auth/**", "/api-guest/auth/**")
                .build();
    }

    /**
     * Auth 로그인 API 그룹 (로그인 필요)
     */
    @Bean
    public GroupedOpenApi authLoginedApi()
    {
        return GroupedOpenApi.builder()
                .group("06-auth-logined")
                .displayName("06. Auth 로그인 API")
                .pathsToMatch("/api-logined/auth/**")
                .build();
    }

    /**
     * Auth 비즈니스 API 그룹 (BUSINESS 권한 필요)
     */
    @Bean
    public GroupedOpenApi authBusinessApi()
    {
        return GroupedOpenApi.builder()
                .group("07-auth-business")
                .displayName("07. Auth 비즈니스 API (BUSINESS 권한)")
                .pathsToMatch("/api-business/auth/**")
                .build();
    }

    /**
     * Auth 관리자 API 그룹 (ADMIN 권한 필요)
     */
    @Bean
    public GroupedOpenApi authAdminApi()
    {
        return GroupedOpenApi.builder()
                .group("08-auth-admin")
                .displayName("08. Auth 관리자 API (ADMIN 권한)")
                .pathsToMatch("/api-admin/auth/**")
                .build();
    }

    // ========================================
    // Image 도메인 API (게스트 → 로그인)
    // ========================================
    
    /**
     * Image 게스트 API 그룹 (비로그인 접근 가능)
     * 경로: /api/images/** OR /api-guest/images/**
     * 
     * 규칙: 게스트 API는 두 경로 모두 포함 필수
     */
    @Bean
    public GroupedOpenApi imageGuestApi()
    {
        return GroupedOpenApi.builder()
                .group("09-image-guest")
                .displayName("09. Image 게스트 API")
                .pathsToMatch("/api/images/**", "/api-guest/images/**")
                .build();
    }

    /**
     * Image 로그인 API 그룹 (로그인 필요)
     */
    @Bean
    public GroupedOpenApi imageLoginedApi()
    {
        return GroupedOpenApi.builder()
                .group("10-image-logined")
                .displayName("10. Image 로그인 API")
                .pathsToMatch("/api-logined/images/**")
                .build();
    }

    // ========================================
    // Study 도메인 API (게스트 → 로그인 → 비즈니스 → 관리자)
    // ========================================
    
    /**
     * Study 게스트 API 그룹 (비로그인 접근 가능)
     * 경로: /api/studies/** OR /api-guest/studies/**
     * 
     * 규칙: 게스트 API는 두 경로 모두 포함 필수
     */
    @Bean
    public GroupedOpenApi studyGuestApi()
    {
        return GroupedOpenApi.builder()
                .group("11-study-guest")
                .displayName("11. Study 게스트 API")
                .pathsToMatch("/api/studies/**", "/api-guest/studies/**")
                .build();
    }

    /**
     * Study 로그인 API 그룹 (로그인 필요)
     */
    @Bean
    public GroupedOpenApi studyLoginedApi()
    {
        return GroupedOpenApi.builder()
                .group("12-study-logined")
                .displayName("12. Study 로그인 API")
                .pathsToMatch("/api-logined/studies/**")
                .build();
    }

    /**
     * Study 비즈니스 API 그룹 (BUSINESS 권한 필요)
     */
    @Bean
    public GroupedOpenApi studyBusinessApi()
    {
        return GroupedOpenApi.builder()
                .group("13-study-business")
                .displayName("13. Study 비즈니스 API (BUSINESS 권한)")
                .pathsToMatch("/api-business/studies/**")
                .build();
    }

    /**
     * Study 관리자 API 그룹 (ADMIN 권한 필요)
     */
    @Bean
    public GroupedOpenApi studyAdminApi()
    {
        return GroupedOpenApi.builder()
                .group("14-study-admin")
                .displayName("14. Study 관리자 API (ADMIN 권한)")
                .pathsToMatch("/api-admin/studies/**")
                .build();
    }
}

