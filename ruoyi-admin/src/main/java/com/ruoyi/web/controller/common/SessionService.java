package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import io.github.stylesmile.annotation.Service;

/**
 * session 处理
 * @author Stylesmile
 */
@Service
public class SessionService {
    public static ThreadLocal<LoginUser> userSessionThreadLocal = new ThreadLocal<>();

    public static LoginUser getSession() {
        return userSessionThreadLocal.get();
    }

    public static void setSession(LoginUser userSession) {
        userSessionThreadLocal.set(userSession);
    }

}
