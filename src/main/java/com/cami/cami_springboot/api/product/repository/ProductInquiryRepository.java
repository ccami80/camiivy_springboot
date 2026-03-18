package com.cami.cami_springboot.api.product.repository;

import com.cami.cami_springboot.api.product.entity.ProductInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInquiryRepository extends JpaRepository<ProductInquiry, Long> {
    List<ProductInquiry> findByProductId(Long productId);
}
