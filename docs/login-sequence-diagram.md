# 로그인 방식 시퀀스 다이어그램

현재 프로젝트(Leeds Profile Spring Boot Core)의 **소셜 로그인(카카오/구글)** 흐름을 화면·백엔드·DB 관점으로 정리한 시퀀스 다이어그램입니다.

---

## 카카오 로그인 (화면 · 카카오 · 백엔드 서버 · 카카오)

```mermaid
sequenceDiagram
    participant 화면
    participant 카카오
    participant 백엔드서버 as 백엔드 서버
    participant 카카오2 as 카카오

    화면->>카카오: 1. 인가 요청 (리다이렉트)
    카카오->>화면: 2. 로그인/동의 화면
    화면->>카카오: 3. 사용자 동의
    카카오->>화면: 4. redirect_uri?code=인가코드

    화면->>백엔드서버: 5. POST /api-guest/auth/tokens { provider: "kakao", code }
    백엔드서버->>카카오2: 6. code → 액세스 토큰 요청
    카카오2-->>백엔드서버: 7. 액세스 토큰
    백엔드서버->>카카오2: 8. 사용자 정보 요청 (Bearer 토큰)
    카카오2-->>백엔드서버: 9. 사용자 정보 (id 등)

    Note over 백엔드서버: 회원 여부 확인<br>토큰 발급 등 처리

    백엔드서버-->>화면: 10. 200 OK + Set-Cookie(access/refresh) + 로그인 결과
```

---

## 1. 카카오 로그인 (전체 흐름, 상세)

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Screen as 화면(클라이언트)
    participant Kakao as 카카오 OAuth
    participant AuthCtrl as AuthController
    participant AuthSvc as AuthService
    participant AuthCall as AuthCallService
    participant UserProv as UserProviderService
    participant UserSvc as UserService
    participant UserRepo as UserRepository
    participant AuthRepo as AuthRepository
    participant DB as Database

    User->>Screen: 카카오 로그인 버튼 클릭
    Screen->>Kakao: 인가 요청 (리다이렉트)
    Kakao->>User: 로그인/동의 화면
    User->>Kakao: 동의 후 인가 코드 발급
    Kakao->>Screen: redirect_uri?code=xxx
    Screen->>AuthCtrl: POST /api-guest/auth/tokens { provider: "kakao", code }

    AuthCtrl->>AuthSvc: kakaoLogin(code)
    AuthSvc->>AuthCall: getKakaoAccessToken(code)
    AuthCall->>Kakao: POST /oauth/token (code → access_token)
    Kakao-->>AuthCall: access_token
    AuthCall-->>AuthSvc: KakaoTokenResponse
    AuthSvc->>AuthCall: getKakaoUserInfo(access_token)
    AuthCall->>Kakao: GET /v2/user/me (Bearer access_token)
    Kakao-->>AuthCall: 사용자 정보 (id, 등)
    AuthCall-->>AuthSvc: KakaoUserInfoResponse

    AuthSvc-->>AuthCtrl: KakaoUserInfoResponse (socialId 추출)
    AuthCtrl->>AuthSvc: login(LoginRequest: KAKAO, socialId)

    AuthSvc->>AuthCall: checkAccountExists(socialProvider, socialId)
    AuthCall->>UserProv: checkAccountExists(socialProvider, socialId)
    UserProv->>UserSvc: checkAccountExists(UsersAccountCheckRequest)
    UserSvc->>UserRepo: customSelectUserAccountBySocialInfo(KAKAO, socialId)
    UserRepo->>DB: SELECT ... FROM user_module_account WHERE social_provider=? AND social_id=?
    DB-->>UserRepo: UserAccount or empty
    UserRepo-->>UserSvc: Optional<UserAccount>
    UserSvc-->>UserProv: UsersAccountCheckResponse(exists, userId)
    UserProv-->>AuthCall: UserProviderResponse(exists, userId)
    AuthCall-->>AuthSvc: UserProviderResponse

    alt 회원가입 안 됨
        AuthSvc-->>AuthCtrl: throw CustomException(ACCOUNT_NOT_FOUND)
        AuthCtrl-->>Screen: 4xx + 에러 메시지
    else 회원가입 됨 (userId 확보)
        AuthSvc->>AuthRepo: findByUserIdAndTokenType(userId, ACCESS)
        AuthRepo->>DB: SELECT FROM auth_module WHERE user_id=? AND token_type='ACCESS'
        DB-->>AuthRepo: List<Auth>
        AuthRepo-->>AuthSvc: existingTokens
        AuthSvc->>AuthSvc: createReplacedHistory() → persist(history)
        AuthSvc->>AuthRepo: deleteAll(existingTokens)
        AuthRepo->>DB: INSERT auth_module_history / DELETE auth_module
        AuthSvc->>AuthRepo: findByUserIdAndTokenType(userId, REFRESH) → 동일 히스토리 이동 후 삭제
        AuthRepo->>DB: INSERT auth_module_history / DELETE auth_module
        AuthSvc->>AuthRepo: findByUserId(userId)
        AuthRepo->>DB: SELECT FROM auth_module WHERE user_id=?
        DB-->>AuthRepo: remaining (있으면 재삭제)
        AuthSvc->>AuthSvc: TokenUtil.generateToken(ACCESS/REFRESH)
        AuthSvc->>AuthSvc: accessAuth.createLoginHistory() → persist(loginHistory)
        AuthSvc->>AuthRepo: save(accessAuth)
        AuthRepo->>DB: INSERT auth_module (ACCESS)
        AuthSvc->>AuthRepo: save(refreshAuth)
        AuthRepo->>DB: INSERT auth_module (REFRESH)
        AuthSvc-->>AuthCtrl: LoginResponse(accessToken, refreshToken, userId)
        AuthCtrl->>AuthCtrl: ResponseCookie (accessTokenLeeds, refreshTokenLeeds, HttpOnly)
        AuthCtrl-->>Screen: 200 OK + Set-Cookie + body(LoginResponse)
        Screen-->>User: 로그인 완료 (쿠키 저장)
    end
