package com.goodskill.core.rest.client;

import com.goodskill.core.pojo.dto.OrderDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OrderRestClient {

    private final RestClient restClient;

    public OrderRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://goodskill-order")
                .build();
    }

    public Boolean deleteRecord(Long seckillId) {
        return restClient.delete()
                .uri("/deleteRecord?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Boolean.class);
    }

    public String saveRecord(OrderDTO orderDTO) {
        return restClient.post()
                .uri("/saveRecord")
                .body(orderDTO)
                .retrieve()
                .body(String.class);
    }

    public long count(Long seckillId) {
        return restClient.get()
                .uri("/count?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Long.class);
    }
}