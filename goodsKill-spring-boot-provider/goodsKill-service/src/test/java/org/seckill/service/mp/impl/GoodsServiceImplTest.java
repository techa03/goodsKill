package org.seckill.service.mp.impl;

import com.goodskill.es.api.GoodsEsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Goods;
import org.seckill.mp.dao.mapper.GoodsMapper;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GoodsServiceImplTest {
    @InjectMocks
    private GoodsServiceImpl goodsService;
    @Mock
    private GoodsMapper baseMapper;
    @Mock
    private GoodsEsService goodsEsService;

    @Test
    public void uploadGoodsPhoto() {
        goodsService.uploadGoodsPhoto(1L, new byte[100]);
        assertTrue(true);
    }

    @Test
    public void addGoods() {
        Goods goods = new Goods();
        goodsService.addGoods(goods, new byte[100]);
        assertTrue(true);
    }
}