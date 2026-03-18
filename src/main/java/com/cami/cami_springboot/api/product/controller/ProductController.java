package com.cami.cami_springboot.api.product.controller;

import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.product.entity.Product;
import com.cami.cami_springboot.api.product.entity.ProductInquiry;
import com.cami.cami_springboot.api.product.repository.ProductInquiryRepository;
import com.cami.cami_springboot.api.product.request.ProductInquiryRequest;
import com.cami.cami_springboot.api.product.repository.ProductRepository;
import com.cami.cami_springboot.api.user.entity.Review;
import com.cami.cami_springboot.api.user.repository.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "상품 조회 API")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductInquiryRepository productInquiryRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/products")
    @Operation(summary = "상품 목록", description = "brand, petType, categoryId, sort, q, minPrice, maxPrice, color, includeVariants로 필터링")
    public ResponseEntity<CommonResponse> getProducts(
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Boolean includeVariants) {
        List<Product> all = productRepository.findByApprovalStatus("APPROVED");
        List<Product> filtered = all.stream()
                .filter(p -> brand == null || (p.getBrandId() != null && p.getBrandId().equals(brand)))
                .filter(p -> petType == null || petType.isBlank() || petType.equals(p.getPetType()))
                .filter(p -> categoryId == null || (p.getCategoryId() != null && p.getCategoryId().equals(categoryId)))
                .filter(p -> q == null || q.isBlank() || (p.getName() != null && p.getName().contains(q)))
                .filter(p -> minPrice == null || (p.getSalePrice() != null ? p.getSalePrice() : p.getPrice()).compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || (p.getSalePrice() != null ? p.getSalePrice() : p.getPrice()).compareTo(maxPrice) <= 0)
                .filter(p -> color == null || color.isBlank() || (p.getColor() != null && p.getColor().equals(color)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CommonResponse(true, "OK", filtered));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "상품 상세")
    public ResponseEntity<CommonResponse> getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return ResponseEntity.ok(new CommonResponse(true, "OK", product));
    }

    @GetMapping("/products/{id}/reviews")
    @Operation(summary = "상품 리뷰 목록")
    public ResponseEntity<CommonResponse> getProductReviews(
            @PathVariable Long id,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String petType) {
        List<Review> reviews = reviewRepository.findByProductId(id);
        List<Review> filtered = reviews.stream()
                .filter(r -> bodyType == null || bodyType.isBlank() || (r.getBodyType() != null && r.getBodyType().equals(bodyType)))
                .filter(r -> petType == null || petType.isBlank() || (r.getPetType() != null && r.getPetType().equals(petType)))
                .collect(Collectors.toList());
        Map<String, Object> summary = new HashMap<>();
        summary.put("reviews", filtered);
        summary.put("summary", Map.of("count", filtered.size(), "avgRating", filtered.isEmpty() ? 0 : filtered.stream().mapToInt(Review::getRating).average().orElse(0)));
        return ResponseEntity.ok(new CommonResponse(true, "OK", summary));
    }

    @GetMapping("/products/{id}/inquiries")
    @Operation(summary = "상품 문의 목록")
    public ResponseEntity<CommonResponse> getProductInquiries(@PathVariable Long id) {
        List<ProductInquiry> inquiries = productInquiryRepository.findByProductId(id);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("inquiries", inquiries)));
    }

    @PostMapping("/products/{id}/inquiries")
    @Operation(summary = "상품 문의 등록")
    public ResponseEntity<CommonResponse> createProductInquiry(
            @PathVariable Long id,
            @RequestBody ProductInquiryRequest request,
            @RequestAttribute(value = "userId", required = false) String userId) {
        ProductInquiry inquiry = ProductInquiry.builder()
                .productId(id)
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .emailReply(request.getEmailReply() != null ? request.getEmailReply() : false)
                .secret(request.getSecret() != null ? request.getSecret() : false)
                .build();
        ProductInquiry saved = productInquiryRepository.save(inquiry);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("id", saved.getId(), "message", "등록되었습니다")));
    }

    @GetMapping("/products/{id}/category-best")
    @Operation(summary = "같은 카테고리 베스트 상품")
    public ResponseEntity<CommonResponse> getCategoryBest(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        List<Product> list = product.getCategoryId() != null
                ? productRepository.findByCategoryId(product.getCategoryId()).stream().limit(10).collect(Collectors.toList())
                : List.of();
        return ResponseEntity.ok(new CommonResponse(true, "OK", list));
    }

    @GetMapping("/products/{id}/recommended")
    @Operation(summary = "추천 상품")
    public ResponseEntity<CommonResponse> getRecommended(@PathVariable Long id) {
        List<Product> list = productRepository.findByApprovalStatus("APPROVED").stream().limit(10).collect(Collectors.toList());
        return ResponseEntity.ok(new CommonResponse(true, "OK", list));
    }
}
