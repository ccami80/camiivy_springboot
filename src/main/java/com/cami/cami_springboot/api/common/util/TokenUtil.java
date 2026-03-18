package com.cami.cami_springboot.api.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 토큰 생성 및 디코딩 유틸리티
 * 
 * 토큰 형식: valid-token-{Base64(JSON)}
 * JSON 페이로드: {"userId": "xxx", "socialProvider": "KAKAO", "socialId": "yyy"}
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtil
{
    
    private final ObjectMapper objectMapper;
    
    /**
     * JWT 토큰 생성 (간소화 버전 - JSON 페이로드)
     * 실제 프로덕션에서는 JWT 라이브러리(jjwt, auth0 등) 사용 권장
     * 
     * @param userId 사용자 ID
     * @param socialProvider 소셜 제공자
     * @param socialId 소셜 ID
     * @return 생성된 토큰
     */
    public String generateToken(String userId, String socialProvider, String socialId, String tokenType)
    {
        try
        {
            // 한국 시간으로 현재 시간 생성
            String createdAt = java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
                    .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            // JSON Payload 생성
            Map<String, String> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("socialProvider", socialProvider);
            payload.put("socialId", socialId);
            payload.put("createdAt", createdAt);
            payload.put("tokenType", tokenType);
            
            // JSON 직렬화
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            // Base64 인코딩
            String encodedPayload = Base64.getEncoder()
                    .encodeToString(jsonPayload.getBytes(StandardCharsets.UTF_8));
            
            // 토큰 생성: valid-token-{encodedPayload}
            String token = "valid-token-" + encodedPayload;
            
            log.debug("토큰 생성 성공: userId={}, socialProvider={}, socialId={}, createdAt={}", 
                    userId, socialProvider, socialId, createdAt);
            
            return token;
        }
        catch (Exception e)
        {
            log.error("토큰 생성 실패: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("토큰 생성 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * 토큰에서 사용자 정보 추출 (JSON 디코딩)
     * 
     * @param token 토큰
     * @return Map<String, String> {userId, socialProvider, socialId} 또는 null
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> decodeToken(String token)
    {
        try
        {
            if (token == null || !token.startsWith("valid-token-"))
            {
                log.debug("토큰 디코딩 실패: 유효하지 않은 토큰 형식");
                return null;
            }
            
            // "valid-token-" 제거
            String encodedPayload = token.substring("valid-token-".length());
            
            // Base64 디코딩
            String jsonPayload = new String(
                Base64.getDecoder().decode(encodedPayload), 
                StandardCharsets.UTF_8
            );
            
            // JSON 파싱
            Map<String, String> payload = objectMapper.readValue(jsonPayload, Map.class);
            
            // 필수 필드 확인
            if (payload.containsKey("userId") && 
                payload.containsKey("socialProvider") && 
                payload.containsKey("socialId"))
            {
                
                log.debug("토큰 디코딩 성공: userId={}, socialProvider={}, socialId={}", 
                        payload.get("userId"), payload.get("socialProvider"), payload.get("socialId"));
                
                return payload;
            }
            
            log.debug("토큰 디코딩 실패: 필수 필드 누락");
            return null;
        }
        catch (Exception e)
        {
            log.error("토큰 디코딩 실패: error={}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 토큰에서 userId 추출
     * 
     * @param token 토큰
     * @return userId 또는 null
     */
    public String extractUserId(String token)
    {
        Map<String, String> decoded = decodeToken(token);
        return decoded != null ? decoded.get("userId") : null;
    }
    
    /**
     * 토큰에서 socialProvider 추출
     * 
     * @param token 토큰
     * @return socialProvider 또는 null
     */
    public String extractSocialProvider(String token)
    {
        Map<String, String> decoded = decodeToken(token);
        return decoded != null ? decoded.get("socialProvider") : null;
    }
    
    /**
     * 토큰에서 socialId 추출
     * 
     * @param token 토큰
     * @return socialId 또는 null
     */
    public String extractSocialId(String token)
    {
        Map<String, String> decoded = decodeToken(token);
        return decoded != null ? decoded.get("socialId") : null;
    }

    
    /**
     * 토큰 형식 검증 및 정보 반환
     * 
     * @param token 토큰
     * @return Map<String, Object> {result: boolean, userId: String, message: String}
     */
    public Map<String, Object> validateToken(String token)
    {
        Map<String, Object> response = new HashMap<>();
        
        // 토큰 디코딩
        Map<String, String> decodedInfo = decodeToken(token);
        
        if (decodedInfo == null)
        {
            response.put("result", false);
            response.put("message", "유효하지 않은 토큰 형식입니다 token: " + token);
            return response;
        }
        
        response.put("result", true);
        response.put("message", "유효한 토큰 형식입니다 token: " + token);
        
        return response;
    }
}
