package com.culwonder.leeds_profile_springboot_core.api.auth.repository;

import com.culwonder.leeds_profile_springboot_core.api.auth.code.AuthEventType;
import com.culwonder.leeds_profile_springboot_core.api.auth.entity.AuthHistory;
import com.culwonder.leeds_profile_springboot_core.api.auth.request.AuthListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.auth.response.AuthListResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthRepositoryCustom
{
    // Auth 조회
    List<AuthListResponse> customSearchAuthList(AuthListSearchRequest request, Pageable pageable);
    long customSearchAuthCount(AuthListSearchRequest request);
    
    // AuthHistory 조회
    List<AuthHistory> customSelectAuthHistoryByUserId(String userId);
    List<AuthHistory> customSelectAuthHistoryTop10ByUserId(String userId);
    List<AuthHistory> customSelectAuthHistoryByUserIdAndEventType(String userId, AuthEventType eventType);
    List<AuthHistory> customSelectAuthHistoryByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
