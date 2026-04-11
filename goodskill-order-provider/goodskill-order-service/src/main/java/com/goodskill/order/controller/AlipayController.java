package com.goodskill.order.controller;

import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import com.goodskill.order.api.AlipayService;
import com.goodskill.core.info.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@RestController
@RequestMapping("/pay/alipay")
@Slf4j
public class AlipayController {

    @Autowired
    private AlipayService alipayService;

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
    public String handleCallback(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            return alipayService.handleCallback(requestBody.toString());
        } catch (IOException e) {
            log.error("读取回调请求失败: {}", e.getMessage(), e);
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
    public String handleReturn(HttpServletRequest request) {
        String orderId = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");
        log.info("支付宝同步回调: orderId={}, tradeStatus={}", orderId, tradeStatus);
        return "<script>window.location.href='http://localhost:5174/order/" + orderId + "';</script>";
    }
}
