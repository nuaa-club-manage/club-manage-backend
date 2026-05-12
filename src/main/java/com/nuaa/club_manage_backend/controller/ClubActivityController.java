package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ActivityCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ActivityUpdateReqDTO;
import com.nuaa.club_manage_backend.dto.resp.ActivityListRespDTO;
import com.nuaa.club_manage_backend.service.IClubActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ClubActivityController {

    @Autowired
    private IClubActivityService clubActivityService;

    /**
     * 发布活动
     */
    @PostMapping
    public Result<String> createActivity(HttpServletRequest request,
                                         @Validated @RequestBody ActivityCreateReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubActivityService.createActivity(userId, reqDTO);
        return Result.success("活动发布成功，请等待管理员审核");
    }

    /**
     * 修改活动信息
     */
    @PutMapping
    public Result<String> updateActivity(HttpServletRequest request,
                                         @Validated @RequestBody ActivityUpdateReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubActivityService.updateActivity(userId, reqDTO);
        return Result.success("活动信息修改成功");
    }

    /**
     * 结束活动
     */
    @PutMapping("/end")
    public Result<String> endActivity(HttpServletRequest request,
                                      @RequestParam String activityId) {
        String userId = (String) request.getAttribute("currentUserId");
        clubActivityService.endActivity(userId, activityId);
        return Result.success("活动已结束");
    }

    /**
     * 删除活动
     */
    @DeleteMapping
    public Result<String> deleteActivity(HttpServletRequest request,
                                         @RequestParam String activityId) {
        String userId = (String) request.getAttribute("currentUserId");
        clubActivityService.deleteActivity(userId, activityId);
        return Result.success("活动已删除");
    }

    /**
     * 查看所有已发布的活动（支持按标题模糊搜索）
     */
    @GetMapping
    public Result<List<ActivityListRespDTO>> getPublishedActivities(
            @RequestParam(required = false) String title) {
        return Result.success(clubActivityService.getPublishedActivities(title));
    }
}
