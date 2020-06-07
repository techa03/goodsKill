package org.seckill.service.test.base;

import com.github.pagehelper.PageInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.service.GoodsKillRpcServiceSimpleApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by heng on 2017/6/28.
 */
@SpringBootTest(classes = GoodsKillRpcServiceSimpleApplication.class)
@RunWith(SpringRunner.class)
@Ignore
public class BaseServiceConfigForTest {
    @Resource
    private SeckillService seckillService;
    @Resource
    private GoodsService goodsService;

    @Test
    public void test(){
        PageInfo seckillList = seckillService.getSeckillList(0, 10, "小");
        seckillList.getList().forEach(System.out::println);
        Assert.assertTrue(seckillList.getPageSize() > 0);
    }
}
