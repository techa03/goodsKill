package org.seckill.api.service;

import org.seckill.entity.UserRole;

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
     * @param userId
     * @return
     */
    List<UserRole> list(Integer userId);
}
