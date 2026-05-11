package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ClubJoinReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubLeaveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubMemberAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberApplyRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberAuditRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberListRespDTO;
import com.nuaa.club_manage_backend.service.IClubMemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class ClubMemberController {

    @Autowired
    private IClubMemberService clubMemberService;

    /**
     * 提交入社申请
     */
    @PostMapping("/join")
    public Result<String> applyJoin(HttpServletRequest request,
                                    @Validated @RequestBody ClubJoinReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubMemberService.applyJoin(userId, reqDTO);
        return Result.success("入社申请提交成功，请等待管理员审核");
    }

    /**
     * 查看我的入社申请列表
     */
    @GetMapping("/my")
    public Result<List<ClubMemberApplyRespDTO>> getMyApplications(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(clubMemberService.getMyApplications(userId));
    }

    /**
     * 社团管理员查看待审核的入社申请
     */
    @GetMapping("/pending")
    public Result<List<ClubMemberAuditRespDTO>> getPendingApplications(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(clubMemberService.getPendingApplications(userId));
    }

    /**
     * 社团管理员审核入社申请
     */
    @PutMapping("/audit")
    public Result<String> auditApplication(HttpServletRequest request,
                                           @Validated @RequestBody ClubMemberAuditReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubMemberService.auditApplication(userId, reqDTO);
        return Result.success("审核操作成功");
    }

    /**
     * 退出社团
     */
    @DeleteMapping("/leave")
    public Result<String> leaveClub(HttpServletRequest request,
                                    @Validated @RequestBody ClubLeaveReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubMemberService.leaveClub(userId, reqDTO);
        return Result.success("已退出社团");
    }

    /**
     * 社团管理员查看自己管理的社团成员名单
     */
    @GetMapping("/list")
    public Result<List<ClubMemberListRespDTO>> getClubMembers(HttpServletRequest request,
                                                               @RequestParam(required = false) String clubId) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(clubMemberService.getClubMembers(userId, clubId));
    }
}
