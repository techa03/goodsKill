package com.goodskill.auth.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginTokenVO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessExpireSeconds;
    private long refreshExpireSeconds;
}
