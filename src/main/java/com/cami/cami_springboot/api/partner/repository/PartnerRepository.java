package com.cami.cami_springboot.api.partner.repository;

import com.cami.cami_springboot.api.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, String> {
    Optional<Partner> findByUserId(String userId);
    List<Partner> findByStatus(String status);
}
