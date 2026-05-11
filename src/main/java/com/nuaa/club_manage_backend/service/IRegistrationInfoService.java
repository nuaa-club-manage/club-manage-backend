package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;

public interface IRegistrationInfoService extends IService<RegistrationInfo> {
    /**
     * 用户报名活动
     */
    void registerActivity(String userId, ActivityRegisterReqDTO reqDTO);
}
