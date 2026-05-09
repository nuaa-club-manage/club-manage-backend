package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.service.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdministratorService administratorService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody AdminLoginReqDTO reqDTO) {
        String token = administratorService.login(reqDTO);
        return Result.success(token);
    }
}
