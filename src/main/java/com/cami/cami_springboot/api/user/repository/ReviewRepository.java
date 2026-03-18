package com.cami.cami_springboot.api.user.repository;

import com.cami.cami_springboot.api.user.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(String userId);
    List<Review> findByProductId(Long productId);
    Optional<Review> findByUserIdAndProductIdAndOrderItemId(String userId, Long productId, Long orderItemId);
}
