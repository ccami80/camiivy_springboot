package com.cami.cami_springboot.api.banner.repository;

import com.cami.cami_springboot.api.banner.entity.HomeSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeSectionRepository extends JpaRepository<HomeSection, Long> {
    List<HomeSection> findBySectionTypeOrderByDisplayOrderAsc(String sectionType);
}
