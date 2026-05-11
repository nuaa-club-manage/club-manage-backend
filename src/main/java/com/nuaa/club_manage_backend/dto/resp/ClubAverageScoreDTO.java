package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

@Data
public class ClubAverageScoreDTO {
    private String clubId;
    private String clubName;
    private Double averageScore;
    private Integer ratingCount;
}
