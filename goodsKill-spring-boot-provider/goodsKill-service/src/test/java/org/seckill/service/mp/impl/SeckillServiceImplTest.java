//package org.seckill.service.mp.impl;
//
//import com.github.pagehelper.PageInfo;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.seckill.api.dto.Exposer;
//import org.seckill.api.dto.SeckillExecution;
//import org.seckill.api.dto.SeckillInfo;
//import org.seckill.api.service.SeckillService;
//import org.seckill.entity.Seckill;
//import org.seckill.service.GoodsKillRpcServiceSimpleApplication;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import static org.junit.Assert.*;
//
//@SpringBootTest(classes = GoodsKillRpcServiceSimpleApplication.class)
//@RunWith(SpringRunner.class)
//@Transactional(rollbackFor = Exception.class)
//public class SeckillServiceImplTest {
//    @Autowired
//    private SeckillService seckillService;
//
//    @Test
//    public void getSeckillList() {
//        PageInfo pageInfo = seckillService.getSeckillList(1,1);
//        assertEquals(pageInfo.getList().size(), 1);
//    }
//
//    @Test
//    public void getById() {
//        SeckillInfo seckill = seckillService.getById(1001L);
//        assertEquals(1001L, (long) seckill.getSeckillId());
//    }
//
//    @Test
//    public void exportSeckillUrl() {
//        Exposer exposer = seckillService.exportSeckillUrl(1001L);
//        assertNotNull(exposer.getMd5());
//    }
//
//    @Test
//    @Transactional
//    public void executeSeckill() {
//        SeckillExecution seckillExecution = seckillService.executeSeckill(1001L, "1", "fdf");
//        assertNotNull(seckillExecution);
//    }
//
//    @Test
//    @Transactional
//    public void addSeckill() {
//        Seckill seckill = new Seckill();
//        seckill.setSeckillId(1100L);
//        seckill.setName("1");
//        seckill.setPrice(BigDecimal.TEN);
//        seckill.setCreateTime(new Date());
//        seckill.setGoodsId(1);
//        seckill.setNumber(1);
//        seckill.setStatus("1");
//        assertTrue(seckillService.addSeckill(seckill) > 0);;
//    }
//
//    @Test
//    public void deleteSeckill() {
//        assertTrue(seckillService.deleteSeckill(1001L) > 0);;
//    }
//
//    @Test
//    public void updateSeckill() {
//    }
//
//    @Test
//    public void selectById() {
//    }
//
//    @Test
//    public void deleteSuccessKillRecord() {
//    }
//
//    @Test
//    public void execute() {
//    }
//
//    @Test
//    public void getSuccessKillCount() {
//    }
//
//    @Test
//    public void prepareSeckill() {
//    }
//}