package com.cami.cami_springboot.api.cart.entity;

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
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "option_label", length = 100)
    private String optionLabel;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CartItem(Cart cart, Long productId, Integer quantity, String optionLabel) {
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
        this.optionLabel = optionLabel;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        }
    }
}
