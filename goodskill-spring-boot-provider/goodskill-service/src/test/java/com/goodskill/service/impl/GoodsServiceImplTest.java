package com.goodskill.service.impl;

import com.goodskill.entity.Goods;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.mp.dao.mapper.GoodsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoodsServiceImplTest {
    @InjectMocks
    private GoodsServiceImpl goodsService;
    @Mock
    private GoodsMapper baseMapper;
    @Mock
    private GoodsEsService goodsEsService;

    @Test
    public void uploadGoodsPhoto() {
        goodsService.uploadGoodsPhoto(1L, "http://localhost/goodskill/1.png");
        verify(baseMapper, only()).updateById(any());
    }

    @Test
    public void addGoods() {
        Goods goods = new Goods();
        goodsService.addGoods(goods);
        verify(goodsEsService, only()).save(any());
    }
}