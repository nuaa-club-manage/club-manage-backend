package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ClubRatingReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ClubAverageScoreDTO;
import com.nuaa.club_manage_backend.dto.resp.UserRatingRecordDTO;
import com.nuaa.club_manage_backend.service.IRatingClubService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingClubController {

    @Autowired
    private IRatingClubService ratingClubService;

    /**
     * 用户提交/修改社团评分
     */
    @PostMapping("/submit")
    public Result<String> submit(HttpServletRequest request,
                                 @Validated @RequestBody ClubRatingReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        ratingClubService.submitRating(userId, reqDTO);
        return Result.success("评价提交成功");
    }

    /**
     * 用户查看个人评分记录
     */
    @GetMapping("/my/list")
    public Result<List<UserRatingRecordDTO>> getMyRatings(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(ratingClubService.getMyRatingRecords(userId));
    }

    /**
     * 用户查看对特定社团的评分详情
     */
    @GetMapping("/my/detail")
    public Result<UserRatingRecordDTO> getMyRatingForClub(HttpServletRequest request,
                                                          @RequestParam("clubID") String clubID) {
        String userId = (String) request.getAttribute("currentUserId");
        return Result.success(ratingClubService.getMyRatingForClub(userId, clubID));
    }

    /**
     * 用户取消对社团的评分
     */
    @PostMapping("/cancel")
    public Result<String> cancelRating(HttpServletRequest request,
                                       @RequestParam("clubID") String clubID) {
        String userId = (String) request.getAttribute("currentUserId");
        ratingClubService.cancelRating(userId, clubID);
        return Result.success("评分已成功撤回");
    }

    /**
     * 获取所有社团的平均分和评价人数（公开接口，无需登录）
     */
    @GetMapping("/public/average-scores")
    public Result<List<ClubAverageScoreDTO>> getAverageScores() {
        return Result.success(ratingClubService.getAllClubAverageScores());
    }
}
