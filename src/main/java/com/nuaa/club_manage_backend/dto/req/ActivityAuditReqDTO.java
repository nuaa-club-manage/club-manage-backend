package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivityAuditReqDTO {

    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotNull(message = "审核结果不能为空")
    private Boolean pass;
}
