package com.culwonder.leeds_profile_springboot_core.api.user.service;

import com.culwonder.leeds_profile_springboot_core.api.user.entity.User;
import com.culwonder.leeds_profile_springboot_core.api.user.entity.UserAccount;
import com.culwonder.leeds_profile_springboot_core.api.user.repository.UserRepository;
import com.culwonder.leeds_profile_springboot_core.api.user.code.PermissionType;
import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import com.culwonder.leeds_profile_springboot_core.api.user.code.UserStatus;
import com.culwonder.leeds_profile_springboot_core.api.common.util.UserIdGenerator;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountCreateRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountCheckRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountCheckResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountListResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountDetailResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountSimpleResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UserPermissionResponse;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UserProfileResponse;
import com.culwonder.leeds_profile_springboot_core.api.common.exception.CustomException;
import com.culwonder.leeds_profile_springboot_core.api.common.code.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountKakaoCreateRequest;
import com.culwonder.leeds_profile_springboot_core.api.auth.service.AuthCallService;
import com.culwonder.leeds_profile_springboot_core.api.auth.response.KakaoTokenResponse;
import com.culwonder.leeds_profile_springboot_core.api.auth.response.KakaoUserInfoResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService
{
    
    private final UserRepository userRepository;
    private final UserIdGenerator userIdGenerator;
    private final UserCallService userCallService;
    private final AuthCallService authCallService;
    
    /**
     * 사용자 로그인 처리
     * 
     * 비즈니스 로직:
     * 1. Users에 중복된 휴대폰번호 있는지 확인
     * 2. 중복된 휴대폰 번호가 있을 경우: Users에 있는 userId 기반으로 UsersAccount 데이터 생성
     * 3. 중복된 휴대폰 번호가 없을 경우: Users 생성 + USER 권한 부여 후 UsersAccount 데이터 생성
     */
    @Transactional
    public UserAccount userSignIn(UsersAccountCreateRequest request)
    {
        log.info("사용자 로그인 요청: phone={}, socialProvider={}, socialId={}", 
                request.phone(), request.socialProvider(), request.socialId());
        
        // 0. 이미 UserAccount가 존재하는지 확인 (socialProvider + socialId 조합)
        UserAccount existingUserAccount = userRepository.customSelectUserAccountBySocialInfo(
            request.socialProvider(), request.socialId()).orElse(null);
        
        if (existingUserAccount != null)
        {
            log.warn("이미 존재하는 UserAccount: socialProvider={}, socialId={}", 
                    request.socialProvider(), request.socialId());
            throw new CustomException(ErrorCode.DUPLICATE_SOCIAL_ACCOUNT);
        }

        // phone이 null인 경우 처리 (카카오 로그인 등)
        String phone = request.phone();
        
        // 1. Users에 중복된 휴대폰번호 있는지 확인 (phone이 null이 아닌 경우만)
        User existingUser = null;
        if (phone != null && !phone.isEmpty())
        {
            existingUser = userRepository.findByPhone(phone).orElse(null);
        }
        
        String userId;
        
        if (existingUser != null)
        {
            // 2. 중복된 휴대폰 번호가 있을 경우: Users에 있는 userId 기반으로 UsersAccount 데이터 생성
            log.info("중복된 휴대폰 번호 발견: userId={}, phone={}", existingUser.getUserId(), existingUser.getPhone());
            userId = existingUser.getUserId();
            
                } else
        {
            // 3. 중복된 휴대폰 번호가 없을 경우: Users 생성 + USER 권한 부여 후 UsersAccount 데이터 생성
            log.info("새로운 User 생성: phone={}", phone);
            userId = userIdGenerator.generateUserId();
            
            User newUser = User.builder()
                .userId(userId)
                .email(null) // 이메일은 입력받지 않음
                .name(null)  // 이름은 입력받지 않음
                .phone(phone)  // null 허용
                .address(null) // 주소는 입력받지 않음
                .createdId("system")  // 시스템 생성
                .build();
            
            // 최초 회원가입 시 USER 권한 자동 부여
            newUser.addPermission(PermissionType.USER);
            log.info("USER 권한 부여: userId={}", userId);
            
            User savedUser = userRepository.save(newUser);
            log.info("User 생성 완료: userId={}, phone={}, permissions={}", 
                    savedUser.getUserId(), savedUser.getPhone(), 
                    savedUser.getPermissions().stream()
                        .map(p -> p.getPermissionType().name())
                        .toArray());
        }
        
        // UsersAccount 데이터 생성 (Users에 있는 userId 기반)
        UserAccount usersAccount = UserAccount.builder()
            .userId(userId)
            .socialProvider(request.socialProvider())
            .socialId(request.socialId())
            .phone(phone)  // null 허용
            .email(null) // 이메일은 입력받지 않음
            .name(null)  // 이름은 입력받지 않음
            .createdId("system")  // 시스템 생성
            .build();
        
        // UsersAccount 저장 (User를 통한 저장)
        User user = userRepository.findByUserId(userId).orElseThrow(
            () -> new IllegalArgumentException("User를 찾을 수 없습니다: " + userId)
        );
        user.addUsersAccount(usersAccount);
        UserAccount savedUsersAccount = userRepository.save(user).getUsersAccounts().get(0);
        log.info("UsersAccount 생성 완료: userId={}, socialProvider={}, socialId={}", 
                savedUsersAccount.getUserId(), savedUsersAccount.getSocialProvider(), savedUsersAccount.getSocialId());
        
        return savedUsersAccount;
    }
    
    @Transactional
    public UserAccount userSignInKakao(UsersAccountKakaoCreateRequest request)
    {
        log.info("카카오 회원가입 요청: phone={}, code={}", request.phone(), request.code());

        // 1. 카카오에서 액세스 토큰 발급
        KakaoTokenResponse kakaoTokenResponse = authCallService.getKakaoAccessToken(request.code());
        log.info("카카오 액세스 토큰 발급 완료");

        // 2. 카카오 사용자 정보 조회
        KakaoUserInfoResponse kakaoUserInfo = authCallService.getKakaoUserInfo(kakaoTokenResponse.getAccessToken());
        log.info("카카오 사용자 정보 조회 완료: id={}", kakaoUserInfo.getId());

        // 3. 카카오 ID를 socialId로 사용하여 UsersAccountCreateRequest 생성
        UsersAccountCreateRequest accountRequest = new UsersAccountCreateRequest(
            request.phone(),
            SocialProvider.KAKAO,
            String.valueOf(kakaoUserInfo.getId())
        );

        // 4. 기존 userSignIn 메서드 호출
        UserAccount userAccount = userSignIn(accountRequest);
        
        log.info("카카오 회원가입 완료: userId={}, socialId={}", 
                userAccount.getUserId(), userAccount.getSocialId());
        
        return userAccount;
    }
    
    /**
     * 계정 존재 확인
     * 
     * 비즈니스 로직:
     * 1. socialProvider + socialId 조합으로 UserAccount가 존재하는지 확인
     * 2. 존재하는 경우: exists=true, userId 반환
     * 3. 존재하지 않는 경우: exists=false 반환
     */
        public UsersAccountCheckResponse checkAccountExists(UsersAccountCheckRequest request)
    {
        log.info("계정 존재 확인 요청: socialProvider={}, socialId={}", 
                request.socialProvider(), request.socialId());
        
        // socialProvider + socialId 조합으로 UserAccount 조회
        UserAccount existingUserAccount = userRepository.customSelectUserAccountBySocialInfo(
            SocialProvider.valueOf(request.socialProvider()), request.socialId()).orElse(null);
        
        if (existingUserAccount != null)
        {
            log.info("계정 존재: userId={}, socialProvider={}, socialId={}", 
                    existingUserAccount.getUserId(), existingUserAccount.getSocialProvider(), existingUserAccount.getSocialId());
            
            return UsersAccountCheckResponse.builder()
                .exists(true)
                .userId(existingUserAccount.getUserId())
                .socialProvider(existingUserAccount.getSocialProvider().name())
                .socialId(existingUserAccount.getSocialId())
                .message("계정이 존재합니다")
                .build();
                } else
        {
            log.info("계정 미존재: socialProvider={}, socialId={}", 
                    request.socialProvider(), request.socialId());
            
            return UsersAccountCheckResponse.builder()
                .exists(false)
                .userId(null)
                .socialProvider(request.socialProvider())
                .socialId(request.socialId())
                .message("계정이 존재하지 않습니다")
                .build();
        }
    }
    
    /**
     * 회원가입된 계정 리스트 조회 (페이징)
     * 
     * 비즈니스 로직:
     * 1. UsersAccountListSearchRequest 조건에 맞는 계정 리스트 조회
     * 2. 검색 조건: userId, socialProvider, socialId, phone, status
     * 3. 생성일시 내림차순 정렬
     * 4. 페이징 처리
     * 5. 커스텀 페이지 응답 형식으로 변환 (PageResponseUtil 사용)
     */
    public Page<UsersAccountListResponse> searchUsersAccountList(UsersAccountListSearchRequest request, Pageable pageable)
    {
        log.info("계정 리스트 조회 요청: userId={}, socialProvider={}, socialId={}, phone={}, status={}, page={}, size={}", 
                request.getUserId(), request.getSocialProvider(), request.getSocialId(), 
                request.getPhone(), request.getStatus(), pageable.getPageNumber(), pageable.getPageSize());
        
        // 계정 리스트 조회
        List<UsersAccountListResponse> accountList = 
            userRepository.customSelectUsersAccountList(request, pageable);
        
        // 전체 개수 조회
        long total = userRepository.customCountUsersAccountList(request);
        
        log.info("계정 리스트 조회 완료: count={}, total={}, page={}/{}", 
                accountList.size(), total, pageable.getPageNumber() + 1, 
                (int) Math.ceil((double) total / pageable.getPageSize()));
        
        // Page 객체 생성 및 반환
        return new PageImpl<>(accountList, pageable, total);
    }
    
    /**
     * 계정 단건 상세 조회 (복합키)
     * 
     * 비즈니스 로직:
     * 1. userId, socialProvider, socialId 복합키로 UserAccount 조회
     * 2. 계정이 존재하지 않으면 예외 발생
     * 3. UsersAccountDetailResponse로 변환하여 반환
     * 
     * @deprecated 사용하지 않음. searchUsersAccountDetailBySocial 또는 searchUsersAccountDetailByUserId 사용
     */
    @Deprecated
    public UsersAccountDetailResponse searchUsersAccountDetail(String userId, String socialProvider, String socialId)
    {
        log.info("계정 상세 조회 요청: userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
        
        // socialProvider String을 Enum으로 변환
        SocialProvider provider;
        try
        {
            provider = SocialProvider.valueOf(socialProvider.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            log.error("잘못된 소셜 제공자: socialProvider={}", socialProvider);
            throw new CustomException(HttpStatus.BAD_REQUEST, 
                "잘못된 소셜 제공자입니다: " + socialProvider);
        }
        
        // 계정 조회
        UserAccount userAccount = userRepository.customSelectUserAccountDetail(userId, provider, socialId)
            .orElseThrow(() -> {
                log.error("계정을 찾을 수 없음: userId={}, socialProvider={}, socialId={}", 
                    userId, socialProvider, socialId);
                return new CustomException(HttpStatus.NOT_FOUND, 
                    "계정을 찾을 수 없습니다: userId=" + userId + 
                    ", socialProvider=" + socialProvider + 
                    ", socialId=" + socialId);
            });
        
        log.info("계정 상세 조회 완료: userId={}, socialProvider={}, socialId={}", 
                userAccount.getUserId(), userAccount.getSocialProvider(), userAccount.getSocialId());
        
        return convertToUsersAccountDetailResponse(userAccount);
    }
    
    /**
     * 계정 조회 (소셜 정보)
     * 
     * 비즈니스 로직:
     * 1. socialProvider, socialId로 UserAccount 조회
     * 2. 계정이 존재하지 않으면 예외 발생
     * 3. UsersAccountDetailResponse로 변환하여 반환
     */
    public UsersAccountDetailResponse searchUsersAccountDetailBySocial(String socialProvider, String socialId)
    {
        log.info("계정 조회 요청 (소셜 정보): socialProvider={}, socialId={}", 
                socialProvider, socialId);
        
        // socialProvider String을 Enum으로 변환
        SocialProvider provider;
        try
        {
            provider = SocialProvider.valueOf(socialProvider.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            log.error("잘못된 소셜 제공자: socialProvider={}", socialProvider);
            throw new CustomException(HttpStatus.BAD_REQUEST, 
                "잘못된 소셜 제공자입니다: " + socialProvider);
        }
        
        // 소셜 정보로 계정 조회
        UserAccount userAccount = userRepository.customSelectUserAccountBySocialInfo(provider, socialId)
            .orElseThrow(() -> {
                log.error("계정을 찾을 수 없음 (소셜 정보): socialProvider={}, socialId={}", 
                    socialProvider, socialId);
                return new CustomException(HttpStatus.NOT_FOUND, 
                    "계정을 찾을 수 없습니다: socialProvider=" + socialProvider + 
                    ", socialId=" + socialId);
            });
        
        log.info("계정 조회 완료 (소셜 정보): userId={}, socialProvider={}, socialId={}", 
                userAccount.getUserId(), userAccount.getSocialProvider(), userAccount.getSocialId());
        
        return convertToUsersAccountDetailResponse(userAccount);
    }
    
    /**
     * 계정 조회 (userId)
     * 
     * 비즈니스 로직:
     * 1. userId로 UserAccount 조회 (첫 번째 계정만 반환)
     * 2. 계정이 존재하지 않으면 예외 발생
     * 3. UsersAccountDetailResponse로 변환하여 반환
     */
        public UsersAccountDetailResponse searchUsersAccountDetailByUserId(String userId)
    {
        log.info("계정 조회 요청 (userId): userId={}", userId);
        
        // userId로 계정 조회 (첫 번째 계정)
        List<UserAccount> userAccounts = userRepository.customSelectUserAccountsByUserId(userId);
        
        if (userAccounts.isEmpty())
        {
            log.error("계정을 찾을 수 없음 (userId): userId={}", userId);
            throw new CustomException(HttpStatus.NOT_FOUND, 
                "계정을 찾을 수 없습니다: userId=" + userId);
        }
        
        UserAccount userAccount = userAccounts.get(0);
        
        log.info("계정 조회 완료 (userId): userId={}, socialProvider={}, socialId={}", 
                userAccount.getUserId(), userAccount.getSocialProvider(), userAccount.getSocialId());
        
        return convertToUsersAccountDetailResponse(userAccount);
    }
    
    /**
     * 사용자 권한 리스트 조회
     * 
     * @param userId 사용자 ID
     * @return 권한 리스트
     */
    @Transactional(readOnly = true)
    public List<UserPermissionResponse> searchUserPermissions(String userId)
    {
        log.info("사용자 권한 리스트 조회 요청: userId={}", userId);
        
        // 사용자 조회
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, 
                "사용자를 찾을 수 없습니다: " + userId));
        
        // 권한 리스트 변환
        List<UserPermissionResponse> permissions = user.getPermissions().stream()
            .map(permission -> UserPermissionResponse.builder()
                .userId(permission.getUserId())
                .permissionType(permission.getPermissionType())
                .createdAt(permission.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        log.info("사용자 권한 리스트 조회 성공: userId={}, permissionCount={}", userId, permissions.size());
        
        return permissions;
    }
    
    /**
     * 회원 탈퇴 처리
     * 
     * 비즈니스 로직:
     * 1. socialProvider + socialId로 UserAccount 조회
     * 2. 해당 userId의 모든 활성 토큰 무효화 (로그아웃)
     * 3. UserAccount 삭제
     * 4. 삭제 후 해당 userId의 다른 UserAccount가 없으면 User status를 DELETED로 변경
     * 
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     */
    @Transactional
    public void userSignOut(String socialProvider, String socialId)
    {
        log.info("회원 탈퇴 요청: socialProvider={}, socialId={}", socialProvider, socialId);
        
        // 1. UserAccount 조회
        SocialProvider provider;
        try
        {
            provider = SocialProvider.valueOf(socialProvider.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            log.error("잘못된 소셜 제공자: socialProvider={}", socialProvider);
            throw new CustomException(HttpStatus.BAD_REQUEST, 
                "잘못된 소셜 제공자입니다: " + socialProvider);
        }
        
        // socialProvider + socialId로 UserAccount 조회
        UserAccount userAccount = userRepository.customSelectUserAccountBySocialInfo(provider, socialId)
            .orElseThrow(() -> {
                log.error("계정을 찾을 수 없음: socialProvider={}, socialId={}", 
                    socialProvider, socialId);
                return new CustomException(HttpStatus.NOT_FOUND,
                    "계정을 찾을 수 없습니다: socialProvider=" + socialProvider + 
                    ", socialId=" + socialId);
            });
        
        String userId = userAccount.getUserId();
        log.info("UserAccount 조회 완료: userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
        
        // 2. 해당 userId의 모든 활성 토큰 무효화 (로그아웃)
        // UserCallService를 통해 Auth 모듈의 AuthProviderService 호출
        userCallService.logout(userId);
        log.info("모든 활성 토큰 무효화 완료 (로그아웃): userId={}", userId);
        
        // 3. UserAccount 삭제
        userRepository.deleteUserAccount(userId, provider, socialId);
        log.info("UserAccount 삭제 완료: userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
        
        // 4. 해당 userId의 다른 UserAccount가 있는지 확인
        List<UserAccount> remainingAccounts = userRepository.customSelectUserAccountsByUserId(userId);
        
        // 5. 남아있는 UserAccount가 없으면 User status를 DELETED로 변경
        if (remainingAccounts.isEmpty())
        {
            User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: userId={}", userId);
                    return new CustomException(HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다: " + userId);
                });
            
            user.delete();
            userRepository.save(user);
            log.info("User status를 DELETED로 변경: userId={}", userId);
                } else
        {
            log.info("다른 UserAccount가 존재하여 User는 유지: userId={}, remainingCount={}", 
                    userId, remainingAccounts.size());
        }
        
        log.info("회원 탈퇴 완료: userId={}, socialProvider={}, socialId={}", 
                userId, socialProvider, socialId);
    }
    
    /**
     * 내 프로필 조회 (User 정보 + 연결된 소셜 계정 리스트)
     * 
     * @param userId 사용자 ID
     * @return User 정보 + 연결된 모든 UserAccount 정보
     */
        public UserProfileResponse searchMyProfile(String userId)
    {
        log.info("내 프로필 조회 요청: userId={}", userId);
        
        // 1. User 존재 여부 확인
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() ->
        {
            log.error("사용자를 찾을 수 없음: userId={}", userId);
            return new CustomException(HttpStatus.NOT_FOUND,
                "사용자를 찾을 수 없습니다: " + userId);
        });
        
        // 2. DELETED 상태 확인
        if (user.getStatus() == UserStatus.DELETED)
        {
            log.error("삭제된 사용자: userId={}", userId);
            throw new CustomException(HttpStatus.GONE,
                "삭제된 사용자입니다: " + userId);
        }
        
        // 3. 해당 userId의 모든 UserAccount 조회
        List<UserAccount> userAccounts = userRepository.customSelectUserAccountsByUserId(userId);
        
        if (userAccounts.isEmpty())
        {
            log.warn("연결된 소셜 계정이 없습니다: userId={}", userId);
            throw new CustomException(HttpStatus.NOT_FOUND,
                "연결된 소셜 계정이 없습니다");
        }
        
        // 4. UsersAccountSimpleResponse 리스트로 변환 (email, name, address, updatedAt 제외)
        List<UsersAccountSimpleResponse> accountList = userAccounts.stream()
            .map(this::convertToUsersAccountSimpleResponse)
            .collect(Collectors.toList());
        
        // 5. UserPermission 조회
        List<UserPermissionResponse> permissions = searchUserPermissions(userId);
        
        // 6. UserProfileResponse 생성 (updatedAt 제외)
        UserProfileResponse profileResponse = UserProfileResponse.builder()
            .userId(user.getUserId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .name(user.getName())
            .address(user.getAddress())
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .permissions(permissions)
            .accountList(accountList)
            .build();
        
        log.info("내 프로필 조회 완료: userId={}, accountCount={}, permissionCount={}", 
                userId, accountList.size(), permissions.size());
        
        return profileResponse;
    }
    
    /**
     * 사용자 존재 여부 확인 (userId 기반)
     * 
     * @param userId 사용자 ID
     * @return 사용자가 존재하고 ACTIVE 상태면 true
     */
    public boolean checkUserExists(String userId)
    {
        log.info("사용자 존재 확인 요청: userId={}", userId);
        
        User user = userRepository.findByUserId(userId).orElse(null);
        
        boolean exists = user != null && user.getStatus() != UserStatus.DELETED;
        
        log.info("사용자 존재 확인 결과: userId={}, exists={}", userId, exists);
        
        return exists;
    }
    
    /**
     * UserAccount를 UsersAccountDetailResponse로 변환
     */
    private UsersAccountDetailResponse convertToUsersAccountDetailResponse(UserAccount userAccount)
    {
        return UsersAccountDetailResponse.builder()
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
    }
    
    /**
     * UserAccount를 UsersAccountSimpleResponse로 변환 (accountList용)
     * email, name, address, updatedAt 제외
     */
    private UsersAccountSimpleResponse convertToUsersAccountSimpleResponse(UserAccount userAccount)
    {
        return UsersAccountSimpleResponse.builder()
            .userId(userAccount.getUserId())
            .socialProvider(userAccount.getSocialProvider())
            .socialId(userAccount.getSocialId())
            .phone(userAccount.getPhone())
            .status(userAccount.getStatus())
            .createdAt(userAccount.getCreatedAt())
            .build();
    }
    
}
