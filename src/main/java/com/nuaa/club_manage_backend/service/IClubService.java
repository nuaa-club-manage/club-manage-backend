package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ClubAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubDissolveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubUpdateReqDTO;
import com.nuaa.club_manage_backend.entity.Club;

import java.util.List;

public interface IClubService extends IService<Club> {
    /**
     * 提交成立社团申请
     */
    void createClub(String userId, ClubCreateReqDTO reqDTO);

    /**
     * 提交解散社团申请
     */
    void dissolveClub(String userId, ClubDissolveReqDTO reqDTO);

    /**
     * 修改社团信息
     */
    void updateClub(String userId, ClubUpdateReqDTO reqDTO);

    /**
     * 查看所有已成立的社团信息
     */
    List<Club> getActiveClubs();

    /**
     * 管理员审核成立社团申请
     */
    void auditClubCreation(ClubAuditReqDTO reqDTO);

    /**
     * 查看待审核的社团列表
     */
    List<Club> getPendingClubs();
}
