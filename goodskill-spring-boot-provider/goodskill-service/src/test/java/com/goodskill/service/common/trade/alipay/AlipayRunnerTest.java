package com.goodskill.service.common.trade.alipay;

import com.goodskill.service.GoodsKillServiceApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = GoodsKillServiceApplication.class)
@ExtendWith(SpringExtension.class)
@Disabled
class AlipayRunnerTest {
    @Autowired
    private AlipayRunner alipayRunner;

    @Test
    void tradePrecreate() {
        assertTrue(StringUtils.isNotBlank(alipayRunner.tradePrecreate(1001L)));
    }
}