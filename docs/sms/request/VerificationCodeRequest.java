package com.leeds.profile.leeds_profile_core.api.ext.sms.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@Schema(description = "인증번호 전송 요청")
public class VerificationCodeRequest {

    @Schema(description = "수신자 전화번호", example = "+821051664177")
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "올바른 전화번호 형식이 아닙니다")
    private String toPhoneNumber;
} 