package com.goodskill.common.core.pojo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 模拟秒杀请求
 * @author techa03
 * @since 2020/12/29
 */
@Data
public class SeckillWebMockRequestDTO {
    /**
     * 秒杀活动id
     */
    @NotNull
    private Long seckillId;

    /**
     * 秒杀商品库存
     */
    @Min(1)
    private int seckillCount;

    /**
     * 秒杀请求数
     */
    @Min(1)
    private int requestCount;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 秒杀处理线程池核心线程数
     */
    private Integer corePoolSize;

    /**
     * 秒杀处理线程池最大线程数
     */
    private Integer maxPoolSize;

    /**
     * 是否开启虚拟线程
     */
    private boolean allowVirtualThread;
}
