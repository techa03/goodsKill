package org.seckill.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.entity.Seckill;

/**
 * 秒杀服务
 * @author heng
 * @date 2016/7/16
 */
public interface SeckillService extends IService<Seckill> {

    /**
     * 获取秒杀活动列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return
     */
    PageInfo getSeckillList(int pageNum, int pageSize);

    /**
     * 根据id获取秒杀信息
     *
     * @param seckillId 秒杀活动id
     * @return 秒杀信息
     */
    SeckillInfo getById(long seckillId);

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
     * 增加秒杀活动
     *
     * @param seckill 秒杀活动
     */
    void addSeckill(Seckill seckill);

    /**
     * 删除秒杀活动
     *
     * @param seckillId 秒杀活动id
     */
    void deleteSeckill(Long seckillId);

    /**
     * 更新秒杀信息
     *
     * @param seckill 秒杀信息
     */
    void updateSeckill(Seckill seckill);

    /**
     * 根据id查询
     *
     * @param seckillId 活动id
     * @return 秒杀信息
     */
    Seckill selectById(Long seckillId);

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


}
