package com.goodskill.order.dto;

import lombok.Data;

@Data
public class AlipayResponseDTO {
    private String orderId;
    private String status;
    private String form;
}
