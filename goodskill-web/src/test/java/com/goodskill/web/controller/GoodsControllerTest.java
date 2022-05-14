package com.goodskill.web.controller;

import com.goodskill.api.service.GoodsService;
import com.goodskill.entity.Goods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class GoodsControllerTest {
    @Mock
    GoodsService goodsService;
    @InjectMocks
    GoodsController goodsController;
    @TempDir
    private File file;

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
        when(goodsService.list()).thenReturn(List.of(new Goods()));

        List<Goods> result = goodsController.list();
        Assertions.assertEquals(List.of(new Goods()), result);
    }

    @Test
    void testGetGoodsById() {
        when(goodsService.getById(any())).thenReturn(new Goods());

        Goods result = goodsController.getGoodsById(0L);
        Assertions.assertEquals(new Goods(), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme