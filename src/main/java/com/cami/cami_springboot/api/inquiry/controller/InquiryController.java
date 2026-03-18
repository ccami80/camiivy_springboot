package com.cami.cami_springboot.api.inquiry.controller;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.inquiry.entity.Inquiry;
import com.cami.cami_springboot.api.inquiry.repository.InquiryRepository;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Inquiry API", description = "문의 API")
public class InquiryController {

    private final InquiryRepository inquiryRepository;

    @PostMapping("/inquiry")
    @Operation(summary = "문의 등록")
    public ResponseEntity<CommonResponse> createInquiry(
            @RequestBody Map<String, Object> body,
            @RequestAttribute(value = "userId", required = false) String userId) {
        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .inquiryType((String) body.get("inquiryType"))
                .orderId(body.get("orderId") != null ? Long.valueOf(body.get("orderId").toString()) : null)
                .content((String) body.get("content"))
                .imageUrls(body.get("imageUrls") != null ? body.get("imageUrls").toString() : null)
                .build();
        inquiryRepository.save(inquiry);
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("ok", true)));
    }

    @GetMapping("/inquiry/my")
    @Operation(summary = "내 문의 목록", description = "로그인 필요")
    public ResponseEntity<CommonResponse> getMyInquiries(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new CommonResponse(true, "OK", inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId)));
    }

    @PostMapping(value = "/inquiry/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문의 이미지 업로드")
    public ResponseEntity<CommonResponse> uploadInquiryImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile f : files) {
            urls.add("/uploads/inquiry/" + f.getOriginalFilename());
        }
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of("urls", urls)));
    }

    @GetMapping("/customer-center/settings")
    @Operation(summary = "고객센터 설정")
    public ResponseEntity<CommonResponse> getCustomerCenterSettings() {
        return ResponseEntity.ok(new CommonResponse(true, "OK", Map.of()));
    }
}
