package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.entity.Administrator;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.AdministratorMapper;
import com.nuaa.club_manage_backend.service.IAdministratorService;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl extends ServiceImpl<AdministratorMapper, Administrator> implements IAdministratorService {

    @Override
    public String login(AdminLoginReqDTO reqDTO) {
        // 1. 根据 userId 查询管理员
        Administrator admin = this.getById(reqDTO.getUserId());
        if (admin == null) {
            throw new BusinessException("管理员账号不存在");
        }

        // 2. 明文比对密码
        if (!admin.getUserPassword().equals(reqDTO.getUserPassword())) {
            throw new BusinessException("密码错误");
        }

        // 3. 生成 Token
        return JwtUtils.generateToken(admin.getUserId());
    }
}