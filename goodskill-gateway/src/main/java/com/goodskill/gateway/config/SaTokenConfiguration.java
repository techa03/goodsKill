package com.goodskill.gateway.config;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.goodskill.gateway.properties.IgnoreProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

import static com.goodskill.core.enums.CommonConstants.USER_ID_HEADER;

/**
 * [Sa-Token 权限认证] 全局配置类
 */
@Configuration
@Slf4j
public class SaTokenConfiguration {
    @Resource
    private IgnoreProperties ignoreProperties;

    /**
     * 注册 [Sa-Token全局过滤器]，优先级高于GlobalFilter，即在GlobalFilter之前执行
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 指定 [拦截路由]
                .addInclude("/**")    /* 拦截所有path */
                // 指定 [放行路由]
                .addExclude("/favicon.ico", "/actuator/**")
                // 指定[认证函数]: 每次请求执行
                .setAuth(obj -> {
                    // 打印路径
                    ServerHttpRequest request = SaReactorSyncHolder.getContext().getRequest();
                    RequestPath path = request.getPath();
                    log.info("---------- sa全局认证, 请求url:{}", path);
                    SaRouter.notMatch(ignoreProperties.getWhiteUrl())
                            .check(r -> {
                                StpUtil.checkLogin();
                                request.mutate().header(USER_ID_HEADER, StpUtil.getLoginId() + "");
                                List<String> userWhiteUrlList = ignoreProperties.getLoginUserWhiteUrls();
                                // 登录用户白名单接口放行
                                if (!ifMatchUserLoginWhiteUrl(userWhiteUrlList, path)) {
//                                    StpUtil.checkPermission(path.value());
                                }
                            });
                })
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> {
                    log.error("---------- sa全局异常", e);
                    return SaResult.error(e.getMessage());
                });
    }


    /**
     * 判断当前url是否命中白名单
     *
     * @param userWhite
     * @param path
     * @return
     */
    private static boolean ifMatchUserLoginWhiteUrl(List<String> userWhite, RequestPath path) {
        boolean found = false;
        for (String pattern : userWhite) {
            boolean match = SaPathMatcherHolder.getPathMatcher().match(pattern, path.value());
            if (match) {
                found = true;
                break;
            }
        }
        return found;
    }
}
