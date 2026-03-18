package com.cami.cami_springboot.api.common.util;

import com.cami.cami_springboot.api.common.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * 페이지네이션 응답 변환 유틸리티
 * Spring Data의 Page 객체를 커스텀 PageResponse로 변환
 */
public class PageResponseUtil
{
    
    /**
     * Spring Page를 PageResponse로 변환
     * 
     * @param page Spring Data의 Page 객체
     * @param <T> 데이터 타입
     * @return PageResponse 객체
     */
        public static <T> PageResponse<T> toPageResponse(Page<T> page)
    {
        PageResponse.PaginationInfo pagination = PageResponse.PaginationInfo.builder()
            .isFirst(page.isFirst())
            .isLast(page.isLast())
            .totalPages(page.getTotalPages())
            .pageSize(page.getSize())
            .hasPrevious(page.hasPrevious())
            .hasNext(page.hasNext())
            .currentPage(page.getNumber())
            .totalElements(page.getTotalElements())
            .build();
        
        return PageResponse.<T>builder()
            .content(page.getContent())
            .pagination(pagination)
            .build();
    }
    
    /**
     * Spring Page를 PageResponse로 변환하고 특정 키로 래핑
     * 
     * @param page Spring Data의 Page 객체
     * @param wrapperKey 래핑할 키 이름 (예: "usersAccountList", "productList")
     * @param <T> 데이터 타입
     * @return 래핑된 Map 객체
     */
        public static <T> Map<String, Object> toWrappedPageResponse(Page<T> page, String wrapperKey)
    {
        PageResponse<T> pageResponse = toPageResponse(page);
        
        Map<String, Object> result = new HashMap<>();
        result.put(wrapperKey, pageResponse);
        
        return result;
    }
    
    /**
     * Spring Page를 PageResponse로 변환하고 기본 키로 래핑
     * 기본 키는 "list"
     * 
     * @param page Spring Data의 Page 객체
     * @param <T> 데이터 타입
     * @return 래핑된 Map 객체
     */
        public static <T> Map<String, Object> toWrappedPageResponse(Page<T> page)
    {
        return toWrappedPageResponse(page, "list");
    }
}
