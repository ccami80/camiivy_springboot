package com.culwonder.leeds_profile_springboot_core.api.image.repository;

import com.culwonder.leeds_profile_springboot_core.api.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 이미지 리포지토리
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, String>, ImageRepositoryCustom
{
}

