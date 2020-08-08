package org.seckill.api.dto;

import java.io.Serializable;

/**
 *
 * @author techa03
 * @date 2020/8/8
 */
public class SeckillResponseDto implements Serializable {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
