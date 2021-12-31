package com.goodskill.service.transaction;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.entity.Goods;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mp.dao.mapper.GoodsMapper;
import com.goodskill.mp.dao.mapper.SuccessKilledMapper;
import com.goodskill.service.common.SuccessKilledService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 多数据源事务测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore
public class MultiDatasourceTransactionTest {
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private SuccessKilledService successKilledService;
    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    @Transactional
    public void testInsert() {
        Goods entity = new Goods();
        entity.setName("test");
        goodsMapper.insert(entity);

        SuccessKilled su = new SuccessKilled();
        su.setUserPhone("3434");
        su.setSeckillId(1432L);
        successKilledMapper.insert(su);

        su = new SuccessKilled();
        su.setUserPhone("3435");
        su.setSeckillId(1432L);
        successKilledMapper.insert(su);

        su = new SuccessKilled();
        su.setUserPhone("3435");
        su.setSeckillId(1431L);
        successKilledMapper.insert(su);

        su = new SuccessKilled();
        su.setUserPhone("3434");
        su.setSeckillId(1431L);
        successKilledMapper.insert(su);
    }

    @Test
    public void testPage() {
        IPage page = successKilledService.page(new Page<>(1,100));
        Assert.assertTrue(page.getSize() > 0);
    }

    @Test
    public void testPage1() {
        IPage page = goodsMapper.selectPage(new Page(1,2), null);
        Assert.assertTrue(page.getSize() > 0);
    }


}
