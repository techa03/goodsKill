package com.goodskill.web.controller;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.service.SeckillService;
import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SeckillMockControllerTest {

    @Mock
    private SeckillService seckillService;
    @Mock
    private KafkaTemplate kafkaTemplate;
    @Mock
    private StreamBridge streamBridge;
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @InjectMocks
    private SeckillMockController seckillMockController;
    @Spy
    private ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        taskExecutor.initialize();

        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @Test
    void testDoWithSychronized() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        requestDTO.setCorePoolSize(1);
        requestDTO.setMaxPoolSize(10);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithSychronized(requestDTO);

        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void testDoWithRedissonLock() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithRedissonLock(requestDTO);

        // 等待异步任务执行完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(seckillService, times(1)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void doWithActiveMqMessage() {

    }

    @Test
    void doWithKafkaMqMessage() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithKafkaMqMessage(requestDTO);

        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void doWithProcedure() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithProcedure(requestDTO);

        // 等待异步任务执行完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(seckillService, times(1)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void doWithActiveMqMessageWithReply() {

    }

    @Test
    void doWithZookeeperLock() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithZookeeperLock(requestDTO);

        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void redisReactiveMongo() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.redisReactiveMongo(requestDTO);

        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void doWithRabbitmq() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.doWithRabbitmq(requestDTO);

        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

    @Test
    void limit() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.limit(requestDTO);
        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

//    @Test
    void atomicWithCanal() {
        SeckillWebMockRequestDTO requestDTO = new SeckillWebMockRequestDTO();
        requestDTO.setSeckillId(1L);
        requestDTO.setRequestCount(1);
        SeckillMockRequestDTO any = new SeckillMockRequestDTO();
        any.setSeckillId(1L);
        Result response = seckillMockController.atomicWithCanal(requestDTO);
        verify(seckillService, times(0)).execute(any(SeckillMockRequestDTO.class), anyInt());
        assertEquals(0, response.getCode());
    }

}
