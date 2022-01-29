package com.goodskill.mp.dao.mapper;

import com.goodskill.mp.dao.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;


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
