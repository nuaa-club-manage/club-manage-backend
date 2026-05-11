package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivityRegisterReqDTO {
    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;
}
