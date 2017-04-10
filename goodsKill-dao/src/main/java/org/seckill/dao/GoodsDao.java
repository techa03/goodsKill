package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Goods;

public interface GoodsDao extends BaseDao<Goods>{

	int updatePhotoUrl(@Param("goodsId")long goodsId,@Param("photoUrl")String photoUrl);

	Goods selectById(long goodsId);

}
