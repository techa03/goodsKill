package org.seckill.service.impl;

import com.github.pagehelper.PageInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillInfo;
import org.seckill.entity.Seckill;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:META-INF/spring/spring-dao.xml",
        "classpath:META-INF/spring/spring-service.xml"})
@Transactional
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        PageInfo<Seckill> pageInfo = seckillService.getSeckillList(3,2);
        for(Seckill seckill:pageInfo.getList()){
            logger.info("seckill={}", seckill.toString());
        }
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000;
        SeckillInfo seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill.toString());
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
        Assert.assertNotNull(exposer);
    }


}