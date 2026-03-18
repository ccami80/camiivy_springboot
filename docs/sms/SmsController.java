package com.leeds.profile.leeds_profile_core.api.ext.sms;

import com.leeds.profile.leeds_profile_core.api.ext.sms.request.SmsRequest;
import com.leeds.profile.leeds_profile_core.api.ext.sms.request.VerificationCodeRequest;
import com.leeds.profile.leeds_profile_core.api.ext.sms.response.SmsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@Tag(name = "SMS", description = "SMS 전송 관련 API")
@RequestMapping("/api-ext/sms")
@Slf4j
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    @Operation(description = "SMS 전송", summary = "일반 SMS 메시지를 전송합니다")
    public ResponseEntity<SmsResponse> sendSms(@Valid @RequestBody SmsRequest request) {
        log.info("SMS 전송 API 호출: {}", request.getToPhoneNumber());
        
        boolean success = smsService.sendSms(request.getToPhoneNumber(), request.getMessage());

        SmsResponse response = SmsResponse.builder()
                .success(success)
                .message(success ? "SMS 전송이 완료되었습니다." : "SMS 전송에 실패했습니다.")
                .build();
        
        log.info("SMS 전송 결과: {}", success ? "성공" : "실패");
        return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/verification-code")
    @Operation(description = "인증번호 전송", summary = "6자리 인증번호를 생성하여 SMS로 전송합니다")
    public ResponseEntity<SmsResponse> sendVerificationCode(@Valid @RequestBody VerificationCodeRequest request) {
        log.info("인증번호 전송 API 호출: {}", request.getToPhoneNumber());
        
        // 6자리 인증번호 생성
        String verificationCode = generateVerificationCode();
        
        boolean success = smsService.sendVerificationCode(request.getToPhoneNumber(), verificationCode);
        
        SmsResponse response = SmsResponse.builder()
                .success(success)
                .message(success ? "인증번호가 전송되었습니다." : "인증번호 전송에 실패했습니다.")
                .build();
        
        log.info("인증번호 전송 결과: {}", success ? "성공" : "실패");
        return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/notification")
    @Operation(description = "알림 메시지 전송", summary = "알림 메시지를 SMS로 전송합니다")
    public ResponseEntity<SmsResponse> sendNotification(
            @RequestParam String toPhoneNumber,
            @RequestParam String notificationMessage) {
        log.info("알림 메시지 전송 API 호출: {}", toPhoneNumber);
        
        boolean success = smsService.sendNotification(toPhoneNumber, notificationMessage);
        
        SmsResponse response = SmsResponse.builder()
                .success(success)
                .message(success ? "알림 메시지가 전송되었습니다." : "알림 메시지 전송에 실패했습니다.")
                .build();
        
        log.info("알림 메시지 전송 결과: {}", success ? "성공" : "실패");
        return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 