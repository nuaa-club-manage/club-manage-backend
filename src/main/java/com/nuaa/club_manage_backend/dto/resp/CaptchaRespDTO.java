package com.nuaa.club_manage_backend.dto.resp;

import lombok.Data;

@Data
public class CaptchaRespDTO {
    private String captchaId;
    private String captchaText;
}
