package com.cami.cami_springboot.api.product.repository;

import com.cami.cami_springboot.api.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
