package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivityUpdateReqDTO {

    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    @NotBlank(message = "活动详情不能为空")
    private String content;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    @NotNull(message = "最大人数不能为空")
    private Integer capacityLimit;
}
