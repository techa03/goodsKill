package org.seckill.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.seckill.entity.Goods;

import java.util.List;

/**
 *
 * @author heng
 * @date 2017/1/7
 */
public interface GoodsService extends IService<Goods> {

    /**
     * @param goodsId
     * @param bytes
     */
    void uploadGoodsPhoto(long goodsId, byte[] bytes);

    /**
     * @param goodsId
     * @return
     */
    byte[] getPhotoImage(int goodsId);

    /**
     * @param goods
     * @param bytes
     */
    void addGoods(Goods goods, byte[] bytes);

    /**
     * @return
     */
    List<Goods> queryAll();

    /**
     * @param goodsId
     * @return
     */
    Goods queryByGoodsId(long goodsId);
}
