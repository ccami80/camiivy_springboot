package com.cami.cami_springboot.api.user.controller;

import com.cami.cami_springboot.api.common.exception.CustomException;
import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.order.entity.Order;
import com.cami.cami_springboot.api.order.repository.OrderRepository;
import com.cami.cami_springboot.api.user.entity.*;
import com.cami.cami_springboot.api.user.repository.*;
import com.cami.cami_springboot.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 API (로그인 필요)")
public class UserApiController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final ReviewRepository reviewRepository;
    private final PetRepository petRepository;

    @GetMapping("/me")
    @Operation(summary = "내 정보")
    public ResponseEntity<CommonResponse> getMe(@RequestAttribute("userId") String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(new CommonResponse(true, "OK", user));
    }

    @GetMapping("/orders")
    @Operation(summary = "내 주문 목록")
    public ResponseEntity<CommonResponse> getMyOrders(@RequestAttribute("userId") String userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(new CommonResponse(true, "OK", orders));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "내 주문 상세")
    public ResponseEntity<CommonResponse> getMyOrder(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!order.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        return ResponseEntity.ok(new CommonResponse(true, "OK", order));
    }

    @GetMapping("/wishlist")
    @Operation(summary = "위시리스트")
    public ResponseEntity<CommonResponse> getWishlist(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", wishlistRepository.findByUserId(userId)));
    }

    @PostMapping("/wishlist/{productId}")
    @Operation(summary = "위시리스트 추가")
    public ResponseEntity<CommonResponse> addWishlist(
            @RequestAttribute("userId") String userId,
            @PathVariable Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("success", true)));
        }
        Wishlist w = Wishlist.builder().userId(userId).productId(productId).build();
        wishlistRepository.save(w);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("success", true)));
    }

    @GetMapping("/reviews")
    @Operation(summary = "내 리뷰 목록")
    public ResponseEntity<CommonResponse> getMyReviews(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", reviewRepository.findByUserId(userId)));
    }

    @GetMapping("/reviews/{id}")
    @Operation(summary = "내 리뷰 상세")
    public ResponseEntity<CommonResponse> getMyReview(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!review.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        return ResponseEntity.ok(new CommonResponse(true, "OK", review));
    }

    @GetMapping("/can-review")
    @Operation(summary = "리뷰 작성 가능 여부")
    public ResponseEntity<CommonResponse> canReview(
            @RequestAttribute("userId") String userId,
            @RequestParam Long productId) {
        // TODO: check if user has purchased and not yet reviewed
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("canReview", false, "orderItemId", (Object) null)));
    }

    @PostMapping("/reviews")
    @Operation(summary = "리뷰 작성")
    public ResponseEntity<CommonResponse> createReview(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Long orderItemId = body.get("orderItemId") != null ? Long.valueOf(body.get("orderItemId").toString()) : null;
        Integer rating = body.get("rating") != null ? (Integer) body.get("rating") : 5;
        String content = (String) body.get("content");
        Review review = Review.builder()
                .userId(userId)
                .productId(productId)
                .orderItemId(orderItemId)
                .rating(rating)
                .content(content)
                .build();
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("id", saved.getId())));
    }

    @PatchMapping("/reviews/{id}")
    @Operation(summary = "리뷰 수정")
    public ResponseEntity<CommonResponse> updateReview(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!review.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        review.update(
                body.get("rating") != null ? (Integer) body.get("rating") : null,
                (String) body.get("content"));
        reviewRepository.save(review);
        return ResponseEntity.ok(new CommonResponse(true, "OK", review));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "리뷰 삭제")
    public ResponseEntity<Void> deleteReview(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!review.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        reviewRepository.delete(review);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pets")
    @Operation(summary = "내 반려동물 목록")
    public ResponseEntity<CommonResponse> getPets(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", petRepository.findByUserId(userId)));
    }

    @PostMapping("/pets")
    @Operation(summary = "반려동물 등록")
    public ResponseEntity<CommonResponse> createPet(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, Object> body) {
        Pet pet = Pet.builder()
                .userId(userId)
                .name((String) body.get("name"))
                .petType((String) body.get("petType"))
                .breed((String) body.get("breed"))
                .bodyType((String) body.get("bodyType"))
                .build();
        Pet saved = petRepository.save(pet);
        return ResponseEntity.ok(new CommonResponse(true, "OK", saved));
    }

    @PatchMapping("/pets/{id}")
    @Operation(summary = "반려동물 수정")
    public ResponseEntity<CommonResponse> updatePet(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!pet.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        pet.update(
                (String) body.get("name"),
                (String) body.get("petType"),
                (String) body.get("breed"),
                (String) body.get("bodyType"));
        petRepository.save(pet);
        return ResponseEntity.ok(new CommonResponse(true, "OK", pet));
    }

    @DeleteMapping("/pets/{id}")
    @Operation(summary = "반려동물 삭제")
    public ResponseEntity<CommonResponse> deletePet(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!pet.getUserId().equals(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        petRepository.delete(pet);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("success", true)));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드")
    public ResponseEntity<CommonResponse> upload(
            @RequestAttribute("userId") String userId,
            @RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        // TODO: 실제 스토리지 업로드 연동 (ImageService 등)
        for (MultipartFile f : files) {
            urls.add("/uploads/" + f.getOriginalFilename());
        }
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("urls", urls)));
    }
}
