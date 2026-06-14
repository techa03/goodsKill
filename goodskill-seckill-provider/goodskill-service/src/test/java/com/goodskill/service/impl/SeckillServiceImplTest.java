package com.goodskill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.service.GoodsThirdPartyService;
import com.goodskill.core.feign.OrderFeignClient;
import com.goodskill.core.pojo.dto.SeckillMockRequestDTO;
import com.goodskill.core.pojo.dto.SuccessKilledDTO;
import com.goodskill.service.common.GoodsService;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.handler.PreRequestPipeline;
import com.goodskill.service.impl.dubbo.SeckillServiceImpl;
import com.goodskill.service.inner.SeckillService;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.util.StateMachineService;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
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

import static com.goodskill.core.enums.SeckillSolutionEnum.SYCHRONIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeckillServiceImplTest {
    @InjectMocks
    private SeckillServiceImpl seckillService;
    @Spy
    private SeckillService seckillServiceInterface;
    @Mock
    private SeckillMapper baseMapper;
    @Mock
    private RedisService redisService;
    @Mock
    private SuccessKilledMapper successKilledMapper;
    @Mock
    private OrderFeignClient orderFeignClient;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ValueOperations valueOperations;
    @Mock
    private List<GoodsKillStrategy> goodskillStrategies;
    @Spy
    private ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1,1,2L, TimeUnit.SECONDS, new ArrayBlockingQueue(1));
    @Mock
    private GoodsThirdPartyService goodsThirdPartyService;
    @Mock
    private StateMachineService stateMachineService;
    @Mock
    private PreRequestPipeline preRequestPipeline;
    @Mock
    private GoodsService goodsService;

    @BeforeEach
    public void setUp() throws Exception {
        // 设置 baseMapper，因为 ServiceImpl 需要通过反射访问
        java.lang.reflect.Field baseMapperField = com.baomidou.mybatisplus.extension.repository.CrudRepository.class.getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(seckillService, baseMapper);
    }

    @Test
    public void getSeckillList() {
        Seckill seckillEntity = new Seckill();
        seckillEntity.setGoodsId(1);
        String goodsName = "test";
        String key = "seckill:list:" + 1 + ":" + 1 + ":" + goodsName;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        Page<Seckill> page = new Page<>();
        page.setRecords(Lists.newArrayList(seckillEntity));
        when(baseMapper.selectPage(any(), any())).thenReturn(page);
        when(goodsService.getById(1)).thenReturn(null);
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
        when(orderFeignClient.count(1L)).thenReturn(1L);
        assertEquals(seckillService.getSuccessKillCount(1L), 1L);
    }

    @Test
    public void prepareSeckill() {
        long seckillId = 1L;
        Seckill t = new Seckill();
        seckillService.prepareSeckill(seckillId, 10, "1");
        verify(orderFeignClient, times(0)).deleteRecord(seckillId);
    }

    @Test
    public void execute() {
        seckillService.execute(new SeckillMockRequestDTO(), SYCHRONIZED.getCode());
    }

    @Test
    public void reduceNumber() {
        SuccessKilledDTO successKilled = new SuccessKilledDTO();
        // Mock the actual behavior instead of throwing exception
        lenient().when(successKilledMapper.insert(any(SuccessKilled.class))).thenThrow(new RuntimeException());
        int result = seckillService.reduceNumber(successKilled);
        // Should catch exception and return 0
        assertEquals(0, result);
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
        long seckillId = 1L;
        int goodsId = 2;

        // Mock baseMapper.selectById to return a Seckill entity
        Seckill seckillEntity = new Seckill();
        seckillEntity.setSeckillId(seckillId);
        seckillEntity.setGoodsId(goodsId);
        when(baseMapper.selectById(seckillId)).thenReturn(seckillEntity);

        // Mock goodsService
        com.goodskill.service.entity.Goods goods = new com.goodskill.service.entity.Goods();
        goods.setGoodsId(goodsId);
        goods.setName("test goods");
        when(goodsService.getById(goodsId)).thenReturn(goods);

        seckillService.getInfoById(seckillId);

        verify(baseMapper, times(1)).selectById(seckillId);
        // getInfoById 会调用两次 getById: 一次在 findById 中，一次在 getInfoById 中
        verify(goodsService, times(2)).getById(goodsId);
    }
}
