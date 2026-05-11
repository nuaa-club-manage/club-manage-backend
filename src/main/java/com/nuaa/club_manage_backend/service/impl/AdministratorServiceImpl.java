package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.AdminEditUserReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.AdminUserSearchReqDTO;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.entity.Administrator;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.AdministratorMapper;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.service.IAdministratorService;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdministratorServiceImpl extends ServiceImpl<AdministratorMapper, Administrator> implements IAdministratorService {

    @Autowired
    private OrdinaryUserMapper ordinaryUserMapper;

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

    @Override
    public IPage<UserInfoRespDTO> searchUsersByAdmin(String adminId, AdminUserSearchReqDTO reqDTO) {
        // 1. 校验管理员身份
        Administrator admin = this.getById(adminId);
        if (admin == null) {
            throw new BusinessException("无权访问，仅管理员可执行此操作");
        }

        // 2. 构建动态查询条件
        LambdaQueryWrapper<OrdinaryUser> wrapper = new LambdaQueryWrapper<>();
        if (reqDTO.getSearch() != null && !reqDTO.getSearch().isEmpty()) {
            String keyword = reqDTO.getSearch();
            wrapper.and(w -> w
                    .like(OrdinaryUser::getUserId, keyword)
                    .or()
                    .like(OrdinaryUser::getUserName, keyword)
                    .or()
                    .like(OrdinaryUser::getPhoneNumber, keyword)
                    .or()
                    .like(OrdinaryUser::getUserMailbox, keyword)
            );
        }

        // 3. 分页查询
        Page<OrdinaryUser> page = ordinaryUserMapper.selectPage(new Page<>(reqDTO.getPageNo(), reqDTO.getPageSize()), wrapper);

        // 4. 转换为 UserInfoRespDTO
        Page<UserInfoRespDTO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(page.getRecords().stream().map(user -> {
            UserInfoRespDTO dto = new UserInfoRespDTO();
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getUserName());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setUserMailbox(user.getUserMailbox());
            dto.setRealName(user.getRealName());
            dto.setGender(user.getGender());
            dto.setDegree(user.getDegree());
            dto.setSchool(user.getSchool());
            dto.setRegisterTime(user.getRegisterTime());
            return dto;
        }).collect(Collectors.toList()));

        return resultPage;
    }

    @Override
    public void updateUserBySystemAdmin(String adminID, AdminEditUserReqDTO reqDTO) {
        // 1. 越权校验：确认操作者是系统管理员
        Administrator admin = this.getById(adminID);
        if (admin == null) {
            throw new BusinessException("您不是系统管理员");
        }

        // 2. 查询目标用户
        OrdinaryUser targetUser = ordinaryUserMapper.selectById(reqDTO.getTargetUserID());
        if (targetUser == null) {
            throw new BusinessException("目标用户不存在");
        }

        // 3. 直接覆盖更新（前端会传入完整信息）
        targetUser.setUserName(reqDTO.getUserName());
        targetUser.setPhoneNumber(reqDTO.getPhoneNumber());
        targetUser.setUserMailbox(reqDTO.getUserMailbox());
        targetUser.setRealName(reqDTO.getRealName());
        targetUser.setGender(reqDTO.getGender());
        targetUser.setDegree(reqDTO.getDegree());
        targetUser.setSchool(reqDTO.getSchool());
        targetUser.setUserPassword(reqDTO.getUserPassword());

        ordinaryUserMapper.updateById(targetUser);
    }
}