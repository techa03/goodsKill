package org.seckill.service.mp.impl;

import com.goodskill.es.api.GoodsEsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Goods;
import org.seckill.mp.dao.mapper.GoodsMapper;

@RunWith(MockitoJUnitRunner.class)
public class GoodsServiceImplTest {
    @InjectMocks
    GoodsServiceImpl goodsService;
    @Mock
    GoodsMapper baseMapper;
    @Mock
    GoodsEsService goodsEsService;

    @Test
    public void uploadGoodsPhoto() {
        goodsService.uploadGoodsPhoto(1L, new byte[100]);
    }

    @Test
    public void addGoods() {
        Goods goods = new Goods();
        goodsService.addGoods(goods, new byte[100]);
    }
}