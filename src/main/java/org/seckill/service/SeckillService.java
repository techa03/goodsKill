package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;

import java.util.List;

/**
 * Created by heng on 2016/7/16.
 */
public interface SeckillService {

    List<Seckill> getSeckillList();

    Seckill getById(long seckillId);

    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5);

    int addSeckill(Seckill seckill);

    int deleteSeckill(Long seckillId);

    int updateSeckill(Seckill seckill);
}
