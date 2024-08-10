package com.goodskill.service.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DatabasePreRequestHandler extends AbstractPreRequestHandler {
    @Resource
    private SeckillMapper seckillMapper;
    @Resource
    private SuccessKilledMapper successKilledMapper;
    @Override
    public void handle(SeckillWebMockRequestDTO request) {
        Seckill entity = new Seckill();
        entity.setSeckillId(request.getSeckillId());
        entity.setNumber(request.getSeckillCount());
        seckillMapper.updateById(entity);

        // 清理已成功秒杀记录
        SuccessKilled example = new SuccessKilled();
        example.setSeckillId(request.getSeckillId());
        successKilledMapper.delete(new QueryWrapper<>(example));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
