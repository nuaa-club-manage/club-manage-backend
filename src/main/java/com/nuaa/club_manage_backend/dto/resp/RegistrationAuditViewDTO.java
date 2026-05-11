package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

@Data
public class RegistrationAuditViewDTO {
    private String registrationId;
    private String activityId;
    private String title;
    private String userId;
    private String realName;
    private String phoneNumber;
    private String reviewState;
}
