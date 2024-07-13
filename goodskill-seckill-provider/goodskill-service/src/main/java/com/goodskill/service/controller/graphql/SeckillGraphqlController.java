package com.goodskill.service.controller.graphql;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.service.common.SuccessKilledService;
import com.goodskill.service.entity.SuccessKilled;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SeckillGraphqlController {
    @Resource
    private SeckillService seckillService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private SuccessKilledService successKilledService;

    @QueryMapping
    public SeckillVO seckillById(@Argument String id) {
        return seckillService.findById(id);
    }

    @SchemaMapping
    public GoodsVO goods(SeckillVO seckill) {
        return goodsService.findById(seckill.getGoodsId());
    }

    @SchemaMapping
    public List<SuccessKilled> successKilledList(SeckillVO seckill) {
        return successKilledService.list(Wrappers.<SuccessKilled>lambdaQuery().eq(SuccessKilled::getSeckillId, seckill.getSeckillId()));
    }



}
