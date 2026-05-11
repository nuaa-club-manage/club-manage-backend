package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubCreateReqDTO {

    @NotBlank(message = "社团名称不能为空")
    private String clubName;

    @NotBlank(message = "社团简介不能为空")
    private String clubInformation;

    @NotBlank(message = "社团分类不能为空")
    private String clubCategory;

    @NotBlank(message = "社团封面图片URL不能为空")
    private String clubCoverImage;
}
