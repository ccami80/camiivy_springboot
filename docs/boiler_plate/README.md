# Boilerplate - 새 프로젝트용 공통 코드

## 개요

이 폴더는 **다른 프로젝트에 이 구조를 적용**할 때 참조/복사할 공통 코드를 담고 있습니다.

## 사용 방법

1. **규칙 먼저 복사**: `.cursor/rules/`, `.cursorrules` (core/project-setup-guide.mdc 참고)
2. **common 모듈 복사**: `modules/common/` → 새 프로젝트 `api/common/`
3. **패키지명 변경**: `com.culwonder` → `com.{회사}.{프로젝트}`

## 폴더 구조

```
boiler_plate/
├── modules/
│   ├── common/           # 필수 - 공통 응답, 예외, 설정, 인터셉터
│   ├── user/             # 예시 - User 도메인 (참고용)
│   ├── auth/             # 예시 - Auth 도메인 (참고용)
│   └── image/            # 예시 - Image 도메인 (참고용)
├── resources/            # application.properties 예시
└── build.gradle          # 의존성 예시
```

## 필수 복사 대상 (common)

- `common/response/` - CommonResponse, PageResponse, ErrorCodeResponse
- `common/util/PageResponseUtil.java`
- `common/exception/` - CustomException, GlobalExceptionHandler
- `common/code/ErrorCode.java`
- `common/config/` - WebConfig, SwaggerConfig, JpaConfig, AppProperties
- `common/interceptor/AuthInterceptor.java`

## 참조

- **적용 가이드**: `.cursor/rules/core/project-setup-guide.mdc`
- **구현체 상세**: `.cursor/rules/core/common-implementation.mdc`
