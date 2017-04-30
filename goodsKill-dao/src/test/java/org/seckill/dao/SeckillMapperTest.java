package org.seckill.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by heng on 2017/4/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@Transactional
public class SeckillMapperTest {
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Test
    public void reduceNumber() throws Exception {
        Assert.assertEquals(seckillMapper.reduceNumber(1000L,new Date()),1);
    }

    @Test
    public void testInsertSelective(){
        SuccessKilled record=new SuccessKilled();
        record.setUserPhone("18516536081");
        record.setSeckillId(1000l);
        Assert.assertEquals(successKilledMapper.insertSelective(record),1);
    }

}