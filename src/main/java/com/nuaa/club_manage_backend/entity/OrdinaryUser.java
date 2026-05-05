package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("OrdinaryUser")
public class OrdinaryUser {
    @TableId("userID")
    private String userId;

    @TableField("userPassword")
    private String userPassword;

    @TableField("UserType")
    private String userType;

    @TableField("userName")
    private String userName;

    @TableField("Phonenumber")
    private String phoneNumber;

    @TableField("userMailbox")
    private String userMailbox;

    @TableField("RealName")
    private String realName;

    @TableField("Gender")
    private String gender;

    @TableField("Degree")
    private String degree;

    @TableField("School")
    private String school;

    @TableField("RegisterTime")
    private LocalDateTime registerTime;
}
