package com.cami.cami_springboot.api.inquiry.entity;

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
@Table(name = "inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "inquiry_type", length = 50)
    private String inquiryType;

    @Column(name = "order_id")
    private Long orderId;

    @Column(length = 2000)
    private String content;

    @Column(name = "image_urls", length = 2000)
    private String imageUrls;

    @Column(length = 20)
    private String status = "PENDING";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Inquiry(String userId, String inquiryType, Long orderId, String content, String imageUrls) {
        this.userId = userId;
        this.inquiryType = inquiryType;
        this.orderId = orderId;
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
