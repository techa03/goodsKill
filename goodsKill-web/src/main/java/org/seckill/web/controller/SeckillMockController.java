package org.seckill.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 */
@Api(tags = "模拟秒杀场景(无需登录)")
@RestController("/seckill")
@Slf4j
public class SeckillMockController {

    @Autowired
    SeckillService seckillService;

    /**
     * 通过同步锁控制秒杀并发，效率较低
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以很好的解决秒杀问题，适用于单机模式。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized/{seckillId}")
    public void doWithSychronized(@PathVariable("seckillId") Long seckillId) throws InterruptedException {
        // 初始化库存数量
        Seckill entity = new Seckill();
        entity.setSeckillId(seckillId);
        int totalCount = 1000;
        entity.setNumber(totalCount);
        seckillService.updateByPrimaryKeySelective(entity);
        // 清理已成功秒杀记录
        seckillService.deleteSuccessKillRecord(seckillId);
        seckillService.executeWithSynchronized(seckillId,2000);
    }
}
