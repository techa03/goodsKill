package org.seckill.api.service;

import org.seckill.entity.Goods;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author heng
 * @date 2017/1/7
 */
public interface GoodsService {

    /**
     * @param goodsId
     * @param bytes
     */
    void uploadGoodsPhoto(long goodsId, byte[] bytes);

    /**
     * @param goods
     * @param bytes
     */
    void addGoods(Goods goods, byte[] bytes);

    List<Goods> list();

    Goods getById(Serializable goodsId);
}
