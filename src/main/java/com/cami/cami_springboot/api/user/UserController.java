package com.cami.cami_springboot.api.user;

import com.cami.cami_springboot.api.user.request.UsersAccountCheckRequest;
import com.cami.cami_springboot.api.user.request.UsersAccountCreateRequest;
import com.cami.cami_springboot.api.user.request.UsersAccountGoogleCreateRequest;
import com.cami.cami_springboot.api.user.request.UsersAccountKakaoCreateRequest;
import com.cami.cami_springboot.api.user.response.UsersAccountDetailResponse;
import com.cami.cami_springboot.api.user.response.UsersAccountCheckResponse;
import com.cami.cami_springboot.api.user.entity.UserAccount;
import com.cami.cami_springboot.api.user.service.UserService;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User guest API controller.
 * Sign-in (registration) and account check (no login required).
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
@Tag(name = "User Guest API", description = "User sign-in and account APIs (guest access)")
public class UserController
{

    private final UserService userService;

    @PostMapping("/api-guest/users/accounts/sign-in")
    @Operation(summary = "Sign-in (create account)", description = "Register with social provider (guest access)")
    public ResponseEntity<CommonResponse> userSignIn(
            @Valid @RequestBody UsersAccountCreateRequest request)
    {
        log.info("Sign-in API: socialProvider={}, socialId={}", request.socialProvider(), request.socialId());
        UserAccount userAccount = userService.userSignIn(request);
        UsersAccountDetailResponse response = UsersAccountDetailResponse.builder()
            .userId(userAccount.getUserId())
            .socialProvider(userAccount.getSocialProvider())
            .socialId(userAccount.getSocialId())
            .phone(userAccount.getPhone())
            .email(userAccount.getEmail())
            .name(userAccount.getName())
            .status(userAccount.getStatus())
            .createdAt(userAccount.getCreatedAt())
            .updatedAt(userAccount.getUpdatedAt())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponse(true, "OK", response));
    }

    @PostMapping("/api-guest/users/accounts/check")
    @Operation(summary = "Check account exists", description = "Check if account exists by social provider/id (guest access)")
    public ResponseEntity<CommonResponse> checkAccountExists(
            @Valid @RequestBody UsersAccountCheckRequest request)
    {
        UsersAccountCheckResponse response = userService.checkAccountExists(request);
        return ResponseEntity.ok(new CommonResponse(true, response.message(), response));
    }

    @PostMapping("/api-guest/users/accounts/sign-in/kakao")
    @Operation(summary = "Kakao sign-in", description = "Sign-in with Kakao authorization code (guest access)")
    public ResponseEntity<CommonResponse> userSignInKakao(
            @Valid @RequestBody UsersAccountKakaoCreateRequest request)
    {
        UserAccount userAccount = userService.userSignInKakao(request);
        UsersAccountDetailResponse response = UsersAccountDetailResponse.builder()
            .userId(userAccount.getUserId())
            .socialProvider(userAccount.getSocialProvider())
            .socialId(userAccount.getSocialId())
            .phone(userAccount.getPhone())
            .email(userAccount.getEmail())
            .name(userAccount.getName())
            .status(userAccount.getStatus())
            .createdAt(userAccount.getCreatedAt())
            .updatedAt(userAccount.getUpdatedAt())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponse(true, "OK", response));
    }

    @PostMapping("/api-guest/users/accounts/sign-in/google")
    @Operation(summary = "Google sign-in", description = "Sign-in with Google authorization code (guest access)")
    public ResponseEntity<CommonResponse> userSignInGoogle(
            @Valid @RequestBody UsersAccountGoogleCreateRequest request)
    {
        UserAccount userAccount = userService.userSignInGoogle(request);
        UsersAccountDetailResponse response = UsersAccountDetailResponse.builder()
            .userId(userAccount.getUserId())
            .socialProvider(userAccount.getSocialProvider())
            .socialId(userAccount.getSocialId())
            .phone(userAccount.getPhone())
            .email(userAccount.getEmail())
            .name(userAccount.getName())
            .status(userAccount.getStatus())
            .createdAt(userAccount.getCreatedAt())
            .updatedAt(userAccount.getUpdatedAt())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponse(true, "OK", response));
    }
}
