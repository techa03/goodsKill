package com.goodskill.es.api;

import com.goodskill.es.dto.GoodsDto;
import com.goodskill.es.model.Goods;

/**
 * @author heng
 */
public interface GoodsEsService {

    /**
     * @param goodsDto
     * @return
     */
    Goods save(GoodsDto goodsDto);
}
