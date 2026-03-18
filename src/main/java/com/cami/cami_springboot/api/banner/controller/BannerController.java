package com.cami.cami_springboot.api.banner.controller;

import com.cami.cami_springboot.api.banner.entity.Banner;
import com.cami.cami_springboot.api.banner.entity.CurationItem;
import com.cami.cami_springboot.api.banner.entity.HomeSection;
import com.cami.cami_springboot.api.banner.entity.PageSection;
import com.cami.cami_springboot.api.banner.repository.BannerRepository;
import com.cami.cami_springboot.api.banner.repository.CurationItemRepository;
import com.cami.cami_springboot.api.banner.repository.HomeSectionRepository;
import com.cami.cami_springboot.api.banner.repository.PageSectionRepository;
import com.cami.cami_springboot.api.common.response.CommonResponse;
import com.cami.cami_springboot.api.product.entity.Product;
import com.cami.cami_springboot.api.notice.entity.Notice;
import com.cami.cami_springboot.api.notice.entity.Faq;
import com.cami.cami_springboot.api.notice.repository.NoticeRepository;
import com.cami.cami_springboot.api.notice.repository.FaqRepository;
import com.cami.cami_springboot.api.product.repository.ProductRepository;
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
@Tag(name = "Banner & Section API", description = "배너·홈섹션·큐레이션 API")
public class BannerController {

    private final BannerRepository bannerRepository;
    private final HomeSectionRepository homeSectionRepository;
    private final CurationItemRepository curationItemRepository;
    private final PageSectionRepository pageSectionRepository;
    private final ProductRepository productRepository;
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;

    @GetMapping("/banners")
    @Operation(summary = "배너 목록")
    public ResponseEntity<CommonResponse> getBanners() {
        List<Banner> banners = bannerRepository.findAllByOrderByDisplayOrderAsc();
        return ResponseEntity.ok(new CommonResponse(true, "OK", banners));
    }

    @GetMapping("/home-sections")
    @Operation(summary = "홈 섹션")
    public ResponseEntity<CommonResponse> getHomeSections() {
        Map<String, List<Product>> result = new HashMap<>();
        List<HomeSection> newBest = homeSectionRepository.findBySectionTypeOrderByDisplayOrderAsc("newBest");
        List<HomeSection> best = homeSectionRepository.findBySectionTypeOrderByDisplayOrderAsc("best");
        result.put("newBest", newBest.stream().map(h -> productRepository.findById(h.getProductId())).filter(java.util.Optional::isPresent).map(java.util.Optional::get).collect(Collectors.toList()));
        result.put("best", best.stream().map(h -> productRepository.findById(h.getProductId())).filter(java.util.Optional::isPresent).map(java.util.Optional::get).collect(Collectors.toList()));
        return ResponseEntity.ok(new CommonResponse(true, "OK", result));
    }

    @GetMapping("/curation")
    @Operation(summary = "큐레이션")
    public ResponseEntity<CommonResponse> getCuration() {
        List<CurationItem> items = curationItemRepository.findAllByOrderByDisplayOrderAsc();
        List<Product> products = items.stream()
                .map(i -> productRepository.findById(i.getProductId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CommonResponse(true, "OK", products));
    }

    @GetMapping("/page-sections")
    @Operation(summary = "페이지 섹션", description = "page 쿼리 필수")
    public ResponseEntity<CommonResponse> getPageSections(@RequestParam String page) {
        List<PageSection> sections = pageSectionRepository.findByPageTypeOrderByDisplayOrderAsc(page);
        List<Product> products = sections.stream()
                .map(s -> productRepository.findById(s.getProductId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CommonResponse(true, "OK", products));
    }

    @GetMapping("/notices")
    @Operation(summary = "공지 목록")
    public ResponseEntity<CommonResponse> getNotices() {
        return ResponseEntity.ok(new CommonResponse(true, "OK", noticeRepository.findAll()));
    }

    @GetMapping("/faq")
    @Operation(summary = "FAQ 목록")
    public ResponseEntity<CommonResponse> getFaq() {
        return ResponseEntity.ok(new CommonResponse(true, "OK", faqRepository.findAllByOrderByDisplayOrderAsc()));
    }
}
