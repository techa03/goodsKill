package com.goodskill.service.controller;

import com.goodskill.core.pojo.dto.GoodsDTO;
import com.goodskill.service.inner.SeckillGoodsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GoodsEsController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class GoodsEsControllerTest {

    @InjectMocks
    private GoodsEsController goodsEsController;

    @Mock
    private SeckillGoodsService seckillGoodsService;

    @Test
    void shouldSaveGoods() {
        // Given
        GoodsDTO goodsDto = new GoodsDTO();
        goodsDto.setGoodsId(1);
        goodsDto.setName("Test Product");

        // When
        goodsEsController.save(goodsDto);

        // Then
        verify(seckillGoodsService, times(1)).save(goodsDto);
    }

    @Test
    void shouldSaveBatchGoods() {
        // Given
        List<GoodsDTO> goodsList = new ArrayList<>();
        GoodsDTO goods1 = new GoodsDTO();
        goods1.setGoodsId(1);
        goods1.setName("Product 1");
        GoodsDTO goods2 = new GoodsDTO();
        goods2.setGoodsId(2);
        goods2.setName("Product 2");
        goodsList.add(goods1);
        goodsList.add(goods2);

        // When
        goodsEsController.saveBatch(goodsList);

        // Then
        verify(seckillGoodsService, times(1)).saveBatch(goodsList);
    }

    @Test
    void shouldSaveBatchEmptyList() {
        // Given
        List<GoodsDTO> emptyList = new ArrayList<>();

        // When
        goodsEsController.saveBatch(emptyList);

        // Then
        verify(seckillGoodsService, times(1)).saveBatch(emptyList);
    }

    @Test
    void shouldDeleteGoods() {
        // Given
        GoodsDTO goodsDto = new GoodsDTO();
        goodsDto.setGoodsId(1);

        // When
        goodsEsController.delete(goodsDto);

        // Then
        verify(seckillGoodsService, times(1)).delete(goodsDto);
    }

    @Test
    void shouldSearchWithNameByPage() {
        // Given
        String input = "iPhone";
        List<GoodsDTO> expectedResult = new ArrayList<>();
        GoodsDTO goods = new GoodsDTO();
        goods.setName("iPhone 15");
        expectedResult.add(goods);

        when(seckillGoodsService.searchWithNameByPage(input)).thenReturn(expectedResult);

        // When
        List<GoodsDTO> result = goodsEsController.searchWithNameByPage(input);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("iPhone 15", result.get(0).getName());
        verify(seckillGoodsService, times(1)).searchWithNameByPage(input);
    }

    @Test
    void shouldSearchReturnEmptyList() {
        // Given
        String input = "NonExistentProduct";
        when(seckillGoodsService.searchWithNameByPage(input)).thenReturn(new ArrayList<>());

        // When
        List<GoodsDTO> result = goodsEsController.searchWithNameByPage(input);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
