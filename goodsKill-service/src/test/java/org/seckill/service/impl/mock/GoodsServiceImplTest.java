package org.seckill.service.impl.mock;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seckill.dao.GoodsMapper;
import org.seckill.entity.Goods;
import org.seckill.service.impl.GoodsServiceImpl;

import static org.junit.Assert.*;

/**
 * Created by heng on 2017/6/28.
 */
public class GoodsServiceImplTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private GoodsServiceImpl goodsService=new GoodsServiceImpl();
    private GoodsMapper goodsDao=context.mock(GoodsMapper.class);

    @Before
    public void setUp(){
        goodsService.setGoodsMapper(goodsDao);
    }

    @Test
    public void uploadGoodsPhoto() throws Exception {
        byte[] bytes = new byte[1];
        context.checking(new Expectations(){{
            Goods record=new Goods();
            record.setGoodsId(1);
            record.setPhotoImage(bytes);
            oneOf(goodsDao).updateByPrimaryKeySelective(record);
        }});
        goodsService.uploadGoodsPhoto(1L, bytes);
    }

    @Test
    public void getPhotoImage() throws Exception {
        Goods goods=new Goods();
        context.checking(new Expectations(){{
            oneOf(goodsDao).selectByPrimaryKey(1);
            will(returnValue(goods));
        }});
        assertEquals(goodsService.getPhotoImage(1),goods.getPhotoImage());
    }

    @Test
    public void addGoods() throws Exception {
        Goods goods=new Goods();
        byte[] bytes = new byte[1];
        context.checking(new Expectations(){{
            goods.setPhotoImage(bytes);
            oneOf(goodsDao).insert(goods);
        }});
        goodsService.addGoods(goods,bytes);
    }

    @Test
    public void queryAll() throws Exception {
        context.checking(new Expectations(){{
            oneOf(goodsDao).selectByExample(null);
            will(returnValue(null));
        }});
        assertNull(goodsService.queryAll());
    }

    @Test
    public void queryByGoodsId() throws Exception {
        Goods goods=new Goods();
        context.checking(new Expectations(){{
            oneOf(goodsDao).selectByPrimaryKey(1);
            will(returnValue(goods));
        }});
        assertNotNull(goodsService.queryByGoodsId(1L));
    }

}