package org.seckill.service;

import org.seckill.entity.Goods;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by heng on 2017/1/7.
 */
public interface GoodsService {

    void uploadGoodsPhoto(CommonsMultipartFile file, long goodsId);

    String getPhotoUrl(int goodsId);

    void addGoods(Goods goods, CommonsMultipartFile file);
}
