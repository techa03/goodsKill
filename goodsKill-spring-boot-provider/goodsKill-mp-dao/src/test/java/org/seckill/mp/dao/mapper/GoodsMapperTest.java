package org.seckill.mp.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsMapperTest {
    @Autowired
    GoodsMapper goodsMapper;

    @Test
    public void test() {
        long count = goodsMapper.selectCount(new QueryWrapper<>(null));
        System.out.println(count);
    }
}