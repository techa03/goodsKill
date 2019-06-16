package com.goodskill.es.repository;

import com.goodskill.es.model.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @author heng
 */
public interface GoodsRepository extends ElasticsearchCrudRepository<Goods, Integer> {
}
