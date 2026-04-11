package com.goodskill.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.goodskill.order.config.AlipayConfig;
import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import com.goodskill.order.api.AlipayService;
import com.goodskill.order.enums.OrderStatusEnum;
import com.goodskill.order.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public AlipayResponseDTO createPayOrder(AlipayRequestDTO request) {
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(request.getOrderId());

        try {
            if ("pc".equals(request.getPayType())) {
                AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
                payRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
                payRequest.setReturnUrl(alipayConfig.getReturnUrl());

                String bizContent = "{" +
                    "\"out_trade_no\":\"" + request.getOrderId() + "\"," +
                    "\"total_amount\":\"" + request.getAmount() + "\"," +
                    "\"subject\":\"" + request.getSubject() + "\"," +
                    "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                    "}";
                payRequest.setBizContent(bizContent);

                String form = alipayClient.pageExecute(payRequest).getBody();
                response.setForm(form);
            } else {
                AlipayTradeWapPayRequest payRequest = new AlipayTradeWapPayRequest();
                payRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
                payRequest.setReturnUrl(alipayConfig.getReturnUrl());

                String bizContent = "{" +
                    "\"out_trade_no\":\"" + request.getOrderId() + "\"," +
                    "\"total_amount\":\"" + request.getAmount() + "\"," +
                    "\"subject\":\"" + request.getSubject() + "\"," +
                    "\"product_code\":\"QUICK_WAP_WAY\"" +
                    "}";
                payRequest.setBizContent(bizContent);

                String form = alipayClient.pageExecute(payRequest).getBody();
                response.setForm(form);
            }
            response.setStatus("SUCCESS");
        } catch (AlipayApiException e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            response.setStatus("FAILED");
        }

        return response;
    }

    @Override
    public String handleCallback(Map<String, String> params) {
        try {
            log.info("支付宝回调参数: {}", params);

            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getPublicKey(),
                "UTF-8",
                "RSA2"
            );

            if (signVerified) {
                log.info("支付宝回调验证成功: {}", params);
                
                // 获取订单号和交易状态
                String orderId = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");
                String tradeNo = params.get("trade_no");
                
                // 如果交易成功，更新订单状态
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    log.info("支付成功: orderId={}, tradeNo={}", orderId, tradeNo);
                    OrderStatusEnum paidStatus = OrderStatusEnum.PAID;
                    orderService.updateOrderStatus(orderId, paidStatus.getCode(), paidStatus.getDesc(), tradeNo);
                }
                
                return "success";
            } else {
                log.warn("支付宝回调验证失败: {}", params);
                return "failure";
            }
        } catch (Exception e) {
            log.error("处理支付宝回调失败: {}", e.getMessage(), e);
            return "failure";
        }
    }

    @Override
    public AlipayResponseDTO queryPayStatus(String orderId) {
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(orderId);

        try {
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            String bizContent = "{" +
                "\"out_trade_no\":\"" + orderId + "\"" +
                "}";
            queryRequest.setBizContent(bizContent);

            AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
            if (queryResponse.isSuccess()) {
                response.setStatus(queryResponse.getTradeStatus());
            } else {
                response.setStatus("UNKNOWN");
            }
            log.info("支付宝查询结果: {}", queryResponse.getBody());
        } catch (AlipayApiException e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            response.setStatus("UNKNOWN");
        }

        return response;
    }

    @Override
    public boolean verifyCallbackSignature(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getPublicKey(),
                "UTF-8",
                "RSA2"
            );
        } catch (Exception e) {
            log.error("验证签名失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
