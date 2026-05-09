package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterReqDTO {

    @NotBlank(message = "学号不能为空")
    private String userId;

    @NotBlank(message = "手机号或邮箱不能为空")
    private String contact;

    @NotBlank(message = "密码不能为空")
    private String userPassword;

    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
