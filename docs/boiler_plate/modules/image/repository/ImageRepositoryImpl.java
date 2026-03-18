package com.culwonder.leeds_profile_springboot_core.api.image.repository;

import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageStatus;
import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageType;
import com.culwonder.leeds_profile_springboot_core.api.image.entity.Image;
import com.culwonder.leeds_profile_springboot_core.api.image.entity.QImage;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 이미지 커스텀 리포지토리 구현체
 */
@Repository
public class ImageRepositoryImpl implements ImageRepositoryCustom
{
    
    private final JPAQueryFactory queryFactory;
    private final QImage qImage = QImage.image;
    
    public ImageRepositoryImpl(EntityManager entityManager)
    {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<Image> customSearchImageList(
        ImageType imageType,
        ImageStatus imageStatus,
        String uploadedBy,
        String relatedEntityId,
        String relatedEntityType,
        Pageable pageable
    )
    {
        BooleanBuilder builder = createSearchConditions(
            imageType, imageStatus, uploadedBy, relatedEntityId, relatedEntityType
        );
        
        List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiers(pageable.getSort());
        
        return queryFactory
            .selectFrom(qImage)
            .where(builder)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
    
    @Override
    public long customSearchImageCount(
        ImageType imageType,
        ImageStatus imageStatus,
        String uploadedBy,
        String relatedEntityId,
        String relatedEntityType
    )
    {
        BooleanBuilder builder = createSearchConditions(
            imageType, imageStatus, uploadedBy, relatedEntityId, relatedEntityType
        );
        
        Long count = queryFactory
            .select(qImage.count())
            .from(qImage)
            .where(builder)
            .fetchOne();
        
        return count != null ? count : 0;
    }
    
    @Override
    public List<Image> customSearchImageListByUploader(String uploadedBy, Pageable pageable)
    {
        return queryFactory
            .selectFrom(qImage)
            .where(qImage.uploadedBy.eq(uploadedBy))
            .orderBy(qImage.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
    
    @Override
    public List<Image> customSearchImageListByRelatedEntity(
        String relatedEntityId,
        String relatedEntityType,
        ImageStatus imageStatus
    )
    {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (relatedEntityId != null && !relatedEntityId.isEmpty())
        {
            builder.and(qImage.relatedEntityId.eq(relatedEntityId));
        }
        
        if (relatedEntityType != null && !relatedEntityType.isEmpty())
        {
            builder.and(qImage.relatedEntityType.eq(relatedEntityType));
        }
        
        if (imageStatus != null)
        {
            builder.and(qImage.imageStatus.eq(imageStatus));
        }
        
        return queryFactory
            .selectFrom(qImage)
            .where(builder)
            .orderBy(qImage.createdAt.desc())
            .fetch();
    }
    
    /**
     * 검색 조건 생성
     */
    private BooleanBuilder createSearchConditions(
        ImageType imageType,
        ImageStatus imageStatus,
        String uploadedBy,
        String relatedEntityId,
        String relatedEntityType
    )
    {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (imageType != null)
        {
            builder.and(qImage.imageType.eq(imageType));
        }
        
        if (imageStatus != null)
        {
            builder.and(qImage.imageStatus.eq(imageStatus));
        }
        
        if (uploadedBy != null && !uploadedBy.isEmpty())
        {
            builder.and(qImage.uploadedBy.eq(uploadedBy));
        }
        
        if (relatedEntityId != null && !relatedEntityId.isEmpty())
        {
            builder.and(qImage.relatedEntityId.eq(relatedEntityId));
        }
        
        if (relatedEntityType != null && !relatedEntityType.isEmpty())
        {
            builder.and(qImage.relatedEntityType.eq(relatedEntityType));
        }
        
        return builder;
    }
    
    /**
     * 정렬 조건 생성
     */
    private List<OrderSpecifier<?>> createOrderSpecifiers(Sort sort)
    {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        
        sort.forEach(order -> {
            String property = order.getProperty();
            boolean isAscending = order.isAscending();
            
            switch (property)
            {
                case "createdAt":
                    orderSpecifiers.add(isAscending ? qImage.createdAt.asc() : qImage.createdAt.desc());
                    break;
                case "updatedAt":
                    orderSpecifiers.add(isAscending ? qImage.updatedAt.asc() : qImage.updatedAt.desc());
                    break;
                case "fileSize":
                    orderSpecifiers.add(isAscending ? qImage.fileSize.asc() : qImage.fileSize.desc());
                    break;
                case "imageId":
                    orderSpecifiers.add(isAscending ? qImage.imageId.asc() : qImage.imageId.desc());
                    break;
                default:
                    orderSpecifiers.add(qImage.createdAt.desc());
                    break;
            }
        });
        
        if (orderSpecifiers.isEmpty())
        {
            orderSpecifiers.add(qImage.createdAt.desc());
        }
        
        return orderSpecifiers;
    }
}

