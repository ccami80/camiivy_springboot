package com.cami.cami_springboot.api.partner.controller;

import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.partner.entity.Partner;
import com.cami.cami_springboot.api.partner.repository.PartnerRepository;
import com.cami.cami_springboot.api.product.entity.Product;
import com.cami.cami_springboot.api.product.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partner")
@RequiredArgsConstructor
@Tag(name = "Partner API", description = "파트너 API (로그인 필요)")
public class PartnerController {

    private final PartnerRepository partnerRepository;
    private final ProductRepository productRepository;

    @GetMapping("/me")
    @Operation(summary = "파트너 정보")
    public ResponseEntity<CommonResponse> getMe(@RequestAttribute("userId") String userId) {
        Partner partner = partnerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return ResponseEntity.ok(new CommonResponse(true, "OK", partner));
    }

    @PatchMapping("/me")
    @Operation(summary = "파트너 정보 수정")
    public ResponseEntity<CommonResponse> updateMe(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, String> body) {
        Partner partner = partnerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        partner.update(
                body.get("companyName"),
                body.get("contactName"),
                body.get("contactPhone"));
        partnerRepository.save(partner);
        return ResponseEntity.ok(new CommonResponse(true, "OK", partner));
    }

    @GetMapping("/settlement")
    @Operation(summary = "정산 정보")
    public ResponseEntity<CommonResponse> getSettlement(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }

    @GetMapping("/orders")
    @Operation(summary = "파트너 주문 목록")
    public ResponseEntity<CommonResponse> getOrders(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", List.of()));
    }

    @GetMapping("/products")
    @Operation(summary = "파트너 상품 목록")
    public ResponseEntity<CommonResponse> getProducts(@RequestAttribute("userId") String userId) {
        List<Product> products = productRepository.findByPartnerId(userId);
        return ResponseEntity.ok(new CommonResponse(true, "OK", products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "파트너 상품 상세")
    public ResponseEntity<CommonResponse> getProduct(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!userId.equals(product.getPartnerId())) throw new CustomException(ErrorCode.FORBIDDEN);
        return ResponseEntity.ok(new CommonResponse(true, "OK", product));
    }

    @PostMapping("/products")
    @Operation(summary = "상품 등록")
    public ResponseEntity<CommonResponse> createProduct(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, Object> body) {
        Product product = Product.builder()
                .name((String) body.get("name"))
                .description((String) body.get("description"))
                .brandId(body.get("brandId") != null ? Long.valueOf(body.get("brandId").toString()) : null)
                .categoryId(body.get("categoryId") != null ? Long.valueOf(body.get("categoryId").toString()) : null)
                .petType((String) body.get("petType"))
                .price(body.get("price") != null ? new java.math.BigDecimal(body.get("price").toString()) : null)
                .salePrice(body.get("salePrice") != null ? new java.math.BigDecimal(body.get("salePrice").toString()) : null)
                .imageUrls((String) body.get("imageUrls"))
                .color((String) body.get("color"))
                .approvalStatus("PENDING")
                .partnerId(userId)
                .build();
        product = productRepository.save(product);
        return ResponseEntity.ok(new CommonResponse(true, "OK", product));
    }

    @PatchMapping("/products/{id}")
    @Operation(summary = "상품 수정")
    public ResponseEntity<CommonResponse> updateProduct(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!userId.equals(product.getPartnerId())) throw new CustomException(ErrorCode.FORBIDDEN);
        product.update(
                (String) body.get("name"),
                (String) body.get("description"),
                body.get("brandId") != null ? Long.valueOf(body.get("brandId").toString()) : null,
                body.get("categoryId") != null ? Long.valueOf(body.get("categoryId").toString()) : null,
                (String) body.get("petType"),
                body.get("price") != null ? new java.math.BigDecimal(body.get("price").toString()) : null,
                body.get("salePrice") != null ? new java.math.BigDecimal(body.get("salePrice").toString()) : null,
                (String) body.get("imageUrls"),
                (String) body.get("color"));
        productRepository.save(product);
        return ResponseEntity.ok(new CommonResponse(true, "OK", product));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "상품 삭제")
    public ResponseEntity<CommonResponse> deleteProduct(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!userId.equals(product.getPartnerId())) throw new CustomException(ErrorCode.FORBIDDEN);
        productRepository.delete(product);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("message", "삭제되었습니다")));
    }

    @PostMapping("/products/{id}/generate-detail")
    @Operation(summary = "상품 상세 AI 생성")
    public ResponseEntity<CommonResponse> generateDetail(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!userId.equals(product.getPartnerId())) throw new CustomException(ErrorCode.FORBIDDEN);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }

    @GetMapping("/inquiries")
    @Operation(summary = "파트너 문의 목록")
    public ResponseEntity<CommonResponse> getInquiries(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", List.of()));
    }

    @GetMapping("/inquiries/{id}")
    @Operation(summary = "파트너 문의 상세")
    public ResponseEntity<CommonResponse> getInquiry(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "파트너 파일 업로드")
    public ResponseEntity<CommonResponse> upload(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("urls", List.of())));
    }
}
