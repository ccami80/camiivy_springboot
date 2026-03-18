package com.cami.cami_springboot.api.admin.controller;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.order.repository.OrderRepository;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "관리자 API (ADMIN 권한 필요)")
public class AdminController {

    private final PartnerRepository partnerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "대시보드")
    public ResponseEntity<CommonResponse> getDashboard(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }

    @GetMapping("/partners")
    @Operation(summary = "파트너 목록")
    public ResponseEntity<CommonResponse> getPartners(@RequestParam(required = false) String status) {
        List<Partner> partners = status != null ? partnerRepository.findByStatus(status) : partnerRepository.findAll();
        return ResponseEntity.ok(new CommonResponse(true, "OK", partners));
    }

    @GetMapping("/partners/{id}")
    @Operation(summary = "파트너 상세")
    public ResponseEntity<CommonResponse> getPartner(@PathVariable String id) {
        Partner partner = partnerRepository.findById(id).orElse(null);
        return ResponseEntity.ok(new CommonResponse(true, "OK", partner));
    }

    @PatchMapping("/partners/{id}")
    @Operation(summary = "파트너 상태 변경")
    public ResponseEntity<CommonResponse> updatePartner(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        Partner partner = partnerRepository.findById(id).orElse(null);
        if (partner != null && body.get("status") != null) {
            partner.updateStatus(body.get("status"));
            partnerRepository.save(partner);
        }
        return ResponseEntity.ok(new CommonResponse(true, "OK", partner));
    }

    @GetMapping("/products")
    @Operation(summary = "상품 목록")
    public ResponseEntity<CommonResponse> getProducts(@RequestParam(required = false) String status) {
        List<Product> products = status != null ? productRepository.findByApprovalStatus(status) : productRepository.findAll();
        return ResponseEntity.ok(new CommonResponse(true, "OK", products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "상품 상세")
    public ResponseEntity<CommonResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", productRepository.findById(id).orElse(null)));
    }

    @PatchMapping("/products/{id}")
    @Operation(summary = "상품 승인/정렬")
    public ResponseEntity<CommonResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            if (body.get("approvalStatus") != null) {
                product.updateApprovalStatus(body.get("approvalStatus").toString());
            }
            if (body.get("displayOrder") != null) {
                product.updateDisplayOrder(Integer.valueOf(body.get("displayOrder").toString()));
            }
            productRepository.save(product);
        }
        return ResponseEntity.ok(new CommonResponse(true, "OK", product));
    }

    @GetMapping("/orders")
    @Operation(summary = "주문 목록")
    public ResponseEntity<CommonResponse> getOrders(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", orderRepository.findAll()));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "주문 상세")
    public ResponseEntity<CommonResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", orderRepository.findById(id).orElse(null)));
    }

    @PatchMapping("/orders/{id}")
    @Operation(summary = "주문 상태 변경")
    public ResponseEntity<CommonResponse> updateOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        var order = orderRepository.findById(id);
        if (order.isPresent() && body.get("status") != null) {
            order.get().updateStatus(body.get("status"));
            orderRepository.save(order.get());
        }
        return ResponseEntity.ok(new CommonResponse(true, "OK", order.orElse(null)));
    }
}
