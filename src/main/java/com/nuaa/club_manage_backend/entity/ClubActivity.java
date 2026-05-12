package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ClubActivity")
public class ClubActivity {
    @TableId("ActivityID")
    private String activityId;

    @TableField("ClubID")
    private String clubId;

    @TableField("UserID")
    private String userId;

    @TableField("Title")
    private String title;

    @TableField("Content")
    private String content;

    @TableField("Location")
    private String location;

    @TableField("CapacityLimit")
    private Integer capacityLimit;

    @TableField("ActivityState")
    private String activityState;

    @TableField("PublishTime")
    private LocalDateTime publishTime;
}
