package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminEditUserReqDTO {
    @NotBlank(message = "目标用户ID不能为空")
    private String targetUserID;

    private String userName;
    private String phoneNumber;
    private String userMailbox;
    private String realName;
    private String gender;
    private String degree;
    private String school;
    private String userPassword;
}
