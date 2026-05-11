package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubRatingReqDTO {
    @NotBlank(message = "社团ID不能为空")
    private String clubId;

    @NotBlank(message = "评分不能为空")
    private String rating;
}
