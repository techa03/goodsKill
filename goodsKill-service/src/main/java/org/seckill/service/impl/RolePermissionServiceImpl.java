package org.seckill.service.impl;

import org.seckill.api.service.RolePermissionService;
import org.seckill.dao.RolePermissionMapper;
import org.seckill.entity.RolePermission;
import org.seckill.entity.RolePermissionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends AbstractServiceImpl<RolePermissionMapper, RolePermissionExample, RolePermission> implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
}
