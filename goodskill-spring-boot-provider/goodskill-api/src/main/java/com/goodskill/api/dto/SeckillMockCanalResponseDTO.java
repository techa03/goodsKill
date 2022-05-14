package com.goodskill.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * canal binlog监听的秒杀结果通知
 *
 * @author heng
 * @date 2022/5/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMockCanalResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 秒杀活动id
     */
    private long seckillId;

    /**
     * 备注
     */
    private String note;

    /**
     * 秒杀活动状态,true已终止
     */
    private Boolean status;


}
