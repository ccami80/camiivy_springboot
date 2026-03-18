package com.cami.cami_springboot.api.user;

import com.cami.cami_springboot.api.user.service.UserService;
import com.cami.cami_springboot.api.user.response.UserPermissionResponse;
import com.cami.cami_springboot.api.user.response.UserProfileResponse;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 로그인 API 컨트롤러
 * 로그인한 사용자를 위한 API (권한 무관, 로그인만 필요)
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api-logined/users")
@Tag(name = "User Logined API", description = "사용자 로그인 API (로그인 필요)")
public class UserLoginedController
{
    
    private final UserService userService;
    
    @GetMapping("/profile")
    @Operation(
        summary = "내 프로필 조회", 
        description = "로그인한 사용자의 프로필 정보를 조회합니다. (로그인 필요)\n\n" +
                     "**반환 정보**:\n" +
                     "- userId: 사용자 ID\n" +
                     "- phone: 전화번호\n" +
                     "- email: 이메일\n" +
                     "- name: 이름\n" +
                     "- address: 주소\n" +
                     "- status: 계정 상태\n" +
                     "- createdAt: 생성 일시\n" +
                     "- updatedAt: 수정 일시\n" +
                     "- permissions: 권한 목록\n" +
                     "- accountList: 연결된 소셜 계정 목록\n\n" +
                     "**특징**:\n" +
                     "- User 상세 정보 + 연결된 모든 소셜 계정 정보를 함께 반환합니다"
    )
    public ResponseEntity<CommonResponse> getMyProfile(
            @RequestAttribute("userId") String userId) 
    {
        log.info("내 프로필 조회 API 호출 (로그인 필요): userId={}", userId);
        
        UserProfileResponse profile = userService.searchMyProfile(userId);
        
        CommonResponse commonResponse = new CommonResponse(
            true, 
            "내 프로필 조회 성공", 
            profile
        );
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 프로필 정보를 수정합니다. (로그인 필요)")
    public ResponseEntity<CommonResponse> updateMyProfile(
            @RequestAttribute("userId") String userId) {
        log.info("내 프로필 수정 API 호출 (로그인 필요): userId={}", userId);
        
        // TODO: 프로필 수정 구현
        // 임시 응답
        CommonResponse commonResponse = new CommonResponse(true, "프로필 수정 성공 (구현 예정)", null);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    @GetMapping("/my-permissions")
    @Operation(
        summary = "내 권한 조회", 
        description = "로그인한 사용자의 권한 리스트를 조회합니다. (로그인 필요)\n\n" +
                     "**반환 정보**:\n" +
                     "- userId: 사용자 ID\n" +
                     "- permissionType: 권한 타입 (USER, BUSINESS, ADMIN)\n" +
                     "- createdAt: 권한 부여 일시"
    )
    public ResponseEntity<CommonResponse> getMyPermissions(
            @RequestAttribute("userId") String userId) {
        log.info("내 권한 조회 API 호출 (로그인 필요): userId={}", userId);
        
        List<UserPermissionResponse> permissions = userService.searchUserPermissions(userId);
        
        CommonResponse commonResponse = new CommonResponse(true, "내 권한 조회 성공", permissions);
        
        return ResponseEntity.ok(commonResponse);
    }
    
    
    @DeleteMapping("/accounts/sign-out")
    @Operation(
        summary = "계정 삭제 (회원 탈퇴)", 
        description = "로그인한 사용자의 계정을 삭제(탈퇴) 처리합니다. (로그인 필요)\n\n" +
                     "**처리 내용**:\n" +
                     "1. 현재 로그인한 UserAccount 삭제\n" +
                     "2. 삭제 후 해당 userId의 다른 UserAccount가 없으면 User status를 DELETED로 변경\n" +
                     "3. 데이터는 보관되지만 해당 소셜 계정으로는 로그인 불가\n\n" +
                     "**주의사항**:\n" +
                     "- 현재 로그인한 소셜 계정만 삭제됩니다\n" +
                     "- 다른 소셜 계정이 있으면 User는 유지됩니다\n" +
                     "- 모든 소셜 계정을 삭제하면 User도 탈퇴 처리됩니다"
    )
    public ResponseEntity<CommonResponse> deleteAccount(
            @RequestAttribute("socialProvider") String socialProvider,
            @RequestAttribute("socialId") String socialId) {
        log.info("계정 삭제 API 호출 (로그인 필요): socialProvider={}, socialId={}", 
                socialProvider, socialId);
        
        // 회원 탈퇴 처리 (현재 로그인한 UserAccount만 삭제)
        userService.userSignOut(socialProvider, socialId);
        
        log.info("계정 삭제 완료: socialProvider={}, socialId={}", socialProvider, socialId);
        
        CommonResponse commonResponse = new CommonResponse(
            true, 
            "계정 삭제가 성공적으로 완료되었습니다",
            null
        );
        
        return ResponseEntity.ok(commonResponse);
    }
    
}
