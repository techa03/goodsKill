package org.seckill.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.seckill.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by heng on 2017/4/25.
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService{
    @Override
    public void sayHello() {
        log.info("hello,heng!");
    }

}
