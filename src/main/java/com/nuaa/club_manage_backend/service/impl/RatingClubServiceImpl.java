package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.dto.req.ClubRatingReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubAverageScoreDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRatingRecordDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.RatingClub;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.RatingClubMapper;
import com.nuaa.club_manage_backend.service.IRatingClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RatingClubServiceImpl extends ServiceImpl<RatingClubMapper, RatingClub> implements IRatingClubService {

    @Autowired
    private ClubMapper clubMapper;

    @Override
    public void submitRating(String currentUserID, ClubRatingReqDTO reqDTO) {
        // 1. 查询该用户对该社团是否已有评价
        RatingClub existing = this.lambdaQuery()
                .eq(RatingClub::getClubId, reqDTO.getClubId())
                .eq(RatingClub::getUserId, currentUserID)
                .one();

        if (existing == null) {
            // 2a. 首次评价 — 新增
            RatingClub rating = new RatingClub();
            rating.setClubId(reqDTO.getClubId());
            rating.setUserId(currentUserID);
            rating.setRating(reqDTO.getRating());
            rating.setRatingTime(LocalDateTime.now());
            this.save(rating);
        } else {
            // 2b. 修改评价 — 更新
            existing.setRating(reqDTO.getRating());
            existing.setRatingTime(LocalDateTime.now());
            this.updateById(existing);
        }
    }

    @Override
    public List<UserRatingRecordDTO> getMyRatingRecords(String currentUserID) {
        // 1. 查询当前用户所有评分记录
        List<RatingClub> ratings = this.lambdaQuery()
                .eq(RatingClub::getUserId, currentUserID)
                .list();
        if (ratings.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 批量查询社团名称
        List<String> clubIds = ratings.stream()
                .map(RatingClub::getClubId)
                .collect(Collectors.toList());
        List<Club> clubs = clubMapper.selectList(
                new LambdaQueryWrapper<Club>()
                        .in(Club::getClubId, clubIds)
        );
        Map<String, String> clubNameMap = clubs.stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getClubName));

        // 3. 组装 DTO 并按时间降序排列
        return ratings.stream()
                .map(r -> {
                    UserRatingRecordDTO dto = new UserRatingRecordDTO();
                    dto.setRatingId(r.getRatingId());
                    dto.setClubId(r.getClubId());
                    dto.setClubName(clubNameMap.get(r.getClubId()));
                    dto.setRating(r.getRating());
                    dto.setRatingTime(r.getRatingTime());
                    return dto;
                })
                .sorted((a, b) -> {
                    if (a.getRatingTime() == null) return 1;
                    if (b.getRatingTime() == null) return -1;
                    return b.getRatingTime().compareTo(a.getRatingTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserRatingRecordDTO getMyRatingForClub(String currentUserID, String clubID) {
        // 1. 查询该用户对该社团的评价
        RatingClub rating = this.lambdaQuery()
                .eq(RatingClub::getUserId, currentUserID)
                .eq(RatingClub::getClubId, clubID)
                .one();
        if (rating == null) {
            return null;
        }

        // 2. 查询社团名称
        Club club = clubMapper.selectById(clubID);

        // 3. 组装 DTO
        UserRatingRecordDTO dto = new UserRatingRecordDTO();
        dto.setRatingId(rating.getRatingId());
        dto.setClubId(rating.getClubId());
        dto.setClubName(club != null ? club.getClubName() : null);
        dto.setRating(rating.getRating());
        dto.setRatingTime(rating.getRatingTime());
        return dto;
    }

    @Override
    public void cancelRating(String currentUserID, String clubID) {
        // 1. 查询是否存在评分记录
        RatingClub existing = this.lambdaQuery()
                .eq(RatingClub::getClubId, clubID)
                .eq(RatingClub::getUserId, currentUserID)
                .one();
        if (existing == null) {
            throw new BusinessException("您尚未对该社团进行评分");
        }

        // 2. 物理删除
        this.removeById(existing.getRatingId());
    }

    @Override
    public List<ClubAverageScoreDTO> getAllClubAverageScores() {
        // 预编译正则：提取字符串开头的数字（整数或小数）
        Pattern scorePattern = Pattern.compile("^\\d+(\\.\\d+)?");

        // 1. 查询所有社团
        List<Club> clubs = clubMapper.selectList(null);
        if (clubs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查询所有评分记录
        List<RatingClub> ratings = this.lambdaQuery().list();

        // 3. 按 ClubID 分组
        Map<String, List<RatingClub>> ratingMap = ratings.stream()
                .collect(Collectors.groupingBy(RatingClub::getClubId));

        // 4. 遍历社团计算平均分和评价人数
        return clubs.stream().map(club -> {
            ClubAverageScoreDTO dto = new ClubAverageScoreDTO();
            dto.setClubId(club.getClubId());
            dto.setClubName(club.getClubName());

            List<RatingClub> clubRatings = ratingMap.get(club.getClubId());
            if (clubRatings == null || clubRatings.isEmpty()) {
                dto.setAverageScore(0.0);
                dto.setRatingCount(0);
            } else {
                double sum = 0.0;
                int validCount = 0;
                for (RatingClub r : clubRatings) {
                    String ratingStr = r.getRating();
                    if (ratingStr != null) {
                        Matcher matcher = scorePattern.matcher(ratingStr);
                        if (matcher.find()) {
                            try {
                                sum += Double.parseDouble(matcher.group());
                                validCount++;
                            } catch (NumberFormatException ignored) {
                                // 解析失败则跳过该条记录
                            }
                        }
                    }
                }
                double avg = validCount > 0 ? sum / validCount : 0.0;
                dto.setAverageScore(Math.round(avg * 10.0) / 10.0);
                dto.setRatingCount(validCount);
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
