package com.goodskill.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SuccessKilledDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 秒杀商品
     */
    private Long seckillId;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 状态标示：-1：无效   0：成功   1：已付款
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;
}
