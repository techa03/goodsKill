package com.goodskill.es.controller;

import com.goodskill.es.feign.MongoFeignClient;
import com.goodskill.mongo.entity.SuccessKilledDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class EsController {
    @Autowired
    private MongoFeignClient mongoFeignClient;

    /**
     * 测试服务降级
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    public long test() {
        SuccessKilledDto entity = new SuccessKilledDto();
        entity.setSeckillId(BigInteger.valueOf(1001));
        return mongoFeignClient.count(entity);
    }
}
