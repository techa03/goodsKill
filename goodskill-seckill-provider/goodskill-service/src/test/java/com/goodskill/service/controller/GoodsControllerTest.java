package com.goodskill.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.core.rest.client.OrderRestClient;
import com.goodskill.core.info.Result;
import com.goodskill.service.common.GoodsService;
import com.goodskill.service.entity.Goods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsControllerTest {
    @Mock
    private OrderRestClient orderRestClient;

    @Mock
    private GoodsService goodsService;

    @InjectMocks
    private GoodsController goodsController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCountOrders() {
        Long seckillId = 1000L;
        Long expectedCount = 10L;

        when(orderRestClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderRestClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithZero() {
        Long seckillId = 1001L;
        Long expectedCount = 0L;

        when(orderRestClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderRestClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithLargeNumber() {
        Long seckillId = 1002L;
        Long expectedCount = 999999L;

        when(orderRestClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderRestClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithException() {
        Long seckillId = 1003L;

        when(orderRestClient.count(seckillId)).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> {
            goodsController.countOrders(seckillId);
        });

        verify(orderRestClient, times(1)).count(seckillId);
    }

    @Test
    void testListGoodsWithDefaultParams() {
        // Given
        Page<Goods> pageResult = new Page<>(1, 10);
        when(goodsService.page(any(Page.class), any())).thenReturn(pageResult);

        // When
        Result<IPage<Goods>> result = goodsController.list(1, 10, null);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        verify(goodsService, times(1)).page(any(Page.class), any());
    }

    @Test
    void testListGoodsWithKeyword() {
        // Given
        Page<Goods> pageResult = new Page<>(1, 10);
        when(goodsService.page(any(Page.class), any())).thenReturn(pageResult);

        // When
        Result<IPage<Goods>> result = goodsController.list(1, 10, "iPhone");

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        verify(goodsService, times(1)).page(any(Page.class), any());
    }

    @Test
    void testGetGoodsByIdSuccess() {
        // Given
        Goods goods = new Goods();
        goods.setGoodsId(1);
        when(goodsService.getById(1)).thenReturn(goods);

        // When
        Result<Goods> result = goodsController.getGoodsById(1);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        verify(goodsService, times(1)).getById(1);
    }

    @Test
    void testGetGoodsByIdNotFound() {
        // Given
        when(goodsService.getById(999)).thenReturn(null);

        // When
        Result<Goods> result = goodsController.getGoodsById(999);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("商品不存在", result.getMsg());
    }

    @Test
    void testCreateGoodsSuccess() {
        // Given
        Goods goods = new Goods();
        goods.setName("New Product");
        when(goodsService.save(goods)).thenReturn(true);

        // When
        Result<String> result = goodsController.createGoods(goods);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals("创建成功", result.getData());
    }

    @Test
    void testCreateGoodsFailure() {
        // Given
        Goods goods = new Goods();
        when(goodsService.save(goods)).thenReturn(false);

        // When
        Result<String> result = goodsController.createGoods(goods);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("创建失败", result.getMsg());
    }

    @Test
    void testUpdateGoodsSuccess() {
        // Given
        Goods goods = new Goods();
        goods.setGoodsId(1);
        when(goodsService.updateById(goods)).thenReturn(true);

        // When
        Result<String> result = goodsController.updateGoods(goods);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals("更新成功", result.getData());
    }

    @Test
    void testUpdateGoodsFailure() {
        // Given
        Goods goods = new Goods();
        when(goodsService.updateById(goods)).thenReturn(false);

        // When
        Result<String> result = goodsController.updateGoods(goods);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("更新失败", result.getMsg());
    }

    @Test
    void testDeleteGoodsSuccess() {
        // Given
        when(goodsService.removeById(1)).thenReturn(true);

        // When
        Result<String> result = goodsController.deleteGoods(1);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals("删除成功", result.getData());
    }

    @Test
    void testDeleteGoodsFailure() {
        // Given
        when(goodsService.removeById(999)).thenReturn(false);

        // When
        Result<String> result = goodsController.deleteGoods(999);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("删除失败", result.getMsg());
    }

    @Test
    void testBatchDeleteGoodsSuccess() {
        // Given
        List<Integer> ids = List.of(1, 2, 3);
        when(goodsService.removeByIds(ids)).thenReturn(true);

        // When
        Result<String> result = goodsController.batchDeleteGoods(ids);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals("批量删除成功", result.getData());
    }

    @Test
    void testBatchDeleteGoodsFailure() {
        // Given
        List<Integer> ids = List.of(1, 2);
        when(goodsService.removeByIds(ids)).thenReturn(false);

        // When
        Result<String> result = goodsController.batchDeleteGoods(ids);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("批量删除失败", result.getMsg());
    }
}
