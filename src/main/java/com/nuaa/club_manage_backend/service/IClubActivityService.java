package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ActivityAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityUpdateReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ActivityListRespDTO;
import com.nuaa.club_manage_backend.entity.ClubActivity;

import java.util.List;

public interface IClubActivityService extends IService<ClubActivity> {
    /**
     * 发布活动
     */
    void createActivity(String userId, ActivityCreateReqDTO reqDTO);

    /**
     * 审核活动（系统管理员）
     */
    void auditActivity(ActivityAuditReqDTO reqDTO);

    /**
     * 修改活动信息
     */
    void updateActivity(String userId, ActivityUpdateReqDTO reqDTO);

    /**
     * 结束活动
     */
    void endActivity(String userId, String activityId);

    /**
     * 删除活动
     */
    void deleteActivity(String userId, String activityId);

    /**
     * 查看已发布的活动（支持按标题模糊搜索）
     */
    List<ActivityListRespDTO> getPublishedActivities(String title);

    /**
     * 查看待审核的活动列表（系统管理员）
     */
    List<ActivityListRespDTO> getPendingActivities();
}
