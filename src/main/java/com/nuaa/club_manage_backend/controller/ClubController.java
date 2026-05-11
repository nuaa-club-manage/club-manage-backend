package com.nuaa.club_manage_backend.controller;

import com.nuaa.club_manage_backend.common.Result;
import com.nuaa.club_manage_backend.dto.req.ClubCreateReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubDissolveReqDTO;
import com.nuaa.club_manage_backend.dto.req.ClubUpdateReqDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.service.IClubService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Autowired
    private IClubService clubService;

    /**
     * 提交成立社团申请
     */
    @PostMapping("/create")
    public Result<String> createClub(HttpServletRequest request,
                                     @Validated @RequestBody ClubCreateReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubService.createClub(userId, reqDTO);
        return Result.success("社团成立申请信息提交成功");
    }

    /**
     * 提交解散社团申请
     */
    @PostMapping("/dissolve")
    public Result<String> dissolveClub(HttpServletRequest request,
                                       @Validated @RequestBody ClubDissolveReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubService.dissolveClub(userId, reqDTO);
        return Result.success("社团解散成功");
    }

    /**
     * 修改社团信息
     */
    @PutMapping("/update")
    public Result<String> updateClub(HttpServletRequest request,
                                     @Validated @RequestBody ClubUpdateReqDTO reqDTO) {
        String userId = (String) request.getAttribute("currentUserId");
        clubService.updateClub(userId, reqDTO);
        return Result.success("社团信息修改成功");
    }

    /**
     * 查看所有已成立的社团信息
     */
    @GetMapping("/list")
    public Result<List<Club>> getActiveClubs() {
        return Result.success(clubService.getActiveClubs());
    }
}
