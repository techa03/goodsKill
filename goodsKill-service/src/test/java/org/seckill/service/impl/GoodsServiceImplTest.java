package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Goods;
import org.seckill.service.GoodsService;
import org.seckill.service.base.BaseServiceConfigForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by heng on 2017/6/28.
 */
public class GoodsServiceImplTest extends BaseServiceConfigForTest {
    @Autowired
    private GoodsService goodsService;
    @Test
    public void uploadGoodsPhoto() throws Exception {
        goodsService.uploadGoodsPhoto(1L,new byte[1]);
    }

    @Test
    public void getPhotoImage() throws Exception {
        assertNotNull(goodsService.getPhotoImage(1));
    }

    @Test
    public void addGoods() throws Exception {
        goodsService.addGoods(new Goods(),new byte[1]);
    }

    @Test
    public void queryAll() throws Exception {
        assertTrue(goodsService.queryAll().size()!=0);
    }

    @Test
    public void queryByGoodsId() throws Exception {
        assertNotNull(goodsService.queryByGoodsId(1));
    }

}