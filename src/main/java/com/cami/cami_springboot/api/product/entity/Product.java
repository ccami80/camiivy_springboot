package com.cami.cami_springboot.api.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "pet_type", length = 20)
    private String petType;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "image_urls", length = 2000)
    private String imageUrls; // JSON array or comma-separated URLs

    @Column(length = 50)
    private String color;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "partner_id", length = 50)
    private String partnerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Product(String name, String description, Long brandId, Long categoryId, String petType,
                    BigDecimal price, BigDecimal salePrice, String imageUrls, String color,
                    Integer displayOrder, String approvalStatus, String partnerId) {
        this.name = name;
        this.description = description;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.petType = petType;
        this.price = price;
        this.salePrice = salePrice;
        this.imageUrls = imageUrls;
        this.color = color;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.approvalStatus = approvalStatus != null ? approvalStatus : "PENDING";
        this.partnerId = partnerId;
    }

    public void update(String name, String description, Long brandId, Long categoryId, String petType,
                       BigDecimal price, BigDecimal salePrice, String imageUrls, String color) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (brandId != null) this.brandId = brandId;
        if (categoryId != null) this.categoryId = categoryId;
        if (petType != null) this.petType = petType;
        if (price != null) this.price = price;
        if (salePrice != null) this.salePrice = salePrice;
        if (imageUrls != null) this.imageUrls = imageUrls;
        if (color != null) this.color = color;
    }

    public void updateApprovalStatus(String approvalStatus) {
        if (approvalStatus != null) this.approvalStatus = approvalStatus;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        if (displayOrder != null) this.displayOrder = displayOrder;
    }
}
