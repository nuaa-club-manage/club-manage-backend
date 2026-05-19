package com.nuaa.club_manage_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.club_manage_backend.dto.req.UserChangePwdReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserInfoUpdateReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserLoginReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserRegisterReqDTO;
import com.nuaa.club_manage_backend.dto.req.UserResetPwdReqDTO;
import com.nuaa.club_manage_backend.dto.resp.CaptchaRespDTO;
import com.nuaa.club_manage_backend.dto.resp.UserInfoRespDTO;
import com.nuaa.club_manage_backend.entity.Club;
import com.nuaa.club_manage_backend.entity.ClubActivity;
import com.nuaa.club_manage_backend.entity.ClubMember;
import com.nuaa.club_manage_backend.entity.OrdinaryUser;
import com.nuaa.club_manage_backend.entity.RatingClub;
import com.nuaa.club_manage_backend.entity.RegistrationInfo;
import com.nuaa.club_manage_backend.exception.BusinessException;
import com.nuaa.club_manage_backend.mapper.ClubActivityMapper;
import com.nuaa.club_manage_backend.mapper.ClubMapper;
import com.nuaa.club_manage_backend.mapper.ClubMemberMapper;
import com.nuaa.club_manage_backend.mapper.OrdinaryUserMapper;
import com.nuaa.club_manage_backend.mapper.RatingClubMapper;
import com.nuaa.club_manage_backend.mapper.RegistrationInfoMapper;
import com.nuaa.club_manage_backend.mapper.UserMapper;
import com.nuaa.club_manage_backend.service.IOrdinaryUserService;
import com.nuaa.club_manage_backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrdinaryUserServiceImpl extends ServiceImpl<OrdinaryUserMapper, OrdinaryUser> implements IOrdinaryUserService {

    /**
     * 图形验证码缓存（key = captchaId, value = 验证码文本）
     */
    private static final Map<String, String> CAPTCHA_CACHE = new ConcurrentHashMap<>();

    @Autowired
    private ClubMapper clubMapper;
    @Autowired
    private ClubActivityMapper clubActivityMapper;
    @Autowired
    private ClubMemberMapper clubMemberMapper;
    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;
    @Autowired
    private RatingClubMapper ratingClubMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public CaptchaRespDTO getCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        int code = (int) ((Math.random() * 9000) + 1000);
        String captchaText = String.valueOf(code);

        CAPTCHA_CACHE.put(captchaId, captchaText);

        CaptchaRespDTO resp = new CaptchaRespDTO();
        resp.setCaptchaId(captchaId);
        resp.setCaptchaText(captchaText);
        return resp;
    }

    @Override
    public void sendVerifyCode(String contact) {
        boolean isEmail = contact.contains("@");
        boolean isPhone = contact.matches("\\d{11}");

        if (!isEmail && !isPhone) {
            throw new BusinessException("手机或邮箱格式不正确");
        }

        // 生成 6 位随机数字验证码
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        // 存入 Redis，5 分钟过期
        stringRedisTemplate.opsForValue().set("verify_code:" + contact, code, java.time.Duration.ofMinutes(5));

        if (isEmail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(contact);
            message.setSubject("社团管理系统 - 邮箱验证码");
            message.setText("您的验证码为: " + code + "，有效期5分钟。如非本人操作，请忽略此邮件。");
            mailSender.send(message);
        } else {
            System.out.println("===== 短信服务模拟 =====");
            System.out.println("发送至手机: " + contact);
            System.out.println("验证码: " + code);
            System.out.println("========================");
        }
    }

    @Override
    public void resetPassword(UserResetPwdReqDTO reqDTO) {
        // 1. 校验验证码
        String cachedCode = stringRedisTemplate.opsForValue().get("verify_code:" + reqDTO.getContact());
        if (cachedCode == null || !cachedCode.equals(reqDTO.getVerifyCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        stringRedisTemplate.delete("verify_code:" + reqDTO.getContact());

        // 2. 根据联系方式查找用户
        OrdinaryUser user = this.lambdaQuery()
                .eq(OrdinaryUser::getPhoneNumber, reqDTO.getContact())
                .or()
                .eq(OrdinaryUser::getUserMailbox, reqDTO.getContact())
                .one();
        if (user == null) {
            throw new BusinessException("该联系方式未绑定任何账号");
        }

        // 3. 更新密码
        user.setUserPassword(reqDTO.getNewPassword());
        this.updateById(user);
    }

    @Override
    public void register(UserRegisterReqDTO reqDTO) {
        // 1. 校验验证码
        String cachedCode = stringRedisTemplate.opsForValue().get("verify_code:" + reqDTO.getContact());
        if (cachedCode == null || !cachedCode.equals(reqDTO.getVerifyCode())) {
            throw new BusinessException("验证码错误或已过期");
        }
        stringRedisTemplate.delete("verify_code:" + reqDTO.getContact());

        // 2. 校验学号是否已被注册
        OrdinaryUser exist = this.getById(reqDTO.getUserId());
        if (exist != null) {
            throw new BusinessException("该学号已被注册");
        }

        // 3. 组装新用户
        OrdinaryUser newUser = new OrdinaryUser();
        newUser.setUserId(reqDTO.getUserId());
        newUser.setUserPassword(reqDTO.getUserPassword());
        newUser.setUserType("user");
        newUser.setUserName("用户" + UUID.randomUUID().toString().substring(0, 8));
        newUser.setRegisterTime(LocalDate.now());

        // 根据 contact 格式自动存入对应字段
        if (reqDTO.getContact().contains("@")) {
            newUser.setUserMailbox(reqDTO.getContact());
        } else {
            newUser.setPhoneNumber(reqDTO.getContact());
        }

        // 4. 入库
        this.save(newUser);
    }

    @Override
    public String login(UserLoginReqDTO reqDTO) {
        // 1. 校验图形验证码
        String cachedCaptcha = CAPTCHA_CACHE.get(reqDTO.getCaptchaId());
        if (cachedCaptcha == null || !cachedCaptcha.equalsIgnoreCase(reqDTO.getCaptchaCode())) {
            throw new BusinessException("人机验证码错误");
        }
        CAPTCHA_CACHE.remove(reqDTO.getCaptchaId());

        OrdinaryUser user;

        // 2. 根据登录类型校验
        if (reqDTO.getLoginType() == 0) {
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
            if (reqDTO.getContact() == null || reqDTO.getContact().isEmpty()) {
                throw new BusinessException("手机号或邮箱不能为空");
            }
            user = this.lambdaQuery()
                    .eq(OrdinaryUser::getPhoneNumber, reqDTO.getContact())
                    .or()
                    .eq(OrdinaryUser::getUserMailbox, reqDTO.getContact())
                    .one();
            if (user == null) {
                throw new BusinessException("该账号未注册");
            }
            if (reqDTO.getVerifyCode() == null || reqDTO.getVerifyCode().isEmpty()) {
                throw new BusinessException("验证码不能为空");
            }
            String cachedCode = stringRedisTemplate.opsForValue().get("verify_code:" + reqDTO.getContact());
            if (cachedCode == null || !cachedCode.equals(reqDTO.getVerifyCode())) {
                throw new BusinessException("验证码错误或已过期");
            }
            stringRedisTemplate.delete("verify_code:" + reqDTO.getContact());
        } else {
            throw new BusinessException("不支持的登录类型");
        }

        return JwtUtils.generateToken(user.getUserId());
    }

    @Override
    public UserInfoRespDTO getCurrentUserInfo(String userId) {
        OrdinaryUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

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
    }

    @Override
    public UserInfoRespDTO getUserInfoById(String targetUserId) {
        OrdinaryUser user = this.getById(targetUserId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

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
    }

    @Override
    public void updateUserInfo(String userId, UserInfoUpdateReqDTO reqDTO) {
        OrdinaryUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setUserName(reqDTO.getUserName());
        user.setPhoneNumber(reqDTO.getPhoneNumber());
        user.setUserMailbox(reqDTO.getUserMailbox());
        user.setRealName(reqDTO.getRealName());
        user.setGender(reqDTO.getGender());
        user.setDegree(reqDTO.getDegree());
        user.setSchool(reqDTO.getSchool());

        this.updateById(user);
    }

    @Override
    public void changePassword(String userId, UserChangePwdReqDTO reqDTO) {
        OrdinaryUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验原密码
        if (!user.getUserPassword().equals(reqDTO.getOldPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 防呆：新密码不能和原密码相同
        if (reqDTO.getOldPassword().equals(reqDTO.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        user.setUserPassword(reqDTO.getNewPassword());
        this.updateById(user);
    }

    @Override
    public void cancelAccount(String userId) {
        OrdinaryUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在或已注销");
        }

        // 先删除关联表数据
        clubMemberMapper.delete(new QueryWrapper<ClubMember>().eq("UserID", userId));
        registrationInfoMapper.delete(new QueryWrapper<RegistrationInfo>().eq("UserID", userId));
        ratingClubMapper.delete(new QueryWrapper<RatingClub>().eq("UserID", userId));
        clubActivityMapper.update(null, new UpdateWrapper<ClubActivity>().eq("UserID", userId).set("UserID", ""));
        clubMapper.update(null, new UpdateWrapper<Club>().eq("UserID", userId).set("UserID", ""));
        userMapper.deleteById(userId);

        // 最后删除用户本身
        this.removeById(userId);
    }
}
