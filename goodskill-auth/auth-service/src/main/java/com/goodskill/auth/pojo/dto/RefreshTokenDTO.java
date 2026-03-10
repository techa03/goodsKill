package com.goodskill.auth.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
