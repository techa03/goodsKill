package org.seckill.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleDto implements Serializable {
    private Integer roleId;

    private String roleName;

}