package com.goodskill.service.impl.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.goodskill.api.service.GoodsThirdPartyService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.core.enums.Events;
import com.goodskill.core.exception.SeckillCloseException;
import com.goodskill.core.pojo.dto.ExposerDTO;
import com.goodskill.core.pojo.dto.SeckillInfoDTO;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.core.pojo.dto.SuccessKilledDTO;
import com.goodskill.core.pojo.vo.SeckillVO;
import com.goodskill.core.rest.client.OrderRestClient;
import com.goodskill.service.common.GoodsService;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.handler.PreRequestPipeline;
import com.goodskill.service.inner.SeckillService;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.util.StateMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeckillServiceImplTest {
    @Mock
    private OrderRestClient orderRestClient;
    @Mock
    private SuccessKilledMapper successKilledMapper;
    @Mock
    private RedisService redisService;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private GoodsThirdPartyService goodsThirdPartyService;
    @Mock
    private ThreadPoolExecutor taskExecutor;
    @Mock
    private StreamBridge streamBridge;
    @Mock
    private SeckillMapper baseMapper;
    @Mock
    private StateMachineService stateMachineService;
    @Mock
    private PreRequestPipeline preRequestPipeline;
    @Mock
    private ValueOperations valueOperations;
    @Mock
    private GoodsService goodsService;
    @Mock
    private SeckillService seckillServiceSelf;

    @InjectMocks
    private SeckillServiceImpl seckillService;

    private Seckill seckill;
    private SeckillVO seckillVO;
    private GoodsVO goodsVO;

    @BeforeEach
    void setUp() throws Exception {
        seckill = new Seckill();
        seckill.setSeckillId(1000L);
        seckill.setGoodsId(1);
        seckill.setName("test seckill");
        seckill.setNumber(100);
        seckill.setStartTime(new Date(System.currentTimeMillis() - 10000));
        seckill.setEndTime(new Date(System.currentTimeMillis() + 10000));

        seckillVO = new SeckillVO();
        seckillVO.setSeckillId(1000L);
        seckillVO.setGoodsId(1);
        seckillVO.setName("test seckill");
        seckillVO.setNumber(100);

        goodsVO = new GoodsVO();
        goodsVO.setGoodsId(1);
        goodsVO.setName("test goods");
        goodsVO.setPhotoUrl("http://test.com/image.jpg");

        Field baseMapperField = CrudRepository.class.getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(seckillService, baseMapper);

        // 设置自引用，使 reduceNumber 能正常工作
        Field seckillServiceField = SeckillServiceImpl.class.getDeclaredField("seckillService");
        seckillServiceField.setAccessible(true);
        seckillServiceField.set(seckillService, seckillService);

        // 设置 goodsService mock
        Field goodsServiceField = SeckillServiceImpl.class.getDeclaredField("goodsService");
        goodsServiceField.setAccessible(true);
        goodsServiceField.set(seckillService, goodsService);
    }

    @Test
    void testExportSeckillUrlSuccess() {
        when(redisService.getSeckill(1000L)).thenReturn(seckill);

        ExposerDTO result = seckillService.exportSeckillUrl(1000L);

        assertTrue(result.isExposed());
        assertNotNull(result.getMd5());
        assertEquals(1000L, result.getSeckillId());
        verify(redisService, times(1)).getSeckill(1000L);
    }

    @Test
    void testExportSeckillUrlBeforeStartTime() {
        seckill.setStartTime(new Date(System.currentTimeMillis() + 100000));
        when(redisService.getSeckill(1000L)).thenReturn(seckill);

        ExposerDTO result = seckillService.exportSeckillUrl(1000L);

        assertFalse(result.isExposed());
        assertEquals(1000L, result.getSeckillId());
        verify(redisService, times(1)).getSeckill(1000L);
    }

    @Test
    void testExportSeckillUrlAfterEndTime() {
        seckill.setEndTime(new Date(System.currentTimeMillis() - 100000));
        when(redisService.getSeckill(1000L)).thenReturn(seckill);

        ExposerDTO result = seckillService.exportSeckillUrl(1000L);

        assertFalse(result.isExposed());
        assertEquals(1000L, result.getSeckillId());
        verify(redisService, times(1)).getSeckill(1000L);
    }

    @Test
    void testDeleteSuccessKillRecord() {
        seckillService.deleteSuccessKillRecord(1000L);

        verify(successKilledMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testGetSuccessKillCountFromDatabase() {
        when(successKilledMapper.selectCount(any(QueryWrapper.class))).thenReturn(10L);

        long count = seckillService.getSuccessKillCount(1000L);

        assertEquals(10L, count);
        verify(successKilledMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(orderRestClient, never()).count(anyLong());
    }

    @Test
    void testGetSuccessKillCountFromMongo() {
        when(successKilledMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(orderRestClient.count(1000L)).thenReturn(20L);

        long count = seckillService.getSuccessKillCount(1000L);

        assertEquals(20L, count);
        verify(successKilledMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(orderRestClient, times(1)).count(1000L);
    }

    @Test
    void testPrepareSeckill() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(100);
        request.setTaskId("task1");

        seckillService.prepareSeckill(1000L, 100, "task1");

        verify(preRequestPipeline, times(1)).handle(any(SeckillWebMockRequestDTO.class));
    }

    @Test
    void testReduceNumberSuccess() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        successKilled.setSeckillId(1000L);
        successKilled.setUserPhone("13800138000");

        when(successKilledMapper.insert(any(SuccessKilled.class))).thenReturn(1);
        when(baseMapper.update(any(), any(UpdateWrapper.class))).thenReturn(1);

        int result = seckillService.reduceNumber(successKilled);

        assertEquals(1, result);
        verify(successKilledMapper, times(1)).insert(any(SuccessKilled.class));
        verify(baseMapper, times(1)).update(any(), any(UpdateWrapper.class));
    }

    @Test
    void testReduceNumberWithException() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        successKilled.setSeckillId(1000L);
        successKilled.setUserPhone("13800138000");

        when(successKilledMapper.insert(any(SuccessKilled.class))).thenThrow(new RuntimeException("test error"));

        int result = seckillService.reduceNumber(successKilled);

        assertEquals(0, result);
        verify(successKilledMapper, times(1)).insert(any(SuccessKilled.class));
    }

    @Test
    void testReduceNumberInnerSuccess() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        successKilled.setSeckillId(1000L);
        successKilled.setUserPhone("13800138000");
        successKilled.setCreateTime(new Date());

        when(successKilledMapper.insert(any(SuccessKilled.class))).thenReturn(1);
        when(baseMapper.update(any(), any(UpdateWrapper.class))).thenReturn(1);

        int result = seckillService.reduceNumberInner(successKilled);

        assertEquals(1, result);
        verify(successKilledMapper, times(1)).insert(any(SuccessKilled.class));
        verify(baseMapper, times(1)).update(any(), any(UpdateWrapper.class));
    }

    @Test
    void testReduceNumberInnerFailure() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        successKilled.setSeckillId(1000L);
        successKilled.setUserPhone("13800138000");
        successKilled.setCreateTime(new Date());

        when(successKilledMapper.insert(any(SuccessKilled.class))).thenReturn(1);
        when(baseMapper.update(any(), any(UpdateWrapper.class))).thenReturn(0);

        assertThrows(SeckillCloseException.class, () -> {
            seckillService.reduceNumberInner(successKilled);
        });

        verify(successKilledMapper, times(1)).insert(any(SuccessKilled.class));
        verify(baseMapper, times(1)).update(any(), any(UpdateWrapper.class));
    }

    @Test
    void testGetInfoById() {
        when(baseMapper.selectById(1000L)).thenReturn(seckill);

        com.goodskill.service.entity.Goods goods = new com.goodskill.service.entity.Goods();
        goods.setGoodsId(1);
        goods.setName("test goods");
        when(goodsService.getById(1)).thenReturn(goods);

        SeckillInfoDTO result = seckillService.getInfoById(1000L);

        assertNotNull(result);
        assertEquals("test seckill", result.getName());
        assertEquals("test goods", result.getGoodsName());
        verify(baseMapper, times(1)).selectById(1000L);
        // getInfoById 会调用两次 getById: 一次在 findById 中，一次在 getInfoById 中
        verify(goodsService, times(2)).getById(1);
    }

    @Test
    void testEndSeckill() {
        when(stateMachineService.feedMachine(Events.ACTIVITY_END, 1000L)).thenReturn(true);

        boolean result = seckillService.endSeckill(1000L);

        assertTrue(result);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_END, 1000L);
    }

    @Test
    void testRemoveBySeckillId() {
        when(baseMapper.deleteById(1000L)).thenReturn(1);

        boolean result = seckillService.removeBySeckillId(1000L);

        assertTrue(result);
        verify(baseMapper, times(1)).deleteById(1000L);
    }

    @Test
    void testSave() {
        when(baseMapper.insert(any(Seckill.class))).thenReturn(1);

        boolean result = seckillService.save(seckillVO);

        assertTrue(result);
        verify(baseMapper, times(1)).insert(any(Seckill.class));
    }

    @Test
    void testFindById() {
        when(baseMapper.selectById(1000L)).thenReturn(seckill);

        com.goodskill.service.entity.Goods goods = new com.goodskill.service.entity.Goods();
        goods.setGoodsId(1);
        goods.setPhotoUrl("http://test.com/image.jpg");
        when(goodsService.getById(1)).thenReturn(goods);

        SeckillVO result = seckillService.findById(1000L);

        assertNotNull(result);
        assertEquals(1000L, result.getSeckillId());
        assertEquals("http://test.com/image.jpg", result.getPhotoUrl());
        verify(baseMapper, times(1)).selectById(1000L);
        verify(goodsService, times(1)).getById(1);
    }
}
