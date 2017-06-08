package org.seckill.service;

import org.seckill.entity.Goods;

import java.io.IOException;
import java.util.List;

/**
 * Created by heng on 2017/1/7.
 */
public interface GoodsService {

    void uploadGoodsPhoto(long goodsId, byte[] bytes) throws IOException;

    byte[] getPhotoImage(int goodsId);

    void addGoods(Goods goods, byte[] bytes);

    List<Goods> queryAll();

    Goods queryByGoodsId(long goodsId);
}
