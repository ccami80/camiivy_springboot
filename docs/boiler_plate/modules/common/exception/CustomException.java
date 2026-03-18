package com.culwonder.leeds_profile_springboot_core.api.common.exception;

import com.culwonder.leeds_profile_springboot_core.api.common.code.ErrorCode;
import com.culwonder.leeds_profile_springboot_core.api.common.response.CommonResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * 커스텀 예외 클래스
 * 애플리케이션 전반에서 발생하는 예외를 처리하기 위한 클래스
 * 
 * 사용 예시:
 * throw new CustomException(ErrorCode.USER_NOT_FOUND);
 * throw new CustomException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
 */
@Slf4j
@Getter
public class CustomException extends RuntimeException
{
    private final HttpStatus httpStatus;
    private final CommonResponse response;

    /**
     * 예외 상세 정보를 포함하는 생성자
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     */
    public CustomException(HttpStatus httpStatus, String message)
    {
        this.httpStatus = httpStatus;
        this.response = new CommonResponse(false, message, null);
        
        log.error("CustomException 발생 - HTTP 상태: {}, 에러 메시지: {}", 
                httpStatus, message);
    }
    
    /**
     * 에러 코드를 포함하는 생성자 (String)
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     * @param errorCode 에러 코드
     */
    public CustomException(HttpStatus httpStatus, String message, String errorCode)
    {
        this.httpStatus = httpStatus;
        this.response = new CommonResponse(false, message, errorCode, null);
        
        log.error("CustomException 발생 - HTTP 상태: {}, 에러 코드: {}, 에러 메시지: {}", 
                httpStatus, errorCode, message);
    }
    
    /**
     * ErrorCode를 사용하는 생성자 (권장)
     * @param errorCode ErrorCode Enum
     */
    public CustomException(ErrorCode errorCode)
    {
        this.httpStatus = errorCode.getHttpStatus();
        this.response = new CommonResponse(false, errorCode.getMessage(), errorCode.getCode(), null);
        
        log.error("CustomException 발생 - 에러 코드: {}, HTTP 상태: {}, 에러 메시지: {}", 
                errorCode.getCode(), errorCode.getHttpStatus(), errorCode.getMessage());
    }
    

    /**
     * 예외 상세 정보를 포함하는 생성자
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     * @param e 원본 예외
     */
    public CustomException(HttpStatus httpStatus, String message, Exception e)
    {
        this.httpStatus = httpStatus;
        this.response = new CommonResponse(false, message, null);
        
        log.error("CustomException 발생 - HTTP 상태: {}, 에러 메시지: {}, 예외 스택 트레이스: {}", 
                httpStatus, message, e.getMessage());
    }

    /**
     * CommonResponse를 직접 받는 생성자
     * @param httpStatus HTTP 상태 코드
     * @param response 커스텀 응답 객체
     */
    public CustomException(HttpStatus httpStatus, CommonResponse response)
    {
        this.httpStatus = httpStatus;
        this.response = response;
        
        log.error("CustomException 발생 - HTTP 상태: {}, 에러 메시지: {}", 
                httpStatus, response.getErrorMessage());
    }
}

