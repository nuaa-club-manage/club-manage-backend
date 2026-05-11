package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.CancelRegistrationReqDTO;
import com.nuaa.club_manage_backend.dto.req.RegistrationAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ApprovedParticipantDTO;
import com.nuaa.club_manage_backend.dto.resp.RegistrationAuditViewDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRegistrationDetailDTO;
import com.nuaa.club_manage_backend.service.IRegistrationInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 用户取消报名
     */
    @PostMapping("/cancel")
    public Result<String> cancel(HttpServletRequest request,
                                 @Validated @RequestBody CancelRegistrationReqDTO req) {
        String userId = (String) request.getAttribute("currentUserId");
        registrationInfoService.cancelRegistration(userId, req.getRegistrationID());
        return Result.success("报名已成功取消");
    }

    /**
     * 管理员获取待审核报名列表
     */
    @GetMapping("/admin/list")
    public Result<List<RegistrationAuditViewDTO>> getPendingList(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(registrationInfoService.getPendingRegistrations(userId));
    }

    /**
     * 用户查看个人报名列表
     */
    @GetMapping("/my")
    public Result<List<UserRegistrationDetailDTO>> getMyRegistrations(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(registrationInfoService.getUserRegistrations(userId));
    }

    /**
     * 管理员审核报名
     */
    @PutMapping("/admin/audit")
    public Result<String> audit(HttpServletRequest request,
                                @Validated @RequestBody RegistrationAuditReqDTO req) {
        String userId = (String) request.getAttribute("currentUserId");
        registrationInfoService.auditRegistration(userId, req);
        return Result.success("审核操作成功");
    }

    /**
     * 管理员查看已通过报名的用户列表
     */
    @GetMapping("/admin/participants")
    public Result<List<ApprovedParticipantDTO>> getApprovedParticipants(HttpServletRequest request,
                                                                        @RequestParam("activityID") String activityID) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(registrationInfoService.getApprovedParticipants(userId, activityID));
    }
}
