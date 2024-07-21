package com.goodskill.order.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author techa03
 * @date 2019/4/3
 */
@Data
public class OrderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    private BigInteger seckillId;

    private String userPhone;

    private Byte status;

    private LocalDateTime createTime;

    private String serverIp;

    private String userIp;

    private String userId;

}
