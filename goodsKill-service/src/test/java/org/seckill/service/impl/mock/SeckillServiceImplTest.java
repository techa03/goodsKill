package org.seckill.service.impl.mock;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.seckill.common.trade.alipay.AlipayRunner;
import org.seckill.dao.GoodsMapper;
import org.seckill.dao.RedisDao;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.service.impl.SeckillServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SeckillServiceImplTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private SeckillServiceImpl seckillService = new SeckillServiceImpl();
    private AlipayRunner alipayRunner = new AlipayRunner();
    private GoodsMapper goodsMapper = context.mock(GoodsMapper.class);
    private SuccessKilledMapper successKilledMapper = context.mock(SuccessKilledMapper.class);
    private ExtSeckillMapper seckillMapper = context.mock(ExtSeckillMapper.class);

    @Before
    public void setUp() {
        seckillService.setGoodsMapper(goodsMapper);
        seckillService.setAlipayRunner(alipayRunner);
        seckillService.setRedisDao(new RedisDao());
        seckillService.setExtSeckillMapper(seckillMapper);
        seckillService.setSuccessKilledMapper(successKilledMapper);

    }

    @Test
    public void getSeckillList() throws Exception {
        context.checking(new Expectations() {{
            oneOf(seckillMapper).selectByExample(null);
            will(returnValue(null));
        }});
        assertNotNull(seckillService.getSeckillList(3, 2));
    }

    @Test
    public void getById() throws Exception {
        context.checking(new Expectations() {{
            Seckill seckill = new Seckill();
            seckill.setGoodsId(1);
            oneOf(seckillMapper).selectByPrimaryKey(1000L);
            will(returnValue(seckill));
            oneOf(goodsMapper).selectByPrimaryKey(1);
            will(returnValue(new Goods()));
        }});
        assertNotNull(seckillService.getById(1000L));
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        RedisDao redisDao = PowerMockito.mock(RedisDao.class);

        context.checking(new Expectations() {{
//            PowerMockito.when(redisDao.getSeckill(1000L)).thenReturn(new Seckill());
            oneOf(seckillMapper).selectByPrimaryKey(1000L);
            will(returnValue(new Seckill()));
        }});
        try {
            seckillService.exportSeckillUrl(1000L);
        } catch (Exception e) {
            // TODO
        }


    }

    @Test
    public void executeSeckill() throws Exception {
//        suppress(AlipayRunner.class.getDeclaredMethods());
//        PowerMockito.mockStatic(DateUtil.class);
//        context.checking(new Expectations() {{
//            PowerMockito.when(DateUtil.getNowTime()).thenReturn(new Date(100));
//            Date date = new Date();
//            oneOf(seckillMapper).reduceNumber(1, new Date(100));
//            will(returnValue(1));
//            SuccessKilled record = new SuccessKilled();
//            record.setSeckillId(1L);
//            record.setUserPhone("13212343245");
//            oneOf(successKilledMapper).insertSelective(record);
//            will(returnValue(1));
//            oneOf(successKilledMapper).selectByPrimaryKey(1L, "13212343245");
//            will(returnValue(new SuccessKilled()));
//        }});
//        seckillService.executeSeckill(1L, "13212343245", MD5Util.getMD5(1));
    }

    @Test
    public void addSeckill() throws Exception {
        Seckill seckill = new Seckill();
        context.checking(new Expectations() {{
            oneOf(seckillMapper).insert(seckill);
            will(returnValue(1));
        }});
        assertEquals(seckillService.addSeckill(seckill), 1);
    }

    @Test
    public void deleteSeckill() throws Exception {
        Seckill seckill = new Seckill();
        context.checking(new Expectations() {{
            oneOf(seckillMapper).deleteByPrimaryKey(1000L);
            will(returnValue(1));
        }});
        assertEquals(seckillService.deleteSeckill(1000L), 1);
    }

    @Test
    public void updateSeckill() throws Exception {
        Seckill seckill = new Seckill();
        context.checking(new Expectations() {{
            oneOf(seckillMapper).updateByPrimaryKeySelective(seckill);
            will(returnValue(1));
        }});
        assertEquals(seckillService.updateSeckill(seckill), 1);
    }

    @Test
    public void selectById() throws Exception {
        Seckill seckill = new Seckill();
        context.checking(new Expectations() {{
            oneOf(seckillMapper).selectByPrimaryKey(1L);
            will(returnValue(seckill));
        }});
        assertEquals(seckillService.selectById(1L), seckill);
    }


}