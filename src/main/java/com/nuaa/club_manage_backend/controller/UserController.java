package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.UserChangePwdReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserInfoUpdateReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserResetPwdReqDTO;
import com.nuaa.club_manage_backend.dto.resp.CaptchaRespDTO;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.service.IOrdinaryUserService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PostMapping("/sendCode")
    public Result<String> sendCode(@RequestParam String contact) {
        ordinaryUserService.sendVerifyCode(contact);
        return Result.success("验证码发送成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoRespDTO> getCurrentUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(ordinaryUserService.getCurrentUserInfo(userId));
    }

    /**
     * 修改个人基本信息
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(HttpServletRequest request,
                                          @RequestBody UserInfoUpdateReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        ordinaryUserService.updateUserInfo(userId, reqDTO);
        return Result.success("个人信息修改成功");
    }

    /**
     * 修改密码（已登录状态）
     */
    @PutMapping("/password")
    public Result<String> changePassword(HttpServletRequest request,
                                          @Validated @RequestBody UserChangePwdReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        ordinaryUserService.changePassword(userId, reqDTO);
        return Result.success("密码修改成功，请重新登录");
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
