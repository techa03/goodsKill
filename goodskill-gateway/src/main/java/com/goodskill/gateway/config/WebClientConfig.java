package com.goodskill.gateway.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder(ObjectProvider<WebClientCustomizer> customizerProvider) {
        WebClient.Builder builder = WebClient.builder();
        customizerProvider.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder;
    }
}
