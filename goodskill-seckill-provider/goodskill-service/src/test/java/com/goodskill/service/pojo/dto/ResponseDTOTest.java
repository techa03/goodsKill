package com.goodskill.service.pojo.dto;

import com.goodskill.service.common.ResponseInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResponseDTO 单元测试
 */
class ResponseDTOTest {

    @Test
    void shouldCreateOkResponse() {
        // When
        ResponseDTO result = ResponseDTO.ok();

        // Then
        assertNotNull(result);
        assertEquals(ResponseInfo.SUCCESS.getCode(), result.getCode());
        assertEquals(ResponseInfo.SUCCESS.getMessage(), result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateOkResponseWithData() {
        // Given
        String[] data = {"item1", "item2"};

        // When
        ResponseDTO<String> result = ResponseDTO.ok(data);

        // Then
        assertNotNull(result);
        assertEquals(ResponseInfo.SUCCESS.getCode(), result.getCode());
        assertEquals(ResponseInfo.SUCCESS.getMessage(), result.getMsg());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().length);
        assertEquals("item1", result.getData()[0]);
    }

    @Test
    void shouldCreateFailResponse() {
        // When
        ResponseDTO result = ResponseDTO.fail();

        // Then
        assertNotNull(result);
        assertEquals(ResponseInfo.FAIL.getCode(), result.getCode());
        assertEquals(ResponseInfo.FAIL.getMessage(), result.getMsg());
    }

    @Test
    void shouldSetAndGetCount() {
        // Given
        ResponseDTO dto = ResponseDTO.ok();

        // When
        dto.setCount(100);

        // Then
        assertEquals(100, dto.getCount());
    }

    @Test
    void shouldHaveDefaultCodeOfZero() {
        // When
        ResponseDTO dto = new ResponseDTO();

        // Then
        assertEquals("0", dto.getCode());
    }

    @Test
    void shouldReturnOkWithEmptyArray() {
        // Given
        String[] emptyData = {};

        // When
        ResponseDTO<String> result = ResponseDTO.ok(emptyData);

        // Then
        assertNotNull(result.getData());
        assertEquals(0, result.getData().length);
    }

    @Test
    void shouldVerifySuccessResponseInfoValues() {
        assertEquals("0000", ResponseInfo.SUCCESS.getCode());
        assertEquals("响应成功", ResponseInfo.SUCCESS.getMessage());
    }

    @Test
    void shouldVerifyFailResponseInfoValues() {
        assertEquals("9999", ResponseInfo.FAIL.getCode());
        assertEquals("响应失败", ResponseInfo.FAIL.getMessage());
    }

    @Test
    void shouldVerifyInnerFailResponseInfoValues() {
        assertEquals("9001", ResponseInfo.INNER_FAIL.getCode());
        assertEquals("服务器内部错误", ResponseInfo.INNER_FAIL.getMessage());
    }

    @Test
    void shouldVerifyTimeOutResponseInfoValues() {
        assertEquals("0001", ResponseInfo.TIME_OUT.getCode());
        assertEquals("服务器处理超时", ResponseInfo.TIME_OUT.getMessage());
    }
}
