package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ClubMember")
public class ClubMember {
    @TableField("ClubID")
    private String clubId;

    @TableField("UserID")
    private String userId;

    @TableField("ReviewState")
    private String reviewState;

    @TableField("ClubManager")
    private String clubManager;
}
