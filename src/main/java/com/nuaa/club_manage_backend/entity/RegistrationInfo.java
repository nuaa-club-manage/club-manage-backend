package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("RegistrationInfo")
public class RegistrationInfo {
    @TableId("RegistrationID")
    private String registrationId;

    @TableField("ActivityID")
    private String activityId;

    @TableField("UserID")
    private String userId;

    @TableField("RealName")
    private String realName;

    @TableField("PhoneNumer")
    private String phoneNumber;

    @TableField("ReviewState")
    private String reviewState;
}
