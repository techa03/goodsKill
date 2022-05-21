package com.goodskill.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 模拟秒杀请求
 * @author techa03
 * @since 2020/12/29
 */
@Data
@ApiModel("模拟秒杀请求")
public class SeckillWebMockRequestDTO {
    @ApiModelProperty(value = "秒杀活动id", example = "1000")
    @NotNull
    private Long seckillId;

    @ApiModelProperty(value = "秒杀商品库存", example = "10")
    @Min(1)
    private int seckillCount;

    @ApiModelProperty(value = "秒杀请求数", example = "20")
    @Min(1)
    private int requestCount;

    @ApiModelProperty(value = "秒杀处理线程池核心线程数", example = "2")
    private Integer corePoolSize;

    @ApiModelProperty(value = "秒杀处理线程池最大线程数", example = "10")
    private Integer maxPoolSize;
}
