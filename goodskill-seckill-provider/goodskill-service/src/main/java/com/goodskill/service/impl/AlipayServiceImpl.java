package com.goodskill.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.goodskill.api.dto.AlipayRequestDTO;
import com.goodskill.api.dto.AlipayResponseDTO;
import com.goodskill.api.service.AlipayService;
import com.goodskill.service.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private AlipayConfig alipayConfig;

    @Override
    public AlipayResponseDTO createPayOrder(AlipayRequestDTO request) {
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(request.getOrderId());

        try {
            if ("pc".equals(request.getPayType())) {
                // 电脑网站支付
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
                // 手机网站支付
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
    public String handleCallback(String requestBody) {
        try {
            // 解析请求参数
            Map<String, String> params = parseCallbackParams(requestBody);
            
            // 验证签名
            boolean signVerified = com.alipay.api.internal.util.AlipaySignature.rsaCheckV1(
                params, 
                alipayConfig.getPublicKey(), 
                "UTF-8", 
                "RSA2"
            );
            
            if (signVerified) {
                // 处理业务逻辑
                log.info("支付宝回调验证成功: {}", requestBody);
                // 更新订单状态等操作
                return "success";
            } else {
                log.warn("支付宝回调验证失败: {}", requestBody);
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
        } catch (AlipayApiException e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            response.setStatus("UNKNOWN");
        }

        return response;
    }

    private Map<String, String> parseCallbackParams(String requestBody) throws java.io.UnsupportedEncodingException {
        java.util.Map<String, String> params = new java.util.HashMap<>();
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
            }
        }
        return params;
    }
}
