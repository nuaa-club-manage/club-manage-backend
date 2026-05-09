package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserResetPwdReqDTO;
import com.nuaa.club_manage_backend.dto.resp.CaptchaRespDTO;
import com.nuaa.club_manage_backend.service.IOrdinaryUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IOrdinaryUserService ordinaryUserService;

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaRespDTO> getCaptcha() {
        return Result.success(ordinaryUserService.getCaptcha());
    }

    /**
     * 发送验证码（自动识别手机或邮箱）
     */
    @GetMapping("/sendCode")
    public Result<String> sendCode(@RequestParam String contact) {
        ordinaryUserService.sendVerifyCode(contact);
        return Result.success("验证码发送成功");
    }

    /**
     * 发送重置密码验证码
     */
    @GetMapping("/sendResetCode")
    public Result<String> sendResetCode(@RequestParam String contact) {
        ordinaryUserService.sendResetCode(contact);
        return Result.success("验证码发送成功");
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@Validated @RequestBody UserResetPwdReqDTO reqDTO) {
        ordinaryUserService.resetPassword(reqDTO);
        return Result.success("密码重置成功");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody UserRegisterReqDTO reqDTO) {
        ordinaryUserService.register(reqDTO);
        return Result.success("注册成功");
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody UserLoginReqDTO reqDTO) {
        String token = ordinaryUserService.login(reqDTO);
        return Result.success(token);
    }
}
