package com.goodskill.api.dto;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author heng
 */
@Data
public class GoodsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;

    private Integer goodsId;

    private String photoUrl;

    private String name;

    private String price;

    private String introduce;

    private String rawName;

}
