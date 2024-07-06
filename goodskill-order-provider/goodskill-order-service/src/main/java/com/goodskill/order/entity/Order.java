package com.goodskill.order.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

/**
 * 库存实体
 * @author techa03
 * @date 2020/5/24
 */
@Data
@Builder
@Document
public class Order {

    @Id
    private String id;

    /**
     * 秒杀活动id
     */
    private BigInteger seckillId;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 状态
     */
    private Byte status;

    private Date createTime;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 用户ip
     */
    private String userIp;

    /**
     * 用户id
     */
    private String userId;
}
