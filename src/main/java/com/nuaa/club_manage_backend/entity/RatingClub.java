package com.nuaa.club_manage_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("RatingClub")
public class RatingClub {
    @TableId(value = "RatingID", type = IdType.ASSIGN_ID)
    private String ratingId;

    @TableField("ClubID")
    private String clubId;

    @TableField("UserID")
    private String userId;

    @TableField("Rating")
    private String rating;

    @TableField("RatingTime")
    private LocalDateTime ratingTime;
}
