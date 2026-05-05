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

    @TableField("UserID")
    private String userId;

    @TableField("Title")
    private String title;

    @TableField("Content")
    private String content;

    @TableField("CapacityLimit")
    private Integer capacityLimit;

    @TableField("ActivityState")
    private String activityState;

    @TableField("PublishTime")
    private LocalDateTime publishTime;
}
