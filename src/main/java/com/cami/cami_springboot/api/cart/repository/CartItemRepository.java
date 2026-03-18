package com.cami.cami_springboot.api.cart.repository;

import com.cami.cami_springboot.api.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
