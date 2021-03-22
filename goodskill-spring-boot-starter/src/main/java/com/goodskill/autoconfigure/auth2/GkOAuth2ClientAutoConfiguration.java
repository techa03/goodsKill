package com.goodskill.autoconfigure.auth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自定义OAuth2授权服务提供方配置
 * 支持gitee码云OAuth2.0授权方式
 *
 * @author techa03
 * @since 2021/3/21
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import(OAuth2LoginConfig.class)
public class GkOAuth2ClientAutoConfiguration {
}
