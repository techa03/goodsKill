package com.goodskill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.dto.SuccessKilledDTO;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.common.core.enums.Events;
import com.goodskill.order.api.SuccessKilledMongoService;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.impl.dubbo.SeckillServiceImpl;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.util.StateMachineUtil;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static com.goodskill.common.core.enums.SeckillSolutionEnum.SYCHRONIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private ValueOperations valueOperations;
    @Mock
    private List<GoodsKillStrategy> goodskillStrategies;
    @Spy
    private ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1,1,2L, TimeUnit.SECONDS, new ArrayBlockingQueue(1));
    @Mock
    private GoodsService goodsService;
    @Mock
    private StateMachineUtil stateMachineUtil;

    @Test
    public void getSeckillList() {
        Seckill seckillEntity = new Seckill();
        String goodsName = "test";
        String key = "seckill:list:" + 1 + ":" + 1 + ":" + goodsName;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        Page<Seckill> page = new Page<>();
        page.setRecords(Lists.newArrayList(seckillEntity));
        when(baseMapper.selectPage(any(), any())).thenReturn(page);
        assertEquals(1, seckillService.getSeckillList(1, 1, goodsName).getRecords().size());
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
    public void deleteSuccessKillRecord() {
        seckillService.deleteSuccessKillRecord(1L);
        verify(successKilledMapper, only()).delete(any());
    }

    @Test
    public void getSuccessKillCount() {
        when(successKilledMapper.selectCount(any())).thenReturn(0L);
        when(successKilledMongoService.count(1L)).thenReturn(1L);
        assertEquals(seckillService.getSuccessKillCount(1L), 1L);
    }

    @Test
    public void prepareSeckill() {
        long seckillId = 1L;
        Seckill t = new Seckill();
        when(redisService.getSeckill(seckillId)).thenReturn(t);
        when(redisTemplate.delete(seckillId)).thenReturn(true);
        when(stateMachineUtil.feedMachine(Events.ACTIVITY_RESET, seckillId)).thenReturn(true);
        seckillService.prepareSeckill(seckillId, 10, "1");
        verify(successKilledMongoService, times(1)).deleteRecord(seckillId);
    }

    @Test
    public void execute() {
        seckillService.execute(new SeckillMockRequestDTO(), SYCHRONIZED.getCode());
    }

    @Test
    public void reduceNumber() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        when(seckillService.reduceNumber(successKilled)).thenThrow(new RuntimeException());
        seckillService.reduceNumber(successKilled);
    }

    @Test
    public void reduceNumberInner() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        successKilled.setSeckillId(100L);
        successKilled.setUserPhone("110");
        when(baseMapper.update(eq(null), any())).thenReturn(1);
        seckillService.reduceNumberInner(successKilled);
        verify(successKilledMapper, times(1)).insert(BeanUtil.copyProperties(successKilled, SuccessKilled.class));
//        verify(seckillMockSaveTopic, times(1)).output();
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
        SeckillVO seckill = new SeckillVO();
        long seckillId = 1L;
        seckill.setSeckillId(seckillId);
        int goodsId = 2;
        seckill.setGoodsId(goodsId);
        when(seckillServiceInterface.findById(seckillId)).thenReturn(seckill);
        when(goodsService.findById(goodsId)).thenReturn(new GoodsVO());
        seckillService.getInfoById(seckillId);
    }
}
