package com.goodskill.es.controller;

import com.goodskill.es.feign.MongoFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return mongoFeignClient.count(1001);
    }
}
