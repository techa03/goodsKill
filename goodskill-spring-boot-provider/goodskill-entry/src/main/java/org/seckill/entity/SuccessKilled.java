package org.seckill.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 秒杀成功明细表
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SuccessKilled implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 秒杀商品
     */
    private Long seckillId;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 状态标示：-1：无效   0：成功   1：已付款
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;


}
