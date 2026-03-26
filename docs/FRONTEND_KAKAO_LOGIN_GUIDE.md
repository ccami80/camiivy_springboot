# 프론트엔드 카카오 로그인 가이드

백엔드 API와 연동하는 **회원가입** 및 **카카오 로그인** 흐름을 프론트엔드 관점에서 정리한 가이드입니다.

---

## 목차

1. [전체 흐름 개요](#1-전체-흐름-개요)
2. [카카오 개발자 콘솔 설정](#2-카카오-개발자-콘솔-설정)
3. [회원가입 방식 (2가지)](#3-회원가입-방식-2가지)
4. [프론트엔드 구현 예시](#4-프론트엔드-구현-예시)
5. [API 명세](#5-api-명세)
6. [주의사항](#6-주의사항)

---

## 1. 전체 흐름 개요

### 카카오 OAuth 2.0 흐름

```
[사용자] → [프론트엔드] → [카카오 인가 서버] → [프론트엔드] → [백엔드 API]
```

1. 사용자가 "카카오로 로그인" 버튼 클릭
2. 프론트엔드가 카카오 인가 URL로 리다이렉트
3. 사용자가 카카오에서 로그인/동의
4. 카카오가 `redirect_uri?code=인가코드` 로 리다이렉트
5. 프론트엔드가 URL에서 `code` 추출 후 백엔드 API 호출
6. 백엔드가 토큰 발급 후 **HttpOnly 쿠키**로 반환

---

## 2. 카카오 개발자 콘솔 설정

### 2.1 앱 생성 및 키 발급

1. [카카오 개발자 콘솔](https://developers.kakao.com/) 접속
2. **내 애플리케이션** → **애플리케이션 추가하기**
3. **앱 키** 메뉴에서 **REST API 키** 복사
4. **카카오 로그인** → **활성화 설정** ON
5. **Redirect URI** 등록 (아래 참고)

### 2.2 Redirect URI 등록

| 환경 | Redirect URI 예시 |
|------|-------------------|
| 로컬 개발 | `http://localhost:3000/auth/kakao/callback` |
| 프로덕션 | `https://your-domain.com/auth/kakao/callback` |

- **프론트엔드**에서 인가 코드를 받을 URL을 등록합니다.
- 카카오 콘솔에 등록한 URI와 프론트엔드 실제 경로가 **정확히 일치**해야 합니다.

### 2.3 백엔드 설정

백엔드 `application.properties` (또는 `application-local.properties`)에 다음 값을 설정해야 합니다:

```properties
# 카카오 OAuth (카카오 개발자 콘솔에서 발급)
app.kakao.client-id=YOUR_REST_API_KEY
app.kakao.client-secret=YOUR_CLIENT_SECRET  # 보안 설정에서 발급
app.kakao.redirect-uri=http://localhost:3000/auth/kakao/callback  # 프론트엔드 Redirect URI와 동일
```

- `redirect_uri`는 **프론트엔드**의 Redirect URI와 동일해야 합니다.
- 카카오가 인가 코드를 프론트엔드로 보내고, 백엔드는 그 코드를 받아 토큰 교환에 사용합니다.

---

## 3. 회원가입 방식 (2가지)

### 방식 A: 회원가입 + 로그인 분리 (휴대폰 번호 수집)

**휴대폰 번호를 반드시 수집**해야 할 때 사용합니다.

| 단계 | API | 설명 |
|------|-----|------|
| 1 | `POST /api-guest/users/accounts/sign-in/kakao` | 회원가입 (phone + code) |
| 2 | `POST /api-guest/auth/login/kakao` | 로그인 (code만) |

**흐름:**
1. 카카오 인가 코드 발급
2. **회원가입** API 호출 (`phone`, `code` 전달)
3. 이후 **로그인** API 호출 (`code` 전달) → 토큰 발급

### 방식 B: 로그인+회원가입 통합 (권장)

**휴대폰 번호 없이** 카카오만으로 가입·로그인할 때 사용합니다.

| API | 설명 |
|-----|------|
| `POST /api-guest/auth/login-signin/kakao` | 미가입 시 자동 회원가입 후 로그인 |

**흐름:**
1. 카카오 인가 코드 발급
2. **로그인+회원가입 통합** API 호출 (`code` 전달)
3. 미가입 사용자 → 자동 회원가입 후 토큰 발급  
   기가입 사용자 → 바로 토큰 발급

---

## 4. 프론트엔드 구현 예시

### 4.1 카카오 인가 URL 생성

```javascript
// 환경변수 또는 설정
const KAKAO_REST_API_KEY = 'YOUR_REST_API_KEY';  // 카카오 개발자 콘솔에서 발급
const REDIRECT_URI = 'http://localhost:3000/auth/kakao/callback';

function getKakaoAuthUrl() {
  return `https://kauth.kakao.com/oauth/authorize?` +
    `client_id=${KAKAO_REST_API_KEY}` +
    `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}` +
    `&response_type=code`;
}
```

### 4.2 카카오 로그인 버튼 클릭 → 리다이렉트

```javascript
function handleKakaoLogin() {
  window.location.href = getKakaoAuthUrl();
}
```

### 4.3 Redirect URI 페이지에서 code 추출 후 백엔드 호출

**예: `/auth/kakao/callback` 페이지 (React 예시)**

```javascript
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

const API_BASE_URL = 'http://localhost:8082';  // 백엔드 URL

export default function KakaoCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [error, setError] = useState(null);

  useEffect(() => {
    const code = searchParams.get('code');
    if (!code) {
      setError('인가 코드가 없습니다.');
      return;
    }

    // 방식 B: 로그인+회원가입 통합 (권장)
    fetch(`${API_BASE_URL}/api-guest/auth/login-signin/kakao`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',  // 쿠키 전송 필수!
      body: JSON.stringify({ code }),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          // accessToken, refreshToken은 HttpOnly 쿠키로 자동 저장됨
          navigate('/');  // 메인으로 이동
        } else {
          setError(data.message || '로그인 실패');
        }
      })
      .catch((err) => {
        setError(err.message || '로그인 중 오류 발생');
      });
  }, [searchParams, navigate]);

  if (error) return <div>오류: {error}</div>;
  return <div>로그인 처리 중...</div>;
}
```

### 4.4 방식 A: 회원가입 후 로그인 (휴대폰 번호 수집)

```javascript
// 1단계: 회원가입
async function signUpWithKakao(phone, code) {
  const res = await fetch(`${API_BASE_URL}/api-guest/users/accounts/sign-in/kakao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ phone, code }),
  });
  return res.json();
}

// 2단계: 로그인
async function loginWithKakao(code) {
  const res = await fetch(`${API_BASE_URL}/api-guest/auth/login/kakao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ code }),
  });
  return res.json();
}

