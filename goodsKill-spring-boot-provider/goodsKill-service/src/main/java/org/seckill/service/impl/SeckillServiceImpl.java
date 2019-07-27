package org.seckill.service.impl;

import com.github.pagehelper.PageInfo;
import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.SeckillService;
import org.seckill.dao.GoodsMapper;
import org.seckill.dao.SeckillMapper;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.*;
import org.seckill.service.common.RedisService;
import org.seckill.service.common.trade.alipay.AlipayRunner;
import org.seckill.service.inner.SeckillExecutor;
import org.seckill.service.mock.strategy.GoodsKillStrategyEnum;
import org.seckill.util.common.util.DateUtil;
import org.seckill.util.common.util.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;


/**
 * 秒杀接口实现类
 *
 * @author heng
 * @date 2016/7/16
 */
@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Slf4j
public class SeckillServiceImpl extends AbstractServiceImpl<SeckillMapper, SeckillExample, Seckill> implements SeckillService {
    @Autowired
    SeckillExecutor seckillExecutor;
    @Autowired
    JmsTemplate jmsTemplate;
    @Reference(version = "1.0.0", check = false)
    SuccessKilledMongoService successKilledMongoService;
    @Autowired
    private AlipayRunner alipayRunner;
    @Autowired
    private ExtSeckillMapper extSeckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public void setAlipayRunner(AlipayRunner alipayRunner) {
        this.alipayRunner = alipayRunner;
    }

    public void setExtSeckillMapper(ExtSeckillMapper extSeckillMapper) {
        this.extSeckillMapper = extSeckillMapper;
    }

    public void setSuccessKilledMapper(SuccessKilledMapper successKilledMapper) {
        this.successKilledMapper = successKilledMapper;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void setGoodsMapper(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public PageInfo getSeckillList(int pageNum, int pageSize) {
        return selectByPage(new SeckillExample(), pageNum, pageSize);
    }

    @Override
    public SeckillInfo getById(long seckillId) {
        Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
        SeckillInfo seckillInfo = new SeckillInfo();
        BeanUtils.copyProperties(seckill, seckillInfo);
        Goods goods = goodsMapper.selectByPrimaryKey(seckill.getGoodsId());
        seckillInfo.setGoodsName(goods.getName());
        return seckillInfo;
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
            int updateCount = extSeckillMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserPhone(userPhone);
                int insertCount = successKilledMapper.insertSelective(successKilled);
                String qrfilepath = alipayRunner.trade_precreate(seckillId);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilledKey key = new SuccessKilledKey();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilledMapper.selectByPrimaryKey(key), qrfilepath);
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
    public int addSeckill(Seckill seckill) {
        return extSeckillMapper.insert(seckill);
    }

    @Override
    public int deleteSeckill(Long seckillId) {
        return extSeckillMapper.deleteByPrimaryKey(seckillId);
    }

    @Override
    public int updateSeckill(Seckill seckill) {
        return extSeckillMapper.updateByPrimaryKeySelective(seckill);
    }

    @Override
    public Seckill selectById(Long seckillId) {
        return extSeckillMapper.selectByPrimaryKey(seckillId);
    }

    @Override
    public int deleteSuccessKillRecord(long seckillId) {
        SuccessKilledExample example = new SuccessKilledExample();
        example.createCriteria().andSeckillIdEqualTo(seckillId);
        return successKilledMapper.deleteByExample(example);
    }

    @Override
    public void execute(SeckillMockRequestDto requestDto, int strategyNumber) {
        GoodsKillStrategyEnum.stateOf(strategyNumber).getGoodsKillStrategy().execute(requestDto);
    }

    /**
     * 获取秒杀成功笔数
     *
     * @param seckillId 秒杀活动id
     * @return
     */
    @Override
    public long getSuccessKillCount(Long seckillId) {
        SuccessKilledExample example = new SuccessKilledExample();
        example.createCriteria().andSeckillIdEqualTo(seckillId);
        long count = successKilledMapper.countByExample(example);
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
        this.updateByPrimaryKeySelective(entity);
        // 清理已成功秒杀记录
        this.deleteSuccessKillRecord(seckillId);
        Seckill seckill = redisService.getSeckill(seckillId);
        redisTemplate.opsForValue().increment(seckillId);
        while (redisTemplate.opsForValue().decrement(seckillId) > 1) {
            redisTemplate.opsForValue().decrement(seckillId);
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


}
