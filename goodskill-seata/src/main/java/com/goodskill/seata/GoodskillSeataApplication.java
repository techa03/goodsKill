package com.goodskill.seata;

import com.alibaba.cloud.seata.feign.SeataFeignClientAutoConfiguration;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = SeataFeignClientAutoConfiguration.class)
@RestController
public class GoodskillSeataApplication {
    @DubboReference
    private SeckillService seckillService;
    @DubboReference
    private GoodsService goodsService;

    /**
     * 测试seata分布式事务
     */
    @GetMapping("/test")
    @GlobalTransactional
    public void testGlobalTransaction() {
        Goods goods = new Goods();
        goods.setName("test");
        goodsService.addGoods(goods, null);
        Seckill seckill = seckillService.getById(1001L);
        seckill.setNumber(seckill.getNumber() - 1);
        seckillService.saveOrUpdate(seckill);
        // 测试异常情况
        throw new RuntimeException();
    }

    public static void main(String[] args) {
        SpringApplication.run(GoodskillSeataApplication.class, args);
    }

}
