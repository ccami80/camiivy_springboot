package com.culwonder.leeds_profile_springboot_core.api.user.repository;

import com.culwonder.leeds_profile_springboot_core.api.user.entity.UserAccount;
import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * User Repository Custom Interface
 * 복잡한 조회를 위한 커스텀 인터페이스
 */
public interface UserRepositoryCustom
{
    
    /**
     * socialProvider와 socialId로 UserAccount 조회
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return UserAccount (존재하지 않으면 null)
     */
    Optional<UserAccount> customSelectUserAccountBySocialInfo(SocialProvider socialProvider, String socialId);
    
    /**
     * 회원가입된 계정 리스트 조회 (페이징)
     * @param request 검색 조건
     * @param pageable 페이징 정보
     * @return 계정 리스트
     */
    List<UsersAccountListResponse> customSelectUsersAccountList(UsersAccountListSearchRequest request, Pageable pageable);
    
    /**
     * 회원가입된 계정 수 조회
     * @param request 검색 조건
     * @return 계정 수
     */
    long customCountUsersAccountList(UsersAccountListSearchRequest request);
    
    /**
     * 계정 단건 상세 조회 (복합키 사용)
     * @param userId 사용자 ID
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return UserAccount (존재하지 않으면 null)
     */
    Optional<UserAccount> customSelectUserAccountDetail(String userId, SocialProvider socialProvider, String socialId);
    
    /**
     * userId로 UserAccount 리스트 조회
     * @param userId 사용자 ID
     * @return UserAccount 리스트
     */
    List<UserAccount> customSelectUserAccountsByUserId(String userId);
    
    /**
     * UserAccount 삭제
     * @param userId 사용자 ID
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     */
    void deleteUserAccount(String userId, SocialProvider socialProvider, String socialId);
}
