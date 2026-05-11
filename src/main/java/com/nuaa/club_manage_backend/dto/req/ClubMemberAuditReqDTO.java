package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClubMemberAuditReqDTO {

    @NotBlank(message = "社团ID不能为空")
    private String clubId;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotNull(message = "审核结果不能为空")
    private Boolean pass;
}
