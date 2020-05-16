package org.seckill.api.service;

import org.seckill.entity.RolePermission;

import java.util.List;

/**
 * @author heng
 */
public interface RolePermissionService {

    boolean remove(int roleId);

    boolean save(RolePermission record);

    List<RolePermission> list();

    List<RolePermission> list(Integer roleId);
}
