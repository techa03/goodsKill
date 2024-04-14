package com.goodskill.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.goodskill.api.dto.*;
import com.goodskill.api.vo.SeckillVO;
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
@FeignClient(value = "goodskill-seckill", contextId = "seckill")
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
    PageDTO<SeckillVO> getSeckillList(@RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize,
                                      @RequestParam(value = "goodsName", required = false) String goodsName);


    /**
     * 暴露秒杀活动url
     *
     * @param seckillId 秒杀活动id
     * @return 活动信息
     */
    @PostMapping("/exportSeckillUrl")
    ExposerDTO exportSeckillUrl(@RequestParam("seckillId") long seckillId);


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
    void execute(@RequestBody SeckillMockRequestDTO requestDto,
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
     * @param taskId 任务id
     */
    @PostMapping("/prepareSeckill")
    void prepareSeckill(@RequestParam("seckillId") Long seckillId,
                        @RequestParam("seckillCount") int seckillCount, @RequestParam("taskId") String taskId);


    @GetMapping("/getById")
    SeckillVO findById(@RequestParam("seckillId") Serializable seckillId);

    @PostMapping("/saveOrUpdate")
    boolean saveOrUpdateSeckill(@RequestBody SeckillVO seckill);

    @DeleteMapping("/removeById")
    boolean removeBySeckillId(@RequestParam("seckillId") Serializable seckillId);

    @PostMapping("/save")
    boolean save(@RequestBody SeckillVO seckill);

    /**
     * 减商品库存
     *
     * @param successKilled
     * @return 1代表成功，小于1为失败
     */
    @PostMapping("/reduceNumber")
    int reduceNumber(@RequestBody SuccessKilledDTO successKilled);

    @PostMapping("/reduceNumberInner")
    int reduceNumberInner(@RequestBody SuccessKilledDTO successKilled);

    /**
     * 获取二维码
     *
     * @param fileName 二维码图片名称
     * @return SeckillResponseDto
     */
    @GetMapping("/getQrcode")
    SeckillResponseDTO getQrcode(@RequestParam("fileName") String fileName) throws IOException;

    /**
     * 根据秒杀id获取秒杀活动信息
     *
     * @param seckillId
     * @return
     */
    @GetMapping("/getInfoById")
    SeckillInfoDTO getInfoById(@RequestParam("seckillId") Serializable seckillId);


    /**
     * 结束秒杀操作
     *
     * @param seckillId 秒杀ID
     * @return boolean 结束秒杀是否成功
     */
    boolean endSeckill(@RequestParam("seckillId") Long seckillId);

}
