package org.seckill.dao;

import org.seckill.entity.Goods;
import org.seckill.entity.GoodsExample;

import java.util.List;

/**
 * @author heng
 */
public interface GoodsMapper extends BaseMapper<GoodsExample, Goods> {

    List<Goods> selectByExampleWithBLOBs(GoodsExample example);

    int updateByPrimaryKeyWithBLOBs(Goods record);

}