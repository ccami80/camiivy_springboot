package com.cami.cami_springboot.api.cart.controller;

import com.cami.cami_springboot.api.cart.entity.Cart;
import com.cami.cami_springboot.api.cart.entity.CartItem;
import com.cami.cami_springboot.api.cart.repository.CartItemRepository;
import com.cami.cami_springboot.api.cart.repository.CartRepository;
import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "장바구니 API (로그인 필요)")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @GetMapping("/cart")
    @Operation(summary = "장바구니 조회")
    public ResponseEntity<CommonResponse> getCart(@RequestAttribute("userId") String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("items", cart.getItems())));
    }

    @PostMapping("/cart")
    @Operation(summary = "장바구니 담기")
    public ResponseEntity<CommonResponse> addToCart(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = body.get("quantity") != null ? (Integer) body.get("quantity") : 1;
        String optionLabel = (String) body.get("optionLabel");

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(productId)
                .quantity(quantity)
                .optionLabel(optionLabel)
                .build();
        cart.addItem(item);
        cartItemRepository.save(item);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("items", cart.getItems())));
    }

    @PatchMapping("/cart/items/{itemId}")
    @Operation(summary = "장바구니 수량 수정")
    public ResponseEntity<CommonResponse> updateCartItem(
            @RequestAttribute("userId") String userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        Cart cart = item.getCart();
        if (!cart.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        Integer quantity = body.get("quantity");
        if (quantity != null && quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
            return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("removed", true)));
        }
        item.updateQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok(new CommonResponse(true, "OK", item));
    }

    @DeleteMapping("/cart/items/{itemId}")
    @Operation(summary = "장바구니 항목 삭제")
    public ResponseEntity<Void> deleteCartItem(
            @RequestAttribute("userId") String userId,
            @PathVariable Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!item.getCart().getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        item.getCart().removeItem(item);
        cartItemRepository.delete(item);
        return ResponseEntity.ok().build();
    }
}
