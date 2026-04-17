package com.goodskill.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.goodskill.order.config.AlipayConfig;
import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AlipayServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AlipayServiceImplTest {

    @InjectMocks
    private AlipayServiceImpl alipayService;

    @Mock
    private AlipayClient alipayClient;

    @Mock
    private AlipayConfig alipayConfig;

    @Mock
    private OrderServiceImpl orderService;

    @Test
    void shouldCreatePcPayOrderSuccessfully() throws AlipayApiException {
        // Given
        AlipayRequestDTO request = new AlipayRequestDTO();
        request.setOrderId("order-123");
        request.setAmount("100.00");
        request.setSubject("Test Product");
        request.setPayType("pc");

        when(alipayConfig.getNotifyUrl()).thenReturn("http://localhost/notify");
        when(alipayConfig.getReturnUrl()).thenReturn("http://localhost/return");

        AlipayTradePagePayResponse pagePayResponse = new AlipayTradePagePayResponse();
        pagePayResponse.setBody("<form>pay form</form>");
        when(alipayClient.pageExecute(any(AlipayTradePagePayRequest.class))).thenReturn(pagePayResponse);

        // When
        AlipayResponseDTO result = alipayService.createPayOrder(request);

        // Then
        assertNotNull(result);
        assertEquals("order-123", result.getOrderId());
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("<form>pay form</form>", result.getForm());
    }

    @Test
    void shouldCreateWapPayOrderSuccessfully() throws AlipayApiException {
        // Given
        AlipayRequestDTO request = new AlipayRequestDTO();
        request.setOrderId("order-456");
        request.setAmount("50.00");
        request.setSubject("Mobile Product");
        request.setPayType("wap");

        when(alipayConfig.getNotifyUrl()).thenReturn("http://localhost/notify");
        when(alipayConfig.getReturnUrl()).thenReturn("http://localhost/return");

        AlipayTradeWapPayResponse wapPayResponse = new AlipayTradeWapPayResponse();
        wapPayResponse.setBody("<form>wap pay form</form>");
        when(alipayClient.pageExecute(any(AlipayTradeWapPayRequest.class))).thenReturn(wapPayResponse);

        // When
        AlipayResponseDTO result = alipayService.createPayOrder(request);

        // Then
        assertNotNull(result);
        assertEquals("order-456", result.getOrderId());
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void shouldReturnFailedWhenAlipayApiThrowsException() throws AlipayApiException {
        // Given
        AlipayRequestDTO request = new AlipayRequestDTO();
        request.setOrderId("order-789");
        request.setAmount("100.00");
        request.setSubject("Test");
        request.setPayType("pc");

        when(alipayConfig.getNotifyUrl()).thenReturn("http://localhost/notify");
        when(alipayConfig.getReturnUrl()).thenReturn("http://localhost/return");

        when(alipayClient.pageExecute(any(AlipayTradePagePayRequest.class)))
                .thenThrow(new AlipayApiException("API Error"));

        // When
        AlipayResponseDTO result = alipayService.createPayOrder(request);

        // Then
        assertEquals("order-789", result.getOrderId());
        assertEquals("FAILED", result.getStatus());
    }

    @Test
    void shouldQueryPayStatusSuccessfully() throws AlipayApiException {
        // Given
        String orderId = "order-123";
        AlipayTradeQueryResponse queryResponse = mock(AlipayTradeQueryResponse.class);
        when(queryResponse.isSuccess()).thenReturn(true);
        when(queryResponse.getTradeStatus()).thenReturn("TRADE_SUCCESS");
        when(queryResponse.getBody()).thenReturn("{}");
        when(alipayClient.execute(any(AlipayTradeQueryRequest.class))).thenReturn(queryResponse);

        // When
        AlipayResponseDTO result = alipayService.queryPayStatus(orderId);

        // Then
        assertEquals(orderId, result.getOrderId());
        assertEquals("TRADE_SUCCESS", result.getStatus());
    }

    @Test
    void shouldReturnUnknownWhenQueryFails() throws AlipayApiException {
        // Given
        String orderId = "order-123";
        AlipayTradeQueryResponse queryResponse = mock(AlipayTradeQueryResponse.class);
        when(queryResponse.isSuccess()).thenReturn(false);
        when(queryResponse.getBody()).thenReturn("{}");
        when(alipayClient.execute(any(AlipayTradeQueryRequest.class))).thenReturn(queryResponse);

        // When
        AlipayResponseDTO result = alipayService.queryPayStatus(orderId);

        // Then
        assertEquals("UNKNOWN", result.getStatus());
    }

    @Test
    void shouldReturnUnknownWhenQueryThrowsException() throws AlipayApiException {
        // Given
        String orderId = "order-123";
        when(alipayClient.execute(any(AlipayTradeQueryRequest.class)))
                .thenThrow(new AlipayApiException("Query Error"));

        // When
        AlipayResponseDTO result = alipayService.queryPayStatus(orderId);

        // Then
        assertEquals("UNKNOWN", result.getStatus());
    }
}
