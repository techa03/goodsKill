package com.goodskill.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by heng on 2017/3/15.
 */
@Data
public class SeckillInfoDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long seckillId;
    private String name;
    private Integer number;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Integer goodsId;
    private BigDecimal price;
    private String goodsName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}
