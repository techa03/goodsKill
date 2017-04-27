package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillInfo;
import org.seckill.entity.Seckill;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by heng on 2016/7/16.
 */
public interface SeckillService {

    List<Seckill> getSeckillList();

    SeckillInfo getById(long seckillId) throws InvocationTargetException, IllegalAccessException;

    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, String userPhone, String md5);

    int addSeckill(Seckill seckill);

    int deleteSeckill(Long seckillId);

    int updateSeckill(Seckill seckill);

    Seckill selectById(Long seckillId);
}
