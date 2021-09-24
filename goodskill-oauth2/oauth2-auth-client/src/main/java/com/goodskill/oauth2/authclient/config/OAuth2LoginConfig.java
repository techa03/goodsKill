package com.goodskill.oauth2.authclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义OAuth2授权服务提供方配置
 *
 * @author techa03
 * @since 2021/3/21
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class OAuth2LoginConfig {

    @Value("${spring.security.oauth2.client.registration.goodskill.client-id}")
    private String goodskillClientId;

    @Value("${spring.security.oauth2.client.registration.goodskill.client-secret}")
    private String goodskillClientSecret;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        properties.getRegistration().remove("gitee");
        properties.getRegistration().remove("goodskill");
        List<ClientRegistration> registrations = new ArrayList<>(
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values());
        registrations.add(goodskillClientRegistration());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration goodskillClientRegistration() {
        return ClientRegistration.withRegistrationId("goodskill")
                .clientId(goodskillClientId)
                .clientSecret(goodskillClientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://www.goodskill.com:19021/login/oauth2/code/{registrationId}")
                .scope("resource:read")
                .authorizationUri("http://auth-server:8083/oauth/authorize")
                .tokenUri("http://auth-server:8083/oauth/token")
                .userInfoUri("http://resource-server:8081/api/v5/user")
                .userNameAttributeName("id")
                .jwkSetUri("http://auth-server:8083/.well-known/jwks.json")
                .clientName("Goodskill")
                .build();
    }

    public void setGoodskillClientId(String goodskillClientId) {
        this.goodskillClientId = goodskillClientId;
    }

    public void setGoodskillClientSecret(String goodskillClientSecret) {
        this.goodskillClientSecret = goodskillClientSecret;
    }
}
