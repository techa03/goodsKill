package com.goodskill.es.api;

import com.goodskill.es.dto.GoodsDto;

import java.util.List;

/**
 * @author heng
 */
public interface GoodsEsService {

    /**
     * 保存
     * @param goodsDto
     * @return
     */
    void save(GoodsDto goodsDto);

    /**
     * 删除商品
     */
    void delete(GoodsDto goodsDto);

    /**
     * 根据商品名称检索商品
     * @param input 用户输入的商品关键词
     * @return 分页结果
     */
    List<GoodsDto> searchWithNameByPage(String input);
}
