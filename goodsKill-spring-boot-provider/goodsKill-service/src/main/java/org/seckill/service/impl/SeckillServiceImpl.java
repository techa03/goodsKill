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
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.*;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.common.trade.alipay.AlipayRunner;
import org.seckill.service.mock.strategy.GoodsKillStrategy;
import org.seckill.service.mock.strategy.GoodsKillStrategyEnum;
import org.seckill.util.common.util.DateUtil;
import org.seckill.util.common.util.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.seckill.api.enums.SeckillSolutionEnum.REDIS_MONGO_REACTIVE;

/**
 * <p>
 * 秒杀库存表 服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Slf4j
@RestController
@Service
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements SeckillService {

    @Resource
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
    @Autowired
    private AlipayRunner alipayRunner;
    @Autowired
    private GoodsService goodsService;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Autowired
    private Source source;
    @Value("${alipay.qrcodeImagePath:1}")
    private String qrcodeImagePath;

    @Override
    public PageInfo getSeckillList(int pageNum, int pageSize, String goodsName) {
        String key = "seckill:list:" + pageNum + ":" + pageSize + ":" + goodsName;
        List list = (List) redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isEmpty(list)) {
            PageHelper.startPage(pageNum, pageSize);
            QueryWrapper<Seckill> queryWrapper = new QueryWrapper<>();
            if (StringUtils.isNotBlank(goodsName)) {
                queryWrapper.lambda().like(Seckill::getName, goodsName);
            }
            list = this.list(queryWrapper);
            redisTemplate.opsForValue().set(key, list, 5, TimeUnit.MINUTES);
        }
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
                String qrfilepath = alipayRunner.tradePrecreate(seckillId);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled key = new SuccessKilled();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilledMapper.selectOne(new QueryWrapper<>(key)), qrfilepath);
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
        goodsKillStrategies.stream()
                .filter(n -> GoodsKillStrategyEnum.stateOf(strategyNumber).getClassName().equals(n.getClass().getName()))
                .findFirst().ifPresent(n -> n.execute(requestDto));
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
                SuccessKilledDto successKilledDto = new SuccessKilledDto();
                successKilledDto.setSeckillId(BigInteger.valueOf(seckillId));
                count = successKilledMongoService.count(successKilledDto);
            } catch (Exception e) {
                log.error("mongo服务不可用，请检查！", e);
                throw e;
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
            throw e;
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
        UpdateWrapper<Seckill> updateWrapper = new UpdateWrapper(wrapper);
        updateWrapper.gt("end_time", successKilled.getCreateTime());
        updateWrapper.lt("start_time", successKilled.getCreateTime());
        updateWrapper.gt("number", 0);
        updateWrapper.setSql("number = number - 1");
        int update = baseMapper.update(null, updateWrapper);
        if (update <= 0) {
            throw new SeckillCloseException("seckill is closed");
        } else {
            taskExecutor.execute(() ->
                    source.output().send(MessageBuilder.withPayload(
                            SeckillMockResponseDto
                                    .builder()
                                    .seckillId(successKilled.getSeckillId())
                                    .note(REDIS_MONGO_REACTIVE.getName())
                                    .build())
                            .build())
            );
            log.info("已发送");
            return update;
        }
    }

    @Override
    public SeckillResponseDto getQrcode(String fileName) throws IOException {
        SeckillResponseDto seckillResponseDto = new SeckillResponseDto();
        FileInputStream inputStream = new FileInputStream(new File(qrcodeImagePath + "/" + fileName + ".png"));
        int b;
        // 二维码一般不超过10KB
        byte[] data = new byte[1024 * 10];
        int i = 0;
        while ((b = inputStream.read()) != -1) {
            data[i] = (byte) b;
            i++;
        }
        seckillResponseDto.setData(data);
        return seckillResponseDto;
    }

    @Override
    public SeckillInfo getInfoById(Serializable seckillId) {
        SeckillInfo seckillInfo = new SeckillInfo();
        Seckill seckill = seckillService.getById(seckillId);
        Goods goods = goodsService.getById(seckill.getGoodsId());
        BeanUtils.copyProperties(seckill, seckillInfo);
        seckillInfo.setGoodsName(goods.getName());
        return seckillInfo;
    }
}
