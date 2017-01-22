package org.seckill.service;

import java.io.IOException;
import java.util.List;

import org.seckill.entity.Goods;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by heng on 2017/1/7.
 */
public interface GoodsService {

    void uploadGoodsPhoto(CommonsMultipartFile file, long goodsId) throws IOException;

    String getPhotoUrl(int goodsId);

    void addGoods(Goods goods, CommonsMultipartFile file);

    List<Goods> queryAll();
}
