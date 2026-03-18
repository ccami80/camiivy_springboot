# 광고 잔액 모듈 이전 마이그레이션 가이드

## 개요

User 모듈의 `ad_balance` 컬럼을 광고 모듈의 `ad_module_balance` 테이블로 이전하는 마이그레이션 가이드입니다.

## 변경 사항

### 삭제되는 컬럼
- `user_module.ad_balance` 컬럼 삭제

### 새로 생성되는 테이블
- `ad_module_balance` 테이블 생성

## 마이그레이션 스크립트

### 1. 데이터 마이그레이션 (기존 데이터 보존)

```sql
-- 1. ad_module_balance 테이블 생성
CREATE TABLE ad_module_balance (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '사용자 ID (User와 1:1 관계)',
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '광고 잔액',
    created_id VARCHAR(50) COMMENT '생성자 ID',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    updated_id VARCHAR(50) COMMENT '수정자 ID',
    updated_at DATETIME NOT NULL COMMENT '수정 일시',
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user_module(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='광고 잔액 테이블';

-- 2. 기존 데이터 마이그레이션 (user_module의 ad_balance → ad_module_balance)
INSERT INTO ad_module_balance (user_id, balance, created_id, created_at, updated_id, updated_at)
SELECT 
    user_id,
    COALESCE(ad_balance, 0) as balance,
    created_id,
    created_at,
    updated_id,
    updated_at
FROM user_module
WHERE ad_balance IS NOT NULL OR ad_balance != 0
ON DUPLICATE KEY UPDATE
    balance = VALUES(balance),
    updated_id = VALUES(updated_id),
    updated_at = VALUES(updated_at);

-- 3. ad_balance가 NULL이거나 0인 사용자도 기본 레코드 생성 (선택사항)
INSERT INTO ad_module_balance (user_id, balance, created_id, created_at, updated_id, updated_at)
SELECT 
    user_id,
    0 as balance,
    created_id,
    created_at,
    updated_id,
    updated_at
FROM user_module
WHERE user_id NOT IN (SELECT user_id FROM ad_module_balance)
ON DUPLICATE KEY UPDATE
    balance = VALUES(balance);

-- 4. user_module 테이블에서 ad_balance 컬럼 삭제
ALTER TABLE user_module DROP COLUMN ad_balance;
```

### 2. 롤백 스크립트 (필요시)

```sql
-- 1. user_module 테이블에 ad_balance 컬럼 복구
ALTER TABLE user_module 
ADD COLUMN ad_balance DECIMAL(10, 2) DEFAULT 0 COMMENT '광고 잔액';

-- 2. ad_module_balance 데이터를 user_module로 복구
UPDATE user_module u
INNER JOIN ad_module_balance a ON u.user_id = a.user_id
SET u.ad_balance = a.balance;

-- 3. ad_module_balance 테이블 삭제 (선택사항)
DROP TABLE IF EXISTS ad_module_balance;
```

## 주의사항

### 1. 데이터 무결성
- 마이그레이션 전에 **반드시 백업**을 수행하세요.
- 마이그레이션 중에는 애플리케이션을 중지하는 것을 권장합니다.

### 2. 트랜잭션 처리
- 마이그레이션은 트랜잭션으로 묶어서 실행하세요.
- 실패 시 롤백이 가능하도록 준비하세요.

### 3. 데이터 검증
- 마이그레이션 후 데이터 일치 여부를 확인하세요:

```sql
-- 데이터 검증 쿼리
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN ad_balance IS NOT NULL THEN 1 ELSE 0 END) as users_with_ad_balance_old,
    (SELECT COUNT(*) FROM ad_module_balance) as users_with_ad_balance_new
FROM user_module;
```

### 4. 애플리케이션 배포 순서
1. **1단계**: 새 코드 배포 (AdBalance 엔티티 사용)
2. **2단계**: 마이그레이션 스크립트 실행
3. **3단계**: 데이터 검증

또는

1. **1단계**: 마이그레이션 스크립트 실행 (데이터만 이전)
2. **2단계**: 새 코드 배포 (AdBalance 엔티티 사용)

## 테이블 구조

### ad_module_balance 테이블

```sql
CREATE TABLE ad_module_balance (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '사용자 ID (User와 1:1 관계)',
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '광고 잔액',
    created_id VARCHAR(50) COMMENT '생성자 ID',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    updated_id VARCHAR(50) COMMENT '수정자 ID',
    updated_at DATETIME NOT NULL COMMENT '수정 일시',
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user_module(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='광고 잔액 테이블';
```

## 엔티티 매핑

### AdBalance 엔티티

```java
@Entity
@Table(name = "ad_module_balance")
public class AdBalance {
    @Id
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private User user;
    
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    // Audit 컬럼들...
}
```

## 검증 체크리스트

- [ ] 마이그레이션 전 백업 완료
- [ ] ad_module_balance 테이블 생성 확인
- [ ] 기존 데이터 마이그레이션 완료
- [ ] 데이터 일치 여부 확인
- [ ] user_module.ad_balance 컬럼 삭제 확인
- [ ] 애플리케이션 정상 동작 확인
- [ ] 광고 잔액 조회/충전/차감 기능 테스트

---

**작성일**: 2024-01-15  
**버전**: 1.0

