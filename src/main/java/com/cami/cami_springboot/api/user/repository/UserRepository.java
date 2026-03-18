package com.cami.cami_springboot.api.user.repository;

import com.cami.cami_springboot.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom
{
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUserId(String userId);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
}
