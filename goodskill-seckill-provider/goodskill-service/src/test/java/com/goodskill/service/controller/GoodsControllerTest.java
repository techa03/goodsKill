package com.goodskill.service.controller;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.vo.GoodsVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class GoodsControllerTest {
    @Mock
    GoodsService goodsService;
    @InjectMocks
    GoodsController goodsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddGoodsPage() {
        String result = goodsController.addGoodsPage();
        Assertions.assertEquals("addGoods", result);
    }

    @Test
    void testList() {
        when(goodsService.findMany()).thenReturn(List.of(new GoodsVO()));

        List<GoodsVO> result = goodsController.list();
        Assertions.assertEquals(List.of(new GoodsVO()), result);
    }

    @Test
    void testGetGoodsById() {
        when(goodsService.findById(any())).thenReturn(new GoodsVO());

        GoodsVO result = goodsController.getGoodsById(0L);
        Assertions.assertEquals(new GoodsVO(), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
