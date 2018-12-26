package org.seckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SuccessKilled extends SuccessKilledKey implements Serializable {
    private Byte status;

    private Date createTime;

    private String serverIp;

    private String userIp;

    private String userId;

    private static final long serialVersionUID = 1L;

}