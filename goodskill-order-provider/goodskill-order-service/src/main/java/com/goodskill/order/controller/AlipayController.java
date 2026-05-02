package com.goodskill.order.controller;

import com.goodskill.core.info.Result;
import com.goodskill.order.api.AlipayService;
import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import com.goodskill.order.enums.OrderStatusEnum;
import com.goodskill.order.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pay/alipay")
@Slf4j
public class AlipayController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/create")
    public Result<AlipayResponseDTO> createPayOrder(@RequestBody AlipayRequestDTO request) {
        try {
            AlipayResponseDTO response = alipayService.createPayOrder(request);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            return Result.fail("创建支付订单失败");
        }
    }

    @PostMapping("/callback")
    public String handleCallback(@RequestParam Map<String, String> params) {
        try {
            log.info("支付宝异步回调参数: {}", params);
            return alipayService.handleCallback(params);
        } catch (Exception e) {
            log.error("处理支付宝回调失败: {}", e.getMessage(), e);
            return "failure";
        }
    }

    @GetMapping("/query/{orderId}")
    public Result<AlipayResponseDTO> queryPayStatus(@PathVariable String orderId) {
        try {
            AlipayResponseDTO response = alipayService.queryPayStatus(orderId);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            return Result.fail("查询支付状态失败");
        }
    }

    @GetMapping("/return")
    public String handleReturn(
            @RequestParam("out_trade_no") String orderId,
            @RequestParam(value = "trade_status", required = false) String tradeStatus,
            @RequestParam("trade_no") String tradeNo,
            @RequestParam("total_amount") String totalAmount,
            @RequestParam Map<String, String> allParams) {
        log.info("支付宝同步回调: orderId={}, tradeStatus={}, tradeNo={}, totalAmount={}, allParams={}",
                orderId, tradeStatus, tradeNo, totalAmount, allParams);

        // 验证签名
        boolean signVerified = alipayService.verifyCallbackSignature(allParams);

        if (signVerified) {
            log.info("支付宝同步回调签名验证成功");
            // 检查交易状态 沙箱环境tradeStatus返回null，实际交易成功
            if ("TRADE_SUCCESS".equals(tradeStatus) || tradeStatus == null) {
                log.info("支付成功: orderId={}", orderId);
                // 更新订单状态为已支付
                OrderStatusEnum paidStatus = OrderStatusEnum.PAID;
                String timestamp = allParams.get("timestamp");
                orderService.updateOrderStatus(orderId, paidStatus.getCode(), paidStatus.getDesc(), tradeNo, timestamp);
            } else {
                log.warn("支付状态异常: orderId={}, tradeStatus={}", orderId, tradeStatus);
            }
        } else {
            log.warn("支付宝同步回调签名验证失败");
        }

        return "<script>window.location.href='http://localhost:5174/order/" + orderId + "';</script>";
    }
}
