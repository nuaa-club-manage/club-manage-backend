package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.entity.Administrator;

public interface IAdministratorService extends IService<Administrator> {
    /**
     * 管理员登录
     */
    String login(AdminLoginReqDTO reqDTO);
}
