package org.seckill.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionDto implements Serializable {
    private String id;
    private String pId;
    private String name;
}
