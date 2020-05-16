package org.seckill.service.mp.impl;

import com.goodskill.es.api.GoodsEsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Goods;
import org.seckill.mp.dao.mapper.GoodsMapper;
import org.seckill.service.impl.GoodsServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

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
        verify(baseMapper, only()).updateById(any());
    }

    @Test
    public void addGoods() {
        Goods goods = new Goods();
        goodsService.addGoods(goods, new byte[100]);
        verify(goodsEsService, only()).save(any());
    }
}