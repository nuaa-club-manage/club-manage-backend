package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

@Data
public class ClubMemberListRespDTO {
    private String userId;
    private String userName;
    private String realName;
    private String school;
    private String degree;
    private String phoneNumber;
    private String clubId;
    private String clubName;
    private String clubManager;
    private String reviewState;
}
