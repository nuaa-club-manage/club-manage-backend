package com.nuaa.club_manage_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.AdminEditUserReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminUserSearchReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubManagerSetReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ActivityListRespDTO;
import com.nuaa.club_manage_backend.service.IClubActivityService;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.entity.Administrator;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberListRespDTO;
import com.nuaa.club_manage_backend.entity.Administrator;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.service.IAdministratorService;
import com.nuaa.club_manage_backend.service.IClubMemberService;
import com.nuaa.club_manage_backend.service.IClubService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdministratorService administratorService;
    @Autowired
    private IClubService clubService;
    @Autowired
    private IClubMemberService clubMemberService;
    @Autowired
    private IClubActivityService clubActivityService;

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

    /**
     * 系统管理员修改用户资料
     */
    @PutMapping("/user/profile")
    public Result<String> updateUserProfile(HttpServletRequest request,
                                             @Validated @RequestBody AdminEditUserReqDTO reqDTO) {
        String adminId = (String) request.getAttribute("currentUserId");
        administratorService.updateUserBySystemAdmin(adminId, reqDTO);
        return Result.success("用户资料修改成功");
    }

    /**
     * 查看待审核的社团列表
     */
    @GetMapping("/clubs/pending")
    public Result<List<Club>> getPendingClubs(HttpServletRequest request) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        return Result.success(clubService.getPendingClubs());
    }

    /**
     * 审核成立社团申请
     */
    @PostMapping("/club/audit")
    public Result<String> auditClubCreation(HttpServletRequest request,
                                            @Validated @RequestBody ClubAuditReqDTO reqDTO) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        clubService.auditClubCreation(reqDTO);
        if (Boolean.TRUE.equals(reqDTO.getPass())) {
            return Result.success("社团成立申请审核通过");
        } else {
            return Result.success("社团成立申请已拒绝");
        }
    }

    /**
     * 系统管理员查看所有已成立社团的成员名单（支持按姓名/学号模糊搜索）
     */
    @GetMapping("/clubs/members")
    public Result<List<ClubMemberListRespDTO>> getAllClubMembers(HttpServletRequest request,
                                                                  @RequestParam(required = false) String search) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        return Result.success(clubMemberService.getAllClubMembers(search));
    }

    /**
     * 系统管理员设置/取消社团管理员
     */
    @PutMapping("/club/manager")
    public Result<String> setClubManager(HttpServletRequest request,
                                         @Validated @RequestBody ClubManagerSetReqDTO reqDTO) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        clubMemberService.setClubManager(reqDTO);
        if (Boolean.TRUE.equals(reqDTO.getSetManager())) {
            return Result.success("管理员权限设置成功");
        } else {
            return Result.success("管理员权限已取消");
        }
    }

    /**
     * 系统管理员查看待审核的活动列表
     */
    @GetMapping("/activities/pending")
    public Result<List<ActivityListRespDTO>> getPendingActivities(HttpServletRequest request) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        return Result.success(clubActivityService.getPendingActivities());
    }

    /**
     * 系统管理员审核活动
     */
    @PostMapping("/activity/audit")
    public Result<String> auditActivity(HttpServletRequest request,
                                        @Validated @RequestBody ActivityAuditReqDTO reqDTO) {
        String adminId = (String) request.getAttribute("currentUserId");
        Administrator admin = administratorService.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }
        clubActivityService.auditActivity(reqDTO);
        if (Boolean.TRUE.equals(reqDTO.getPass())) {
            return Result.success("活动审核通过");
        } else {
            return Result.success("活动已拒绝");
        }
    }
}
