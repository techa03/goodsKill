package com.goodskill.mp.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goodskill.mp.dao.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GoodsMapperTest {
    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void test() {
        long count = goodsMapper.selectCount(new QueryWrapper<>(null));
        assertTrue(count > 0);
    }
}