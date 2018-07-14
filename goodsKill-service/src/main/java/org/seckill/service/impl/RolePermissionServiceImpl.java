package org.seckill.service.impl;

import org.seckill.api.service.RolePermissionService;
import org.seckill.dao.RolePermissionMapper;
import org.seckill.entity.RolePermission;
import org.seckill.entity.RolePermissionExample;
import org.springframework.beans.factory.annotation.Autowired;

public class RolePermissionServiceImpl extends CommonServiceImpl<RolePermissionMapper, RolePermissionExample, RolePermission> implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
}
