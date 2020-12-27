package org.seckill.api.dto;

import lombok.AllArgsConstructor;
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
public class SeckillMockRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 秒杀活动id
     */
    private long seckillId;

    /**
     * 秒杀请求次数
     */
    private int count;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 请求时间
     */
    private String requestTime;

    public SeckillMockRequestDto(long seckillId, int count, String phoneNumber) {
        this.seckillId = seckillId;
        this.count = count;
        this.phoneNumber = phoneNumber;
    }

}
