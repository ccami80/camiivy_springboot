package com.cami.cami_springboot.api.product.controller;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.product.entity.Brand;
import com.cami.cami_springboot.api.product.repository.BrandRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Brand API", description = "브랜드 조회 API")
public class BrandController {

    private final BrandRepository brandRepository;

    @GetMapping("/brands")
    @Operation(summary = "브랜드 목록")
    public ResponseEntity<CommonResponse> getBrands() {
        List<Brand> brands = brandRepository.findAll();
        return ResponseEntity.ok(new CommonResponse(true, "OK", brands));
    }
}
