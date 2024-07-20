package com.goodskill.auth.pojo.vo;

import com.goodskill.auth.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    /**
     * 用户信息
     */
    private User user;
    /**
     * 用户权限列表
     */
    private List<String> permissions;
    /**
     * 用户角色列表
     */
    private List<String> roles;
}
