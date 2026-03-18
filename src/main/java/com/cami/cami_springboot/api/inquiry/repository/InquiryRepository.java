package com.cami.cami_springboot.api.inquiry.repository;

import com.cami.cami_springboot.api.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUserIdOrderByCreatedAtDesc(String userId);
}
