package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.resp.CaptchaRespDTO;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;

public interface IOrdinaryUserService extends IService<OrdinaryUser> {
    /**
     * 登录
     */
    String login(UserLoginReqDTO reqDTO);

    /**
     * 获取图形验证码
     */
    CaptchaRespDTO getCaptcha();

    /**
     * 发送验证码（自动识别手机或邮箱）
     */
    void sendVerifyCode(String contact);

    /**
     * 用户注册
     */
    void register(UserRegisterReqDTO reqDTO);
}
