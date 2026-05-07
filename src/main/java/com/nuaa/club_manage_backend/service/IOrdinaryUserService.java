package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;

public interface IOrdinaryUserService extends IService<OrdinaryUser> {
    String login(UserLoginReqDTO reqDTO);
}