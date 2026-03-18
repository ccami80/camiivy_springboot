package com.culwonder.leeds_profile_springboot_core.api.user.repository;

import com.culwonder.leeds_profile_springboot_core.api.user.entity.UserAccount;
import com.culwonder.leeds_profile_springboot_core.api.user.code.SocialProvider;
import com.culwonder.leeds_profile_springboot_core.api.user.request.UsersAccountListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.user.response.UsersAccountListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.culwonder.leeds_profile_springboot_core.api.user.entity.QUserAccount.userAccount;

/**
 * User Repository Implementation
 * 복잡한 조회를 위한 구현체
 */
@Repository
public class UserRepositoryImpl implements UserRepositoryCustom
{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager entityManager)
    {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * socialProvider와 socialId로 UserAccount 조회
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return UserAccount (존재하지 않으면 null)
     */
    @Override
    public Optional<UserAccount> customSelectUserAccountBySocialInfo(SocialProvider socialProvider, String socialId)
    {
        // QueryDSL을 사용한 복잡한 조회
        // Aggregate Root를 통한 하위 엔티티 조회
        UserAccount result = queryFactory
            .selectFrom(userAccount)
            .where(userAccount.socialProvider.eq(socialProvider)
                .and(userAccount.socialId.eq(socialId)))
            .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    /**
     * 회원가입된 계정 리스트 조회 (페이징)
     * @param request 검색 조건
     * @param pageable 페이징 정보
     * @return 계정 리스트
     */
    @Override
    public List<UsersAccountListResponse> customSelectUsersAccountList(UsersAccountListSearchRequest request, Pageable pageable)
    {
        return queryFactory
            .select(Projections.constructor(UsersAccountListResponse.class,
                userAccount.userId,
                userAccount.socialProvider,
                userAccount.socialId,
                userAccount.phone,
                userAccount.email,
                userAccount.name,
                userAccount.status,
                userAccount.createdAt,
                userAccount.updatedAt
            ))
            .from(userAccount)
            .where(createSearchConditions(request))
            .orderBy(userAccount.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
    
    /**
     * 회원가입된 계정 수 조회
     * @param request 검색 조건
     * @return 계정 수
     */
    @Override
    public long customCountUsersAccountList(UsersAccountListSearchRequest request)
    {
        Long count = queryFactory
            .select(userAccount.count())
            .from(userAccount)
            .where(createSearchConditions(request))
            .fetchOne();
        
        return count != null ? count : 0L;
    }
    
    /**
     * 계정 단건 상세 조회 (복합키 사용)
     * @param userId 사용자 ID
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return UserAccount (존재하지 않으면 null)
     */
    @Override
    public Optional<UserAccount> customSelectUserAccountDetail(String userId, SocialProvider socialProvider, String socialId)
    {
        UserAccount result = queryFactory
            .selectFrom(userAccount)
            .where(userAccount.userId.eq(userId)
                .and(userAccount.socialProvider.eq(socialProvider))
                .and(userAccount.socialId.eq(socialId)))
            .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public List<UserAccount> customSelectUserAccountsByUserId(String userId)
    {
        return queryFactory
            .selectFrom(userAccount)
            .where(userAccount.userId.eq(userId))
            .orderBy(userAccount.createdAt.desc())
            .fetch();
    }
    
    /**
     * 검색 조건 생성
     * @param request 검색 조건
     * @return BooleanExpression
     */
    private BooleanExpression createSearchConditions(UsersAccountListSearchRequest request)
    {
        BooleanExpression conditions = Expressions.TRUE;
        
        if (request == null)
        {
            return conditions;
        }
        
        // 사용자 ID 검색
        if (StringUtils.hasText(request.getUserId()))
        {
            conditions = conditions.and(userAccount.userId.eq(request.getUserId()));
        }
        
        // 소셜 제공자 검색
        if (request.getSocialProvider() != null)
        {
            conditions = conditions.and(userAccount.socialProvider.eq(request.getSocialProvider()));
        }
        
        // 소셜 ID 검색
        if (StringUtils.hasText(request.getSocialId()))
        {
            conditions = conditions.and(userAccount.socialId.eq(request.getSocialId()));
        }
        
        // 전화번호 검색
        if (StringUtils.hasText(request.getPhone()))
        {
            conditions = conditions.and(userAccount.phone.eq(request.getPhone()));
        }
        
        // 상태 검색
        if (request.getStatus() != null)
        {
            conditions = conditions.and(userAccount.status.eq(request.getStatus()));
        }
        
        return conditions;
    }
    
    @Override
    public void deleteUserAccount(String userId, SocialProvider socialProvider, String socialId)
    {
        queryFactory
            .delete(userAccount)
            .where(userAccount.userId.eq(userId)
                .and(userAccount.socialProvider.eq(socialProvider))
                .and(userAccount.socialId.eq(socialId)))
            .execute();
    }
}
