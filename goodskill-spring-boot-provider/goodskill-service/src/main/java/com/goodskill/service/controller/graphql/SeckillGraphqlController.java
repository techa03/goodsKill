package com.goodskill.service.controller.graphql;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.entity.Goods;
import com.goodskill.entity.Seckill;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.service.common.SuccessKilledService;
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
    public Seckill seckillById(@Argument String id) {
        return seckillService.getById(id);
    }

    @SchemaMapping
    public Goods goods(Seckill seckill) {
        return goodsService.getById(seckill.getGoodsId());
    }

    @SchemaMapping
    public List<SuccessKilled> successKilledList(Seckill seckill) {
        return successKilledService.list(Wrappers.<SuccessKilled>lambdaQuery().eq(SuccessKilled::getSeckillId, seckill.getSeckillId()));
    }

}
