package com.goodskill.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;


}
