package com.cami.cami_springboot.api.banner.repository;

import com.cami.cami_springboot.api.banner.entity.CurationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurationItemRepository extends JpaRepository<CurationItem, Long> {
    List<CurationItem> findAllByOrderByDisplayOrderAsc();
}
