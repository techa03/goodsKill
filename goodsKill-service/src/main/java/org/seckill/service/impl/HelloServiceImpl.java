package org.seckill.service.impl;

import org.seckill.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by heng on 2017/4/25.
 */
@Service
public class HelloServiceImpl implements HelloService{
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Override
    public void sayHello() {
        logger.info("hello,heng!");
    }

}
