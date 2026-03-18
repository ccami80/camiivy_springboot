package com.leeds.profile.leeds_profile_core.api.ext.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SMS 전송 응답")
public class SmsResponse {

    @Schema(description = "전송 성공 여부")
    private boolean success;

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "CoolSMS Message ID (성공시에만)")
    private String messageId;
} 