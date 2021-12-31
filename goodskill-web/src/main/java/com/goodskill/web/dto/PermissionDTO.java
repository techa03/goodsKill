package com.goodskill.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionDTO implements Serializable {
    private String id;
    private String pId;
    private String name;
}
