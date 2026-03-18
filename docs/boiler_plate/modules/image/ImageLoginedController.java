package com.culwonder.leeds_profile_springboot_core.api.image;

import com.culwonder.leeds_profile_springboot_core.api.common.response.CommonResponse;
import com.culwonder.leeds_profile_springboot_core.api.common.util.PageResponseUtil;
import com.culwonder.leeds_profile_springboot_core.api.image.code.ImageType;
import com.culwonder.leeds_profile_springboot_core.api.image.request.ImageDeleteRequest;
import com.culwonder.leeds_profile_springboot_core.api.image.request.ImageListSearchRequest;
import com.culwonder.leeds_profile_springboot_core.api.image.request.ImageUpdateRequest;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageDeleteResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageDetailResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.response.ImageUploadResponse;
import com.culwonder.leeds_profile_springboot_core.api.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 이미지 로그인 컨트롤러
 * 로그인 필요한 이미지 API
 */
@Tag(name = "Image 로그인 API", description = "로그인 필요한 이미지 API")
@RestController
@RequestMapping("/api-logined/images")
@RequiredArgsConstructor
public class ImageLoginedController
{
    
    private final ImageService imageService;
    
    /**
     * 이미지 업로드
     */
    @Operation(summary = "이미지 업로드", description = "이미지 파일을 업로드합니다")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse uploadImage(
        @Parameter(description = "업로드할 이미지 파일", required = true)
        @RequestPart("file") MultipartFile file,
        
        @Parameter(description = "이미지 타입", required = true, schema = @Schema(implementation = ImageType.class))
        @RequestParam("imageType") ImageType imageType,
        
        @Parameter(description = "관련 엔티티 ID")
        @RequestParam(value = "relatedEntityId", required = false) String relatedEntityId,
        
        @Parameter(description = "관련 엔티티 타입")
        @RequestParam(value = "relatedEntityType", required = false) String relatedEntityType,
        
        @RequestAttribute("userId") String userId
    )
    {
        ImageUploadResponse response = imageService.uploadImage(
            file, imageType, relatedEntityId, relatedEntityType, userId
        );
        return new CommonResponse(true, "이미지 업로드 성공", response);
    }
    
    /**
     * 내 이미지 목록 조회
     */
    @Operation(summary = "내 이미지 목록 조회", description = "로그인한 사용자가 업로드한 이미지 목록을 조회합니다")
    @GetMapping("/my")
    public CommonResponse getMyImageList(
        ImageListSearchRequest request,
        @RequestAttribute("userId") String userId
    )
    {
        // 업로드한 사용자 ID를 현재 로그인한 사용자로 설정
        request.setUploadedBy(userId);
        
        Page<ImageResponse> page = imageService.searchImageList(request);
        
        Map<String, Object> response = PageResponseUtil.toWrappedPageResponse(
            page,
            "imageList"
        );
        
        return new CommonResponse(true, "내 이미지 목록 조회 성공", response);
    }
    
    /**
     * 이미지 상세 조회
     */
    @Operation(summary = "이미지 상세 조회", description = "이미지 상세 정보를 조회합니다")
    @GetMapping("/{imageId}")
    public CommonResponse getImageDetail(
        @PathVariable String imageId,
        @RequestAttribute("userId") String userId
    )
    {
        ImageDetailResponse response = imageService.getImageDetail(imageId);
        return new CommonResponse(true, "이미지 상세 조회 성공", response);
    }
    
    /**
     * 이미지 정보 수정
     */
    @Operation(summary = "이미지 정보 수정", description = "이미지 정보를 수정합니다")
    @PutMapping
    public CommonResponse updateImage(
        @Valid @RequestBody ImageUpdateRequest request,
        @RequestAttribute("userId") String userId
    )
    {
        ImageDetailResponse response = imageService.updateImage(request, userId);
        return new CommonResponse(true, "이미지 정보 수정 성공", response);
    }
    
    /**
     * 이미지 삭제
     */
    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다")
    @DeleteMapping
    public CommonResponse deleteImage(
        @Valid @RequestBody ImageDeleteRequest request,
        @RequestAttribute("userId") String userId
    )
    {
        ImageDeleteResponse response = imageService.deleteImage(request, userId);
        return new CommonResponse(true, "이미지 삭제 성공", response);
    }
}

