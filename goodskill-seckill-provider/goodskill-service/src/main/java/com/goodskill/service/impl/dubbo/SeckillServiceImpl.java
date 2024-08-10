package com.goodskill.service.impl.dubbo;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.dto.*;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.core.enums.Events;
import com.goodskill.core.exception.SeckillCloseException;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.core.util.MD5Util;
import com.goodskill.order.api.OrderService;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.common.enums.GoodsKillStrategyEnum;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.handler.PreRequestPipeline;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.util.StateMachineService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

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
    private OrderService orderService;
    @Resource
    private SuccessKilledMapper successKilledMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private List<GoodsKillStrategy> goodskillStrategies;
    @Resource
    private SeckillService seckillService;
    @DubboReference
    private GoodsService goodsService;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Resource
    private StreamBridge streamBridge;
    @Value("${alipay.qrcodeImagePath:1}")
    private String qrcodeImagePath;
    @Resource
    private SeckillMapper baseMapper;
    @Resource
    private StateMachineService stateMachineService;
    @Resource
    private PreRequestPipeline preRequestPipeline;

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
            Page<Seckill> seckillPage = baseMapper.selectPage(new Page(pageNum, pageSize), queryWrapper);
            List<SeckillVO> collect = seckillPage.getRecords().stream().map(it -> {
                SeckillVO seckillVO = new SeckillVO();
                BeanUtils.copyProperties(it, seckillVO);
                GoodsVO byId = goodsService.findById(it.getGoodsId());
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
                count = orderService.count(seckillId);
            } catch (Exception e) {
                log.error("mongo服务不可用，请检查！", e);
                throw e;
            }
        }
        return count;
    }

    @Override
    public void prepareSeckill(Long seckillId, int seckillCount, String taskId) {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(seckillId);
        request.setSeckillCount(seckillCount);
        request.setTaskId(taskId);
        preRequestPipeline.handle(request);
    }

    @Override
    public int reduceNumber(SuccessKilledDTO successKilled) {
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
    public int reduceNumberInner(SuccessKilledDTO successKilled) {
        SuccessKilled entity = BeanUtil.copyProperties(successKilled, SuccessKilled.class);
        successKilledMapper.insert(entity);
        Seckill wrapper = new Seckill();
        wrapper.setSeckillId(entity.getSeckillId());
        UpdateWrapper<Seckill> updateWrapper = new UpdateWrapper(wrapper);
        updateWrapper.gt("end_time", entity.getCreateTime());
        updateWrapper.lt("start_time", entity.getCreateTime());
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
        SeckillVO seckill = seckillService.findById(seckillId);
        GoodsVO goods = goodsService.findById(seckill.getGoodsId());
        BeanUtils.copyProperties(seckill, seckillInfoDTO);
        seckillInfoDTO.setGoodsName(goods.getName());
        return seckillInfoDTO;
    }

    @Override
    public boolean endSeckill(Long seckillId) {
        return stateMachineService.feedMachine(Events.ACTIVITY_END, seckillId);
    }

    @Override
    public boolean removeBySeckillId(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean save(SeckillVO seckill) {
        return baseMapper.insert(BeanUtil.copyProperties(seckill, Seckill.class)) > 0;
    }

    @Override
    public SeckillVO findById(Serializable id) {
        return BeanUtil.copyProperties(super.getById(id), SeckillVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateSeckill(SeckillVO seckill) {
        return saveOrUpdate(BeanUtil.copyProperties(seckill, Seckill.class));
    }

}
