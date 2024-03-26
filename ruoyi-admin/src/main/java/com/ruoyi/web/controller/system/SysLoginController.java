package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.web.controller.common.SessionService;
import io.github.stylesmile.annotation.AutoWired;
import io.github.stylesmile.annotation.RequestBody;
import io.github.stylesmile.annotation.RequestMapping;
import io.github.stylesmile.request.RequestMethod;
import io.github.stylesmile.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@Controller
public class SysLoginController {
    @Autowired
//    private SysLoginService loginService;
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;
    @AutoWired
    private SessionService sessionService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
//    @PostMapping("/login")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AjaxResult login(@RequestBody LoginBody loginBody, Request request) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(),request);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
//    @GetMapping("getInfo")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public AjaxResult getInfo() {
//        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysUser user = SessionService.getSession().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
//    @GetMapping("getRouters")
    @RequestMapping("getRouters")
    public AjaxResult getRouters() {
//        Long userId = SecurityUtils.getUserId();
        Long userId = SessionService.getSession().getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
