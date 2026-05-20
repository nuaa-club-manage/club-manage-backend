package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.ClubAuditReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubDissolveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubUpdateReqDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.ClubActivity;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.entity.RatingClub;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.ClubActivityMapper;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.mapper.RatingClubMapper;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.service.IClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements IClubService {

    @Autowired
    private ClubMemberMapper clubMemberMapper;
    @Autowired
    private ClubActivityMapper clubActivityMapper;
    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;
    @Autowired
    private RatingClubMapper ratingClubMapper;
    @Autowired
    private OrdinaryUserMapper ordinaryUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createClub(String userId, ClubCreateReqDTO reqDTO) {
        // 1. 校验社团名称是否已存在（排除已解散的社团）
        Club exist = this.lambdaQuery()
                .eq(Club::getClubName, reqDTO.getClubName())
                .ne(Club::getClubState, "已解散")
                .one();
        if (exist != null) {
            throw new BusinessException("该社团名称已存在，请更换名称");
        }

        // 2. 查询创建人的学校
        OrdinaryUser creator = ordinaryUserMapper.selectById(userId);
        if (creator == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 组装社团实体
        Club club = new Club();
        club.setClubId(UUID.randomUUID().toString());
        club.setUserId(userId);
        club.setClubName(reqDTO.getClubName());
        club.setClubInformation(reqDTO.getClubInformation());
        club.setSchool(creator.getSchool());
        club.setClubState("待审核");
        club.setEstablishmentTime(LocalDateTime.now());

        // 4. 入库
        this.save(club);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dissolveClub(String userId, ClubDissolveReqDTO reqDTO) {
        // 1. 校验社团是否存在
        Club club = this.getById(reqDTO.getClubId());
        if (club == null) {
            throw new BusinessException("社团不存在");
        }

        // 2. 校验当前用户是否为社团创建者
        if (!club.getUserId().equals(userId)) {
            throw new BusinessException("无权操作，仅社团创建者可解散社团");
        }

        // 3. 校验社团状态是否为"已通过"（只有已成立的社团才能解散）
        if (!"已通过".equals(club.getClubState())) {
            throw new BusinessException("当前社团状态不允许解散");
        }

        String clubId = reqDTO.getClubId();

        // 4. 级联删除关联数据
        // 4.1 删除报名信息（需先查出该社团所有活动ID）
        List<String> activityIds = clubActivityMapper.selectList(
                new QueryWrapper<ClubActivity>().eq("ClubID", clubId)
        ).stream().map(ClubActivity::getActivityId).collect(Collectors.toList());
        if (!activityIds.isEmpty()) {
            registrationInfoMapper.delete(
                    new QueryWrapper<RegistrationInfo>().in("ActivityID", activityIds)
            );
        }

        // 4.2 删除社团评价
        ratingClubMapper.delete(
                new QueryWrapper<RatingClub>().eq("ClubID", clubId)
        );

        // 4.3 删除社团成员
        clubMemberMapper.delete(
                new QueryWrapper<ClubMember>().eq("ClubID", clubId)
        );

        // 4.4 删除社团活动
        clubActivityMapper.delete(
                new QueryWrapper<ClubActivity>().eq("ClubID", clubId)
        );

        // 5. 更新社团状态为"已解散"
        this.update(new UpdateWrapper<Club>()
                .eq("Clubid", clubId)
                .set("ClubState", "已解散"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClub(String userId, ClubUpdateReqDTO reqDTO) {
        // 1. 校验社团是否存在
        Club club = this.getById(reqDTO.getClubId());
        if (club == null) {
            throw new BusinessException("社团不存在");
        }

        // 2. 校验社团状态是否为"已通过"
        if (!"已通过".equals(club.getClubState())) {
            throw new BusinessException("当前社团状态不允许修改信息");
        }

        // 3. 校验社团名称是否与其他活跃社团冲突（排除自身和已解散的）
        Club duplicate = this.lambdaQuery()
                .eq(Club::getClubName, reqDTO.getClubName())
                .ne(Club::getClubState, "已解散")
                .ne(Club::getClubId, reqDTO.getClubId())
                .one();
        if (duplicate != null) {
            throw new BusinessException("该社团名称已存在，请更换名称");
        }

        // 4. 校验当前用户是否为该社团的管理员
        ClubMember member = clubMemberMapper.selectOne(
                new QueryWrapper<ClubMember>()
                        .eq("ClubID", reqDTO.getClubId())
                        .eq("UserID", userId)
                        .eq("ClubManager", "是")
        );
        if (member == null) {
            throw new BusinessException("无权操作，仅社团管理员可修改社团信息");
        }

        // 5. 更新社团信息
        club.setClubName(reqDTO.getClubName());
        club.setClubInformation(reqDTO.getClubInformation());
        club.setSchool(reqDTO.getSchool());
        this.updateById(club);
    }

    @Override
    public List<Club> getActiveClubs(String clubName) {
        var wrapper = new QueryWrapper<Club>().eq("ClubState", "已通过");
        if (clubName != null && !clubName.isEmpty()) {
            wrapper.like("ClubName", clubName);
        }
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditClubCreation(ClubAuditReqDTO reqDTO) {
        // 1. 校验社团是否存在
        Club club = this.getById(reqDTO.getClubId());
        if (club == null) {
            throw new BusinessException("社团不存在");
        }

        // 2. 校验社团状态是否为"待审核"
        if (!"待审核".equals(club.getClubState())) {
            throw new BusinessException("该社团不在待审核状态，无法审核");
        }

        if (Boolean.TRUE.equals(reqDTO.getPass())) {
            // 3. 审核通过：更新社团状态为"已通过"
            club.setClubState("已通过");
            this.updateById(club);

            // 4. 将申请者自动设置为社团管理员
            ClubMember member = new ClubMember();
            member.setClubId(club.getClubId());
            member.setUserId(club.getUserId());
            member.setReviewState("通过");
            member.setClubManager("是");
            clubMemberMapper.insert(member);
        } else {
            // 3. 审核拒绝：更新社团状态为"未通过"
            club.setClubState("未通过");
            this.updateById(club);
        }
    }

    @Override
    public List<Club> getPendingClubs() {
        return this.lambdaQuery()
                .eq(Club::getClubState, "待审核")
                .list();
    }

    @Override
    public List<Club> getMyApplications(String userId) {
        return this.lambdaQuery()
                .eq(Club::getUserId, userId)
                .in(Club::getClubState, "待审核", "未通过")
                .list();
    }

    @Override
    public List<Club> getManagedClubs(String userId) {
        // 1. 查出当前用户管理的社团 ID 列表
        List<String> managedClubIds = clubMemberMapper.selectList(
                new QueryWrapper<ClubMember>()
                        .eq("UserID", userId)
                        .eq("ClubManager", "是")
        ).stream().map(ClubMember::getClubId).collect(Collectors.toList());

        if (managedClubIds.isEmpty()) {
            return List.of();
        }

        // 2. 查询这些社团的详细信息
        return this.lambdaQuery()
                .in(Club::getClubId, managedClubIds)
                .list();
    }
}
