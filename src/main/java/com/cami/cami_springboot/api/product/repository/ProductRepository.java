package com.cami.cami_springboot.api.product.repository;

import com.cami.cami_springboot.api.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBrandId(Long brandId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByPetType(String petType);
    List<Product> findByApprovalStatus(String status);
    List<Product> findByPartnerId(String partnerId);
}
