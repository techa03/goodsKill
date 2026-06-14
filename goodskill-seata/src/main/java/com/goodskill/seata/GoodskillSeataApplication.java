package com.goodskill.seata;

import com.goodskill.api.service.GoodsThirdPartyService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.core.feign.SeckillRestClient;
import com.goodskill.core.pojo.vo.SeckillVO;
import com.goodskill.seata.feign.AuthRestClient;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.goodskill.seata", "com.goodskill.core"})
@RestController
public class GoodskillSeataApplication {
    @Resource
    private SeckillRestClient seckillRestClient;
    @DubboReference
    private GoodsThirdPartyService goodsThirdPartyService;
    @Resource
    private AuthRestClient authRestClient;

    /**
     * 测试seata分布式事务
     */
    @GetMapping("/test")
    @GlobalTransactional
    public void testGlobalTransaction() {
        GoodsVO goods = new GoodsVO();
        goods.setName("test");
        goodsThirdPartyService.addGoods(goods);
        SeckillVO seckill = seckillRestClient.findById(1001L);
        seckill.setNumber(seckill.getNumber() - 1);
        seckillRestClient.saveOrUpdateSeckill(seckill);
        authRestClient.addRole(21, 7);
        // 测试异常情况
        throw new RuntimeException();
    }

    public static void main(String[] args) {
        SpringApplication.run(GoodskillSeataApplication.class, args);
    }

}
