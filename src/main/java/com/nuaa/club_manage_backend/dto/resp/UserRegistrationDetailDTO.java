package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegistrationDetailDTO {
    private String registrationId;
    private String activityId;
    private String title;
    private String content;
    private String reviewState;
    private LocalDateTime publishTime;
    private String clubId;
    private String clubName;
}
