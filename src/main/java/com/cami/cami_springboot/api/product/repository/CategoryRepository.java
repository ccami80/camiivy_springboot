package com.cami.cami_springboot.api.product.repository;

import com.cami.cami_springboot.api.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByPetTypeOrderByDisplayOrderAsc(String petType);
    List<Category> findAllByOrderByDisplayOrderAsc();
}
