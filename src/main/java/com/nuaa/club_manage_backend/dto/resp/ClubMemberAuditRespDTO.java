package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

@Data
public class ClubMemberAuditRespDTO {
    private String userId;
    private String userName;
    private String realName;
    private String studentId;
    private String school;
    private String degree;
    private String clubId;
    private String clubName;
    private String reviewState;
}
