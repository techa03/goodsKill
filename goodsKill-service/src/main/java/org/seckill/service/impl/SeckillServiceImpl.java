package org.seckill.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.seckill.common.trade.alipay.AlipayRunner;
import org.seckill.common.util.DateUtil;
import org.seckill.common.util.MD5Util;
import org.seckill.dao.GoodsMapper;
import org.seckill.dao.RedisDao;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillInfo;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * Created by heng on 2016/7/16.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private AlipayRunner alipayRunner;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ExtSeckillMapper extSeckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private GoodsMapper goodsMapper;

    public void setAlipayRunner(AlipayRunner alipayRunner) {
        this.alipayRunner = alipayRunner;
    }

    public void setExtSeckillMapper(ExtSeckillMapper extSeckillMapper) {
        this.extSeckillMapper = extSeckillMapper;
    }

    public void setSuccessKilledMapper(SuccessKilledMapper successKilledMapper) {
        this.successKilledMapper = successKilledMapper;
    }

    public void setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    public void setGoodsMapper(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public PageInfo getSeckillList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seckill> list = extSeckillMapper.selectByExample(null);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public SeckillInfo getById(long seckillId) throws InvocationTargetException, IllegalAccessException {
        Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
        SeckillInfo seckillInfo = new SeckillInfo();
        BeanUtils.copyProperties(seckillInfo, seckill);
        Goods goods = goodsMapper.selectByPrimaryKey(seckill.getGoodsId());
        seckillInfo.setGoodsName(goods.getName());
        return seckillInfo;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //从redis中获取缓存秒杀信息
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
            if (seckill != null) {
                redisDao.putSeckill(seckill);
            } else {
                return new Exposer(false, seckillId);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = MD5Util.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }


    @Transactional
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
                String QRfilePath = alipayRunner.trade_precreate(seckillId);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilledMapper.selectByPrimaryKey(seckillId, userPhone), QRfilePath);
                }
            }
        } catch (SeckillCloseException e1) {
            logger.info(e1.getMessage(), e1);
            throw e1;
        } catch (RepeatKillException e2) {
            logger.info(e2.getMessage(), e2);
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
}
