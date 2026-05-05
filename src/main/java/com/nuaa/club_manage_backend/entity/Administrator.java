package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Administrator")
public class Administrator {
    @TableId("UserID")
    private String userId;

    @TableField("userPassword")
    private String userPassword;

    @TableField("userType")
    private String userType;
}
