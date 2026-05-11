package com.nuaa.club_manage_backend.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelRegistrationReqDTO {
    @NotBlank(message = "报名记录ID不能为空")
    private String registrationID;
}
