package com.goodskill.service.common.trade.alipay;

import com.goodskill.service.GoodsKillServiceApplication;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = GoodsKillServiceApplication.class)
@RunWith(SpringRunner.class)
@Ignore
class AlipayRunnerTest {
    @Autowired
    private AlipayRunner alipayRunner;

    @Test
    void tradePrecreate() {
        assertTrue(StringUtils.isNotBlank(alipayRunner.tradePrecreate(1001L)));
    }
}