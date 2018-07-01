package org.seckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String account;
    private String password;
    private Date createTime;
    private Date updateTime;
    private String locked;
    private String username;

}