package com.goodskill.auth.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUserInfoVO {
    private Integer userId;
    private String username;
    private String mobile;
    private String avatar;
    private String emailAddr;
}