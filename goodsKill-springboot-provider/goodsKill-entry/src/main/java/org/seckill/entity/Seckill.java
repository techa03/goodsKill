package org.seckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Seckill implements Serializable {
    private Long seckillId;

    private String name;

    private Integer number;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Integer goodsId;

    private BigDecimal price;

    private String status;

    private String createUser;

    private static final long serialVersionUID = 1L;
}