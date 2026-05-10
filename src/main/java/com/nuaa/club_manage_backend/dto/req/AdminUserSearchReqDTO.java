package com.nuaa.club_manage_backend.dto.req;

import lombok.Data;

@Data
public class AdminUserSearchReqDTO {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String search;
}
