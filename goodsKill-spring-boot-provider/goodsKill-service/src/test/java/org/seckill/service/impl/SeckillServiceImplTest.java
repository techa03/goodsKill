package org.seckill.service.impl;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.topic.SeckillMockSaveTopic;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.common.trade.alipay.AlipayRunner;
import org.seckill.service.mock.strategy.GoodsKillStrategy;
import org.seckill.util.common.util.MD5Util;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.seckill.api.enums.SeckillSolutionEnum.SYCHRONIZED;

@RunWith(MockitoJUnitRunner.class)
public class SeckillServiceImplTest {
    @InjectMocks
    private SeckillServiceImpl seckillService;
    @Mock
    private SeckillService seckillServiceInterface;
    @Mock
    private SeckillMapper baseMapper;
    @Mock
    private RedisService redisService;
    @Mock
    private SuccessKilledMapper successKilledMapper;
    @Mock
    private SuccessKilledMongoService successKilledMongoService;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private AlipayRunner alipayRunner;
    @Mock
    private ValueOperations valueOperations;
    @Mock
    private List<GoodsKillStrategy> goodsKillStrategies;
    @Spy
    private ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1,1,2L, TimeUnit.SECONDS, new ArrayBlockingQueue(1));
    @Mock
    private SeckillMockSaveTopic seckillMockSaveTopic;
    @Mock
    private GoodsService goodsService;

    @Test
    public void getSeckillList() {
        Seckill seckillEntity = new Seckill();
        String goodsName = "test";
        String key = "seckill:list:" + 1 + ":" + 1 + ":" + goodsName;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(Lists.newArrayList());
        when(baseMapper.selectList(any())).thenReturn(Lists.newArrayList(seckillEntity));
        assertEquals(1, seckillService.getSeckillList(1, 1, goodsName).getList().size());
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
        when(successKilledMapper.selectOne(any())).thenReturn(new SuccessKilled());
        when(alipayRunner.tradePrecreate(seckillId)).thenReturn("1");
        assertNotNull(seckillService.executeSeckill(seckillId, userPhone, md5));
    }

    @Test
    public void deleteSuccessKillRecord() {
        seckillService.deleteSuccessKillRecord(1L);
        verify(successKilledMapper, only()).delete(any());
    }

    @Test
    public void getSuccessKillCount() {
        when(successKilledMapper.selectCount(any())).thenReturn(0);
        when(successKilledMongoService.count(1L)).thenReturn(1L);
        assertEquals(seckillService.getSuccessKillCount(1L), 1L);
    }

    @Test
    public void prepareSeckill() {
        long seckillId = 1L;
        Seckill t = new Seckill();
        when(redisService.getSeckill(seckillId)).thenReturn(t);
        when(redisTemplate.delete(seckillId)).thenReturn(true);
        seckillService.prepareSeckill(seckillId, 10);
        verify(successKilledMongoService, times(1)).deleteRecord(seckillId);
    }

    @Test
    public void execute() {
        seckillService.execute(new SeckillMockRequestDto(), SYCHRONIZED.getCode());
    }

    @Test
    public void reduceNumber() {
        SuccessKilled successKilled = new SuccessKilled();
        when(seckillService.reduceNumber(successKilled)).thenThrow(new RuntimeException());
        seckillService.reduceNumber(successKilled);
    }

    @Test
    public void reduceNumberInner() {
        SuccessKilled successKilled = new SuccessKilled();
        successKilled.setSeckillId(100L);
        successKilled.setUserPhone("110");
        when(baseMapper.update(eq(null), any())).thenReturn(1);
        seckillService.reduceNumberInner(successKilled);
        verify(successKilledMapper, times(1)).insert(successKilled);
        verify(seckillMockSaveTopic, times(1)).output();
    }

    @Test
    public void getQrcode() {
        String fileName = "1";
        File file = new File(System.getProperty("user.dir") + "/" + fileName + ".png");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(new byte[10]);
            seckillService.getQrcode(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    @Test
    public void getInfoById() {
        Seckill seckill = new Seckill();
        long seckillId = 1L;
        seckill.setSeckillId(seckillId);
        int goodsId = 2;
        seckill.setGoodsId(goodsId);
        when(seckillServiceInterface.getById(seckillId)).thenReturn(seckill);
        when(goodsService.getById(goodsId)).thenReturn(new Goods());
        seckillService.getInfoById(seckillId);
    }
}