package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoRespDTO {
    private String userId;
    private String userName;
    private String phoneNumber;
    private String userMailbox;
    private String realName;
    private String gender;
    private String degree;
    private String school;
    private LocalDate registerTime;
}
