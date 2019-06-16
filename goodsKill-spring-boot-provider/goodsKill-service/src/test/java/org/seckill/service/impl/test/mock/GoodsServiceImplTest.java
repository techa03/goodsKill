package org.seckill.service.impl.test.mock;

import com.goodskill.es.api.GoodsEsService;
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
    private GoodsServiceImpl goodsService = new GoodsServiceImpl();
    private GoodsMapper goodsDao = context.mock(GoodsMapper.class);
    private GoodsEsService goodsEsService = context.mock(GoodsEsService.class);

    @Before
    public void setUp() {
        goodsService.setGoodsMapper(goodsDao);
        goodsService.setGoodsEsService(goodsEsService);
    }

    @Test
    public void uploadGoodsPhoto() {
        byte[] bytes = new byte[1];
        context.checking(new Expectations() {{
            Goods record = new Goods();
            record.setGoodsId(1);
            record.setPhotoImage(bytes);
            oneOf(goodsDao).updateByPrimaryKeySelective(record);
        }});
        goodsService.uploadGoodsPhoto(1L, bytes);
    }

    @Test
    public void getPhotoImage() throws Exception {
        Goods goods = new Goods();
        context.checking(new Expectations() {{
            oneOf(goodsDao).selectByPrimaryKey(1);
            will(returnValue(goods));
        }});
        assertEquals(goodsService.getPhotoImage(1), goods.getPhotoImage());
    }

//    @Test
//    public void addGoods() throws Exception {
//        PowerMockito.mockStatic(IdWorker.class);
//        Goods goods = new Goods();
//        byte[] bytes = new byte[1];
//        context.checking(new Expectations() {{
//            goods.setPhotoImage(bytes);
//            oneOf(goodsDao).insert(goods);
//            GoodsDto goodsDto = new GoodsDto();
//            PowerMockito.when(IdWorker.getId()).thenReturn(1L);
//            BeanUtils.copyProperties(goods, goodsDto);
//            goodsDto.setId(1L);
//            oneOf(goodsEsService).save(goodsDto);
//        }});
//        goodsService.addGoods(goods, bytes);
//    }

    @Test
    public void queryAll() throws Exception {
        context.checking(new Expectations() {{
            oneOf(goodsDao).selectByExample(null);
            will(returnValue(null));
        }});
        assertNull(goodsService.queryAll());
    }

    @Test
    public void queryByGoodsId() throws Exception {
        Goods goods = new Goods();
        context.checking(new Expectations() {{
            oneOf(goodsDao).selectByPrimaryKey(1);
            will(returnValue(goods));
        }});
        assertNotNull(goodsService.queryByGoodsId(1L));
    }

}