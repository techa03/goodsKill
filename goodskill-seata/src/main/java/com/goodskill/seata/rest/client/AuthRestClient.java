package com.goodskill.seata.rest.client;

import com.goodskill.core.info.Result;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthRestClient {

    private final RestClient restClient;

    public AuthRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://goodskill-auth")
                .build();
    }

    public Result<String> addRole(int userId, int roleId) {
        return restClient.post()
                .uri("/user/role/add?userId={userId}&roleId={roleId}", userId, roleId)
                .retrieve()
                .body(Result.class);
    }
}