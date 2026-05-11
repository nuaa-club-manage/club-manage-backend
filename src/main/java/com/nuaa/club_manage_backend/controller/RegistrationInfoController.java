package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.service.IRegistrationInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public class RegistrationInfoController {

    @Autowired
    private IRegistrationInfoService registrationInfoService;

    /**
     * 用户报名活动
     */
    @PostMapping("/register")
    public Result<String> register(HttpServletRequest request,
                                   @Validated @RequestBody ActivityRegisterReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        registrationInfoService.registerActivity(userId, reqDTO);
        return Result.success("报名成功，请等待管理员审核");
    }
}
