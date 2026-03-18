package com.cami.cami_springboot.api.order.entity;

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

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "option_label", length = 100)
    private String optionLabel;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public OrderItem(Order order, Long productId, String productName, Integer quantity, String optionLabel, BigDecimal price) {
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity != null ? quantity : 1;
        this.optionLabel = optionLabel;
        this.price = price;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
