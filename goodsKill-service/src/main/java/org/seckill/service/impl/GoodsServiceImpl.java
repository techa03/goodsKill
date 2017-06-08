package org.seckill.service.impl;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.seckill.dao.GoodsMapper;
import org.seckill.entity.Goods;
import org.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * Created by heng on 2017/1/7.
 */
@SuppressWarnings("ALL")
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsDao;

    private Logger logger = LoggerFactory.logger(this.getClass());

    @Override
    public void uploadGoodsPhoto(long goodsId,byte[] bytes) throws IOException {
        Goods goods=new Goods();
        goods.setGoodsId((int)goodsId);
        goods.setPhotoImage(bytes);
        goodsDao.updateByPrimaryKeySelective(goods);
    }

    @Override
    public byte[] getPhotoImage(int goodsId) {
        Goods good = goodsDao.selectByPrimaryKey(goodsId);
        return good.getPhotoImage();
    }


    @Override
    public void addGoods(Goods goods, byte[] bytes) {
        goods.setPhotoImage(bytes);
        goodsDao.insert(goods);
    }

    @Override
    public List<Goods> queryAll() {
        return goodsDao.selectByExample(null);
    }

    @Override
    public Goods queryByGoodsId(long goodsId) {
        return goodsDao.selectByPrimaryKey((int)goodsId);
    }
}
