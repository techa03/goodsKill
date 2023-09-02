package com.goodskill.service.impl;

import com.goodskill.api.service.GoodsEsService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.service.impl.dubbo.GoodsServiceImpl;
import com.goodskill.service.mapper.GoodsMapper;
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
        GoodsVO goods = new GoodsVO();
        goodsService.addGoods(goods);
        verify(goodsEsService, only()).save(any());
    }
}
