package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath*:spring/spring-dao.xml",
        "classpath*:spring/spring-service.xml"})
@Transactional
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GoodsService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        String list = seckillService.getPhotoUrl(1);
        logger.info("list={}", list);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000;
//        SeckillInfo seckill = seckillService.getById(id);
//        logger.info("seckill={}", seckill.toString());
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id=1000;
//        Exposer exposer=seckillService.exportSeckillUrl(id);
//        logger.info("exposer={}",exposer);
//        Assert.assertNotNull(exposer);
    }


}