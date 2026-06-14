package com.goodskill.gateway.webclient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AuthWebClient {

    private final WebClient webClient;

    public AuthWebClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://goodskill-auth")
                .build();
    }

    public Mono<List<String>> listUserPermission(String loginId, String loginType) {
        return webClient.get()
                .uri("/user/listUserPermission?loginId={loginId}&loginType={loginType}", loginId, loginType)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }

    public Mono<List<String>> listUserRole(String loginId, String loginType) {
        return webClient.get()
                .uri("/user/listUserRole?loginId={loginId}&loginType={loginType}", loginId, loginType)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }
}
