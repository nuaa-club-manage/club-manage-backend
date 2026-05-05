package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("Club")
public class Club {
    @TableId("Clubid")
    private String clubId;

    @TableField("UserID")
    private String userId;

    @TableField("ClubName")
    private String clubName;

    @TableField("Clubinformation")
    private String clubInformation;

    @TableField("School")
    private String school;

    @TableField("ClubState")
    private String clubState;

    @TableField("EstablishmentTime")
    private LocalDateTime establishmentTime;
}
