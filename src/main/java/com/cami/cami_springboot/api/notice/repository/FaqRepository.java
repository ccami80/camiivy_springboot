package com.cami.cami_springboot.api.notice.repository;

import com.cami.cami_springboot.api.notice.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findAllByOrderByDisplayOrderAsc();
}
