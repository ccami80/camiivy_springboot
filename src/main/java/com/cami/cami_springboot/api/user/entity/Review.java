package com.cami.cami_springboot.api.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(nullable = false)
    private Integer rating = 5;

    @Column(length = 2000)
    private String content;

    @Column(name = "body_type", length = 20)
    private String bodyType;

    @Column(name = "pet_type", length = 20)
    private String petType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Review(String userId, Long productId, Long orderItemId, Integer rating, String content, String bodyType, String petType) {
        this.userId = userId;
        this.productId = productId;
        this.orderItemId = orderItemId;
        this.rating = rating != null ? rating : 5;
        this.content = content;
        this.bodyType = bodyType;
        this.petType = petType;
    }

    public void update(Integer rating, String content) {
        if (rating != null) this.rating = rating;
        if (content != null) this.content = content;
    }
}
