package org.seckill.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Goods {
    // 商品id
    private int goodsId;
    // 商品名称
    private String name;
    private String price;
    private String photoUrl;
    private Date createTime;
    private String introduce;

}
