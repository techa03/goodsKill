package com.goodskill.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author heng
 * @date 2019/7/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMockResponseDTO implements Serializable {
    @Serial
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

    /**
     * 秒杀任务id
     */
    private String taskId;

}
