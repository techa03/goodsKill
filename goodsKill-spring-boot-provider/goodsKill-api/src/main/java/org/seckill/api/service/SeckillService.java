package org.seckill.api.service;

import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.entity.Seckill;

import java.io.Serializable;

/**
 * 秒杀服务
 * @author heng
 * @date 2016/7/16
 */
public interface SeckillService {

    /**
     * 获取秒杀活动列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param goodsName 商品名称，模糊匹配
     * @return
     */
    PageInfo getSeckillList(int pageNum, int pageSize, String goodsName);


    /**
     * 暴露秒杀活动url
     * @param seckillId 秒杀活动id
     * @return 活动信息
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀
     *
     * @param seckillId 秒杀活动id
     * @param userPhone 用户手机号
     * @param md5 md5值
     * @return 秒杀执行状态
     */
    SeckillExecution executeSeckill(long seckillId, String userPhone, String md5);

    /**
     * 根据秒杀id删除成功记录
     *
     * @param seckillId 秒杀活动id
     * @return 删除数量
     */
    void deleteSuccessKillRecord(long seckillId);

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


    Seckill getById(Serializable seckillId);

    boolean saveOrUpdate(Seckill seckill);

    boolean removeById(Serializable seckillId);

    boolean save(Seckill seckill);
}
