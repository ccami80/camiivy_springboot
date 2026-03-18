package com.culwonder.leeds_profile_springboot_core.api.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오 사용자 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfoResponse
{
    private Long id;
    
    @JsonProperty("connected_at")
    private String connectedAt;
    
    private KakaoAccount kakaoAccount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount
    {
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;
        
        @JsonProperty("profile_image_needs_agreement")
        private Boolean profileImageNeedsAgreement;
        
        private KakaoProfile profile;
        
        @JsonProperty("has_email")
        private Boolean hasEmail;
        
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
        
        private String email;
        
        @JsonProperty("has_age_range")
        private Boolean hasAgeRange;
        
        @JsonProperty("age_range_needs_agreement")
        private Boolean ageRangeNeedsAgreement;
        
        @JsonProperty("age_range")
        private String ageRange;
        
        @JsonProperty("has_birthday")
        private Boolean hasBirthday;
        
        @JsonProperty("birthday_needs_agreement")
        private Boolean birthdayNeedsAgreement;
        
        private String birthday;
        
        @JsonProperty("has_gender")
        private Boolean hasGender;
        
        @JsonProperty("gender_needs_agreement")
        private Boolean genderNeedsAgreement;
        
        private String gender;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoProfile
    {
        private String nickname;
        
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
        
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        
        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
    }
}
