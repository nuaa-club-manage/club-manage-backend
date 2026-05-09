package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginReqDTO {

    @NotBlank(message = "管理员账号不能为空")
    private String userId;

    @NotBlank(message = "密码不能为空")
    private String userPassword;
}
