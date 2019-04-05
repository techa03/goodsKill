package com.goodskill.mongoreactive.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author techa03
 * @date 2019/4/3
 */
@Data
@Document
@Builder
public class SuccessKilled {
    private BigInteger seckillId;

    private String userPhone;

    private Byte status;

    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;
}
