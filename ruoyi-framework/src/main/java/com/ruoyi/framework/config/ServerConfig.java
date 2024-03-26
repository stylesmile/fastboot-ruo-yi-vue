package com.ruoyi.framework.config;

import io.github.stylesmile.server.Request;
import org.springframework.stereotype.Component;

/**
 * 服务相关配置
 *
 * @author ruoyi
 */
@Component
public class ServerConfig {
    /**
     * 获取完整的请求路径，包括：域名，端口，上下文访问路径
     *
     * @return 服务地址
     */
    public String getUrl(Request request) {
        return getDomain(request);
    }

    public static String getDomain(Request request) {
        StringBuffer url = new StringBuffer();
        url.append(request.getURI().toString());
//        String contextPath = request.getServletContext().getContextPath();
//        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
        return url.delete(url.length() - request.getURI().toString().length(), url.length()).toString();
    }
}
