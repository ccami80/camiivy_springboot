package com.cami.cami_springboot.api.auth.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.cami.cami_springboot.api.auth.code.AuthEventType;
import com.cami.cami_springboot.api.auth.entity.AuthHistory;
import com.cami.cami_springboot.api.auth.entity.QAuth;
import com.cami.cami_springboot.api.auth.entity.QAuthHistory;
import com.cami.cami_springboot.api.auth.request.AuthListSearchRequest;
import com.cami.cami_springboot.api.auth.response.AuthListResponse;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AuthRepositoryImpl implements AuthRepositoryCustom
{
    
    private final JPAQueryFactory queryFactory;
    
    public AuthRepositoryImpl(EntityManager entityManager)
    {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<AuthListResponse> customSearchAuthList(AuthListSearchRequest request, Pageable pageable)
    {
        QAuth auth = QAuth.auth;
        
        return queryFactory
            .select(Projections.constructor(AuthListResponse.class,
                auth.id,
                auth.token,
                auth.tokenType,
                auth.userId,
                auth.expiresAt,
                auth.deviceInfo,
                auth.ipAddress,
                auth.userAgent,
                auth.createdAt,
                auth.updatedAt
            ))
            .from(auth)
            .where(createSearchConditions(request))
            .orderBy(createOrderSpecifiers(request.getSort()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
    
    @Override
    public long customSearchAuthCount(AuthListSearchRequest request)
    {
        QAuth auth = QAuth.auth;
        
        return queryFactory
            .select(auth.count())
            .from(auth)
            .where(createSearchConditions(request))
            .fetchOne();
    }
    
    private BooleanExpression createSearchConditions(AuthListSearchRequest request)
    {
        QAuth auth = QAuth.auth;
        BooleanExpression conditions = Expressions.TRUE;
        
        if (request.getUserId() != null)
        {
            conditions = conditions.and(auth.userId.eq(request.getUserId()));
        }
        
        if (request.getTokenType() != null)
        {
            conditions = conditions.and(auth.tokenType.eq(request.getTokenType()));
        }
        
        // status 필드 제거됨 - auth_module에는 활성 토큰만 저장
        
        if (StringUtils.hasText(request.getDeviceInfo()))
        {
            conditions = conditions.and(auth.deviceInfo.containsIgnoreCase(request.getDeviceInfo()));
        }
        
        if (StringUtils.hasText(request.getIpAddress()))
        {
            conditions = conditions.and(auth.ipAddress.eq(request.getIpAddress()));
        }
        
        if (request.getExpiresAtFrom() != null)
        {
            conditions = conditions.and(auth.expiresAt.goe(request.getExpiresAtFrom()));
        }
        
        if (request.getExpiresAtTo() != null)
        {
            conditions = conditions.and(auth.expiresAt.loe(request.getExpiresAtTo()));
        }
        
        return conditions;
    }
    
    private OrderSpecifier<?>[] createOrderSpecifiers(String[] sort)
    {
        QAuth auth = QAuth.auth;
        
        if (sort == null || sort.length == 0)
        {
            return new OrderSpecifier[]{auth.createdAt.desc()};
        }
        
                return new OrderSpecifier[]
        {
            auth.createdAt.desc()
        };
    }
    
    // ========================================
    // AuthHistory 관련 메서드
    // ========================================
    
    @Override
    public List<AuthHistory> customSelectAuthHistoryByUserId(String userId)
    {
        QAuthHistory authHistory = QAuthHistory.authHistory;
        
        return queryFactory
            .selectFrom(authHistory)
            .where(authHistory.userId.eq(userId))
            .orderBy(authHistory.eventAt.desc())
            .fetch();
    }
    
    @Override
    public List<AuthHistory> customSelectAuthHistoryTop10ByUserId(String userId)
    {
        QAuthHistory authHistory = QAuthHistory.authHistory;
        
        return queryFactory
            .selectFrom(authHistory)
            .where(authHistory.userId.eq(userId))
            .orderBy(authHistory.eventAt.desc())
            .limit(10)
            .fetch();
    }
    
    @Override
    public List<AuthHistory> customSelectAuthHistoryByUserIdAndEventType(String userId, AuthEventType eventType)
    {
        QAuthHistory authHistory = QAuthHistory.authHistory;
        
        return queryFactory
            .selectFrom(authHistory)
            .where(
                authHistory.userId.eq(userId),
                authHistory.eventType.eq(eventType)
            )
            .orderBy(authHistory.eventAt.desc())
            .fetch();
    }
    
    @Override
    public List<AuthHistory> customSelectAuthHistoryByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate)
    {
        QAuthHistory authHistory = QAuthHistory.authHistory;
        
        return queryFactory
            .selectFrom(authHistory)
            .where(
                authHistory.userId.eq(userId),
                authHistory.eventAt.between(startDate, endDate)
            )
            .orderBy(authHistory.eventAt.desc())
            .fetch();
    }
}
