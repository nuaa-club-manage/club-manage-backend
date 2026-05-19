package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ClubJoinReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubLeaveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubManagerSetReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubMemberAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberApplyRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberAuditRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberListRespDTO;
import com.nuaa.club_manage_backend.entity.ClubMember;

import java.util.List;

public interface IClubMemberService extends IService<ClubMember> {
    /**
     * 提交入社申请
     */
    void applyJoin(String userId, ClubJoinReqDTO reqDTO);

    /**
     * 查看我的申请列表
     */
    List<ClubMemberApplyRespDTO> getMyApplications(String userId);

    /**
     * 社团管理员查看待审核的入社申请
     */
    List<ClubMemberAuditRespDTO> getPendingApplications(String managerUserId);

    /**
     * 社团管理员审核入社申请
     */
    void auditApplication(String managerUserId, ClubMemberAuditReqDTO reqDTO);

    /**
     * 退出社团
     */
    void leaveClub(String userId, ClubLeaveReqDTO reqDTO);

    /**
     * 社团管理员查看自己管理的社团成员名单（支持按姓名/学号模糊搜索）
     */
    List<ClubMemberListRespDTO> getClubMembers(String managerUserId, String clubId, String search);

    /**
     * 系统管理员查看所有已成立社团的成员名单（支持按姓名/学号模糊搜索）
     */
    List<ClubMemberListRespDTO> getAllClubMembers(String search);

    /**
     * 系统管理员设置/取消社团管理员
     */
    void setClubManager(ClubManagerSetReqDTO reqDTO);

    /**
     * 公开查询指定社团的成员数量
     */
    long countClubMembers(String clubId);
}
