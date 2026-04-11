package com.goodskill.api.dto;

import lombok.Data;

@Data
public class AlipayRequestDTO {
    private String orderId;
    private String amount;
    private String subject;
    private String payType;
}
