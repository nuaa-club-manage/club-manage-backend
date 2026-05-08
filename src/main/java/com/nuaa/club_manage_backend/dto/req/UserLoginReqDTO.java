package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginReqDTO {
    private String userId;        // 密码登录时使用

    @NotNull(message = "登录类型不能为空")
    private Integer loginType;   // 0-密码登录，1-验证码登录

    private String userPassword; // 密码登录时使用

    private String verifyCode;   // 验证码登录时使用

    private String phoneNumber;  // 验证码登录时使用（手机号接收验证码）

    @NotBlank(message = "人机验证码凭证不能为空")
    private String captchaId;

    @NotBlank(message = "人机验证码不能为空")
    private String captchaCode;
}