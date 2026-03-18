package com.cami.cami_springboot.api.common.exception;

import com.cami.cami_springboot.api.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션 전반에서 발생하는 예외를 일관되게 처리
 * 
 * 처리 예외:
 * - CustomException: 비즈니스 로직 예외
 * - IllegalArgumentException: 잘못된 인자
 * - RuntimeException: 런타임 예외
 * - Exception: 기타 모든 예외
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{

    /**
     * CustomException 처리
     * @param e CustomException
     * @return ResponseEntity<CommonResponse>
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse> handleCustomException(CustomException e)
    {
        log.error("CustomException 처리: {}", e.getMessage());
        return ResponseEntity.status(Objects.requireNonNull(e.getHttpStatus())).body(e.getResponse());
    }

    /**
     * IllegalArgumentException 처리
     * @param e IllegalArgumentException
     * @return ResponseEntity<CommonResponse>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse> handleIllegalArgumentException(IllegalArgumentException e)
    {
        log.error("IllegalArgumentException 처리: {}", e.getMessage());
        
        CommonResponse response = new CommonResponse(false, e.getMessage(), null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * RuntimeException 처리
     * @param e RuntimeException
     * @return ResponseEntity<CommonResponse>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse> handleRuntimeException(RuntimeException e)
    {
        log.error("RuntimeException 처리: {}", e.getMessage());
        
        CommonResponse response = new CommonResponse(false, "서버 내부 오류가 발생했습니다.", null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Exception 처리 (기타 모든 예외)
     * @param e Exception
     * @return ResponseEntity<CommonResponse>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e)
    {
        log.error("Exception 처리: {}", e.getMessage(), e);
        
        CommonResponse response = new CommonResponse(false, "알 수 없는 오류가 발생했습니다.", null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

