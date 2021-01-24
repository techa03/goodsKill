package org.seckill.service.transaction;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Goods;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.GoodsMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
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
}
