package com.goodskill.order.controller;

import com.goodskill.core.info.Result;
import com.goodskill.order.api.AlipayService;
import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import com.goodskill.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AlipayController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AlipayControllerTest {

    @InjectMocks
    private AlipayController alipayController;

    @Mock
    private AlipayService alipayService;

    @Mock
    private OrderServiceImpl orderService;

    @Test
    void shouldCreatePayOrderSuccessfully() {
        // Given
        AlipayRequestDTO request = new AlipayRequestDTO();
        request.setOrderId("order-123");
        request.setAmount("100.00");
        request.setSubject("Test Product");
        request.setPayType("pc");

        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId("order-123");
        response.setStatus("SUCCESS");

        when(alipayService.createPayOrder(request)).thenReturn(response);

        // When
        Result<AlipayResponseDTO> result = alipayController.createPayOrder(request);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        assertEquals("order-123", result.getData().getOrderId());
        assertEquals("SUCCESS", result.getData().getStatus());
    }

    @Test
    void shouldReturnFailWhenCreatePayOrderThrowsException() {
        // Given
        AlipayRequestDTO request = new AlipayRequestDTO();
        when(alipayService.createPayOrder(request)).thenThrow(new RuntimeException("Payment error"));

        // When
        Result<AlipayResponseDTO> result = alipayController.createPayOrder(request);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("创建支付订单失败", result.getMsg());
    }

    @Test
    void shouldHandleCallbackSuccessfully() {
        // Given
        Map<String, String> params = new HashMap<>();
        params.put("out_trade_no", "order-123");
        params.put("trade_status", "TRADE_SUCCESS");

        when(alipayService.handleCallback(params)).thenReturn("success");

        // When
        String result = alipayController.handleCallback(params);

        // Then
        assertEquals("success", result);
        verify(alipayService).handleCallback(params);
    }

    @Test
    void shouldReturnFailureWhenCallbackThrowsException() {
        // Given
        Map<String, String> params = new HashMap<>();
        when(alipayService.handleCallback(params)).thenThrow(new RuntimeException("Callback error"));

        // When
        String result = alipayController.handleCallback(params);

        // Then
        assertEquals("failure", result);
    }

    @Test
    void shouldQueryPayStatusSuccessfully() {
        // Given
        String orderId = "order-123";
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(orderId);
        response.setStatus("TRADE_SUCCESS");

        when(alipayService.queryPayStatus(orderId)).thenReturn(response);

        // When
        Result<AlipayResponseDTO> result = alipayController.queryPayStatus(orderId);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals("TRADE_SUCCESS", result.getData().getStatus());
    }

    @Test
    void shouldReturnFailWhenQueryPayStatusThrowsException() {
        // Given
        String orderId = "order-123";
        when(alipayService.queryPayStatus(orderId)).thenThrow(new RuntimeException("Query error"));

        // When
        Result<AlipayResponseDTO> result = alipayController.queryPayStatus(orderId);

        // Then
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("查询支付状态失败", result.getMsg());
    }

    @Test
    void shouldHandleReturnWithTradeSuccess() {
        // Given
        String orderId = "order-123";
        String tradeStatus = "TRADE_SUCCESS";
        String tradeNo = "ali-trade-123";
        String totalAmount = "100.00";
        Map<String, String> allParams = new HashMap<>();
        allParams.put("out_trade_no", orderId);

        when(alipayService.verifyCallbackSignature(allParams)).thenReturn(true);
        when(orderService.updateOrderStatus(eq(orderId), anyByte(), anyString(), eq(tradeNo))).thenReturn(true);

        // When
        String result = alipayController.handleReturn(orderId, tradeStatus, tradeNo, totalAmount, allParams);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(orderId));
        verify(orderService).updateOrderStatus(eq(orderId), anyByte(), anyString(), eq(tradeNo));
    }

    @Test
    void shouldHandleReturnWithNullTradeStatus() {
        // Given
        String orderId = "order-123";
        String tradeNo = "ali-trade-123";
        Map<String, String> allParams = new HashMap<>();

        when(alipayService.verifyCallbackSignature(allParams)).thenReturn(true);
        when(orderService.updateOrderStatus(eq(orderId), anyByte(), anyString(), eq(tradeNo))).thenReturn(true);

        // When
        String result = alipayController.handleReturn(orderId, null, tradeNo, "100.00", allParams);

        // Then
        assertNotNull(result);
        verify(orderService).updateOrderStatus(eq(orderId), anyByte(), anyString(), eq(tradeNo));
    }

    @Test
    void shouldHandleReturnWithFailedSignatureVerification() {
        // Given
        Map<String, String> allParams = new HashMap<>();
        when(alipayService.verifyCallbackSignature(allParams)).thenReturn(false);

        // When
        String result = alipayController.handleReturn("order-123", "TRADE_SUCCESS", "ali-123", "100.00", allParams);

        // Then
        assertNotNull(result);
        verify(orderService, never()).updateOrderStatus(anyString(), anyByte(), anyString(), anyString());
    }

    @Test
    void shouldHandleReturnWithNonSuccessTradeStatus() {
        // Given
        Map<String, String> allParams = new HashMap<>();
        when(alipayService.verifyCallbackSignature(allParams)).thenReturn(true);

        // When
        String result = alipayController.handleReturn("order-123", "TRADE_CLOSED", "ali-123", "100.00", allParams);

        // Then
        assertNotNull(result);
        verify(orderService, never()).updateOrderStatus(anyString(), anyByte(), anyString(), anyString());
    }
}
