package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.service.HelloService;
import org.seckill.service.base.BaseServiceConfigForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by heng on 2017/6/28.
 */
public class HelloServiceImplTest extends BaseServiceConfigForTest{
    @Autowired
    private HelloService helloService;
    @Test
    public void sayHello() throws Exception {
        helloService.sayHello();
    }

}