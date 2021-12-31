package com.goodskill.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleDTO implements Serializable {
    private Integer roleId;

    private String roleName;

}