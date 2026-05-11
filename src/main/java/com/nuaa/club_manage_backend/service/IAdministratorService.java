package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.AdminEditUserReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminUserSearchReqDTO;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.entity.Administrator;

public interface IAdministratorService extends IService<Administrator> {
    /**
     * 管理员登录
     */
    String login(AdminLoginReqDTO reqDTO);

    /**
     * 管理员多条件分页查询用户
     */
    IPage<UserInfoRespDTO> searchUsersByAdmin(String adminId, AdminUserSearchReqDTO reqDTO);

    /**
     * 系统管理员修改用户资料
     */
    void updateUserBySystemAdmin(String adminID, AdminEditUserReqDTO reqDTO);
}
