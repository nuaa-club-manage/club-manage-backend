package com.nuaa.club_manage_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminUserSearchReqDTO;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.service.IAdministratorService;
import jakarta.servlet.http.HttpServletRequest;
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

    /**
     * 管理员多条件分页查询用户
     */
    @PostMapping("/users/search")
    public Result<IPage<UserInfoRespDTO>> searchUsers(HttpServletRequest request,
                                                      @RequestBody AdminUserSearchReqDTO reqDTO) {
        String adminId = (String) request.getAttribute("currentUserId");
        return Result.success(administratorService.searchUsersByAdmin(adminId, reqDTO));
    }
}
