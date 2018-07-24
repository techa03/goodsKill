package org.seckill.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseDto<T> implements Serializable {
    private String code = "0";
    private String msg;
    private int count;
    private T data[];
}
