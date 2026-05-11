package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRatingRecordDTO {
    private String ratingId;
    private String clubId;
    private String clubName;
    private String rating;
    private LocalDateTime ratingTime;
}
