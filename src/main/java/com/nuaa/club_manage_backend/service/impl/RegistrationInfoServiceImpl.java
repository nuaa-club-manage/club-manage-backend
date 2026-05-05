package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.service.IRegistrationInfoService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationInfoServiceImpl extends ServiceImpl<RegistrationInfoMapper, RegistrationInfo> implements IRegistrationInfoService {
}
