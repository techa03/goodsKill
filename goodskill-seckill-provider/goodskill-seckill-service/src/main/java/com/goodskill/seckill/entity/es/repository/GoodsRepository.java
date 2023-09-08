package com.goodskill.seckill.entity.es.repository;

import com.goodskill.seckill.entity.es.model.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author heng
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, String> {

   void deleteByGoodsId(Integer goodsId);

}
