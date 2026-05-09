package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.dto.resp.CaptchaRespDTO;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.service.IOrdinaryUserService;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrdinaryUserServiceImpl extends ServiceImpl<OrdinaryUserMapper, OrdinaryUser> implements IOrdinaryUserService {

    /**
     * 短信验证码缓存（key = userId, value = 验证码）
     */
    private static final Map<String, String> SMS_CODE_CACHE = new ConcurrentHashMap<>();

    /**
     * 图形验证码缓存（key = captchaId, value = 验证码文本）
     */
    private static final Map<String, String> CAPTCHA_CACHE = new ConcurrentHashMap<>();

    @Override
    public CaptchaRespDTO getCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        // 生成 4 位随机数字
        int code = (int) ((Math.random() * 9000) + 1000);
        String captchaText = String.valueOf(code);

        CAPTCHA_CACHE.put(captchaId, captchaText);

        CaptchaRespDTO resp = new CaptchaRespDTO();
        resp.setCaptchaId(captchaId);
        resp.setCaptchaText(captchaText);
        return resp;
    }

    @Override
    public void sendVerifyCode(String phoneNumber) {
        OrdinaryUser user = this.lambdaQuery().eq(OrdinaryUser::getPhoneNumber, phoneNumber).one();
        if (user == null) {
            throw new BusinessException("发送失败，请先注册账号");
        }

        String code = "888888";
        SMS_CODE_CACHE.put(phoneNumber, code);
        System.out.println("===== 短信验证码发送 =====");
        System.out.println("手机号: " + phoneNumber);
        System.out.println("验证码: " + code);
        System.out.println("=========================");
    }

    @Override
    public String login(UserLoginReqDTO reqDTO) {
        // 1. 校验图形验证码
        String cachedCaptcha = CAPTCHA_CACHE.get(reqDTO.getCaptchaId());
        if (cachedCaptcha == null || !cachedCaptcha.equalsIgnoreCase(reqDTO.getCaptchaCode())) {
            throw new BusinessException("人机验证码错误");
        }
        // 图形验证码无论正确与否，只要用了就移除（一次性）
        CAPTCHA_CACHE.remove(reqDTO.getCaptchaId());

        OrdinaryUser user;

        // 2. 根据登录类型校验
        if (reqDTO.getLoginType() == 0) {
            // 密码登录：按 userId 查找
            user = this.getById(reqDTO.getUserId());
            if (user == null) {
                throw new BusinessException("该账号不存在");
            }
            if (reqDTO.getUserPassword() == null || reqDTO.getUserPassword().isEmpty()) {
                throw new BusinessException("密码不能为空");
            }
            if (!user.getUserPassword().equals(reqDTO.getUserPassword())) {
                throw new BusinessException("密码错误");
            }
        } else if (reqDTO.getLoginType() == 1) {
            // 验证码登录：按手机号查找
            if (reqDTO.getPhoneNumber() == null || reqDTO.getPhoneNumber().isEmpty()) {
                throw new BusinessException("手机号不能为空");
            }
            user = this.lambdaQuery().eq(OrdinaryUser::getPhoneNumber, reqDTO.getPhoneNumber()).one();
            if (user == null) {
                throw new BusinessException("该手机号未注册");
            }
            if (reqDTO.getVerifyCode() == null || reqDTO.getVerifyCode().isEmpty()) {
                throw new BusinessException("验证码不能为空");
            }
            String cachedCode = SMS_CODE_CACHE.get(reqDTO.getPhoneNumber());
            if (cachedCode == null || !cachedCode.equals(reqDTO.getVerifyCode())) {
                throw new BusinessException("验证码错误或已过期");
            }
            // 验证码校验通过后移除缓存
            SMS_CODE_CACHE.remove(reqDTO.getPhoneNumber());
        } else {
            throw new BusinessException("不支持的登录类型");
        }

        // 3. 全部校验通过，生成 Token
        return JwtUtils.generateToken(user.getUserId());
    }
}
