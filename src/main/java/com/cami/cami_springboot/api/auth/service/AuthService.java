package com.cami.cami_springboot.api.auth.service;

import com.cami.cami_springboot.api.auth.entity.Auth;
import com.cami.cami_springboot.api.auth.repository.AuthRepository;
import com.cami.cami_springboot.api.auth.request.*;
import com.cami.cami_springboot.api.auth.response.*;
import com.cami.cami_springboot.api.auth.code.TokenType;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.util.TokenUtil;
import com.cami.cami_springboot.api.user.response.UserProviderResponse;
import com.cami.cami_springboot.api.user.request.UsersAccountCreateRequest;
import com.cami.cami_springboot.api.user.entity.UserAccount;
import com.cami.cami_springboot.api.user.code.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService
{
    
    private final AuthRepository authRepository;
    private final AuthCallService authCallService;
    private final TokenUtil tokenUtil;
    
    @Value("${app.auth.token.access.expiration-seconds}")
    private int accessTokenExpirationSeconds;
    
    @Value("${app.auth.token.refresh.expiration-seconds}")
    private int refreshTokenExpirationSeconds;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;
    
    @Transactional(readOnly = true)
    public AuthDetailResponse searchAuthDetail(AuthSelectRequest request)
    {
        log.info("인증 토큰 상세 조회 요청: ID={}", request.id());
        
        Auth auth = authRepository.findById(Objects.requireNonNull(request.id()))
            .orElseThrow(() -> new IllegalArgumentException("인증 토큰을 찾을 수 없습니다: " + request.id()));
        
        return convertToDetailResponse(auth);
    }
    
    @Transactional(readOnly = true)
    public Page<AuthListResponse> searchAuthList(AuthListSearchRequest request, Pageable pageable)
    {
        log.info("인증 토큰 목록 조회 요청: userId={}, page={}, size={}", 
            request.getUserId(), pageable.getPageNumber(), pageable.getPageSize());
        
        List<AuthListResponse> authList = authRepository.customSearchAuthList(request, pageable);
        long totalCount = authRepository.customSearchAuthCount(request);
        
        return new PageImpl<>(Objects.requireNonNull(authList), pageable, totalCount);
    }
    
    @Transactional
    public AuthDetailResponse updateAuth(AuthUpdateRequest request)
    {
        log.info("인증 토큰 수정 요청: ID={}", request.id());
        
        Auth auth = authRepository.findById(Objects.requireNonNull(request.id()))
            .orElseThrow(() -> new IllegalArgumentException("인증 토큰을 찾을 수 없습니다: " + request.id()));
        
        // 만료 시간 업데이트
        if (request.expiresAt() != null)
        {
            auth = Auth.builder()
                .token(auth.getToken())
                .tokenType(auth.getTokenType())
                .userId(auth.getUserId())
                .socialProvider(auth.getSocialProvider())
                .socialId(auth.getSocialId())
                .expiresAt(request.expiresAt())
                .deviceInfo(request.deviceInfo() != null ? request.deviceInfo() : auth.getDeviceInfo())
                .ipAddress(request.ipAddress() != null ? request.ipAddress() : auth.getIpAddress())
                .userAgent(request.userAgent() != null ? request.userAgent() : auth.getUserAgent())
                .createdId(auth.getCreatedId())
                .updatedId("system")
                .build();
        }
        
        Auth updatedAuth = authRepository.save(Objects.requireNonNull(auth));
        log.info("인증 토큰 수정 완료: ID={}", updatedAuth.getId());
        
        return convertToDetailResponse(updatedAuth);
    }
    
    @Transactional
        public AuthDeleteResponse deleteAuth(AuthDeleteRequest request)
    {
        log.info("인증 토큰 삭제 요청: ID={}", request.id());
        
        Auth auth = authRepository.findById(Objects.requireNonNull(request.id()))
            .orElseThrow(() -> new IllegalArgumentException("인증 토큰을 찾을 수 없습니다: " + request.id()));
        
        // 히스토리 저장 후 삭제 (관리자 강제 로그아웃) - DDD 방식
        auth.addForceSignOutHistory("관리자에 의한 강제 로그아웃");
        authRepository.save(auth);  // cascade로 히스토리 저장
        
        authRepository.delete(auth);
        
        log.info("인증 토큰 삭제 완료: ID={}", auth.getId());
        
        return AuthDeleteResponse.builder()
            .id(auth.getId())
            .message("인증 토큰이 성공적으로 삭제되었습니다")
            .build();
    }

    /**
     * 관리자 로그인 (환경설정 app.admin.username, app.admin.password 사용)
     */
    @Transactional
    public LoginResponse adminLogin(String username, String password)
    {
        log.info("관리자 로그인 요청: username={}", username);
        if (username == null || password == null || !username.equals(adminUsername) || !password.equals(adminPassword))
        {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        String userId = "admin";
        String socialProvider = "ADMIN";
        String socialId = username;
        deleteExistingTokens(userId, TokenType.ACCESS, "관리자 로그인으로 인한 기존 ACCESS 토큰 삭제");
        deleteExistingTokens(userId, TokenType.REFRESH, "관리자 로그인으로 인한 기존 REFRESH 토큰 삭제");
        String accessToken = tokenUtil.generateToken(userId, socialProvider, socialId, "ACCESS");
        LocalDateTime accessTokenExpiresAt = LocalDateTime.now().plusSeconds(accessTokenExpirationSeconds);
        Auth accessAuth = Auth.builder()
            .token(accessToken)
            .tokenType(TokenType.ACCESS)
            .userId(userId)
            .socialProvider(socialProvider)
            .socialId(socialId)
            .expiresAt(accessTokenExpiresAt)
            .deviceInfo(null)
            .ipAddress(null)
            .userAgent(null)
            .createdId("system")
            .build();
        String refreshToken = tokenUtil.generateToken(userId, socialProvider, socialId, "REFRESH");
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds);
        Auth refreshAuth = Auth.builder()
            .token(refreshToken)
            .tokenType(TokenType.REFRESH)
            .userId(userId)
            .socialProvider(socialProvider)
            .socialId(socialId)
            .expiresAt(refreshTokenExpiresAt)
            .deviceInfo(null)
            .ipAddress(null)
            .userAgent(null)
            .createdId("system")
            .build();
        accessAuth.addLoginHistory();
        authRepository.save(Objects.requireNonNull(accessAuth));
        authRepository.save(Objects.requireNonNull(refreshAuth));
        log.info("관리자 로그인 완료: username={}", username);
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(userId)
            .message("관리자 로그인이 성공적으로 완료되었습니다")
            .build();
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request)
    {
        log.info("로그인 요청: socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        
        // 1. User 모듈에 회원가입 여부 확인 (AuthCallService 통해 UserProviderService 호출)
        UserProviderResponse userCheck = authCallService.checkAccountExists(
            request.socialProvider(), request.socialId());
        
        // 2. 회원가입되어 있지 않으면 자동 회원가입 처리
        String userId;
        if (!userCheck.exists())
        {
            log.warn("회원가입되지 않은 계정: socialProvider={}, socialId={}", 
                    request.socialProvider(), request.socialId());
            throw new IllegalArgumentException(
                "회원가입되지 않은 계정입니다. 먼저 회원가입을 진행해주세요. " +
                "socialProvider: " + request.socialProvider() + ", socialId: " + request.socialId()
            );
          
        }
        else
        {
            userId = userCheck.userId();
        }
        
        // 3. 로그인 처리
        String socialProvider = request.socialProvider();
        String socialId = request.socialId();
        log.info("회원가입 확인 완료. 로그인 진행: userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
        
        // 기존 토큰들 히스토리로 이동 후 삭제
        deleteExistingTokens(userId, TokenType.ACCESS, "로그인으로 인한 기존 ACCESS 토큰 삭제");
        deleteExistingTokens(userId, TokenType.REFRESH, "로그인으로 인한 기존 REFRESH 토큰 삭제");
        
        // 액세스 토큰 생성 - userId, socialProvider, socialId 포함 (TokenUtil 사용)
        String accessToken = tokenUtil.generateToken(userId, socialProvider, socialId, "ACCESS");
        LocalDateTime accessTokenExpiresAt = LocalDateTime.now().plusSeconds(accessTokenExpirationSeconds);
        
        Auth accessAuth = Auth.builder()
            .token(accessToken)
            .tokenType(TokenType.ACCESS)
            .userId(userId)
            .socialProvider(socialProvider)
            .socialId(socialId)
            .expiresAt(accessTokenExpiresAt)
            .deviceInfo(request.deviceInfo())
            .ipAddress(request.ipAddress())
            .userAgent(request.userAgent())
            .createdId("system")  // 시스템 생성
            .build();
        
        // 리프레시 토큰 생성 - userId, socialProvider, socialId 포함 (TokenUtil 사용)
        String refreshToken = tokenUtil.generateToken(userId, socialProvider, socialId, "REFRESH");
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds);
        
        Auth refreshAuth = Auth.builder()
            .token(refreshToken)
            .tokenType(TokenType.REFRESH)
            .userId(userId)
            .socialProvider(socialProvider)
            .socialId(socialId)
            .expiresAt(refreshTokenExpiresAt)
            .deviceInfo(request.deviceInfo())
            .ipAddress(request.ipAddress())
            .userAgent(request.userAgent())
            .createdId("system")  // 시스템 생성
            .build();
        
        // 로그인 히스토리 추가 (DDD 방식)
        accessAuth.addLoginHistory();
        
        authRepository.save(Objects.requireNonNull(accessAuth));
        authRepository.save(Objects.requireNonNull(refreshAuth));
        
        log.info("로그인 히스토리 기록 완료: userId={}", userId);
        
        log.info("로그인 완료: userId={}, socialProvider={}, socialId={}", 
                userId, request.socialProvider(), request.socialId());
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(userId)
            .message("로그인이 성공적으로 완료되었습니다")
            .build();
    }


    @Transactional
    public LoginResponse loginSignIn(LoginRequest request)
    {
        log.info("로그인 요청: socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        
        // 1. User 모듈에 회원가입 여부 확인 (AuthCallService 통해 UserProviderService 호출)
        UserProviderResponse userCheck = authCallService.checkAccountExists(
            request.socialProvider(), request.socialId());
        
        // 2. 회원가입되어 있지 않으면 자동 회원가입 처리
        String userId;
        if (!userCheck.exists())
        {
            log.info("회원가입되지 않은 계정 발견. 자동 회원가입 진행: socialProvider={}, socialId={}", 
                    request.socialProvider(), request.socialId());
            
            // 자동 회원가입 처리 (휴대폰 번호는 null로 설정)
            UsersAccountCreateRequest createRequest = new UsersAccountCreateRequest(
                null, // 휴대폰 번호는 null (카카오 로그인에서는 휴대폰 번호 없음)
                SocialProvider.valueOf(request.socialProvider()),
                request.socialId()
            );
            
            UserAccount userAccount = authCallService.createUserAccount(createRequest);
            userId = userAccount.getUserId();
            
            log.info("자동 회원가입 완료: userId={}, socialProvider={}, socialId={}", 
                    userId, request.socialProvider(), request.socialId());
        }
        else
        {
            log.info("기존 회원 로그인: socialProvider={}, socialId={}", request.socialProvider(), request.socialId());
        }
            
        // 3. 로그인 처리 (회원가입 여부와 관계없이 토큰 발급)
        LoginResponse loginResponse = login(request);
        
        return loginResponse;
    }
    
    @Transactional
    public KakaoUserInfoResponse kakaoLogin(String authorizationCode)
    {
        log.info("카카오 로그인 요청: authorizationCode={}", authorizationCode);
        
        // 1. 카카오에서 액세스 토큰 발급
        KakaoTokenResponse kakaoTokenResponse = authCallService.getKakaoAccessToken(authorizationCode);
        
        // 2. 카카오 사용자 정보 조회
        KakaoUserInfoResponse kakaoUserInfo = authCallService.getKakaoUserInfo(kakaoTokenResponse.getAccessToken());
        
        return kakaoUserInfo;
    }

    @Transactional
    public GoogleUserInfoResponse googleLogin(String authorizationCode)
    {
        log.info("Google 로그인 요청");
        GoogleTokenResponse googleTokenResponse = authCallService.getGoogleAccessToken(authorizationCode);
        return authCallService.getGoogleUserInfo(googleTokenResponse.getAccessToken());
    }
    
    @Transactional
        public LoginResponse refreshToken(String valueRefreshToken)
    {
        log.info("토큰 갱신 요청");
        
        Auth refreshAuth = authRepository.findFirstByTokenAndTokenTypeOrderByCreatedAtDesc(
            valueRefreshToken, TokenType.REFRESH)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        
        if (refreshAuth.isExpired())
        {
            log.warn("만료된 Refresh Token 사용 시도: userId={}, expiresAt={}", 
                refreshAuth.getUserId(), refreshAuth.getExpiresAt());
            
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        String userId = refreshAuth.getUserId();
        
        // 기존 액세스 토큰 히스토리로 이동 후 삭제
        deleteExistingTokens(userId, TokenType.ACCESS, "토큰 갱신으로 인한 기존 ACCESS 토큰 교체를 위한 삭제");
        
        // 새 액세스 토큰 생성 - 기존 Auth의 소셜 정보 사용 (TokenUtil 사용)
        String newAccessToken = tokenUtil.generateToken(userId, refreshAuth.getSocialProvider(), refreshAuth.getSocialId(), "ACCESS");
        LocalDateTime accessTokenExpiresAt = LocalDateTime.now().plusSeconds(accessTokenExpirationSeconds);
        
        Auth newAccessAuth = Auth.builder()
            .token(newAccessToken)
            .tokenType(TokenType.ACCESS)
            .userId(userId)
            .socialProvider(refreshAuth.getSocialProvider())
            .socialId(refreshAuth.getSocialId())
            .expiresAt(accessTokenExpiresAt)
            .deviceInfo(refreshAuth.getDeviceInfo())
            .ipAddress(refreshAuth.getIpAddress())
            .userAgent(refreshAuth.getUserAgent())
            .createdId("system")  // 시스템 생성
            .build();
        
        // 토큰 갱신 히스토리 추가 (DDD 방식)
        newAccessAuth.addRefreshHistory();
        
        authRepository.save(newAccessAuth);
        
        log.info("토큰 갱신 히스토리 기록 완료: userId={}", userId);
        
        log.info("토큰 갱신 완료: userId={}", userId);
        
        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(valueRefreshToken)
            .tokenType("Bearer")
            .userId(userId)
            .message("토큰이 성공적으로 갱신되었습니다")
            .build();
    }
    
    @Transactional
        public LogoutResponse logout(LogoutRequest request)
    {
        log.info("로그아웃 요청: userId={}", request.userId());
        
        // 사용자의 모든 토큰 조회
        List<Auth> userTokens = authRepository.findByUserId(request.userId());
        int deletedCount = 0;
        
        // 히스토리 저장 후 삭제 (DDD 방식)
        for (Auth auth : userTokens)
        {
            auth.addLogoutHistory("사용자 로그아웃");
            authRepository.save(auth);  // cascade로 히스토리 저장
            deletedCount++;
        }
        
        // 토큰 삭제
        authRepository.deleteAll(Objects.requireNonNull(userTokens));
        
        log.info("로그아웃 히스토리 기록 완료: userId={}, deletedCount={}", request.userId(), deletedCount);
        
        log.info("로그아웃 완료: userId={}, deletedCount={}", request.userId(), deletedCount);
        
        return LogoutResponse.builder()
            .message("로그아웃이 성공적으로 완료되었습니다")
            .revokedTokenCount((long) deletedCount)
            .build();
    }
    
    /**
     * 토큰 형식 검증 (디코딩만, DB 검증 없음)
     */
        public TokenValidationResponse validateToken(String token)
    {
        log.debug("토큰 형식 검증 요청");
        
        // TokenUtil을 통한 토큰 형식 검증
        Map<String, Object> validationResult = tokenUtil.validateToken(token);

        boolean result = (boolean) validationResult.get("result");
        String message = (String) validationResult.get("message");
        
        if (!result)
        {
            log.debug("토큰 형식 검증 실패: {}", message);
            return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
        }

        Map<String, String> decodedToken = tokenUtil.decodeToken(token);

        

        return TokenValidationResponse.builder()
            .valid(true)
            .userId(decodedToken.get("userId"))
            .socialProvider(decodedToken.get("socialProvider"))
            .socialId(decodedToken.get("socialId"))
            .createdAt(decodedToken.get("createdAt"))
            .token(token)
            .message(message)
            .build();
    }
    
    /**
     * 로그인 상태 확인 (토큰 기반)
     * DB에 토큰이 존재하면 활성 토큰으로 판단 (auth_module에는 활성 토큰만 저장)
     */
    @Transactional(readOnly = true)
        public LoginStatusResponse checkLoginStatusToken(String token)
    {
        log.info("로그인 상태 확인 요청 (토큰 기반)");
        
        // 1. 토큰 형식 검증 (TokenUtil 사용)
        Map<String, Object> validationResult = tokenUtil.validateToken(token);
        boolean result = (boolean) validationResult.get("result");
        
        if (!result)
        {
            String message = (String) validationResult.get("message");
            log.info("로그인 상태: 로그아웃 (토큰 형식 무효): {}", message);
            return LoginStatusResponse.loggedOut(message);
        }

        Map<String, String> decodedToken = tokenUtil.decodeToken(token);
        
        // 2. 토큰에서 사용자 정보 추출
        String userId = decodedToken.get("userId");
        String socialProvider = decodedToken.get("socialProvider");
        String socialId = decodedToken.get("socialId");
        
        // 3. DB에서 해당 userId의 최신 ACCESS 토큰 조회
        Auth latestToken = authRepository.findFirstByUserIdAndTokenTypeOrderByCreatedAtDesc(
            userId, TokenType.ACCESS
        ).orElse(null);
        
        // 4. 토큰이 없으면 로그아웃
        if (latestToken == null)
        {
            log.info("로그인 상태: 로그아웃 (DB에 토큰 없음): userId={}", userId);
            return LoginStatusResponse.loggedOut("로그인되어 있지 않습니다");
        }
        
        // 5. 토큰 만료 확인
        if (latestToken.isExpired())
        {
            log.info("로그인 상태: 로그아웃 (토큰 만료): userId={}", userId);
            return LoginStatusResponse.loggedOut("토큰이 만료되었습니다");
        }
        
        // 6. 전달받은 token과 DB의 최신 토큰 비교
        if (!latestToken.getToken().equals(token))
        {
            log.info("로그인 상태: 로그아웃 (토큰 불일치, 활성화된 토큰이 아닙니다): userId={}", userId);
            return LoginStatusResponse.loggedOut("활성화된 토큰이 아닙니다");
        }
        
        // 7. 로그인 중
        log.info("로그인 상태: 로그인 중 - userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
        return LoginStatusResponse.loggedIn(userId, socialProvider, socialId);
    }
    
    /**
     * 기존 토큰 삭제 (토큰 교체 시)
     * 
     * 로그인/토큰갱신 시 기존 토큰을 히스토리로 이동 후 삭제합니다.
     * 
     * @param userId 사용자 ID
     * @param tokenType 토큰 타입 (ACCESS 또는 REFRESH)
     * @param message 이벤트 메시지
     */
    private void deleteExistingTokens(String userId, TokenType tokenType, String message)
    {
        List<Auth> existingTokens = authRepository.findByUserIdAndTokenType(userId, tokenType);
        
        if (existingTokens.isEmpty())
        {
            return;
        }
        
        // 히스토리 저장 (토큰 교체 이벤트) - DDD 방식
        for (Auth token : existingTokens)
        {
            token.addReplacedHistory(message);
            authRepository.save(token);  // cascade로 히스토리 저장
        }
        
        // 토큰 삭제
        authRepository.deleteAll(existingTokens);
        
        log.info("기존 토큰 교체 완료: userId={}, tokenType={}, message={}, deletedCount={}", 
                userId, tokenType, message, existingTokens.size());
    }
    
    /**
     * 만료된 토큰 정리 (스케줄러용)
     * 스프링 스케줄러에서 주기적으로 호출
     */
    @Transactional
    public void cleanupExpiredTokens()
    {
        LocalDateTime now = LocalDateTime.now();
        List<Auth> expiredTokens = authRepository.findByExpiresAtBefore(now);
        
        if (expiredTokens.isEmpty())
        {
            log.debug("만료된 토큰 없음");
            return;
        }
        
        // 히스토리 저장 (DDD 방식)
        for (Auth token : expiredTokens)
        {
            token.addExpiredHistory("토큰 만료로 인한 자동 삭제");
            authRepository.save(token);  // cascade로 히스토리 저장
        }
        
        // 토큰 삭제
        authRepository.deleteAll(expiredTokens);
        
        log.info("만료된 토큰 {}개 정리 완료", expiredTokens.size());
    }
    
    private AuthDetailResponse convertToDetailResponse(Auth auth)
    {
        return AuthDetailResponse.builder()
            .id(auth.getId())
            .token(auth.getToken())
            .tokenType(auth.getTokenType())
            .userId(auth.getUserId())
            .expiresAt(auth.getExpiresAt())
            .deviceInfo(auth.getDeviceInfo())
            .ipAddress(auth.getIpAddress())
            .userAgent(auth.getUserAgent())
            .createdAt(auth.getCreatedAt())
            .updatedAt(auth.getUpdatedAt())
            .build();
    }
}
