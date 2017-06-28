package org.seckill.service.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heng on 2017/6/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:META-INF/spring/spring-dao.xml",
        "classpath:META-INF/spring/spring-service.xml"})
@Transactional
public class BaseServiceConfigForTest {
    @Test
    public void test(){

    }
}
