package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.RegistrationAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.RegistrationAuditViewDTO;
import com.nuaa.club_manage_backend.dto.resp.ApprovedParticipantDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRegistrationDetailDTO;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;

import java.util.List;

public interface IRegistrationInfoService extends IService<RegistrationInfo> {
    /**
     * 用户报名活动
     */
    void registerActivity(String userId, ActivityRegisterReqDTO reqDTO);

    /**
     * 管理员获取待审核报名列表
     */
    List<RegistrationAuditViewDTO> getPendingRegistrations(String currentUserID);

    /**
     * 管理员审核报名
     */
    void auditRegistration(String currentUserID, RegistrationAuditReqDTO req);

    /**
     * 用户查看个人报名列表
     */
    List<UserRegistrationDetailDTO> getUserRegistrations(String currentUserID);

    /**
     * 用户取消报名
     */
    void cancelRegistration(String currentUserID, String registrationID);

    /**
     * 管理员查看已通过报名的用户列表
     */
    List<ApprovedParticipantDTO> getApprovedParticipants(String currentUserID, String activityID);
}
