package com.goodskill.job.simple;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyJob2 implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        switch (context.getShardingItem()) {
            case 0:
                log.info("job2 分片0 商品es索引开始更新。。。");
                break;
            case 1:
                log.info("job2 分片1 商品es索引开始更新。。。");
                break;
            case 2:
                log.info("job2 分片2 商品es索引开始更新。。。");
                break;
            // case n: ...
            default:
                break;
        }
    }
}