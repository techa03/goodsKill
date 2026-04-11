package com.goodskill.service.controller;

import com.goodskill.api.dto.SuccessKilledDTO;
import com.goodskill.api.service.SeckillService;
import com.goodskill.core.info.Result;
import com.goodskill.core.util.MD5Util;
import com.goodskill.order.api.OrderService;
import com.goodskill.order.entity.OrderDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@Slf4j
public class SeckillCustomerController {

    @Resource
    private SeckillService seckillService;

    @Resource
    private OrderService orderService;

    /**
     * 执行秒杀
     *
     * @param seckillId 秒杀活动ID
     * @param userPhone 用户手机号
     * @param md5       秒杀地址MD5值
     * @return 秒杀结果
     */
    @PostMapping("/execute")
    public Result<Map<String, Object>> executeSeckill(
            @RequestParam("seckillId") long seckillId,
            @RequestParam("userPhone") String userPhone,
            @RequestParam("md5") String md5) {
        // 验证MD5值
        if (!MD5Util.getMD5(seckillId).equals(md5)) {
            return Result.fail("秒杀地址已失效");
        }

        // 创建秒杀请求对象
        SuccessKilledDTO successKilledDTO = new SuccessKilledDTO();
        successKilledDTO.setSeckillId(seckillId);
        successKilledDTO.setUserPhone(userPhone);
        successKilledDTO.setCreateTime(new Date());

        // 执行秒杀
        int result = seckillService.reduceNumber(successKilledDTO);
        if (result <= 0) {
            return Result.fail("秒杀失败，库存不足");
        }

        // 生成订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(BigInteger.valueOf(seckillId));
        orderDTO.setUserPhone(userPhone);
        orderDTO.setStatus((byte) 1);
        orderDTO.setCreateTime(LocalDateTime.now());
        boolean saveResult = orderService.saveRecord(orderDTO);
        if (!saveResult) {
            return Result.fail("创建订单失败");
        }

        // 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", seckillId + "-" + userPhone + "-" + System.currentTimeMillis());
        data.put("seckillId", seckillId);
        data.put("userPhone", userPhone);
        data.put("state", 1);
        data.put("createTime", new Date());

        return Result.ok(data);
    }
}
