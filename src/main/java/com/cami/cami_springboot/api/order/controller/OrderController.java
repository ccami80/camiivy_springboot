package com.cami.cami_springboot.api.order.controller;

import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.order.entity.Order;
import com.cami.cami_springboot.api.order.entity.OrderItem;
import com.cami.cami_springboot.api.order.repository.OrderRepository;
import com.cami.cami_springboot.api.product.entity.Product;
import com.cami.cami_springboot.api.product.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "주문 API")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @PostMapping("/orders")
    @Operation(summary = "주문 생성")
    public ResponseEntity<CommonResponse> createOrder(
            @RequestAttribute(value = "userId", required = false) String userId,
            @RequestBody Map<String, Object> body) {
        String recipientName = (String) body.get("recipientName");
        String recipientPhone = (String) body.get("recipientPhone");
        String recipientEmail = (String) body.get("recipientEmail");
        String zipCode = (String) body.get("zipCode");
        String address = (String) body.get("address");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        if (items == null || items.isEmpty()) throw new CustomException(ErrorCode.INVALID_INPUT);

        BigDecimal totalAmount = BigDecimal.ZERO;
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6);

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId != null ? userId : "guest")
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .recipientEmail(recipientEmail)
                .zipCode(zipCode)
                .address(address)
                .totalAmount(BigDecimal.ZERO)
                .status("PENDING")
                .build();

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer qty = item.get("quantity") != null ? (Integer) item.get("quantity") : 1;
            Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
            BigDecimal price = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
            if (price != null) totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(qty)));
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(productId)
                    .productName(product.getName())
                    .quantity(qty)
                    .optionLabel((String) item.get("optionLabel"))
                    .price(price)
                    .build();
            order.addItem(orderItem);
        }
        order = orderRepository.save(order);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("id", order.getId(), "orderNumber", orderNumber)));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "주문 상세")
    public ResponseEntity<CommonResponse> getOrder(
            @PathVariable Long id,
            @RequestAttribute(value = "userId", required = false) String userId) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (userId != null && !order.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        return ResponseEntity.ok(new CommonResponse(true, "OK", order));
    }

    @GetMapping("/orders/lookup")
    @Operation(summary = "주문 조회 (비로그인)")
    public ResponseEntity<CommonResponse> lookupOrder(
            @RequestParam String orderNumber,
            @RequestParam String phone) {
        Optional<Order> opt = orderRepository.findByOrderNumberAndRecipientPhone(orderNumber, phone);
        if (opt.isEmpty()) return ResponseEntity.ok(new CommonResponse(false, "주문을 찾을 수 없습니다", Map.of("error", "NOT_FOUND")));
        return ResponseEntity.ok(new CommonResponse(true, "OK", opt.get()));
    }

    @PostMapping("/orders/{id}/pay")
    @Operation(summary = "결제")
    public ResponseEntity<CommonResponse> payOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        order.updateStatus("PAID");
        orderRepository.save(order);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }

    @PostMapping("/orders/{id}/cancel")
    @Operation(summary = "주문 취소")
    public ResponseEntity<CommonResponse> cancelOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        order.updateStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }
}
