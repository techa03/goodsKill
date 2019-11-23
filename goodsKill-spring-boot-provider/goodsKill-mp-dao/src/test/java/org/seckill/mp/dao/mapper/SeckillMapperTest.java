package org.seckill.mp.dao.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.mp.dao.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class SeckillMapperTest {
    @Autowired
    private SeckillMapper seckillMapper;

    @Test
    public void reduceNumber() {
        int count = seckillMapper.reduceNumber(1001L, new Date());
        assertTrue(count >= 0);
    }

    @Test
    public void reduceNumberOptimized() {
        int count = seckillMapper.reduceNumberOptimized(1001L, new Date(), 1);
        assertTrue(count >= 0);
    }

}