```

---

## 2. 구글 로그인 (백엔드·DB만, 화면은 동일 패턴)

```mermaid
sequenceDiagram
    participant Screen as 화면
    participant AuthCtrl as AuthController
    participant AuthSvc as AuthService
    participant AuthCall as AuthCallService
    participant Google as 구글 OAuth
    participant UserProv as UserProviderService
    participant UserSvc as UserService
    participant UserRepo as UserRepository
    participant AuthRepo as AuthRepository
    participant DB as Database

    Screen->>AuthCtrl: POST /api-guest/auth/tokens { provider: "google", code }
    AuthCtrl->>AuthSvc: googleLogin(code)
    AuthSvc->>AuthCall: getGoogleAccessToken(code)
    AuthCall->>Google: token endpoint (code → access_token)
    Google-->>AuthCall: GoogleTokenResponse
    AuthSvc->>AuthCall: getGoogleUserInfo(access_token)
    AuthCall->>Google: userinfo endpoint
    Google-->>AuthCall: GoogleUserInfoResponse (id)
    AuthCall-->>AuthSvc: GoogleUserInfoResponse
    AuthSvc-->>AuthCtrl: GoogleUserInfoResponse
    AuthCtrl->>AuthSvc: login(LoginRequest: GOOGLE, socialId)
    AuthSvc->>AuthCall: checkAccountExists(GOOGLE, socialId)
    AuthCall->>UserProv: checkAccountExists(GOOGLE, socialId)
    UserProv->>UserSvc: checkAccountExists(UsersAccountCheckRequest)
    UserSvc->>UserRepo: customSelectUserAccountBySocialInfo(GOOGLE, socialId)
    UserRepo->>DB: SELECT FROM user_module_account WHERE social_provider='GOOGLE' AND social_id=?
    DB-->>UserRepo: UserAccount or empty
    UserRepo-->>UserSvc: Optional<UserAccount>
    UserSvc-->>UserProv: UsersAccountCheckResponse
    UserProv-->>AuthCall: UserProviderResponse
    AuthCall-->>AuthSvc: UserProviderResponse
    Note over AuthSvc,DB: 이하 카카오와 동일: 기존 토큰 히스토리 이동·삭제 → ACCESS/REFRESH 저장 → 로그인 히스토리 INSERT
    AuthSvc->>AuthRepo: save(accessAuth), save(refreshAuth)
    AuthRepo->>DB: INSERT auth_module (2 rows)
    AuthSvc-->>AuthCtrl: LoginResponse
    AuthCtrl-->>Screen: 200 + Set-Cookie + body
```

---

## 3. DB 테이블 관점 요약

| 구간 | 테이블 | 동작 |
|------|--------|------|
| 회원 여부 확인 | `user_module_account` | SELECT (social_provider, social_id) |
| 기존 토큰 정리 | `auth_module` | SELECT → (해당 토큰) DELETE |
| 히스토리 보관 | `auth_module_history` | INSERT (로그인/교체/로그아웃 등 이벤트) |
| 새 토큰 발급 | `auth_module` | INSERT (ACCESS 1건, REFRESH 1건) |

---

## 4. 참고: 로그인 전제 조건

- **카카오/구글 로그인 API**: 이미 **회원가입**이 되어 있어야 함.
- 회원가입: `POST /api-guest/users/accounts` (body: `provider`, `code`, `phone`)으로 `user_module` + `user_module_account`에 계정 생성 후, 동일한 소셜 정보로 로그인 시 `checkAccountExists`가 true가 되어 토큰이 발급됩니다.
- 회원가입이 안 된 소셜 ID로 로그인하면 `ACCOUNT_NOT_FOUND` 예외로 실패합니다.

---

## 5. 파일 위치 참고

| 역할 | 클래스/파일 |
|------|-------------|
| 화면 연동 API | `AuthController` – `POST /api-guest/auth/tokens` (body: `provider`, `code`) |
| 로그인 오케스트레이션 | `AuthService` – `kakaoLogin()`, `googleLogin()`, `login()` |
| 외부/유저 모듈 호출 | `AuthCallService` – 카카오/구글 API, `UserProviderService.checkAccountExists()` |
| 계정 존재 여부 | `UserProviderService` → `UserService.checkAccountExists()` → `UserRepository.customSelectUserAccountBySocialInfo()` |
| 토큰 저장/조회/삭제 | `AuthRepository` – `auth_module`, `auth_module_history` |
| 계정 조회 | `UserRepository` – `user_module_account` (customSelectUserAccountBySocialInfo) |

---

## 6. REST API 경로 (엄격한 REST 적용)

URL은 명사(리소스), 동작은 HTTP 메서드로 표현합니다.

| 용도 | 메서드·경로 | Request body |
|------|-------------|--------------|
| 소셜 로그인 (토큰 발급) | `POST /api-guest/auth/tokens` | `{ "provider": "kakao" \| "google", "code": "인가코드" }` |
| 소셜 회원가입 (계정 생성) | `POST /api-guest/users/accounts` | `{ "provider": "kakao" \| "google", "code": "인가코드", "phone": "전화번호" }` |
| SMS 인증번호 발송 | `POST /api/sms/verification-codes` | `{ "toPhoneNumber": "+82..." }` |
| SMS 인증번호 검증 | `POST /api/sms/verification-codes/validate` | `{ "toPhoneNumber": "+82...", "verificationCode": "123456" }` |
