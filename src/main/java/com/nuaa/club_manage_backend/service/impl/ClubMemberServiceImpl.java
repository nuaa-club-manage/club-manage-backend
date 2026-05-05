package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.service.IClubMemberService;
import org.springframework.stereotype.Service;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements IClubMemberService {
}
