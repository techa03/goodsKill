package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.dto.*;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.common.enums.SeckillStatEnum;
import com.goodskill.common.exception.RepeatKillException;
import com.goodskill.common.exception.SeckillCloseException;
import com.goodskill.common.exception.SeckillException;
import com.goodskill.common.util.DateUtil;
import com.goodskill.common.util.MD5Util;
import com.goodskill.entity.Goods;
import com.goodskill.entity.Seckill;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.mp.dao.mapper.SuccessKilledMapper;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.common.trade.alipay.AlipayRunner;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.mock.strategy.GoodsKillStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
@DubboService
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements SeckillService {
    @Resource
    private SuccessKilledMongoService successKilledMongoService;
    @Resource
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private List<GoodsKillStrategy> goodskillStrategies;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private AlipayRunner alipayRunner;
    @Autowired
    private GoodsService goodsService;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Autowired
    private StreamBridge streamBridge;
    @Value("${alipay.qrcodeImagePath:1}")
    private String qrcodeImagePath;

    @Override
    public PageDTO<SeckillVO> getSeckillList(int pageNum, int pageSize, String goodsName) {
        String key = "seckill:list:" + pageNum + ":" + pageSize + ":" + goodsName;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object pageCache = valueOperations.get(key);
        PageDTO<SeckillVO> page;
        if (Objects.isNull(pageCache)) {
            QueryWrapper<Seckill> queryWrapper = new QueryWrapper<>();
            if (StringUtils.isNotBlank(goodsName)) {
                queryWrapper.lambda().like(Seckill::getName, goodsName);
            }
            Page<Seckill> seckillPage = this.page(new Page(pageNum, pageSize), queryWrapper);
            List<SeckillVO> collect = seckillPage.getRecords().stream().map(it -> {
                SeckillVO seckillVO = new SeckillVO();
                BeanUtils.copyProperties(it, seckillVO);
                Goods byId = goodsService.getById(it.getGoodsId());
                if (Objects.nonNull(byId)) {
                    String photoUrl = byId.getPhotoUrl();
                    seckillVO.setPhotoUrl(photoUrl);
                }
                return seckillVO;
            }).collect(Collectors.toList());
            page = new PageDTO<>();
            page.setCurrent(pageNum);
            page.setSize(pageSize);
            page.setTotal(seckillPage.getTotal());
            page.setRecords(collect);
            valueOperations.set(key, page, 5, TimeUnit.MINUTES);
        } else {
            page = (PageDTO<SeckillVO>) pageCache;
        }
        return page;
    }

    @Override
    public ExposerDTO exportSeckillUrl(long seckillId) {
        //从redis中获取缓存秒杀信息
        Seckill seckill = redisService.getSeckill(seckillId);
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new ExposerDTO(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = MD5Util.getMD5(seckillId);
        return new ExposerDTO(true, md5, seckillId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public SeckillExecutionDTO executeSeckill(long seckillId, String userPhone, String md5) {
        if (md5 == null || !md5.equals(MD5Util.getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = DateUtil.getNowTime();
        try {
            int updateCount = baseMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed, seckillId: " + seckillId);
            } else {
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserPhone(userPhone);
                int insertCount = successKilledMapper.insert(successKilled);
                String qrfilepath = alipayRunner.tradePrecreate(seckillId);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated, seckillId: " + seckillId);
                } else {
                    SuccessKilled key = new SuccessKilled();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    return new SeckillExecutionDTO(seckillId, SeckillStatEnum.SUCCESS.getStateInfo(), successKilledMapper.selectOne(new QueryWrapper<>(key)), qrfilepath);
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
    public void execute(SeckillMockRequestDTO requestDto, int strategyNumber) {
        goodskillStrategies.stream()
                .filter(n -> n.getClass().getName().startsWith(Objects.requireNonNull(GoodsKillStrategyEnum.stateOf(strategyNumber)).getClassName()))
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
                count = successKilledMongoService.count(seckillId);
            } catch (Exception e) {
                log.error("mongo服务不可用，请检查！", e);
                throw e;
            }
        }
        return count;
    }

    @Override
    public void prepareSeckill(Long seckillId, int seckillCount, String taskId) {
        // 初始化库存数量
        Seckill entity = new Seckill();
        entity.setSeckillId(seckillId);
        entity.setNumber(seckillCount);
        entity.setStatus(SeckillStatusConstant.IN_PROGRESS);
        this.updateById(entity);
        // 清理已成功秒杀记录
        this.deleteSuccessKillRecord(seckillId);
        redisService.removeSeckill(seckillId);
        Seckill seckill = redisService.getSeckill(seckillId);
        redisTemplate.delete(seckillId);
        seckill.setStatus(SeckillStatusConstant.IN_PROGRESS);
        redisService.putSeckill(seckill);
        redisService.clearSeckillEndFlag(seckillId, taskId);

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
            return update;
        }
    }

    @Override
    public SeckillResponseDTO getQrcode(String fileName) throws IOException {
        SeckillResponseDTO seckillResponseDto = new SeckillResponseDTO();
        if (qrcodeImagePath == null) {
            qrcodeImagePath = System.getProperty("user.dir");
        }
        FileInputStream inputStream = new FileInputStream(qrcodeImagePath + "/" + fileName + ".png");
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
    public SeckillInfoDTO getInfoById(Serializable seckillId) {
        SeckillInfoDTO seckillInfoDTO = new SeckillInfoDTO();
        Seckill seckill = seckillService.getById(seckillId);
        Goods goods = goodsService.getById(seckill.getGoodsId());
        BeanUtils.copyProperties(seckill, seckillInfoDTO);
        seckillInfoDTO.setGoodsName(goods.getName());
        return seckillInfoDTO;
    }

    @Override
    public boolean save(Seckill entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public Seckill getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(Seckill entity) {
        boolean b = super.saveOrUpdate(entity);
        return b;
    }
}
