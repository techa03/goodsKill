package org.seckill.web.dto;

import lombok.Data;
import org.seckill.web.constant.ResponseInfo;

import java.io.Serializable;

/**
 * @author heng
 */
@Data
public class ResponseDto<T> implements Serializable {
    private String code = "0";
    private String msg;
    private int count;
    private T[] data;

    /**
     * @return 正常响应返回对象
     */
    public static ResponseDto ok() {
        return getResponseDto(ResponseInfo.SUCCESS);
    }

    public static <T> ResponseDto ok(T[] obj) {
        ResponseDto responseDto = getResponseDto(ResponseInfo.SUCCESS);
        responseDto.setData(obj);
        return responseDto;
    }

    public static ResponseDto fail() {
        return getResponseDto(ResponseInfo.FAIL);
    }

    private static ResponseDto getResponseDto(ResponseInfo responseInfo) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCode(responseInfo.getCode());
        responseDto.setMsg(responseInfo.getMessage());
        return responseDto;
    }
}
