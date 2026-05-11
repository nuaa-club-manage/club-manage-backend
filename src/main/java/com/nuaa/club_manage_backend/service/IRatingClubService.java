package com.nuaa.club_manage_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.club_manage_backend.dto.req.ClubRatingReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubAverageScoreDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRatingRecordDTO;
import com.nuaa.club_manage_backend.entity.RatingClub;

import java.util.List;

public interface IRatingClubService extends IService<RatingClub> {
    /**
     * 用户提交/修改社团评分
     */
    void submitRating(String currentUserID, ClubRatingReqDTO reqDTO);

    /**
     * 用户查看个人评分记录
     */
    List<UserRatingRecordDTO> getMyRatingRecords(String currentUserID);

    /**
     * 用户查看对特定社团的评分详情
     */
    UserRatingRecordDTO getMyRatingForClub(String currentUserID, String clubID);

    /**
     * 用户取消对社团的评分
     */
    void cancelRating(String currentUserID, String clubID);

    /**
     * 获取所有社团的平均分和评价人数
     */
    List<ClubAverageScoreDTO> getAllClubAverageScores();
}
