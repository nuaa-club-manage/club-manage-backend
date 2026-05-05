package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.entity.RatingClub;
import com.nuaa.club_manage_backend.mapper.RatingClubMapper;
import com.nuaa.club_manage_backend.service.IRatingClubService;
import org.springframework.stereotype.Service;

@Service
public class RatingClubServiceImpl extends ServiceImpl<RatingClubMapper, RatingClub> implements IRatingClubService {
}
