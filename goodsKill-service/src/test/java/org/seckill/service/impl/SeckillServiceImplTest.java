package org.seckill.service.impl;

import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillInfo;
import org.seckill.entity.Seckill;
import org.seckill.service.SeckillService;
import org.seckill.service.base.BaseServiceConfigForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SeckillServiceImplTest extends BaseServiceConfigForTest{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        PageInfo<Seckill> pageInfo = seckillService.getSeckillList(3,2);
        for(Seckill seckill:pageInfo.getList()){
            logger.info("seckill={}", seckill.toString());
        }
        assertTrue(pageInfo.getList().size()!=0);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        SeckillInfo seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill.toString());
        assertNotNull(seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
        assertNotNull(exposer);
    }

    @Test
    public void executeSeckill() throws Exception {
//        seckillService.executeSeckill(1000L,"1244354352", MD5Util.getMD5(1000L));
    }

    @Test
    public void addSeckill() throws Exception {
        Seckill seckill=new Seckill();
        seckill.setSeckillId(100L);
        seckill.setGoodsId(3);
        seckill.setName("1");
        seckill.setNumber(10);
        assertTrue(seckillService.addSeckill(seckill)!=0);
    }

    @Test
    public void deleteSeckill() throws Exception {
        assertTrue(seckillService.deleteSeckill(1000L)!=0);
    }

    @Test
    public void updateSeckill() throws Exception {
        Seckill seckill=new Seckill();
        seckill.setSeckillId(1000L);
        seckill.setGoodsId(3);
        assertTrue(seckillService.updateSeckill(seckill)!=0);
    }

    @Test
    public void selectById() throws Exception {
        assertNotNull(seckillService.selectById(1000L));
    }


}