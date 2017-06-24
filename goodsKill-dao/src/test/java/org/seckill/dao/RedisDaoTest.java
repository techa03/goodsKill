package org.seckill.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heng on 2017/6/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:junit/spring/spring-dao.xml"})
@Transactional
public class RedisDaoTest{
    @Autowired
    private RedisDao redisDao;
    @Test
    public void getSeckill() throws Exception {
    }

    @Test
    public void putSeckill() throws Exception {
        Seckill seckill=new Seckill();
        seckill.setName("heng");
        Assert.assertNotNull(redisDao.putSeckill(seckill));
    }

    @Test
    public void put() throws Exception {
    }

    @Test
    public void getData() throws Exception {
    }

    @Test
    public void putData() throws Exception {
//        redisDao.putData(new Goods());
    }

}