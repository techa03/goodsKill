package org.seckill.dao;

import static org.junit.Assert.fail;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author heng
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    @Autowired
    @Qualifier(value = "seckillDao")
    private SeckillDao seckillDao;

    /**
     * Test method for
     * {@link org.seckill.dao.SeckillDao#reduceNumber(long, java.util.Date)}.
     */
    @Test
    public void testReduceNumber() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.seckill.dao.SeckillDao#queryById(long)}.
     */
    @Test
    public void testQueryById() {
        long id = 1000L;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getStartTime());
        System.out.print(seckill.getGoodsId());
    }

    /**
     * Test method for {@link org.seckill.dao.SeckillDao#queryAll(int, int)}.
     */
    @Test
    public void testQueryAll() {
        fail("Not yet implemented");
    }

}
