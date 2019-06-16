package com.goodskill.es.api;

import com.goodskill.es.dto.GoodsDto;

/**
 * @author heng
 */
public interface GoodsEsService {

    /**
     * @param goodsDto
     * @return
     */
    void save(GoodsDto goodsDto);
}
