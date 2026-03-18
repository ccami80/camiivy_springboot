package com.cami.cami_springboot.api.auth.service;

import com.cami.cami_springboot.api.auth.response.GoogleTokenResponse;
import com.cami.cami_springboot.api.auth.response.GoogleUserInfoResponse;
import com.cami.cami_springboot.api.auth.response.KakaoTokenResponse;
import com.cami.cami_springboot.api.auth.response.KakaoUserInfoResponse;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.user.response.UserProviderResponse;
import com.cami.cami_springboot.api.user.service.UserProviderService;
import com.cami.cami_springboot.api.user.request.UsersAccountCreateRequest;
import com.cami.cami_springboot.api.user.entity.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

/**
 * 타 모듈의 서비스를 호출하는 Auth 서비스
 * 
 * 역할:
 * - AuthService에서 타 모듈 서비스가 필요한 경우 사용
 * - 무조건 AuthCallService를 통해서만 타 모듈 호출
 * - AuthProviderService 호출 금지
 */
@Slf4j
@Service
public class AuthCallService
{
    
    private final UserProviderService userProviderService;
    private final RestTemplate restTemplate;
    
    @Value("${app.kakao.client-id}")
    private String clientId;
    
    @Value("${app.kakao.client-secret}")
    private String clientSecret;
    
    @Value("${app.kakao.token-url}")
    private String tokenUrl;
    
    @Value("${app.kakao.user-info-url}")
    private String userInfoUrl;
    
    @Value("${app.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${app.google.client-id}")
    private String googleClientId;

    @Value("${app.google.client-secret}")
    private String googleClientSecret;

    @Value("${app.google.token-url}")
    private String googleTokenUrl;

    @Value("${app.google.user-info-url}")
    private String googleUserInfoUrl;

    @Value("${app.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.profiles.active:dev}")
    private String environment;
    
