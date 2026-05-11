package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.ActivityRegisterReqDTO;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.service.IRegistrationInfoService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationInfoServiceImpl extends ServiceImpl<RegistrationInfoMapper, RegistrationInfo> implements IRegistrationInfoService {

    @Override
    public void registerActivity(String userId, ActivityRegisterReqDTO reqDTO) {
        // 1. 防重校验
        Long count = this.lambdaQuery()
                .eq(RegistrationInfo::getActivityId, reqDTO.getActivityId())
                .eq(RegistrationInfo::getUserId, userId)
                .count();
        if (count > 0) {
            throw new BusinessException("您已报名该活动，请勿重复提交");
        }

        // 2. 组装报名记录
        RegistrationInfo registration = new RegistrationInfo();
        registration.setActivityId(reqDTO.getActivityId());
        registration.setUserId(userId);
        registration.setRealName(reqDTO.getRealName());
        registration.setPhoneNumber(reqDTO.getPhoneNumber());
        registration.setReviewState("审核中");

        // 3. 入库
        this.save(registration);
    }
}
