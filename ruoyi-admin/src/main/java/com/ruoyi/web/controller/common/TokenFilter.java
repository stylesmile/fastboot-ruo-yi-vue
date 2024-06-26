package com.ruoyi.web.controller.common;

import io.github.stylesmile.annotation.AutoWired;
import io.github.stylesmile.annotation.Service;
import io.github.stylesmile.filter.Filter;
import io.github.stylesmile.jedis.JedisTemplate;
import io.github.stylesmile.server.Request;
import io.github.stylesmile.server.Response;

import java.util.HashMap;

@Service
public class TokenFilter implements Filter {
    private static HashMap<String, String> FILTER_MAP = new HashMap<String, String>() {{
        put("/login", "1");
        put("/register", "1");
        put("/logout", "1");
        put("/refreshToken", "1");
    }};


    @AutoWired
    JedisTemplate jedisTemplate;
    @AutoWired
    SessionService sessionService;

    @Override
    public boolean preHandle(Request request, Response response) {
//        String str = FILTER_MAP.get(request.getURI().toString());
//        if (StringUtil.isNotEmpty(str)) {
//            return true;
//        }
//        String token = request.getHeaders().get("accessToken");
//        Stylesmile userSession = jedisTemplate.getSerializeData(
//                String.format(IMRedisKey.TOKEN_USER_SESSION, token),
//                UserSession.class);
//        if (userSession != null) {
//            SessionService.setSession(userSession);
//            return true;
//        }
//        Result result = ResultUtils.error(ResultCode.NO_LOGIN.getCode(), ResultCode.NO_LOGIN.getMsg());
//        ResultUtil.sendJson(response, 200, result);
//        return ;
        return false;
    }

    @Override
    public boolean afterCompletion(Request request, Response response) {
        return true;
    }
}
