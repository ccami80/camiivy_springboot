package com.cami.cami_springboot.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정
 * 
 * @EnableJpaAuditing: JPA Auditing 활성화 (필수)
 * - @CreatedDate: 생성 일시 자동 설정
 * - @LastModifiedDate: 수정 일시 자동 설정
 * 
 * 모든 엔티티는 @EntityListeners(AuditingEntityListener.class) 필수
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig
{
    // JPA Auditing 활성화
}

