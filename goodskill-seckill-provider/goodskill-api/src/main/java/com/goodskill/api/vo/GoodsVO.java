package com.goodskill.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GoodsVO implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    private Integer goodsId;

    private String photoUrl;

    private String name;

    private String price;

    private Date createTime;

    private String introduce;

}
