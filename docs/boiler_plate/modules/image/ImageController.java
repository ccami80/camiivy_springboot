package com.culwonder.leeds_profile_springboot_core.api.image;

import com.culwonder.leeds_profile_springboot_core.api.common.response.CommonResponse;
import com.culwonder.leeds_profile_springboot_core.api.common.util.PageResponseUtil;
import com.culwonder.leeds_profile_springboot_core.api.image.request.ImageListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageDetailResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Image guest API controller.
 * Image list/detail endpoints (no login required).
 */
@Tag(name = "Image Guest API", description = "Image APIs (guest access)")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ImageController
{

    private final ImageService imageService;

    @Operation(summary = "Image list", description = "Search image list (guest access)")
    @GetMapping("/api/images")
    public CommonResponse searchImageList(ImageListSearchRequest request)
    {
        Page<ImageResponse> page = imageService.searchImageList(request);
        Map<String, Object> response = PageResponseUtil.toWrappedPageResponse(
            page,
            "imageList"
        );
        return new CommonResponse(true, "OK", response);
    }

    @Operation(summary = "Image detail", description = "Get image detail by imageId (guest access)")
    @GetMapping("/api/images/{imageId}")
    public CommonResponse getImageDetail(@PathVariable String imageId)
    {
        ImageDetailResponse response = imageService.getImageDetail(imageId);
        return new CommonResponse(true, "OK", response);
    }
}

