package com.cami.cami_springboot.api.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 설정 속성
 * 
 * 역할:
 * - IDE 자동완성 지원 (application.properties 작성 시)
 * - 설정 검증 (스프링 부트 시작 시)
 * - 구조 문서화 (사용자 정의 설정 계층 구조)
 * 
 * 중요 규칙:
 * - 이 클래스를 코드에서 직접 주입하지 말 것! (금지)
 * - 실제 사용은 @Value 어노테이션으로!
 * - 타입과 구조만 정의 (값 설정 금지)
 * 
 * 예시:
 * @Value("${app.auth.token.access.expiration-seconds}")
 * private int accessTokenExpirationSeconds;
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties
{
    private Auth auth = new Auth();
    private Admin admin = new Admin();
    private Kakao kakao = new Kakao();
    private Frontend frontend = new Frontend();
    private Supabase supabase = new Supabase();
    private Datasource datasource = new Datasource();
    
    @Data
    public static class Auth
    {
        private Token token = new Token();
        
        @Data
        public static class Token
        {
            private Access access = new Access();
            private Refresh refresh = new Refresh();
            private Cookie cookie = new Cookie();
            private boolean secure;  // 값 설정 금지, 타입만
            private String domain;  // 값 설정 금지, 타입만

            @Data
            public static class Cookie
            {
                /** Access token cookie name (범용: accessToken 등 플랫폼별 설정) */
                private String accessName;
                /** Refresh token cookie name (범용: refreshToken 등 플랫폼별 설정) */
                private String refreshName;
            }
            
            @Data
            public static class Access
            {
                private int expirationSeconds;  // 값 설정 금지, 타입만
            }
            
            @Data
            public static class Refresh
            {
                private int expirationSeconds;  // 값 설정 금지, 타입만
            }
        }
    }
    
    @Data
    public static class Admin
    {
        /** 관리자 로그인 ID (환경변수 설정) */
        private String username;
        /** 관리자 로그인 비밀번호 (환경변수 설정) */
        private String password;
    }

    @Data
    public static class Kakao
    {
        private String clientId;  // 값 설정 금지, 타입만
        private String clientSecret;  // 값 설정 금지, 타입만
        private String redirectUri;  // 값 설정 금지, 타입만
        private String tokenUrl;  // 값 설정 금지, 타입만
        private String userInfoUrl;  // 값 설정 금지, 타입만
    }
    
    @Data
    public static class Frontend
    {
        private String url;  // 값 설정 금지, 타입만
    }
    
    @Data
    public static class Supabase
    {
        private String url;  // 값 설정 금지, 타입만
        private String anonKey;  // 값 설정 금지, 타입만
        private Storage storage = new Storage();
        
        @Data
        public static class Storage
        {
            private String bucketName;  // 값 설정 금지, 타입만
            private long maxFileSize;  // 값 설정 금지, 타입만
            private int signedUrlExpiresInSeconds;  // 값 설정 금지, 타입만
        }
    }
    
    @Data
    public static class Datasource
    {
        private String active;  // rds 또는 supabase
        private DatasourceConfig supabase = new DatasourceConfig();
        private DatasourceConfig rds = new DatasourceConfig();
        
        @Data
        public static class DatasourceConfig
        {
            private String driver;  // 값 설정 금지, 타입만
            private String url;  // 값 설정 금지, 타입만
            private String username;  // 값 설정 금지, 타입만
            private String password;  // 값 설정 금지, 타입만
            private String dialect;  // 값 설정 금지, 타입만
        }
    }
}

