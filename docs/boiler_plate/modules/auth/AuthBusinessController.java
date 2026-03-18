package com.culwonder.leeds_profile_springboot_core.api.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 비즈니스 API 컨트롤러
 * BUSINESS 권한이 필요한 인증 관리 API
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api-business/auth")
@Tag(name = "Auth Business API", description = "인증 비즈니스 API (BUSINESS 권한 필요)")
public class AuthBusinessController
{
    
    // private final AuthService authService;
    
    // @PostMapping
    // @Operation(summary = "인증 토큰 생성", description = "새로운 인증 토큰을 생성합니다. (토큰 인증 필요)")
    // public ResponseEntity<AuthDetailResponse> createAuth(@Valid @RequestBody AuthCreateRequest request) {
    //     log.info("AuthBusinessController - createAuth called with request: {}", request);
    //     AuthDetailResponse response = authService.createAuth(request);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(response);
    // }
    
    // @GetMapping("/{id}")
    // @Operation(summary = "인증 토큰 상세 조회", description = "특정 인증 토큰의 상세 정보를 조회합니다. (토큰 인증 필요)")
    // public ResponseEntity<AuthDetailResponse> getAuth(
    //         @Parameter(description = "인증 토큰 ID", required = true) @PathVariable Long id) {
    //     log.info("AuthBusinessController - getAuth called with id: {}", id);
    //     AuthSelectRequest request = new AuthSelectRequest(id);
    //     AuthDetailResponse response = authService.searchAuthDetail(request);
    //     return ResponseEntity.ok(response);
    // }
    
    // @PutMapping("/{id}")
    // @Operation(summary = "인증 토큰 수정", description = "인증 토큰 정보를 수정합니다. (토큰 인증 필요)")
    // public ResponseEntity<AuthDetailResponse> updateAuth(
    //         @Parameter(description = "인증 토큰 ID", required = true) @PathVariable Long id,
    //         @Valid @RequestBody AuthUpdateRequest request) {
    //     log.info("AuthBusinessController - updateAuth called with id: {}, request: {}", id, request);
    //     AuthUpdateRequest requestWithId = new AuthUpdateRequest(id, request.expiresAt(), 
    //         request.deviceInfo(), request.ipAddress(), request.userAgent());
    //     AuthDetailResponse response = authService.updateAuth(requestWithId);
    //     return ResponseEntity.ok(response);
    // }
    
    // @DeleteMapping("/{id}")
    // @Operation(summary = "인증 토큰 삭제", description = "인증 토큰을 삭제합니다. (토큰 인증 필요)")
    // public ResponseEntity<AuthDeleteResponse> deleteAuth(
    //         @Parameter(description = "인증 토큰 ID", required = true) @PathVariable Long id) {
    //     log.info("AuthBusinessController - deleteAuth called with id: {}", id);
    //     AuthDeleteRequest request = new AuthDeleteRequest(id);
    //     AuthDeleteResponse response = authService.deleteAuth(request);
    //     return ResponseEntity.ok(response);
    // }
    
    // @GetMapping("/search")
    // @Operation(summary = "인증 토큰 검색", description = "조건에 따라 인증 토큰을 검색합니다. (토큰 인증 필요)")
    // public ResponseEntity<Page<AuthListResponse>> searchAuths(@ModelAttribute AuthListSearchRequest request) {
    //     log.info("AuthBusinessController - searchAuths called with request: {}", request);
    //     Pageable pageable = PageRequest.of(0, 10);
    //     Page<AuthListResponse> response = authService.searchAuthList(request, pageable);
    //     return ResponseEntity.ok(response);
    // }
    
    // @PostMapping("/logout")
    // @Operation(summary = "로그아웃", description = "사용자의 모든 토큰을 만료시킵니다. (토큰 인증 필요)")
    // public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
    //     log.info("AuthBusinessController - logout called with userId: {}", request.userId());
    //     LogoutResponse response = authService.logout(request);
    //     return ResponseEntity.ok(response);
    // }
}
