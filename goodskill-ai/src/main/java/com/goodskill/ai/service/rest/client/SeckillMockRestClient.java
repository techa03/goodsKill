package com.goodskill.ai.service.rest.client;

import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class SeckillMockRestClient {

    private final RestClient restClient;

    public SeckillMockRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://goodskill-web")
                .build();
    }

    public Result doWithSychronized(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/sychronized")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result doWithRedissionLock(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/redisson")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result doWithKafkaMqMessage(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/kafka")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result<Long> doWithProcedure(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/procedure")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result doWithZookeeperLock(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/zookeeperLock")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result redisReactiveMongo(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/redisReactiveMongo")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result doWithRabbitmq(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/rabbitmq")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result limit(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/limit")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result atomicWithCanal(SeckillWebMockRequestDTO dto) {
        return restClient.post()
                .uri("/atomicWithCanal")
                .body(dto)
                .retrieve()
                .body(Result.class);
    }

    public Result<Long> getTaskId(Long seckillId) {
        return restClient.post()
                .uri("/task-info?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Result.class);
    }

    public Result<Map<String, Object>> getTaskDetails(String taskId) {
        return restClient.get()
                .uri("/task-info/{taskId}", taskId)
                .retrieve()
                .body(Result.class);
    }
}