package com.cami.cami_springboot.api.product.controller;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.product.entity.Category;
import com.cami.cami_springboot.api.product.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "카테고리 조회 API")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/categories")
    @Operation(summary = "카테고리 목록", description = "petType으로 필터링 가능")
    public ResponseEntity<CommonResponse> getCategories(@RequestParam(required = false) String petType) {
        List<Category> categories = petType != null && !petType.isBlank()
                ? categoryRepository.findByPetTypeOrderByDisplayOrderAsc(petType)
                : categoryRepository.findAllByOrderByDisplayOrderAsc();
        return ResponseEntity.ok(new CommonResponse(true, "OK", categories));
    }
}
