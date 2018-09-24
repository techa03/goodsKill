package org.seckill.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.GoodsService;
import org.seckill.dao.GoodsMapper;
import org.seckill.entity.Goods;
import org.seckill.entity.GoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * Created by heng on 2017/1/7.
 */
@Service
@Slf4j
public class GoodsServiceImpl extends AbstractServiceImpl<GoodsMapper, GoodsExample, Goods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    public void setGoodsMapper(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public void uploadGoodsPhoto(long goodsId, byte[] bytes) throws IOException {
        Goods goods = new Goods();
        goods.setGoodsId((int) goodsId);
        goods.setPhotoImage(bytes);
        log.info(goods.toString());
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public byte[] getPhotoImage(int goodsId) {
        Goods good = goodsMapper.selectByPrimaryKey(goodsId);
        return good.getPhotoImage();
    }


    @Override
    public void addGoods(Goods goods, byte[] bytes) {
        goods.setPhotoImage(bytes);
        goodsMapper.insert(goods);
    }

    @Override
    public List<Goods> queryAll() {
        return goodsMapper.selectByExample(null);
    }

    @Override
    public Goods queryByGoodsId(long goodsId) {
        return goodsMapper.selectByPrimaryKey((int) goodsId);
    }

}
