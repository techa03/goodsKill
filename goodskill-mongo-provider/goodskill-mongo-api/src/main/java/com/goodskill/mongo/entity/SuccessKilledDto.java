package com.goodskill.mongo.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author techa03
 * @date 2019/4/3
 */
@Data
public class SuccessKilledDto implements Serializable {
    private static final long serialVersionUID=1L;

    private BigInteger seckillId;

    private String userPhone;

    private Byte status;

    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;

}
