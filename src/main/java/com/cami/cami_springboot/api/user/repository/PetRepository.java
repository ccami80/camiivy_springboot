package com.cami.cami_springboot.api.user.repository;

import com.cami.cami_springboot.api.user.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserId(String userId);
}
