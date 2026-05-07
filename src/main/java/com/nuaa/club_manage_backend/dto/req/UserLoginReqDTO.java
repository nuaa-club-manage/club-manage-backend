package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginReqDTO {
    // 使用 validation 注解，如果不填，系统会自动拦截报错
    @NotBlank(message = "账号不能为空")
    private String userId;

    @NotBlank(message = "密码不能为空")
    private String userPassword;
}