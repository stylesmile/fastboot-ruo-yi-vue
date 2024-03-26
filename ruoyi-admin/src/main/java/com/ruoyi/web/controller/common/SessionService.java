package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.entity.SysUser;
import io.github.stylesmile.annotation.Service;

/**
 * session 处理
 * @author Stylesmile
 */
@Service
public class SessionService {
    public static ThreadLocal<SysUser> userSessionThreadLocal = new ThreadLocal<>();

    public static SysUser getSession() {
        return userSessionThreadLocal.get();
    }

    public static void setSession(SysUser userSession) {
        userSessionThreadLocal.set(userSession);
    }

}