    /**
     * 생성자 주입
     * @Lazy: 순환 참조 방지 (AuthService ↔ UserService)
     */
    public AuthCallService(@Lazy UserProviderService userProviderService)
    {
        this.userProviderService = userProviderService;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * User 모듈에 계정 존재 여부 확인 요청 (소셜 정보 기반)
     * 
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return 계정 존재 여부 및 사용자 정보
     */
    public UserProviderResponse checkAccountExists(String socialProvider, String socialId)
    {
        log.info("[AuthCallService] User 모듈 호출: 계정 존재 확인 - socialProvider={}, socialId={}", 
                socialProvider, socialId);
        
        try
        {
            UserProviderResponse response = userProviderService.checkAccountExists(
                socialProvider, socialId
            );
            
            log.info("[AuthCallService] User 모듈 응답: exists={}, userId={}", 
                    response.exists(), response.userId());
            
            return response;
        }
        catch (Exception e)
        {
            log.error("[AuthCallService] User 모듈 호출 실패: {}", e.getMessage());
            throw new RuntimeException("User 모듈 호출 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * User 모듈에 사용자 존재 여부 확인 요청 (userId 기반)
     * 
     * @param userId 사용자 ID
     * @return 계정 존재 여부 및 사용자 정보
     */
    public UserProviderResponse checkUserExists(String userId)
    {
        log.info("[AuthCallService] User 모듈 호출: 사용자 존재 확인 - userId={}", userId);
        
        try
        {
            UserProviderResponse response = userProviderService.checkUserExists(userId);
            
            log.info("[AuthCallService] User 모듈 응답: exists={}, userId={}", 
                    response.exists(), response.userId());
            
            return response;
        }
        catch (Exception e)
        {
            log.error("[AuthCallService] User 모듈 호출 실패: {}", e.getMessage());
            throw new RuntimeException("User 모듈 호출 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * 카카오 인가 코드로 액세스 토큰 발급
     * 
     * @param authorizationCode 카카오 인가 코드
     * @return 카카오 토큰 응답
     */
    public KakaoTokenResponse getKakaoAccessToken(String authorizationCode)
    {
        log.info("[AuthCallService] 카카오 액세스 토큰 발급 요청: authorizationCode={}", authorizationCode);
        
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("code", authorizationCode);
            
            log.info("[AuthCallService] 카카오 토큰 요청 파라미터: clientId={}, redirectUri={}, code={}", 
                clientId, redirectUri,
                authorizationCode.substring(0, Math.min(20, authorizationCode.length())) + "...");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                Objects.requireNonNull(tokenUrl), Objects.requireNonNull(HttpMethod.POST), request, KakaoTokenResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            {
                log.info("[AuthCallService] 카카오 액세스 토큰 발급 성공");
                return response.getBody();
            }
            else
            {
                log.error("[AuthCallService] 카카오 액세스 토큰 발급 실패: status={}", response.getStatusCode());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e)
        {
            log.error("[AuthCallService] 카카오 액세스 토큰 발급 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 카카오 액세스 토큰으로 사용자 정보 조회
     * 
     * @param accessToken 카카오 액세스 토큰
     * @return 카카오 사용자 정보 응답
     */
    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken)
    {
        log.info("[AuthCallService] 카카오 사용자 정보 조회 요청");
        
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = UriComponentsBuilder.fromUriString(Objects.requireNonNull(userInfoUrl))
                .queryParam("secure_resource", "true")
                .toUriString();
            
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                url, Objects.requireNonNull(HttpMethod.GET), request, KakaoUserInfoResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            {
                KakaoUserInfoResponse userInfo = response.getBody();
                if (userInfo != null)
                {
                    log.info("[AuthCallService] 카카오 사용자 정보 조회 성공: id={}", userInfo.getId());
                    return userInfo;
                }
            }
            
            log.error("[AuthCallService] 카카오 사용자 정보 조회 실패: status={}", response.getStatusCode());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e)
        {
            log.error("[AuthCallService] 카카오 사용자 정보 조회 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Google 인가 코드로 액세스 토큰 발급
     */
    public GoogleTokenResponse getGoogleAccessToken(String authorizationCode) {
        log.info("[AuthCallService] Google 액세스 토큰 발급 요청");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", googleClientId);
            params.add("client_secret", googleClientSecret);
            params.add("redirect_uri", googleRedirectUri);
            params.add("code", authorizationCode);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                Objects.requireNonNull(googleTokenUrl), HttpMethod.POST, request, GoogleTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("[AuthCallService] Google 액세스 토큰 발급 성공");
                return response.getBody();
            }
            log.error("[AuthCallService] Google 액세스 토큰 발급 실패: status={}", response.getStatusCode());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[AuthCallService] Google 액세스 토큰 발급 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Google 액세스 토큰으로 사용자 정보 조회
     */
    public GoogleUserInfoResponse getGoogleUserInfo(String accessToken) {
        log.info("[AuthCallService] Google 사용자 정보 조회 요청");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(
                Objects.requireNonNull(googleUserInfoUrl), HttpMethod.GET, request, GoogleUserInfoResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("[AuthCallService] Google 사용자 정보 조회 성공: id={}", response.getBody().getId());
                return response.getBody();
            }
            log.error("[AuthCallService] Google 사용자 정보 조회 실패: status={}", response.getStatusCode());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[AuthCallService] Google 사용자 정보 조회 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * User 모듈에 사용자 계정 생성 요청
     * 
     * @param request 사용자 계정 생성 요청
     * @return 생성된 사용자 계정
     */
    public UserAccount createUserAccount(UsersAccountCreateRequest request)
    {
        log.info("[AuthCallService] User 모듈 호출: 사용자 계정 생성 - socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        
        try
        {
            UserAccount userAccount = userProviderService.createUserAccount(request);
            
            log.info("[AuthCallService] User 모듈 응답: userId={}, socialProvider={}, socialId={}", 
                    userAccount.getUserId(), userAccount.getSocialProvider(), userAccount.getSocialId());
            
            return userAccount;
        }
        catch (Exception e)
        {
            log.error("[AuthCallService] User 모듈 호출 실패: {}", e.getMessage());
            throw new RuntimeException("User 모듈 호출 중 오류가 발생했습니다", e);
        }
    }
}
