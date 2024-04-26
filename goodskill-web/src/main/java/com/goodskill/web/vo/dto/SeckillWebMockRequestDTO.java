package com.goodskill.web.vo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 模拟秒杀请求
 * @author techa03
 * @since 2020/12/29
 */
@Data
@Schema(name = "SeckillWebMockRequestDTO", description = "模拟秒杀请求")
public class SeckillWebMockRequestDTO {
    @Schema(description = "秒杀活动id", example = "1")
    @NotNull
    private Long seckillId;

    @Schema(description = "秒杀商品库存", example = "10")
    @Min(1)
    private int seckillCount;

    @Schema(description = "秒杀请求数", example = "20")
    @Min(1)
    private int requestCount;

    @Schema(description = "秒杀处理线程池核心线程数", example = "2")
    private Integer corePoolSize;

    @Schema(description = "秒杀处理线程池最大线程数", example = "10")
    private Integer maxPoolSize;

    @Schema(description = "是否开启虚拟线程", example = "false")
    private boolean allowVirtualThread;
}
