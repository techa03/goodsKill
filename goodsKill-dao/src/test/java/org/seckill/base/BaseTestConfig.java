package org.seckill.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heng on 2017/6/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:junit/spring/spring-dao.xml"})
@Transactional
public class BaseTestConfig {

    @Test
    public void testSelectByPrimaryKey() throws Exception {

    }
}
