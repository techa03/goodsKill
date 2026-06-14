package com.goodskill.core.feign;

import com.goodskill.core.pojo.dto.GoodsDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SeckillGoodsRestClient {

    private final RestClient restClient;

    public SeckillGoodsRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://goodskill-seckill")
                .build();
    }

    public void save(GoodsDTO goodsDto) {
        restClient.put()
                .uri("/save")
                .body(goodsDto)
                .retrieve()
                .toBodilessEntity();
    }

    public void saveBatch(List<GoodsDTO> list) {
        restClient.put()
                .uri("/saveBatch")
                .body(list)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(GoodsDTO goodsDto) {
        restClient.post()
                .uri("/delete")
                .body(goodsDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<GoodsDTO> searchWithNameByPage(String input) {
        return restClient.get()
                .uri("/searchWithNameByPage?input={input}", input)
                .retrieve()
                .body(List.class);
    }
}