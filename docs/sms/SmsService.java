package com.leeds.profile.leeds_profile_core.api.ext.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class SmsService {

    private String apiKey = "NCS3B1XTCIXSPNWH";
    private String apiSecret = "GUGH73XOCX2GKHLMOA4UALTRLQJRLWEP";
    private String fromPhoneNumber = "01051664177";

    public boolean sendSms(String toPhoneNumber, String messageText) {
        try {
            log.info("SMS 전송 시작: {} -> {}", fromPhoneNumber, toPhoneNumber);
            
            // 현재 시간을 ISO 형식으로 가져옵니다
            String date = Instant.now().toString();
            // UUID를 생성하여 salt로 사용합니다
            String salt = UUID.randomUUID().toString().replace("-", "");

            // 서명을 위한 메시지를 생성합니다
            String message = date + salt;

            // HMAC-SHA256으로 서명을 생성합니다
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = bytesToHex(hmac.doFinal(message.getBytes(StandardCharsets.UTF_8)));

            // SMS 전송을 위한 JSON 데이터 생성
            String jsonData = String.format(
                "{\"message\":{\"to\":\"%s\",\"from\":\"%s\",\"text\":\"%s\"}}",
                toPhoneNumber, fromPhoneNumber, messageText
            );

            // API 요청을 위한 URL을 생성합니다
            URL url = new URL("https://api.solapi.com/messages/v4/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization",
                String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    apiKey, date, salt, signature));
            conn.setDoOutput(true);

            // JSON 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 확인
            int responseCode = conn.getResponseCode();
            log.info("SMS 전송 응답 코드: {}", responseCode);
            
            if (responseCode == 200) {
                log.info("SMS 전송 완료");
                return true;
            } else {
                log.error("SMS 전송 실패: HTTP {}", responseCode);
                return false;
            }
            
        } catch (Exception e) {
            log.error("SMS 전송 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    // 바이트 배열을 16진수 문자열로 변환하는 헬퍼 메서드
    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public boolean sendVerificationCode(String toPhoneNumber, String verificationCode) {
        String message = String.format("[Leeds Profile] 인증번호: %s", verificationCode);
        return sendSms(toPhoneNumber, message);
    }

    public boolean sendNotification(String toPhoneNumber, String notificationMessage) {
        String message = String.format("[Leeds Profile] %s", notificationMessage);
        return sendSms(toPhoneNumber, message);
    }

}