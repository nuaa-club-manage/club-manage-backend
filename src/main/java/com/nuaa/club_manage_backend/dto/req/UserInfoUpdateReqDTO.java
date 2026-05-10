package com.nuaa.club_manage_backend.dto.req;

import lombok.Data;

@Data
public class UserInfoUpdateReqDTO {
    private String userName;
    private String phoneNumber;
    private String userMailbox;
    private String realName;
    private String gender;
    private String degree;
    private String school;
}
