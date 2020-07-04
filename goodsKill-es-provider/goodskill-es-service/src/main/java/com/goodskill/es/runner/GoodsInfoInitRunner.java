package com.goodskill.es.runner;

import com.goodskill.es.model.Goods;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.seckill.api.service.GoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品信息es索引初始化脚本
 *
 * @author techa03
 * @date 2020/7/4
 */
@Component
@Slf4j
public class GoodsInfoInitRunner implements ApplicationRunner {
    @Autowired
    private ElasticsearchOperations client;
    @Reference(check = false)
    private GoodsService goodsService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List list = goodsService.list().parallelStream().map(g -> {
                Goods goods = new Goods();
                BeanUtils.copyProperties(g, goods);
                return goods;
            }).collect(Collectors.toList());
            client.save(list);
        } catch (Exception e) {
            log.warn("商品es索引更新失败，请先启动GoodsKillRpcServiceApplication！");
        }
    }
}
