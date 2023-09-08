package com.goodskill.seckill.impl;

import com.goodskill.seckill.api.service.GoodsEsService;
import com.goodskill.seckill.api.vo.GoodsVO;
import com.goodskill.seckill.mapper.GoodsMapper;
import com.goodskill.seckill.service.impl.dubbo.GoodsServiceImpl;
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
//    @Disabled
    public void uploadGoodsPhoto() {
        goodsService.uploadGoodsPhoto(1L, "http://localhost/goodskill/1.png");
        verify(baseMapper, only()).updateById(any());
    }

    @Test
    public void addGoods() {
        GoodsVO goods = new GoodsVO();
        goodsService.addGoods(goods);
        verify(goodsEsService, only()).save(any());
    }
}
