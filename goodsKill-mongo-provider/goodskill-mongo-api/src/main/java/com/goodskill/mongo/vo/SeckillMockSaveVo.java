package com.goodskill.mongo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heng
 * @date 2019/7/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillMockSaveVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 秒杀活动id
     */
    private long seckillId;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 备注
     */
    private String note;

}
