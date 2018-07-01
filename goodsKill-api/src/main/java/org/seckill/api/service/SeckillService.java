package org.seckill.api.service;

import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.entity.Seckill;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by heng on 2016/7/16.
 */
public interface SeckillService {

    PageInfo getSeckillList(int pageNum, int pageSize);

    SeckillInfo getById(long seckillId) throws InvocationTargetException, IllegalAccessException;

    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, String userPhone, String md5);

    int addSeckill(Seckill seckill);

    int deleteSeckill(Long seckillId);

    int updateSeckill(Seckill seckill);

    Seckill selectById(Long seckillId);
}
