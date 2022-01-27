package com.goodskill.api.service;

import com.goodskill.entity.UserRole;

import java.util.List;

/**
 * @author heng
 */
public interface UserRoleService {
    boolean remove(UserRole entity);

    boolean save(UserRole record);

    List<UserRole> list();

    /**
     * 根据用户id获取角色
     * @param id
     * @return
     */
    List<UserRole> list(Integer id);
}
