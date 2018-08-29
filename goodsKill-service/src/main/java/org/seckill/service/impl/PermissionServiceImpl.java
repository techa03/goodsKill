package org.seckill.service.impl;

import org.seckill.api.service.PermissionService;
import org.seckill.dao.PermissionMapper;
import org.seckill.entity.Permission;
import org.seckill.entity.PermissionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends AbstractServiceImpl<PermissionMapper, PermissionExample, Permission> implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
}
