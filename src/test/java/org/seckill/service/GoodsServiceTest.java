package org.seckill.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heng on 2017/1/7.
 */
public class GoodsServiceTest {
    @Autowired
    private GoodsService goodsService;

    @Transactional
    @Test
    public void uploadGoodsPhoto() {
    }

}