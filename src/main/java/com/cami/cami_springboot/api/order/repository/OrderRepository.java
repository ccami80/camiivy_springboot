package com.cami.cami_springboot.api.order.repository;

import com.cami.cami_springboot.api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Order> findByOrderNumberAndRecipientPhone(String orderNumber, String phone);
}
