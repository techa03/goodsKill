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

    void uploadGoodsPhoto(long goodsId, byte[] bytes);

    byte[] getPhotoImage(int goodsId);

    void addGoods(Goods goods, byte[] bytes);

    List<Goods> queryAll();

    Goods queryByGoodsId(long goodsId);
}
