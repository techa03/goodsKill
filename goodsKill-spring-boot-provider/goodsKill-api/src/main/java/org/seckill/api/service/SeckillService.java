package org.seckill.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.entity.Seckill;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
public interface SeckillService extends IService<Seckill> {

    PageInfo getSeckillList(int pageNum, int pageSize);

    SeckillInfo getById(long seckillId);

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
     * @param requestDto 秒杀请求
     * @param strategyNumber        秒杀策略编码
     */
    void execute(SeckillMockRequestDto requestDto, int strategyNumber);


    /**
     * 获取成功秒杀记录数
     *
     * @param seckillId 秒杀活动id
     */
    long getSuccessKillCount(Long seckillId);


    /**
     * 准备秒杀商品数量
     *
     * @param seckillId    秒杀商品id
     * @param seckillCount 秒杀数量
     */
    void prepareSeckill(Long seckillId, int seckillCount);


}
