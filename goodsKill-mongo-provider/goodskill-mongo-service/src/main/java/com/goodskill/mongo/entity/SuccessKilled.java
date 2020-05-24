package com.goodskill.mongo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author techa03
 * @date 2020/5/24
 */
@Data
@Builder
@Document
public class SuccessKilled {
    private BigInteger seckillId;

    private String userPhone;

    private Byte status;

    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;
}
