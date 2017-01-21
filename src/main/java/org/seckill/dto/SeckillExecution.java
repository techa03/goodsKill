package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

/**
 * Created by heng on 2016/7/16.
 */
public class SeckillExecution {
    private long seckillId;

    private SeckillStatEnum statEnum;

    private int state;

    private String stateInfo;

    private SuccessKilled seccessKilled;

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled seccessKilled) {
        this.seckillId = seckillId;
        this.statEnum = statEnum;
        this.seccessKilled = seccessKilled;
    }

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.statEnum = statEnum;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSeccessKilled() {
        return seccessKilled;
    }

    public void setSeccessKilled(SuccessKilled seccessKilled) {
        this.seccessKilled = seccessKilled;
    }

    public SeckillStatEnum getStatEnum() {
        return statEnum;
    }

    public void setStatEnum(SeckillStatEnum statEnum) {
        this.statEnum = statEnum;
    }
}
