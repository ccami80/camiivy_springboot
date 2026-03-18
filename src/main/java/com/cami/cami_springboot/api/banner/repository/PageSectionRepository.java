package com.cami.cami_springboot.api.banner.repository;

import com.cami.cami_springboot.api.banner.entity.PageSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageSectionRepository extends JpaRepository<PageSection, Long> {
    List<PageSection> findByPageTypeOrderByDisplayOrderAsc(String pageType);
}
