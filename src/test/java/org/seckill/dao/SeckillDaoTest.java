package org.seckill.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author heng
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@Transactional
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
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        try {
          date= formatter.parse("2017-01-26 11:01:47");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(seckillDao.reduceNumber(1000,date),1);
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
        List<Seckill> seckill = seckillDao.queryAll(0,4);
        Assert.assertTrue(seckill.size()>=4);
    }


    @Test
    public void testInsert(){
        Seckill t=new Seckill();
        t.setName("1");
        t.setNumber(100);
        t.setGoodsId(6);
        Assert.assertEquals(seckillDao.insert(t),1);
    }


}
