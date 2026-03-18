package com.culwonder.leeds_profile_springboot_core.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 제네릭 페이지 응답 DTO
 * 모든 도메인에서 재사용 가능한 페이지네이션 응답 형식
 * 
 * @param <T> 응답 데이터 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지 응답")
public class PageResponse<T>
{
    
    @Schema(description = "데이터 목록")
    private List<T> content;
    
    @Schema(description = "페이지네이션 정보")
    private PaginationInfo pagination;
    
    /**
     * 페이지네이션 정보 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "페이지네이션 정보")
    public static class PaginationInfo
    {
        
        @Schema(description = "첫 페이지 여부", example = "true")
        private boolean isFirst;
        
        @Schema(description = "마지막 페이지 여부", example = "false")
        private boolean isLast;
        
        @Schema(description = "전체 페이지 수", example = "5")
        private int totalPages;
        
        @Schema(description = "페이지 크기", example = "10")
        private int pageSize;
        
        @Schema(description = "이전 페이지 존재 여부", example = "false")
        private boolean hasPrevious;
        
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        private boolean hasNext;
        
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int currentPage;
        
        @Schema(description = "전체 데이터 개수", example = "50")
        private long totalElements;
    }
}

