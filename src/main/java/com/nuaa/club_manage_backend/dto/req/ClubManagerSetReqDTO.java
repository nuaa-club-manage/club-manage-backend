package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClubManagerSetReqDTO {

    @NotBlank(message = "社团ID不能为空")
    private String clubId;

    @NotBlank(message = "成员用户ID不能为空")
    private String userId;

    @NotNull(message = "是否设为管理员不能为空")
    private Boolean setManager;
}
