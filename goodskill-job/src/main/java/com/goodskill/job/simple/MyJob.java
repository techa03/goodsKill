package com.goodskill.job.simple;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.es.api.GoodsEsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MyJob implements SimpleJob {
    @DubboReference
    private GoodsService goodsService;
    @Resource
    private GoodsEsService goodsEsService;

    @Override
    public void execute(ShardingContext context) {
        switch (context.getShardingItem()) {
            case 0:
                try {
                    log.info("分片0 商品es索引开始更新。。。");
                    List list = goodsService.findMany().parallelStream().map(g -> {
                        GoodsVO goods = new GoodsVO();
                        BeanUtils.copyProperties(g, goods);
                        return goods;
                    }).collect(Collectors.toList());
                    goodsEsService.saveBatch(list);
                    log.info("分片0 商品es索引更新成功，条数:{}", list.size());
                } catch (Exception e) {
                    log.warn("分片0 商品es索引定时任务更新失败!", e);
                }
                break;
            case 1:
                try {
                    log.info("分片1 商品es索引开始更新。。。");
                } catch (Exception e) {
                    log.warn("分片1 商品es索引定时任务更新失败!", e);
                }
                break;
            case 2:
                try {
                    log.info("分片2 商品es索引开始更新。。。");
                } catch (Exception e) {
                    log.warn("分片2 商品es索引定时任务更新失败!", e);
                }
                break;
            // case n: ...
            default:
                break;
        }
    }
}
