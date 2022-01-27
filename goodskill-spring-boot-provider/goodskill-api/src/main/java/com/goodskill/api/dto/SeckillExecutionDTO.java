package com.goodskill.api.dto;

import com.goodskill.entity.SuccessKilled;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
@Data
public class SeckillExecutionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private long seckillId;

    private String statDesc;

    private int state;

    private String stateInfo;

    private SuccessKilled seccessKilled;

    private String qrfilepath;

    public SeckillExecutionDTO(long seckillId, String statDesc, SuccessKilled seccessKilled, String qrfilepath) {
        this.seckillId = seckillId;
        this.statDesc = statDesc;
        this.seccessKilled = seccessKilled;
        this.qrfilepath = qrfilepath;
    }

    public SeckillExecutionDTO(long seckillId, String statDesc) {
        this.seckillId = seckillId;
        this.statDesc = statDesc;
    }

}
