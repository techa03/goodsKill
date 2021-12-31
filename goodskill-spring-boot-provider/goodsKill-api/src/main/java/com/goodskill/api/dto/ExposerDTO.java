package com.goodskill.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
@Data
public class ExposerDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean exposed;

    private String md5;

    private long seckillId;

    private long now;

    private long start;

    private long end;

    public ExposerDTO(String md5, boolean exposed, long seckillId) {
        this.md5 = md5;
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public ExposerDTO(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public ExposerDTO(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public ExposerDTO(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }


}
