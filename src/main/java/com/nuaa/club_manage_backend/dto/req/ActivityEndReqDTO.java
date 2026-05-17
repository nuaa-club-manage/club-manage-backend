package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivityEndReqDTO {

    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotBlank(message = "活动总结不能为空")
    private String summary;

    @NotBlank(message = "到场名单不能为空")
    private String participantList;
}
