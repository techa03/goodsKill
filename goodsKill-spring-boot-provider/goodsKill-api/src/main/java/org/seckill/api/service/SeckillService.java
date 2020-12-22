package org.seckill.api.service;

import com.github.pagehelper.PageInfo;
import org.seckill.api.dto.*;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;

/**
 * 秒杀服务
 *
 * @author heng
 * @date 2016/7/16
 */
@FeignClient("goodskill-service-provider")
public interface SeckillService {

    /**
     * 获取秒杀活动列表
     *
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @param goodsName 商品名称，模糊匹配
     * @return
     */
    @GetMapping("/getSeckillList")
    PageInfo getSeckillList(@RequestParam("pageNum") int pageNum,
                            @RequestParam("pageSize") int pageSize,
                            @RequestParam(value = "goodsName", required = false) String goodsName);


    /**
     * 暴露秒杀活动url
     *
     * @param seckillId 秒杀活动id
     * @return 活动信息
     */
    @PostMapping("/exportSeckillUrl")
    Exposer exportSeckillUrl(@RequestParam("seckillId") long seckillId);

    /**
     * 执行秒杀
     *
     * @param seckillId 秒杀活动id
     * @param userPhone 用户手机号
     * @param md5       md5值
     * @return 秒杀执行状态
     */
    @PostMapping("/executeSeckill")
    SeckillExecution executeSeckill(@RequestParam("seckillId") long seckillId,
                                    @RequestParam("userPhone") String userPhone,
                                    @RequestParam("md5") String md5);

    /**
     * 根据秒杀id删除成功记录
     *
     * @param seckillId 秒杀活动id
     * @return 删除数量
     */
    @DeleteMapping("/deleteSuccessKillRecord")
    void deleteSuccessKillRecord(@RequestParam("seckillId") long seckillId);

    /**
     * 执行秒杀，通过同步来控制并发
     *
     * @param requestDto     秒杀请求
     * @param strategyNumber 秒杀策略编码
     */
    @PostMapping("/execute")
    void execute(@RequestBody SeckillMockRequestDto requestDto,
                 @RequestParam("strategyNumber") int strategyNumber);


    /**
     * 获取成功秒杀记录数
     *
     * @param seckillId 秒杀活动id
     */
    @GetMapping("/getSuccessKillCount")
    long getSuccessKillCount(@RequestParam("seckillId") Long seckillId);


    /**
     * 准备秒杀商品数量
     *
     * @param seckillId    秒杀商品id
     * @param seckillCount 秒杀数量
     */
    @PostMapping("/prepareSeckill")
    void prepareSeckill(@RequestParam("seckillId") Long seckillId,
                        @RequestParam("seckillCount") int seckillCount);


    @GetMapping("/getById")
    Seckill getById(@RequestParam("seckillId") Serializable seckillId);

    @PostMapping("/saveOrUpdate")
    boolean saveOrUpdate(@RequestBody Seckill seckill);

    @DeleteMapping("/removeById")
    boolean removeById(@RequestParam("seckillId") Serializable seckillId);

    @PostMapping("/save")
    boolean save(@RequestBody Seckill seckill);

    /**
     * 减商品库存
     *
     * @param successKilled
     * @return 1代表成功，小于1为失败
     */
    @PostMapping("/reduceNumber")
    int reduceNumber(@RequestBody SuccessKilled successKilled);

    @PostMapping("/reduceNumberInner")
    int reduceNumberInner(@RequestBody SuccessKilled successKilled);

    /**
     * 获取二维码
     *
     * @param fileName 二维码图片名称
     * @return SeckillResponseDto
     */
    @GetMapping("/getQrcode")
    SeckillResponseDto getQrcode(@RequestParam("fileName") String fileName) throws IOException;

    /**
     * 根据秒杀id获取秒杀活动信息
     *
     * @param seckillId
     * @return
     */
    @GetMapping("/getInfoById")
    SeckillInfo getInfoById(@RequestParam("seckillId") Serializable seckillId);
}
