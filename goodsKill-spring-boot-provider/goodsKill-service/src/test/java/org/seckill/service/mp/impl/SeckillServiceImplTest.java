package org.seckill.service.mp.impl;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.common.trade.alipay.AlipayRunner;
import org.seckill.util.common.util.MD5Util;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SeckillServiceImplTest {
    @InjectMocks
    private SeckillServiceImpl seckillService;
    @Mock
    private SeckillMapper baseMapper;
    @Mock
    private RedisService redisService;
    @Mock
    private SuccessKilledMapper successKilledMapper;
    @Mock
    private AlipayRunner alipayRunner;
    @Mock
    private SuccessKilledMongoService successKilledMongoService;
    @Mock
    private RedisTemplate redisTemplate;

    @Test
    public void getSeckillList() {
        when(baseMapper.selectList(null)).thenReturn(Lists.newArrayList());
        assertNotNull(seckillService.getSeckillList(1, 1));
    }

    @Test
    public void exportSeckillUrl() {
        Seckill t = new Seckill();
        t.setStartTime(new Date());
        t.setEndTime(new Date());
        when(redisService.getSeckill(1L)).thenReturn(t);
        assertNotNull(seckillService.exportSeckillUrl(1L));
    }

    @Test
    public void executeSeckill() {
        long seckillId = 1L;
        String userPhone = "123213";
        String md5 = MD5Util.getMD5(seckillId);
        when(baseMapper.reduceNumber(eq(seckillId), any())).thenReturn(1);
        SuccessKilled successKilled = new SuccessKilled();
        successKilled.setSeckillId(seckillId);
        successKilled.setUserPhone(userPhone);
        when(successKilledMapper.insert(successKilled)).thenReturn(1);
        when(alipayRunner.trade_precreate(seckillId)).thenReturn("1");
        when(successKilledMapper.selectOne(any())).thenReturn(new SuccessKilled());
        assertNotNull(seckillService.executeSeckill(seckillId, userPhone, md5));
    }

    @Test
    public void deleteSuccessKillRecord() {
        seckillService.deleteSuccessKillRecord(1L);
        verify(successKilledMapper, only()).delete(any());
    }

    @Test
    public void execute() {
//        SeckillMockRequestDto requestDto = new SeckillMockRequestDto(1L, 10, "1324");
//        when().thenReturn(null);
//        doNothing(seckillExecutor.dealSeckill(1L, "1324", SQL_PROCEDURE.getName()));
//        seckillService.execute(requestDto, GoodsKillStrategyEnum.PROCEDURE.getCode());
    }

    @Test
    public void getSuccessKillCount() {
        when(successKilledMapper.selectCount(any())).thenReturn(0);
        when(successKilledMongoService.count(any())).thenReturn(1L);
        assertEquals(seckillService.getSuccessKillCount(1L), 1L);
    }

    @Test
    public void prepareSeckill() {
        long seckillId = 1L;
        ValueOperations valueOperations = mock(ValueOperations.class);
        Seckill t = new Seckill();
        when(redisService.getSeckill(seckillId)).thenReturn(t);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(seckillId)).thenReturn(2L);
        when(valueOperations.decrement(seckillId)).thenReturn(10L,9L,1L);
        seckillService.prepareSeckill(seckillId, 10);
        verify(valueOperations, times(3)).decrement(seckillId);
    }
}