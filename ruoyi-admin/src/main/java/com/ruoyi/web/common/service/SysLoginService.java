package com.ruoyi.web.common.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.*;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MD5Util;
//import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
//import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.web.common.factory.AsyncFactory;
import com.ruoyi.web.mapper.SysUserMapper;
import com.ruoyi.web.service.ISysConfigService;
import com.ruoyi.web.service.ISysUserService;
import io.github.stylesmile.annotation.AutoWired;
import io.github.stylesmile.jedis.JedisTemplate;
import io.github.stylesmile.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService {
    @AutoWired
    SysUserMapper sysUserMapper;
    @Autowired
    private TokenService tokenService;

//    @Resource
//    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;
    @AutoWired
    private JedisTemplate jedisTemplate;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @param request
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid, Request request) {
        // 验证码校验
//        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password, request);
        // 用户验证
//        Authentication authentication = null;
//        try {
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
//            AuthenticationContextHolder.setContext(authenticationToken);
//            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
//            authentication = authenticationManager.authenticate(authenticationToken);
//        } catch (Exception e) {
//            if (e instanceof BadCredentialsException) {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
//                throw new UserPasswordNotMatchException();
//            } else {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
//                throw new ServiceException(e.getMessage());
//            }
//        } finally {
//            AuthenticationContextHolder.clearContext();
//        }
//        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
//        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
//        recordLoginInfo(loginUser.getUserId());
        // 生成token
//        return tokenService.createToken(loginUser);
        SysUser user = this.findUserByUserName(username);
        if (null == user) {
//            throw new GlobalException(ResultCode.PROGRAM_ERROR, "用户不存在");
//            ResultUtils.error(ResultCode.PROGRAM_ERROR, "用户不存在");
        }
        if (!MD5Util.calculateMD5(password).equals(user.getPassword())) {
//            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
//            ResultUtils.error(ResultCode.PASSWOR_ERROR, "用户不存在");
        }

        String strJson = JSON.toJSONString(user);
//        String accessToken = MD5Util.calculateMD5(strJson + System.currentTimeMillis() + IPUtil.getClientIP(request));
        String accessToken = MD5Util.calculateMD5(strJson + System.currentTimeMillis() + IpUtils.getIpAddr(request));
        LoginUser loginUser = LoginUser.builder()
                .loginTime(System.currentTimeMillis())
                .userId(user.getUserId())
                .token(accessToken)
                .build();
        jedisTemplate.setSerializeDataEx(
                String.format(Constants.LOGIN_USER_KEY + ":" + user.getUserId(), accessToken),
                user, 7200);
        return accessToken;

    }

    public SysUser findUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getUserName, username);
//        return this.getOne(queryWrapper);
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey, String.class);
            redisCache.deleteObject(verifyKey);
            if (captcha == null) {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            if (!code.equalsIgnoreCase(captcha)) {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     * @param request
     */
    public void loginPreCheck(String username, String password, Request request) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null"), request));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"), request));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"), request));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr(request))) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked"), request));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
//        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
