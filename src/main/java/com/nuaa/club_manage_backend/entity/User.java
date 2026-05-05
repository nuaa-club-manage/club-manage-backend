package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("User")
public class User {
    @TableId("userID")
    private String userId;

    @TableField("userPassword")
    private String userPassword;

    @TableField("UserType")
    private String userType;
}
