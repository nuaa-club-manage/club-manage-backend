package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.ClubJoinReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubLeaveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubManagerSetReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubMemberAuditReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberApplyRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberAuditRespDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubMemberListRespDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.service.IClubMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements IClubMemberService {

    @Autowired
    private ClubMapper clubMapper;
    @Autowired
    private OrdinaryUserMapper ordinaryUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyJoin(String userId, ClubJoinReqDTO reqDTO) {
        // 1. 校验社团是否存在且状态为"已通过"
        Club club = clubMapper.selectById(reqDTO.getClubId());
        if (club == null) {
            throw new BusinessException("社团不存在");
        }
        if (!"已通过".equals(club.getClubState())) {
            throw new BusinessException("该社团暂不接受入社申请");
        }

        // 2. 校验是否已是该社团成员
        ClubMember exist = this.lambdaQuery()
                .eq(ClubMember::getClubId, reqDTO.getClubId())
                .eq(ClubMember::getUserId, userId)
                .one();
        if (exist != null) {
            if ("通过".equals(exist.getReviewState())) {
                throw new BusinessException("您已是该社团成员，请勿重复申请");
            }
            if ("待审核".equals(exist.getReviewState())) {
                throw new BusinessException("您的申请正在审核中，请耐心等待");
            }
            // 曾被拒绝，允许重新申请：更新状态为待审核
            exist.setReviewState("待审核");
            this.update(exist, new QueryWrapper<ClubMember>()
                    .eq("ClubID", reqDTO.getClubId())
                    .eq("UserID", userId));
            return;
        }

        // 3. 插入申请记录
        ClubMember member = new ClubMember();
        member.setClubId(reqDTO.getClubId());
        member.setUserId(userId);
        member.setReviewState("待审核");
        member.setClubManager("否");
        this.save(member);
    }

    @Override
    public List<ClubMemberApplyRespDTO> getMyApplications(String userId) {
        List<ClubMember> members = this.lambdaQuery()
                .eq(ClubMember::getUserId, userId)
                .list();

        // 批量查出社团名称
        List<String> clubIds = members.stream().map(ClubMember::getClubId).collect(Collectors.toList());
        Map<String, String> clubNameMap = Map.of();
        if (!clubIds.isEmpty()) {
            List<Club> clubs = clubMapper.selectBatchIds(clubIds);
            clubNameMap = clubs.stream().collect(Collectors.toMap(Club::getClubId, Club::getClubName));
        }

        // 组装响应
        Map<String, String> finalClubNameMap = clubNameMap;
        return members.stream().map(m -> {
            ClubMemberApplyRespDTO dto = new ClubMemberApplyRespDTO();
            dto.setClubId(m.getClubId());
            dto.setClubName(finalClubNameMap.getOrDefault(m.getClubId(), ""));
            dto.setReviewState(m.getReviewState());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClubMemberAuditRespDTO> getPendingApplications(String managerUserId) {
        // 1. 查出该用户管理的社团 ID 列表
        List<ClubMember> managedClubs = this.lambdaQuery()
                .eq(ClubMember::getUserId, managerUserId)
                .eq(ClubMember::getClubManager, "是")
                .list();
        if (managedClubs.isEmpty()) {
            return List.of();
        }
        List<String> managedClubIds = managedClubs.stream()
                .map(ClubMember::getClubId).collect(Collectors.toList());

        // 2. 查这些社团下所有待审核的申请
        List<ClubMember> pendingList = this.lambdaQuery()
                .in(ClubMember::getClubId, managedClubIds)
                .eq(ClubMember::getReviewState, "待审核")
                .list();
        if (pendingList.isEmpty()) {
            return List.of();
        }

        // 3. 批量查询社团名称和用户信息
        List<String> clubIds = pendingList.stream().map(ClubMember::getClubId).distinct().collect(Collectors.toList());
        List<String> userIds = pendingList.stream().map(ClubMember::getUserId).distinct().collect(Collectors.toList());

        Map<String, String> clubNameMap = clubMapper.selectBatchIds(clubIds).stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getClubName));
        Map<String, OrdinaryUser> userMap = ordinaryUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(OrdinaryUser::getUserId, u -> u));

        // 4. 组装响应
        return pendingList.stream().map(m -> {
            ClubMemberAuditRespDTO dto = new ClubMemberAuditRespDTO();
            dto.setUserId(m.getUserId());
            dto.setStudentId(m.getUserId());
            dto.setClubId(m.getClubId());
            dto.setReviewState(m.getReviewState());
            dto.setClubName(clubNameMap.getOrDefault(m.getClubId(), ""));
            OrdinaryUser user = userMap.get(m.getUserId());
            if (user != null) {
                dto.setUserName(user.getUserName());
                dto.setRealName(user.getRealName());
                dto.setSchool(user.getSchool());
                dto.setDegree(user.getDegree());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditApplication(String managerUserId, ClubMemberAuditReqDTO reqDTO) {
        // 1. 校验操作用户是否为该社团的管理员
        ClubMember manager = this.lambdaQuery()
                .eq(ClubMember::getClubId, reqDTO.getClubId())
                .eq(ClubMember::getUserId, managerUserId)
                .eq(ClubMember::getClubManager, "是")
                .one();
        if (manager == null) {
            throw new BusinessException("无权操作，仅社团管理员可审核入社申请");
        }

        // 2. 查找目标申请
        ClubMember target = this.lambdaQuery()
                .eq(ClubMember::getClubId, reqDTO.getClubId())
                .eq(ClubMember::getUserId, reqDTO.getUserId())
                .one();
        if (target == null) {
            throw new BusinessException("该入社申请不存在");
        }
        if (!"待审核".equals(target.getReviewState())) {
            throw new BusinessException("该申请已审核，请勿重复操作");
        }

        // 3. 更新审核状态
        target.setReviewState(Boolean.TRUE.equals(reqDTO.getPass()) ? "通过" : "未通过");
        this.update(target, new QueryWrapper<ClubMember>()
                .eq("ClubID", reqDTO.getClubId())
                .eq("UserID", reqDTO.getUserId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveClub(String userId, ClubLeaveReqDTO reqDTO) {
        // 1. 查询成员记录
        ClubMember member = this.lambdaQuery()
                .eq(ClubMember::getClubId, reqDTO.getClubId())
                .eq(ClubMember::getUserId, userId)
                .one();
        if (member == null) {
            throw new BusinessException("您不是该社团的成员");
        }
        if (!"通过".equals(member.getReviewState())) {
            throw new BusinessException("当前成员状态不允许退出社团");
        }

        // 2. 社团管理员不允许直接退出
        if ("是".equals(member.getClubManager())) {
            throw new BusinessException("社团管理员无法直接退出，请先转让管理权");
        }

        // 3. 删除成员记录
        this.remove(new QueryWrapper<ClubMember>()
                .eq("ClubID", reqDTO.getClubId())
                .eq("UserID", userId));
    }

    @Override
    public List<ClubMemberListRespDTO> getClubMembers(String managerUserId, String clubId, String search) {
        // 1. 查出该用户管理的社团 ID 列表
        List<ClubMember> managedClubs = this.lambdaQuery()
                .eq(ClubMember::getUserId, managerUserId)
                .eq(ClubMember::getClubManager, "是")
                .list();
        if (managedClubs.isEmpty()) {
            return List.of();
        }
        List<String> managedClubIds = managedClubs.stream()
                .map(ClubMember::getClubId).collect(Collectors.toList());

        // 2. 如果指定了 clubId，校验是否有权查看
        if (clubId != null && !clubId.isEmpty()) {
            if (!managedClubIds.contains(clubId)) {
                throw new BusinessException("无权查看该社团的成员信息");
            }
            managedClubIds = List.of(clubId);
        }

        // 3. 查询这些社团的所有通过审核的成员
        List<ClubMember> members = this.lambdaQuery()
                .in(ClubMember::getClubId, managedClubIds)
                .eq(ClubMember::getReviewState, "通过")
                .list();
        if (members.isEmpty()) {
            return List.of();
        }

        // 4. 如果有搜索条件，按姓名或学号过滤
        if (search != null && !search.isEmpty()) {
            List<String> matchedUserIds = getUserIdsBySearch(search);
            members = members.stream()
                    .filter(m -> matchedUserIds.contains(m.getUserId()))
                    .collect(Collectors.toList());
            if (members.isEmpty()) {
                return List.of();
            }
        }

        return buildMemberListResp(members);
    }

    @Override
    public List<ClubMemberListRespDTO> getAllClubMembers(String search) {
        // 查询所有已通过社团的已通过成员
        List<Club> activeClubs = clubMapper.selectList(
                new QueryWrapper<Club>().eq("ClubState", "已通过")
        );
        if (activeClubs.isEmpty()) {
            return List.of();
        }
        List<String> activeClubIds = activeClubs.stream()
                .map(Club::getClubId).collect(Collectors.toList());

        List<ClubMember> members = this.lambdaQuery()
                .in(ClubMember::getClubId, activeClubIds)
                .eq(ClubMember::getReviewState, "通过")
                .list();
        if (members.isEmpty()) {
            return List.of();
        }

        // 如果有搜索条件，按姓名或学号过滤
        if (search != null && !search.isEmpty()) {
            List<String> matchedUserIds = getUserIdsBySearch(search);
            members = members.stream()
                    .filter(m -> matchedUserIds.contains(m.getUserId()))
                    .collect(Collectors.toList());
            if (members.isEmpty()) {
                return List.of();
            }
        }

        return buildMemberListResp(members);
    }

    @Override
    public long countClubMembers(String clubId) {
        return this.lambdaQuery()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getReviewState, "通过")
                .count();
    }

    private List<ClubMemberListRespDTO> buildMemberListResp(List<ClubMember> members) {
        // 批量查社团名称和用户信息
        List<String> clubIds = members.stream().map(ClubMember::getClubId).distinct().collect(Collectors.toList());
        List<String> userIds = members.stream().map(ClubMember::getUserId).distinct().collect(Collectors.toList());

        Map<String, String> clubNameMap = clubMapper.selectBatchIds(clubIds).stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getClubName));
        Map<String, OrdinaryUser> userMap = ordinaryUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(OrdinaryUser::getUserId, u -> u));

        return members.stream().map(m -> {
            ClubMemberListRespDTO dto = new ClubMemberListRespDTO();
            dto.setUserId(m.getUserId());
            dto.setClubId(m.getClubId());
            dto.setClubManager(m.getClubManager());
            dto.setReviewState(m.getReviewState());
            dto.setClubName(clubNameMap.getOrDefault(m.getClubId(), ""));
            OrdinaryUser user = userMap.get(m.getUserId());
            if (user != null) {
                dto.setUserName(user.getUserName());
                dto.setRealName(user.getRealName());
                dto.setSchool(user.getSchool());
                dto.setDegree(user.getDegree());
                dto.setPhoneNumber(user.getPhoneNumber());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private List<String> getUserIdsBySearch(String search) {
        return ordinaryUserMapper.selectList(
                new QueryWrapper<OrdinaryUser>()
                        .like("userName", search)
                        .or()
                        .like("userID", search)
        ).stream().map(OrdinaryUser::getUserId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setClubManager(ClubManagerSetReqDTO reqDTO) {
        // 1. 查找目标成员
        ClubMember member = this.lambdaQuery()
                .eq(ClubMember::getClubId, reqDTO.getClubId())
                .eq(ClubMember::getUserId, reqDTO.getUserId())
                .one();
        if (member == null) {
            throw new BusinessException("该成员不存在");
        }
        if (!"通过".equals(member.getReviewState())) {
            throw new BusinessException("该成员尚未通过审核，无法设置管理员权限");
        }

        if (Boolean.TRUE.equals(reqDTO.getSetManager())) {
            // 设置管理员
            if ("是".equals(member.getClubManager())) {
                throw new BusinessException("该成员已经是社团管理员");
            }
            member.setClubManager("是");
        } else {
            // 取消管理员
            if (!"是".equals(member.getClubManager())) {
                throw new BusinessException("该成员不是社团管理员，无需取消");
            }
            // 保证每个社团至少有一个管理员
            long adminCount = this.lambdaQuery()
                    .eq(ClubMember::getClubId, reqDTO.getClubId())
                    .eq(ClubMember::getClubManager, "是")
                    .count();
            if (adminCount <= 1) {
                throw new BusinessException("社团至少需要一名管理员，请先设置其他成员为管理员后再取消");
            }
            member.setClubManager("否");
        }

        this.update(member, new QueryWrapper<ClubMember>()
                .eq("ClubID", reqDTO.getClubId())
                .eq("UserID", reqDTO.getUserId()));
    }
}