// 흐름: 카카오 callback 페이지에서
// - 기존 회원인지 확인 후 → loginWithKakao(code)
// - 신규 회원이면 휴대폰 입력 화면 → signUpWithKakao(phone, code) → loginWithKakao(code)
```

### 4.5 인증된 API 호출 (쿠키 자동 포함)

```javascript
// credentials: 'include' 로 쿠키가 자동 전송됨
fetch(`${API_BASE_URL}/api-logined/users/me`, {
  credentials: 'include',
})
  .then((res) => res.json())
  .then((data) => console.log(data));
```

---

## 5. API 명세

### 5.1 회원가입 (카카오, 휴대폰 번호 포함)

```
POST /api-guest/users/accounts/sign-in/kakao
Content-Type: application/json

Request:
{
  "phone": "010-1234-5678",
  "code": "카카오_인가_코드"
}

Response (201):
{
  "success": true,
  "message": "OK",
  "data": {
    "userId": "...",
    "socialProvider": "KAKAO",
    "socialId": "...",
    "phone": "010-1234-5678",
    ...
  }
}
```

### 5.2 로그인 (회원가입 선행 필요)

```
POST /api-guest/auth/login/kakao
Content-Type: application/json

Request:
{
  "code": "카카오_인가_코드"
}

Response (200):
- Set-Cookie: accessTokenLeeds=...; HttpOnly; Path=/
- Set-Cookie: refreshTokenLeeds=...; HttpOnly; Path=/
- Body: { "success": true, "data": { "userId": "...", ... } }
```

### 5.3 로그인+회원가입 통합 (권장)

```
POST /api-guest/auth/login-signin/kakao
Content-Type: application/json

Request:
{
  "code": "카카오_인가_코드"
}

Response (200):
- Set-Cookie: accessTokenLeeds=...; HttpOnly; Path=/
- Set-Cookie: refreshTokenLeeds=...; HttpOnly; Path=/
- Body: { "success": true, "data": { "userId": "...", ... } }
```

### 5.4 계정 존재 확인 (선택)

```
POST /api-guest/users/accounts/check
Content-Type: application/json

Request:
{
  "socialProvider": "KAKAO",
  "socialId": "카카오_사용자_ID"  // 백엔드에서 code로 조회한 id
}
```

---

## 6. 주의사항

### CORS 및 쿠키

- 백엔드는 `allowCredentials(true)` 로 설정되어 있음.
- 프론트엔드 요청 시 **반드시 `credentials: 'include'`** 를 사용해야 쿠키가 전송됩니다.
- CORS `allowedOrigins`에 프론트엔드 도메인이 등록되어 있어야 합니다.  
  (기본: `localhost:3000`, `localhost:3001`, `platform.store` 등)

### 토큰 저장 방식

- `accessToken`, `refreshToken`은 **HttpOnly 쿠키**로만 전달됩니다.
- JavaScript에서 `document.cookie`로 접근할 수 없습니다 (XSS 방지).
- API 호출 시 `credentials: 'include'` 로 쿠키가 자동 전송됩니다.

### Redirect URI 일치

- 카카오 개발자 콘솔의 Redirect URI
- 백엔드 `app.kakao.redirect-uri`
- 프론트엔드 실제 callback URL

위 세 값이 **동일**해야 합니다.

### 에러 처리

| 상황 | 대응 |
|------|------|
| `회원가입되지 않은 계정입니다` | 방식 A 사용 시 → 먼저 `/api-guest/users/accounts/sign-in/kakao` 호출 |
| `DUPLICATE_SOCIAL_ACCOUNT` | 이미 가입된 카카오 계정 → 로그인 API 호출 |
| CORS 오류 | 백엔드 `WebConfig`에 프론트엔드 도메인 추가 |

---

## 요약

| 목적 | API | Request Body |
|------|-----|--------------|
| 휴대폰 수집 회원가입 | `POST /api-guest/users/accounts/sign-in/kakao` | `{ phone, code }` |
| 로그인 (가입자만) | `POST /api-guest/auth/login/kakao` | `{ code }` |
| **로그인+자동가입 (권장)** | `POST /api-guest/auth/login-signin/kakao` | `{ code }` |

1. 카카오 인가 URL로 리다이렉트 → 사용자 동의 → `redirect_uri?code=xxx` 수신
2. `code`를 백엔드에 전달
3. `credentials: 'include'` 로 요청하여 쿠키 수신
4. 이후 API 호출 시 동일하게 `credentials: 'include'` 사용
