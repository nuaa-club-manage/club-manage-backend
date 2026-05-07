package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.service.IOrdinaryUserService;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class OrdinaryUserServiceImpl extends ServiceImpl<OrdinaryUserMapper, OrdinaryUser> implements IOrdinaryUserService {

    // 这里由于是在 impl 内部，你需要去 IOrdinaryUserService 接口里声明一下这个方法
    public String login(UserLoginReqDTO reqDTO) {
        // 1. 去数据库查这个 userID 存不存在 (使用 MyBatis-Plus 的自带方法)
        OrdinaryUser user = this.getById(reqDTO.getUserId());
        
        // 2. 如果查不到，或者密码对不上，抛出我们之前写的全局异常！
        if (user == null) {
            throw new BusinessException("该账号不存在");
        }
        
        // 真实开发中，数据库不能存明文密码，假设你数据库存的是 MD5 加密后的密码：
        // String encryptPwd = DigestUtils.md5DigestAsHex(reqDTO.getUserPassword().getBytes());
        // 但这里为了你能马上在数据库改数据测试，我们先使用明文比对：
        if (!user.getUserPassword().equals(reqDTO.getUserPassword())) {
            throw new BusinessException("密码错误");
        }

        // 3. 校验通过，用工具类生成 Token
        return JwtUtils.generateToken(user.getUserId());
    }
}