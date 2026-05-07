package com.nuaa.club_manage_backend.common;

import lombok.Data;

/**
 * 全局统一响应包装类
 */
@Data
public class Result<T> {
    private Integer code;     // 状态码：200成功，400业务异常，401未登录，500系统异常
    private String message;   // 提示信息
    private T data;           // 具体的业务数据

    // 私有化构造方法，强制通过静态方法调用
    private Result() {}

    // 成功时的快捷调用（不带数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    // 成功时的快捷调用（带具体查出来的数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 失败时的快捷调用
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 默认的业务失败（状态码 400）
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }
}