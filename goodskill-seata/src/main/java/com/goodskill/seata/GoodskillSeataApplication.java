package com.goodskill.seata;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.common.api.dubbo.DictionaryService;
import com.goodskill.entity.Goods;
import com.goodskill.entity.Seckill;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GoodskillSeataApplication {
    @DubboReference
    private SeckillService seckillService;
    @DubboReference
    private GoodsService goodsService;
    @DubboReference
    private DictionaryService dictionaryService;

    /**
     * 测试seata分布式事务
     */
    @GetMapping("/test")
    @GlobalTransactional
    public void testGlobalTransaction() {
        Goods goods = new Goods();
        goods.setName("test");
        goodsService.addGoods(goods);
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
