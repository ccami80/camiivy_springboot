# API 레퍼런스

Camiivy 백엔드 API 전체 명세입니다. 프론트엔드·모바일 연동 시 참고용으로 사용하세요.

---

## 목차

1. [공통 사항](#1-공통-사항)
2. [인증 (Auth)](#2-인증-auth)
3. [카테고리·브랜드](#3-카테고리브랜드)
4. [상품 (Products)](#4-상품-products)
5. [배너·홈 섹션·큐레이션](#5-배너홈-섹션큐레이션)
6. [장바구니 (Cart)](#6-장바구니-cart)
7. [주문 (Orders)](#7-주문-orders)
8. [사용자 (User)](#8-사용자-user)
9. [문의 (Inquiry)](#9-문의-inquiry)
10. [파트너 (Partner)](#10-파트너-partner)
11. [관리자 (Admin)](#11-관리자-admin)

---

## 1. 공통 사항

### Base URL

- 개발: `http://localhost:8082` (또는 설정된 서버 주소)
- 프로덕션: 실제 도메인

### 인증 방식

| 구분 | 설명 |
|------|------|
| Bearer Token | `Authorization: Bearer {token}` 헤더 사용 |
| HttpOnly Cookie | 카카오 로그인 등 일부 API는 쿠키로 토큰 전달 |

### 공통 응답 형식

- 성공: HTTP 2xx + JSON body
- 실패: HTTP 4xx/5xx + `{ error?, message? }` 등

---

## 2. 인증 (Auth)

> **소셜 로그인만 지원** (자체 이메일/비밀번호 로그인 없음)

### 2.1 카카오 로그인

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| POST | `/api-guest/auth/login-signin/kakao` | `{ code }` | `{ success, data: { userId, email?, name? } }` |
| POST | `/api-guest/auth/login/kakao` | `{ code }` | `{ success, data }` |
| POST | `/api-guest/users/accounts/sign-in/kakao` | `{ phone, code }` | `{ success, data }` |

> 카카오 로그인 상세 흐름은 [FRONTEND_KAKAO_LOGIN_GUIDE.md](./FRONTEND_KAKAO_LOGIN_GUIDE.md) 참고.

### 2.2 관리자 로그인

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| POST | `/api/auth/admin-login` | `{ username, password }` | `{ token, userId }` |

> 환경설정 `app.admin.username`, `app.admin.password` 사용. 환경변수 `ADMIN_USERNAME`, `ADMIN_PASSWORD`로 오버라이드 가능.

### 2.3 기타 소셜 로그인

- Google 등 추가 소셜 제공자는 동일한 패턴으로 확장 가능

---

## 3. 카테고리·브랜드

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/categories` | Query: `petType?` | `Array<Category>` |
| GET | `/api/brands` | — | `Array<Brand>` |

---

## 4. 상품 (Products)

### 4.1 목록·상세

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/products` | Query: `brand?`, `petType?`, `categoryId?`, `sort?`, `q?`, `minPrice?`, `maxPrice?`, `color?`, `includeVariants?` | `Array<Product>` |
| GET | `/api/products/:id` | — | `Product` |
| GET | `/api/products/:id/reviews` | Query: `sort?`, `bodyType?`, `petType?` | `{ reviews, summary }` |
| GET | `/api/products/:id/inquiries` | — | `{ inquiries }` |
| POST | `/api/products/:id/inquiries` | `{ title, content, emailReply?, secret? }` | `{ id, message }` |
| GET | `/api/products/:id/category-best` | — | `Array<Product>` |
| GET | `/api/products/:id/recommended` | — | `Array<Product>` |

---

## 5. 배너·홈 섹션·큐레이션

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/banners` | — | `Array<Banner>` |
| GET | `/api/home-sections` | — | `{ newBest?, best? }` |
| GET | `/api/curation` | — | `Array<Product>` |
| GET | `/api/page-sections` | Query: `page` (필수) | `Array<Product>` |
| GET | `/api/notices` | — | `Array` |
| GET | `/api/faq` | — | `Array` |

---

## 6. 장바구니 (Cart)

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/cart` | — | `{ items }` |
| POST | `/api/cart` | `{ productId, quantity, optionLabel? }` | `{ items }` |
| PATCH | `/api/cart/items/:itemId` | `{ quantity }` | 항목 또는 `{ removed }` |
| DELETE | `/api/cart/items/:itemId` | — | 200 |

---

## 7. 주문 (Orders)

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| POST | `/api/orders` | `{ recipientName, recipientPhone, recipientEmail, zipCode, address, ... }` | `{ id, orderNumber? }` |
| GET | `/api/orders/:id` | — | `Order` |
| GET | `/api/orders/lookup` | Query: `orderNumber`, `phone` | `Order` 또는 `{ error }` |
| POST | `/api/orders/:id/pay` | `{}` | `{}` |
| POST | `/api/orders/:id/cancel` | — | `{}` |

---

## 8. 사용자 (User)

> 모든 API에 `Authorization: Bearer {token}` 필요.

### 8.1 프로필·주문

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/user/me` | — | `User` |
| GET | `/api/user/orders` | — | `Array<Order>` |
| GET | `/api/user/orders/:id` | — | `Order` |

### 8.2 위시리스트·리뷰

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/user/wishlist` | — | `Array` |
| POST | `/api/user/wishlist/:productId` | — | `{ success }` |
| GET | `/api/user/reviews` | — | `Array` |
| GET | `/api/user/reviews/:id` | — | `Review` |
| GET | `/api/user/can-review` | Query: `productId` | `{ canReview, orderItemId? }` |
| POST | `/api/user/reviews` | `{ productId, orderItemId, rating, content, ... }` | `{ id }` |
| PATCH | `/api/user/reviews/:id` | `{ rating?, content?, ... }` | `Review` |
| DELETE | `/api/user/reviews/:id` | — | 200 |

### 8.3 반려동물·문의·업로드

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/user/pets` | — | `Array<Pet>` |
| POST | `/api/user/pets` | `{ name, petType, breed?, bodyType?, ... }` | `Pet` |
| PATCH | `/api/user/pets/:id` | `{ name?, petType?, ... }` | `Pet` |
| DELETE | `/api/user/pets/:id` | — | `{ success }` |
| GET | `/api/user/inquiries` | — | `Array` |
| POST | `/api/user/upload` | FormData `files` | `{ urls }` |

---

## 9. 문의 (Inquiry)

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| POST | `/api/inquiry` | `{ inquiryType, orderId?, content, imageUrls?, ... }` | `{ ok }` |
| GET | `/api/inquiry/my` | Header: Bearer token | `Array` |
| POST | `/api/inquiry/upload` | FormData | `{ urls }` |
| GET | `/api/customer-center/settings` | — | `Object` |

---

## 10. 파트너 (Partner)

> 모든 API에 `Authorization: Bearer {token}` 필요.

### 10.1 프로필·정산·주문

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/partner/me` | — | `Partner` |
| PATCH | `/api/partner/me` | `{ companyName?, contactName?, contactPhone? }` | `Partner` |
| GET | `/api/partner/settlement` | — | `Object` |
| GET | `/api/partner/orders` | — | `Array` |

### 10.2 상품 관리

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/partner/products` | — | `Array` |
| GET | `/api/partner/products/:id` | — | `Product` |
| POST | `/api/partner/products` | Body: 상품 필드 | `Product` |
| PATCH | `/api/partner/products/:id` | Body: 상품 필드 | `Product` |
| DELETE | `/api/partner/products/:id` | — | `{ message }` |
| POST | `/api/partner/products/:id/generate-detail` | — | `{ ... }` |

### 10.3 문의·업로드

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/partner/inquiries` | — | `Array` |
| GET | `/api/partner/inquiries/:id` | — | `Object` |
| POST | `/api/partner/upload` | FormData `files` | `{ urls }` |

---

## 11. 관리자 (Admin)

> 모든 API에 `Authorization: Bearer {token}` (관리자 토큰) 필요.

### 11.1 대시보드·파트너·상품

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/admin/dashboard` | — | `Object` |
| GET | `/api/admin/partners` | Query: `status?` | `Array` |
| GET | `/api/admin/partners/:id` | — | `Partner` |
| PATCH | `/api/admin/partners/:id` | `{ status }` | `Partner` |
| GET | `/api/admin/products` | Query: `status?` | `Array` |
| GET | `/api/admin/products/:id` | — | `Product` |
| PATCH | `/api/admin/products/:id` | `{ approvalStatus?, displayOrder? }` | `Product` |
| PATCH | `/api/admin/products/order` | `{ productIds }` | 200 |
| POST | `/api/admin/products/:id/generate-detail` | — | `Object` |

### 11.2 주문·카테고리·추천

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET | `/api/admin/orders` | — | `Array` |
| GET | `/api/admin/orders/:id` | — | `Order` |
| PATCH | `/api/admin/orders/:id` | `{ status }` | `Order` |
| GET/POST | `/api/admin/categories` | — | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/category-best`, `.../:id` | — | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/recommended`, `.../:id` | — | `Array` |

### 11.3 배너·홈·페이지·큐레이션

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET/POST/PATCH/DELETE | `/api/admin/banners`, `.../:id` | — | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/home-sections`, `.../:id` | — | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/page-sections`, `.../:id` | Query: `pageType`, Body: `{ pageType, productId }` 등 | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/curation`, `.../:id` | — | `Array` |

### 11.4 FAQ·공지·1:1 문의·설정

| Method | URL | 요청 (Request) | 응답 (Response) |
|--------|-----|----------------|-----------------|
| GET/POST/PATCH/DELETE | `/api/admin/faq`, `.../:id` | — | `Array` |
| GET/POST/PATCH/DELETE | `/api/admin/notices`, `.../:id` | — | `Array` |
| GET/POST | `/api/admin/one-to-one-inquiries` | — | `Array` |
| GET/PATCH | `/api/admin/one-to-one-inquiries/:id` | — | `Object` |
| GET/PATCH | `/api/admin/customer-center/settings` | — | `Object` |
| POST | `/api/admin/upload` | FormData | `{ url?, urls? }` |

---

## 부록: 주요 타입 참고

| 타입 | 설명 |
|------|------|
| `Category` | 카테고리 (id, name, petType 등) |
| `Brand` | 브랜드 (id, name 등) |
| `Product` | 상품 (id, name, price, images, variants 등) |
| `Order` | 주문 (id, orderNumber, status, items 등) |
| `User` | 사용자 (id, email, name, phone 등) |
| `Partner` | 파트너 (id, companyName, status 등) |
| `Pet` | 반려동물 (id, name, petType, breed 등) |
| `Review` | 리뷰 (id, rating, content 등) |
| `Banner` | 배너 (id, imageUrl, link 등) |

---

*최종 수정: 2025-03-19*
