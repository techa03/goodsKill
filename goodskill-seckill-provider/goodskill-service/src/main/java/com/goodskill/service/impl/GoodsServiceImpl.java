package com.goodskill.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.service.common.GoodsService;
import com.goodskill.service.entity.Goods;
import com.goodskill.service.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

/**
 * 商品信息es库操作类
 *
 * @author techa03
 * @date 2019/6/15
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

}
