package com.cami.cami_springboot.api.user;

import java.util.Map;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.common.util.PageResponseUtil;
import com.cami.cami_springboot.api.user.request.UsersAccountListSearchRequest;
import com.cami.cami_springboot.api.user.response.UsersAccountDetailResponse;
import com.cami.cami_springboot.api.user.response.UsersAccountListResponse;
import com.cami.cami_springboot.api.user.response.UserPermissionResponse;
import com.cami.cami_springboot.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관리자 API 컨트롤러
 * ADMIN 권한이 필요한 사용자 관리 API
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api-admin/users")
@Tag(name = "User Admin API", description = "사용자 관리자 API (ADMIN 권한 필요)")
public class UserAdminController
{
    
    private final UserService userService;
    
    @GetMapping("/accounts")
    @Operation(
        summary = "계정 리스트 조회 (페이징) - 관리자", 
        description = "모든 회원가입된 계정 리스트를 조회합니다. (ADMIN 권한 필요)\n\n" +
                     "**검색 조건**: userId, socialProvider, socialId, phone, status\n\n" +
                     "**페이징 파라미터**:\n" +
                     "- page: 페이지 번호 (0부터 시작, 기본값: 0)\n" +
                     "- size: 페이지 크기 (기본값: 10)\n" +
                     "- sort: 정렬 조건 (예: createdAt,desc)"
    )
    public ResponseEntity<CommonResponse> getUsersAccountList(@ModelAttribute UsersAccountListSearchRequest request, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
        log.info("계정 리스트 조회 API 호출 (관리자): userId={}, socialProvider={}, socialId={}, phone={}, status={}, page={}, size={}", 
                request.getUserId(), request.getSocialProvider(), request.getSocialId(), 
                request.getPhone(), request.getStatus(), pageable.getPageNumber(), pageable.getPageSize());
        
        // Page 객체 조회
        Page<UsersAccountListResponse> page = userService.searchUsersAccountList(request, pageable);
        
        // PageResponseUtil을 사용하여 커스텀 형식으로 변환
        Map<String, Object> responseData = PageResponseUtil.toWrappedPageResponse(page, "usersAccountList");
        
        CommonResponse commonResponse = new CommonResponse(true, "계정 리스트 조회 성공", responseData);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @GetMapping("/accounts/{socialProvider}/{socialId}")
    @Operation(summary = "계정 조회 (소셜 정보) - 관리자", description = "소셜 제공자와 소셜 ID로 특정 계정을 조회합니다. (ADMIN 권한 필요)")
    public ResponseEntity<CommonResponse> getUsersAccountBySocial(
            @PathVariable String socialProvider,
            @PathVariable String socialId) {
        log.info("계정 조회 API 호출 (소셜 정보, 관리자): socialProvider={}, socialId={}", 
                socialProvider, socialId);
        
        UsersAccountDetailResponse accountDetail = userService.searchUsersAccountDetailBySocial(socialProvider, socialId);
        CommonResponse commonResponse = new CommonResponse(true, "계정 조회 성공", accountDetail);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @GetMapping("/accounts/user/{userId}")
    @Operation(summary = "계정 조회 (userId) - 관리자", description = "사용자 ID로 특정 계정을 조회합니다. (ADMIN 권한 필요)")
    public ResponseEntity<CommonResponse> getUsersAccountByUserId(
            @PathVariable String userId) {
        log.info("계정 조회 API 호출 (userId, 관리자): userId={}", userId);
        
        UsersAccountDetailResponse accountDetail = userService.searchUsersAccountDetailByUserId(userId);
        CommonResponse commonResponse = new CommonResponse(true, "계정 조회 성공", accountDetail);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @GetMapping("/{userId}/permissions")
    @Operation(
        summary = "사용자 권한 조회 - 관리자", 
        description = "특정 사용자의 권한 리스트를 조회합니다. (ADMIN 권한 필요)\n\n" +
                     "**반환 정보**:\n" +
                     "- userId: 사용자 ID\n" +
                     "- permissionType: 권한 타입 (USER, BUSINESS, ADMIN)\n" +
                     "- createdAt: 권한 부여 일시"
    )
        public ResponseEntity<CommonResponse> getUserPermissions(@PathVariable String userId)
    {
        log.info("사용자 권한 조회 API 호출 (관리자): userId={}", userId);
        
        List<UserPermissionResponse> permissions = userService.searchUserPermissions(userId);
        
        CommonResponse commonResponse = new CommonResponse(true, "사용자 권한 조회 성공", permissions);
        
        return ResponseEntity.ok(commonResponse);
    }
    
}
