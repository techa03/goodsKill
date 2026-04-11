package com.goodskill.order.api;

import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;

import java.util.Map;

public interface AlipayService {
    AlipayResponseDTO createPayOrder(AlipayRequestDTO request);
    String handleCallback(Map<String, String> params);
    boolean verifyCallbackSignature(Map<String, String> params);
    AlipayResponseDTO queryPayStatus(String orderId);
}
