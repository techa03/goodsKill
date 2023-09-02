package com.goodskill.api.service;

import com.goodskill.api.vo.GoodsVO;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author heng
 * @date 2017/1/7
 */
public interface GoodsService {

    /**
     * @param goodsId
     * @param fileUrl
     */
    void uploadGoodsPhoto(long goodsId, String fileUrl);

    /**
     * @param goods
     */
    void addGoods(GoodsVO goods);

    List<GoodsVO> findMany();

    GoodsVO findById(Serializable goodsId);
}
