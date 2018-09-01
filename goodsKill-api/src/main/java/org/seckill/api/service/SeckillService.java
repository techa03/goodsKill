package org.seckill.api.service;

import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.entity.Seckill;
import org.seckill.entity.SeckillExample;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by heng on 2016/7/16.
 */
public interface SeckillService extends CommonService<SeckillExample, Seckill> {

    PageInfo getSeckillList(int pageNum, int pageSize);

    SeckillInfo getById(long seckillId) throws InvocationTargetException, IllegalAccessException;

    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, String userPhone, String md5);

    int addSeckill(Seckill seckill);

    int deleteSeckill(Long seckillId);

    int updateSeckill(Seckill seckill);

    Seckill selectById(Long seckillId);

    /**
     * 根据秒杀id删除成功记录
     *
     * @param seckillId 秒杀活动id
     * @return 删除数量
     */
    int deleteSuccessKillRecord(long seckillId);

    /**
     * 执行秒杀，通过同步来控制并发
     *
     * @param seckillId   秒杀活动id
     * @param executeTime 执行秒杀次数
     */
    void executeWithSynchronized(Long seckillId, int executeTime);

    /**
     * 执行秒杀，通过同步来控制并发
     *
     * @param seckillId   秒杀活动id
     * @param executeTime 执行秒杀次数
     */
    void executeWithRedisson(Long seckillId, int executeTime);

    /**
     * 执行秒杀，通过消息队列控制并发
     *
     * @param seckillId   秒杀活动id
     * @param executeTime 执行秒杀次数
     */
    void executeWithMq(Long seckillId, int executeTime);
}
