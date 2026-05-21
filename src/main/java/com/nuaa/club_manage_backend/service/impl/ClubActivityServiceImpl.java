package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.ActivityAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityEndReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityUpdateReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ActivityListRespDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.ClubActivity;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.ClubActivityMapper;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.service.IClubActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClubActivityServiceImpl extends ServiceImpl<ClubActivityMapper, ClubActivity> implements IClubActivityService {

    @Autowired
    private ClubMapper clubMapper;
    @Autowired
    private ClubMemberMapper clubMemberMapper;
    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(String userId, ActivityCreateReqDTO reqDTO) {
        // 1. 校验社团是否存在且已通过
        Club club = clubMapper.selectById(reqDTO.getClubId());
        if (club == null || !"已通过".equals(club.getClubState())) {
            throw new BusinessException("社团不存在或未成立");
        }

        // 2. 校验当前用户是否为该社团管理员
        checkClubManager(userId, reqDTO.getClubId());

        // 3. 组装活动实体
        ClubActivity activity = new ClubActivity();
        activity.setActivityId(UUID.randomUUID().toString());
        activity.setClubId(reqDTO.getClubId());
        activity.setUserId(userId);
        activity.setTitle(reqDTO.getTitle());
        activity.setContent(reqDTO.getContent());
        activity.setLocation(reqDTO.getLocation());
        activity.setCapacityLimit(reqDTO.getCapacityLimit());
        activity.setActivityState("待审核");
        activity.setPublishTime(LocalDateTime.now());

        this.save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditActivity(ActivityAuditReqDTO reqDTO) {
        ClubActivity activity = this.getById(reqDTO.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if (!"待审核".equals(activity.getActivityState())) {
            throw new BusinessException("该活动不在待审核状态，无法审核");
        }

        activity.setActivityState(Boolean.TRUE.equals(reqDTO.getPass()) ? "已发布" : "未通过");
        this.updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(String userId, ActivityUpdateReqDTO reqDTO) {
        ClubActivity activity = this.getById(reqDTO.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 校验操作者是该活动所属社团的管理员
        checkClubManager(userId, activity.getClubId());

        activity.setTitle(reqDTO.getTitle());
        activity.setContent(reqDTO.getContent());
        activity.setLocation(reqDTO.getLocation());
        activity.setCapacityLimit(reqDTO.getCapacityLimit());
        this.updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endActivity(String userId, ActivityEndReqDTO reqDTO) {
        ClubActivity activity = this.getById(reqDTO.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        checkClubManager(userId, activity.getClubId());

        if (!"已发布".equals(activity.getActivityState())) {
            throw new BusinessException("只有已发布的活动才能结束");
        }

        // 在 content 末尾追加活动总结和到场名单，用分隔符分隔
        String separator = "\n===== 活动总结 =====\n";
        String content = activity.getContent() + separator + reqDTO.getSummary()
                + "\n===== 到场名单 =====\n" + reqDTO.getParticipantList();

        activity.setContent(content);
        activity.setActivityState("已结束");

        // 删除该活动中处于"审核中"状态的报名记录
        registrationInfoMapper.delete(
                new QueryWrapper<RegistrationInfo>()
                        .eq("ActivityID", reqDTO.getActivityId())
                        .eq("ReviewState", "审核中")
        );

        this.updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(String userId, String activityId) {
        ClubActivity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        checkClubManager(userId, activity.getClubId());

        // 级联删除关联的报名信息
        registrationInfoMapper.delete(
                new QueryWrapper<RegistrationInfo>().eq("ActivityID", activityId)
        );

        // 删除活动
        this.removeById(activityId);
    }

    @Override
    public List<ActivityListRespDTO> getPublishedActivities(String title) {
        QueryWrapper<ClubActivity> wrapper = new QueryWrapper<ClubActivity>()
                .eq("ActivityState", "已发布");
        if (title != null && !title.isEmpty()) {
            wrapper.like("Title", title);
        }
        List<ClubActivity> activities = this.list(wrapper);
        return buildActivityListResp(activities);
    }

    @Override
    public List<ActivityListRespDTO> getPendingActivities() {
        List<ClubActivity> activities = this.lambdaQuery()
                .eq(ClubActivity::getActivityState, "待审核")
                .list();
        return buildActivityListResp(activities);
    }

    @Override
    public List<ActivityListRespDTO> getMyActivities(String userId) {
        List<ClubActivity> activities = this.lambdaQuery()
                .eq(ClubActivity::getUserId, userId)
                .list();
        return buildActivityListResp(activities);
    }

    @Override
    public List<ActivityListRespDTO> getClubActivities(String userId, String clubId) {
        checkClubManager(userId, clubId);
        List<ClubActivity> activities = this.lambdaQuery()
                .eq(ClubActivity::getClubId, clubId)
                .list();
        return buildActivityListResp(activities);
    }

    private void checkClubManager(String userId, String clubId) {
        ClubMember member = clubMemberMapper.selectOne(
                new QueryWrapper<ClubMember>()
                        .eq("ClubID", clubId)
                        .eq("UserID", userId)
                        .eq("ClubManager", "是")
        );
        if (member == null) {
            throw new BusinessException("无权操作，仅社团管理员可执行此操作");
        }
    }

    private List<ActivityListRespDTO> buildActivityListResp(List<ClubActivity> activities) {
        if (activities.isEmpty()) {
            return List.of();
        }
        List<String> clubIds = activities.stream()
                .map(ClubActivity::getClubId).distinct().collect(Collectors.toList());
        Map<String, String> clubNameMap = clubMapper.selectBatchIds(clubIds).stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getClubName));

        return activities.stream().map(a -> {
            ActivityListRespDTO dto = new ActivityListRespDTO();
            dto.setActivityId(a.getActivityId());
            dto.setClubId(a.getClubId());
            dto.setClubName(clubNameMap.getOrDefault(a.getClubId(), ""));
            dto.setUserId(a.getUserId());
            dto.setTitle(a.getTitle());
            dto.setContent(a.getContent());
            dto.setLocation(a.getLocation());
            dto.setCapacityLimit(a.getCapacityLimit());
            dto.setActivityState(a.getActivityState());
            dto.setPublishTime(a.getPublishTime());
            return dto;
        }).collect(Collectors.toList());
    }
}
