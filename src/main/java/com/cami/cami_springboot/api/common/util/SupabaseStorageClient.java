package com.cami.cami_springboot.api.common.util;

import com.cami.cami_springboot.api.common.code.ErrorCode;
import com.cami.cami_springboot.api.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Supabase Storage 클라이언트
 * Supabase Storage API를 호출하는 유틸리티 클래스
 */
@Slf4j
@Component
public class SupabaseStorageClient
{
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.supabase.url}")
    private String supabaseUrl;
    
    @Value("${app.supabase.anon-key}")
    private String supabaseAnonKey;
    
    @Value("${app.supabase.storage.public-bucket-name}")
    private String publicBucketName;  // 공개 버킷 이름
    
    @Value("${app.supabase.storage.private-bucket-name}")
    private String privateBucketName;  // 비공개 버킷 이름
    
    @Value("${app.supabase.storage.signed-url-expires-in-seconds}")
    private int signedUrlExpiresInSeconds;
    
    public SupabaseStorageClient(WebClient.Builder webClientBuilder)
    {
        this.webClientBuilder = webClientBuilder;
    }
    
    /**
     * 파일 업로드
     * 
     * @param fileData 파일 바이트 배열
     * @param fileName 파일명
     * @param contentType MIME 타입
     * @param folderPath 폴더 경로 (예: profile/2025/10)
     * @return 업로드된 파일의 경로
     */
    public String uploadFile(byte[] fileData, String fileName, String contentType, String folderPath)
    {
        try
        {
            // 고유한 파일명 생성 (UUID + 원본 확장자)
            String uniqueFileName = generateUniqueFileName(fileName);
            String storagePath = folderPath + "/" + uniqueFileName;
            
            // Supabase Storage API URL
            String uploadUrl = String.format(
                "%s/storage/v1/object/%s/%s",
                supabaseUrl,
                publicBucketName,
                storagePath
            );
            
            log.info("Supabase Storage 업로드 시작: {}", uploadUrl);
            
            // WebClient로 업로드 요청
            WebClient webClient = webClientBuilder
                .baseUrl(Objects.requireNonNull(supabaseUrl))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseAnonKey)
                .defaultHeader("apikey", supabaseAnonKey)
                .build();
            
            Map<String, Object> response = webClient.post()
                .uri("/storage/v1/object/" + publicBucketName + "/" + storagePath)
                .contentType(MediaType.parseMediaType(Objects.requireNonNull(contentType)))
                .bodyValue(Objects.requireNonNull(fileData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    log.error("Supabase Storage 업로드 실패: HTTP {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Supabase 오류 응답: {}", errorBody);
                            return Mono.error(new CustomException(ErrorCode.FILE_UPLOAD_FAILED));
                        });
                })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
            
            log.info("Supabase Storage 업로드 성공: {}", response);
            
            return storagePath;
        }
        catch (Exception e)
        {
            log.error("파일 업로드 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    /**
     * 파일 삭제
     * 
     * @param storagePath 파일 경로
     */
    public void deleteFile(String storagePath)
    {
        try
        {
            String deleteUrl = String.format(
                "%s/storage/v1/object/%s/%s",
                supabaseUrl,
                publicBucketName,
                storagePath
            );
            
            log.info("Supabase Storage 삭제 시작: {}", deleteUrl);
            
            WebClient webClient = webClientBuilder
                .baseUrl(Objects.requireNonNull(supabaseUrl))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseAnonKey)
                .defaultHeader("apikey", supabaseAnonKey)
                .build();
            
            webClient.delete()
                .uri("/storage/v1/object/" + publicBucketName + "/" + storagePath)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    log.error("Supabase Storage 삭제 실패: HTTP {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Supabase 오류 응답: {}", errorBody);
                            return Mono.error(new CustomException(ErrorCode.FILE_DELETE_FAILED));
                        });
                })
                .bodyToMono(Void.class)
                .block();
            
            log.info("Supabase Storage 삭제 성공: {}", storagePath);
        }
        catch (Exception e)
        {
            log.error("파일 삭제 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
    
    /**
     * 공개 URL 생성
     * 
     * @param storagePath 파일 경로
     * @return 공개 URL
     */
    public String getPublicUrl(String storagePath)
    {
        return String.format(
            "%s/storage/v1/object/public/%s/%s",
            supabaseUrl,
            publicBucketName,
            storagePath
        );
    }
    
    /**
     * Signed URL 생성 (시간 제한)
     * 
     * @param storagePath 파일 경로
     * @return Signed URL (만료 시간 포함)
     */
    public String getSignedUrl(String storagePath)
    {
        try
        {
            log.info("Signed URL 생성 시작: {}", storagePath);
            
            WebClient webClient = webClientBuilder
                .baseUrl(Objects.requireNonNull(supabaseUrl))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseAnonKey)
                .defaultHeader("apikey", supabaseAnonKey)
                .build();
            
            // Signed URL 요청 바디
            Map<String, Object> requestBody = Map.of(
                "expiresIn", signedUrlExpiresInSeconds
            );
            
            Map<String, Object> response = webClient.post()
                .uri("/storage/v1/object/sign/" + 
                    privateBucketName + "/" + 
                    storagePath)
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .bodyValue(Objects.requireNonNull(requestBody))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    log.error("Signed URL 생성 실패: HTTP {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Supabase 오류 응답: {}", errorBody);
                            return Mono.error(new CustomException(ErrorCode.FILE_UPLOAD_FAILED));
                        });
                })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
            
            String signedPath = (String) response.get("signedURL");
            
            // 상대 경로를 절대 URL로 변환
            String signedUrl = signedPath.startsWith("http") 
                ? signedPath 
                : supabaseUrl + "/storage/v1" + signedPath;
            
            log.info("Signed URL 생성 성공: {}", signedUrl);
            
            return signedUrl;
        }
        catch (Exception e)
        {
            log.error("Signed URL 생성 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    /**
     * 고유한 파일명 생성
     * 
     * @param originalFileName 원본 파일명
     * @return UUID + 확장자
     */
    private String generateUniqueFileName(String originalFileName)
    {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0)
        {
            extension = originalFileName.substring(dotIndex);
        }
        
        return UUID.randomUUID().toString() + extension;
    }
}

