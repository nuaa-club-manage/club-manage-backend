package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubLeaveReqDTO {

    @NotBlank(message = "社团ID不能为空")
    private String clubId;
}
