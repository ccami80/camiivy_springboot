package com.cami.cami_springboot.api.product.entity;

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
@Table(name = "product_inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column(name = "email_reply")
    private Boolean emailReply = false;

    @Column(name = "is_secret")
    private Boolean secret = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ProductInquiry(Long productId, String userId, String title, String content, Boolean emailReply, Boolean secret) {
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.emailReply = emailReply != null ? emailReply : false;
        this.secret = secret != null ? secret : false;
    }
}
