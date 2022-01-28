package com.goodskill.service.common.trade.alipay;

import com.goodskill.entity.Seckill;
import com.goodskill.mp.dao.mapper.GoodsMapper;
import com.goodskill.service.common.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
class AlipayRunnerTest {
    @InjectMocks
    private AlipayRunner alipayRunner;
    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private RedisService redisService;

    @Test
    void tradePrecreate() {
        long seckillId = 1001L;
        Seckill seckill = new Seckill();
        seckill.setGoodsId(1);
        when(redisService.getSeckill(seckillId)).thenReturn(seckill);

        alipayRunner.setAlipayPublicKey("1");
        alipayRunner.setNotifyUrl("1");
        alipayRunner.setSignType("RSA2");
        assertTrue(StringUtils.isBlank(alipayRunner.tradePrecreate(seckillId)));
    }
}