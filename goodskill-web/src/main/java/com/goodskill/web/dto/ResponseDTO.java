package com.goodskill.web.dto;

import com.goodskill.web.constant.ResponseInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author heng
 */
@Data
public class ResponseDTO<T> implements Serializable {
    private String code = "0";
    private String msg;
    private int count;
    private T[] data;

    /**
     * @return 正常响应返回对象
     */
    public static ResponseDTO ok() {
        return getResponseDto(ResponseInfo.SUCCESS);
    }

    public static <T> ResponseDTO ok(T[] obj) {
        ResponseDTO responseDto = getResponseDto(ResponseInfo.SUCCESS);
        responseDto.setData(obj);
        return responseDto;
    }

    public static ResponseDTO fail() {
        return getResponseDto(ResponseInfo.FAIL);
    }

    private static ResponseDTO getResponseDto(ResponseInfo responseInfo) {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setCode(responseInfo.getCode());
        responseDto.setMsg(responseInfo.getMessage());
        return responseDto;
    }
}
