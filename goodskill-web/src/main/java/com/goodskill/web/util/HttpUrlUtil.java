package com.goodskill.web.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * http url工具类
 *
 * @author heng
 * @since 21/6/5
 */
@Component
public class HttpUrlUtil {

    /**
     * 网关http地址
     */
    private static String host;

    /**
     * 项目contextPath
     */
    private static String contextPath;

    /**
     * 替换mvc的redirect字符串，带上网关地址
     *
     * @param redirectUrl 原始url
     * @return 处理后的url
     */
    public static String replaceRedirectUrl(String redirectUrl) {
        if (!redirectUrl.contains(":")) {
            return host + redirectUrl;
        }
        return "redirect:" + host + contextPath + redirectUrl.split(":")[1];
    }

    @Value("${goodskill.gateway.host}")
    public synchronized void setHost(String gatewayHost) {
        host = gatewayHost;
    }

    @Value("${server.servlet.context-path}")
    public synchronized void setContextPath(String serveltContextPath) {
        contextPath = serveltContextPath;
    }

    private HttpUrlUtil() {
    }
}
