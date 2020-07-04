package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.mock.strategy.GoodsKillStrategy;
import org.seckill.service.mock.strategy.GoodsKillStrategyEnum;
import org.seckill.util.common.util.DateUtil;
import org.seckill.util.common.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 秒杀库存表 服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
@Slf4j
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements SeckillService {

    @Reference(check = false)
    private SuccessKilledMongoService successKilledMongoService;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private List<GoodsKillStrategy> goodsKillStrategies;
    @Autowired
    private SeckillService seckillService;

    @Override
    public PageInfo getSeckillList(int pageNum, int pageSize, String goodsName) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Seckill> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(goodsName)) {
            queryWrapper.lambda().like(Seckill::getName, goodsName);
        }
        List<Seckill> list = this.list(queryWrapper);
        return new PageInfo(list);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //从redis中获取缓存秒杀信息
        Seckill seckill = redisService.getSeckill(seckillId);
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = MD5Util.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public SeckillExecution executeSeckill(long seckillId, String userPhone, String md5) {
        if (md5 == null || !md5.equals(MD5Util.getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = DateUtil.getNowTime();
        try {
            int updateCount = baseMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserPhone(userPhone);
                int insertCount = successKilledMapper.insert(successKilled);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled key = new SuccessKilled();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    // TODO 支付宝扫码待集成
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilledMapper.selectOne(new QueryWrapper<>(key)), "");
                }
            }
        } catch (SeckillCloseException | RepeatKillException e1) {
            log.info(e1.getMessage(), e1);
            throw e1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public void deleteSuccessKillRecord(long seckillId) {
        SuccessKilled example = new SuccessKilled();
        example.setSeckillId(seckillId);
        successKilledMapper.delete(new QueryWrapper<>(example));
    }

    @Override
    public void execute(SeckillMockRequestDto requestDto, int strategyNumber) {
        Optional<GoodsKillStrategy> first = goodsKillStrategies.stream().filter(n -> GoodsKillStrategyEnum.stateOf(strategyNumber).getClassName().equals(n.getClass().getName())).findFirst();
        first.ifPresent(n -> n.execute(requestDto));
    }

    /**
     * 获取秒杀成功笔数
     *
     * @param seckillId 秒杀活动id
     * @return
     */
    @Override
    public long getSuccessKillCount(Long seckillId) {
        SuccessKilled example = new SuccessKilled();
        example.setSeckillId(seckillId);
        long count = successKilledMapper.selectCount(new QueryWrapper<>(example));
        if (count == 0) {
            try {
                count = successKilledMongoService.count(SuccessKilledDto.builder().seckillId(BigInteger.valueOf(seckillId)).build());
            } catch (Exception e) {
                log.error("mongo服务不可用，请检查！", e);
            }
        }
        return count;
    }

    @Override
    public void prepareSeckill(Long seckillId, int seckillCount) {
        // 初始化库存数量
        Seckill entity = new Seckill();
        entity.setSeckillId(seckillId);
        entity.setNumber(seckillCount);
        entity.setStatus(SeckillStatusConstant.IN_PROGRESS);
        this.updateById(entity);
        // 清理已成功秒杀记录
        this.deleteSuccessKillRecord(seckillId);
        Seckill seckill = redisService.getSeckill(seckillId);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.increment(seckillId);
        while (valueOperations.decrement(seckillId) > 1) {
            valueOperations.decrement(seckillId);
        }
        seckill.setStatus(SeckillStatusConstant.IN_PROGRESS);
        redisService.putSeckill(seckill);

        // 清理mongo表数据
        try {
            successKilledMongoService.deleteRecord(seckillId);
        } catch (Exception e) {
            log.error("mongo服务不可用请检查！", e);
        }
    }

    @Override
    public int reduceNumber(SuccessKilled successKilled) {
        int count = 0;
        try {
            count = seckillService.reduceNumberInner(successKilled);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int reduceNumberInner(SuccessKilled successKilled) {
        successKilledMapper.insert(successKilled);

        Seckill wrapper = new Seckill();
        wrapper.setSeckillId(successKilled.getSeckillId());
        UpdateWrapper updateWrapper = new UpdateWrapper(wrapper);
        updateWrapper.gt("end_time", successKilled.getCreateTime());
        updateWrapper.lt("start_time", successKilled.getCreateTime());
        updateWrapper.gt("number", 0);
        updateWrapper.setSql("number = number - 1");
        int update = baseMapper.update(null, updateWrapper);
        if (update <= 0) {
            throw new SeckillCloseException("seckill is closed");
        } else {
            return update;
        }
    }
}
