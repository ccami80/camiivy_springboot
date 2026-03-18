package com.culwonder.leeds_profile_springboot_core.api.common.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 공통 응답 클래스
 * 모든 API 응답의 표준 형식을 정의
 * 
 * 응답 형식:
 * - 성공: { success: true, successMessage: "...", content: {...} }
 * - 실패: { success: false, errorMessage: "...", errorCode: "E1001", content: null }
 */
@Getter
@Setter
public class CommonResponse
{
    private boolean success;
    private String successMessage;
    private String errorMessage;
    private String errorCode;
    private Object content;


    public CommonResponse()
    {
        this.success = true;
        this.successMessage = "";
        this.errorMessage = "";
        this.errorCode = null;
    } 

    public CommonResponse(boolean success, String message, Object content)
    {
        if(success)
        {
            this.success = true;
            this.successMessage = message;
            this.content = content;
            this.errorCode = null;
        } 
        else
        {
            this.success = false;
            this.errorMessage = message;
            this.content = null;
            this.errorCode = null;
        }
    }
    
    public CommonResponse(boolean success, String message, String errorCode, Object content)
    {
        if(success)
        {
            this.success = true;
            this.successMessage = message;
            this.content = content;
            this.errorCode = null;
        } 
        else
        {
            this.success = false;
            this.errorMessage = message;
            this.errorCode = errorCode;
            this.content = null;
        }
    }
}

