package com.nuaa.club_manage_backend.exception;

import com.nuaa.club_manage_backend.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice // 这个注解让它能自动拦截所有 Controller 层的报错
public class GlobalExceptionHandler {

    /**
     * 拦截我们自定义的业务异常 (BusinessException)
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 拦截系统级未知异常 (如空指针、数据库连不上等)兜底逻辑
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统内部异常：", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}