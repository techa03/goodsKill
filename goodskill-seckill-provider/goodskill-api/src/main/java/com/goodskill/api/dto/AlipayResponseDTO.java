package com.goodskill.api.dto;

import lombok.Data;

@Data
public class AlipayResponseDTO {
    private String form;
    private String orderId;
    private String status;
}
