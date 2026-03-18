package com.cami.cami_springboot.api.notice.repository;

import com.cami.cami_springboot.api.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
