package com.nuaa.club_manage_backend.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认业务错误码为 400
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}