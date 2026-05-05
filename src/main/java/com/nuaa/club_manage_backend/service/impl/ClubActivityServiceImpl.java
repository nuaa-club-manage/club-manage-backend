package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.entity.ClubActivity;
import com.nuaa.club_manage_backend.mapper.ClubActivityMapper;
import com.nuaa.club_manage_backend.service.IClubActivityService;
import org.springframework.stereotype.Service;

@Service
public class ClubActivityServiceImpl extends ServiceImpl<ClubActivityMapper, ClubActivity> implements IClubActivityService {
}
