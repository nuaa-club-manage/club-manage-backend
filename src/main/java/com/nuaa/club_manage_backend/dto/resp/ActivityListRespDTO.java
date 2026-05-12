package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityListRespDTO {
    private String activityId;
    private String clubId;
    private String clubName;
    private String userId;
    private String title;
    private String content;
    private String location;
    private Integer capacityLimit;
    private String activityState;
    private LocalDateTime publishTime;
}
