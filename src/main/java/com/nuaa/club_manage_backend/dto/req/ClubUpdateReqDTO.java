package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubUpdateReqDTO {

    @NotBlank(message = "社团ID不能为空")
    private String clubId;

    @NotBlank(message = "社团名称不能为空")
    private String clubName;

    @NotBlank(message = "社团简介不能为空")
    private String clubInformation;

    @NotBlank(message = "所属学校不能为空")
    private String school;
}
