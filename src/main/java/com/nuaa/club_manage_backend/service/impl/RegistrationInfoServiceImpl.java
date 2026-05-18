package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.RegistrationAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ApprovedParticipantDTO;
import com.nuaa.club_manage_backend.dto.resp.RegistrationAuditViewDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRegistrationDetailDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.ClubActivity;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.ClubActivityMapper;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.service.IRegistrationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationInfoServiceImpl extends ServiceImpl<RegistrationInfoMapper, RegistrationInfo> implements IRegistrationInfoService {

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private ClubActivityMapper clubActivityMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Override
    public void registerActivity(String userId, ActivityRegisterReqDTO reqDTO) {
        // 1. 防重校验
        Long count = this.lambdaQuery()
                .eq(RegistrationInfo::getActivityId, reqDTO.getActivityId())
                .eq(RegistrationInfo::getUserId, userId)
                .count();
        if (count > 0) {
            throw new BusinessException("您已报名该活动，请勿重复提交");
        }

        // 2. 组装报名记录
        RegistrationInfo registration = new RegistrationInfo();
        registration.setActivityId(reqDTO.getActivityId());
        registration.setUserId(userId);
        registration.setRealName(reqDTO.getRealName());
        registration.setPhoneNumber(reqDTO.getPhoneNumber());
        registration.setReviewState("审核中");

        // 3. 入库
        this.save(registration);
    }

    @Override
    public List<RegistrationAuditViewDTO> getPendingRegistrations(String currentUserID) {
        // 1. 查出当前用户管理哪些社团
        List<ClubMember> managedClubs = clubMemberMapper.selectList(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getUserId, currentUserID)
                        .eq(ClubMember::getClubManager, "是")
        );
        if (managedClubs.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> clubIds = managedClubs.stream()
                .map(ClubMember::getClubId)
                .collect(Collectors.toList());

        // 2. 查出这些社团的所有活动，建立 ActivityID → Title 映射
        List<ClubActivity> activities = clubActivityMapper.selectList(
                new LambdaQueryWrapper<ClubActivity>()
                        .in(ClubActivity::getClubId, clubIds)
        );
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, String> activityTitleMap = activities.stream()
                .collect(Collectors.toMap(ClubActivity::getActivityId, ClubActivity::getTitle));
        List<String> activityIds = activities.stream()
                .map(ClubActivity::getActivityId)
                .collect(Collectors.toList());

        // 3. 查出这些活动下状态为"审核中"的报名记录
        List<RegistrationInfo> pendingList = this.lambdaQuery()
                .in(RegistrationInfo::getActivityId, activityIds)
                .eq(RegistrationInfo::getReviewState, "审核中")
                .list();

        // 4. 组装 DTO
        return pendingList.stream().map(r -> {
            RegistrationAuditViewDTO dto = new RegistrationAuditViewDTO();
            dto.setRegistrationId(r.getRegistrationId());
            dto.setActivityId(r.getActivityId());
            dto.setTitle(activityTitleMap.get(r.getActivityId()));
            dto.setUserId(r.getUserId());
            dto.setRealName(r.getRealName());
            dto.setPhoneNumber(r.getPhoneNumber());
            dto.setReviewState(r.getReviewState());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void auditRegistration(String currentUserID, RegistrationAuditReqDTO req) {
        // 1. 获取报名记录
        RegistrationInfo registration = this.getById(req.getRegistrationId());
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 2. 获取活动所属社团
        ClubActivity activity = clubActivityMapper.selectById(registration.getActivityId());
        if (activity == null) {
            throw new BusinessException("关联活动不存在");
        }

        // 3. 越权校验
        Long managerCount = clubMemberMapper.selectCount(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getUserId, currentUserID)
                        .eq(ClubMember::getClubId, activity.getClubId())
                        .eq(ClubMember::getClubManager, "是")
        );
        if (managerCount == null || managerCount == 0) {
            throw new BusinessException("越权操作：您无权审核该社团的报名");
        }

        // 4. 更新审核状态
        registration.setReviewState(req.getPass() ? "审核通过" : "审核失败");
        this.updateById(registration);
    }

    @Override
    public List<UserRegistrationDetailDTO> getUserRegistrations(String currentUserID) {
        // 1. 查出当前用户的所有报名记录
        List<RegistrationInfo> registrations = this.lambdaQuery()
                .eq(RegistrationInfo::getUserId, currentUserID)
                .list();
        if (registrations.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 提取所有 ActivityID，批量查询活动详情
        List<String> activityIds = registrations.stream()
                .map(RegistrationInfo::getActivityId)
                .collect(Collectors.toList());
        List<ClubActivity> activities = clubActivityMapper.selectList(
                new LambdaQueryWrapper<ClubActivity>()
                        .in(ClubActivity::getActivityId, activityIds)
        );
        Map<String, ClubActivity> activityMap = activities.stream()
                .collect(Collectors.toMap(ClubActivity::getActivityId, a -> a));

        // 3. 提取所有社团ID，批量查询社团名称
        List<String> clubIds = activities.stream()
                .map(ClubActivity::getClubId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> clubNameMap = clubIds.isEmpty() ? Collections.emptyMap() :
                clubMapper.selectBatchIds(clubIds).stream()
                        .collect(Collectors.toMap(Club::getClubId, Club::getClubName));

        // 4. 组装 DTO
        return registrations.stream().map(r -> {
            ClubActivity activity = activityMap.get(r.getActivityId());

            UserRegistrationDetailDTO dto = new UserRegistrationDetailDTO();
            dto.setRegistrationId(r.getRegistrationId());
            dto.setActivityId(r.getActivityId());
            dto.setReviewState(r.getReviewState());
            if (activity != null) {
                dto.setTitle(activity.getTitle());
                dto.setContent(activity.getContent());
                dto.setPublishTime(activity.getPublishTime());
                dto.setClubId(activity.getClubId());
                dto.setClubName(clubNameMap.get(activity.getClubId()));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void cancelRegistration(String currentUserID, String registrationID) {
        // 1. 获取报名记录
        RegistrationInfo registration = this.getById(registrationID);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 2. 越权校验
        if (!registration.getUserId().equals(currentUserID)) {
            throw new BusinessException("越权操作：您只能取消自己的报名记录");
        }

        // 3. 状态检查：审核失败的不可取消（已审核通过的也保留记录）
        if ("审核失败".equals(registration.getReviewState())) {
            throw new BusinessException("当前状态无法执行取消操作");
        }

        // 4. 删除记录
        this.removeById(registrationID);
    }

    @Override
    public List<ApprovedParticipantDTO> getApprovedParticipants(String currentUserID, String activityID) {
        // 1. 获取活动所属社团
        ClubActivity activity = clubActivityMapper.selectById(activityID);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 2. 越权校验
        Long managerCount = clubMemberMapper.selectCount(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getUserId, currentUserID)
                        .eq(ClubMember::getClubId, activity.getClubId())
                        .eq(ClubMember::getClubManager, "是")
        );
        if (managerCount == null || managerCount == 0) {
            throw new BusinessException("越权操作：您无权查看此活动的成员名单");
        }

        // 3. 查询已通过报名的用户
        List<RegistrationInfo> approvedList = this.lambdaQuery()
                .eq(RegistrationInfo::getActivityId, activityID)
                .eq(RegistrationInfo::getReviewState, "审核通过")
                .list();

        // 4. 转换 DTO
        return approvedList.stream().map(r -> {
            ApprovedParticipantDTO dto = new ApprovedParticipantDTO();
            dto.setUserId(r.getUserId());
            dto.setRealName(r.getRealName());
            dto.setPhoneNumber(r.getPhoneNumber());
            return dto;
        }).collect(Collectors.toList());
    }
}
