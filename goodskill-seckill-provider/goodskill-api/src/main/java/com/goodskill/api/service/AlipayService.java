package com.goodskill.api.service;

import com.goodskill.api.dto.AlipayRequestDTO;
import com.goodskill.api.dto.AlipayResponseDTO;

public interface AlipayService {
    AlipayResponseDTO createPayOrder(AlipayRequestDTO request);
    String handleCallback(String requestBody);
    AlipayResponseDTO queryPayStatus(String orderId);
}
