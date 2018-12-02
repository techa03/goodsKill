package org.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.seckill.api.service.RolePermissionService;
import org.seckill.dao.RolePermissionMapper;
import org.seckill.entity.RolePermission;
import org.seckill.entity.RolePermissionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service
@Component
public class RolePermissionServiceImpl extends AbstractServiceImpl<RolePermissionMapper, RolePermissionExample, RolePermission> implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
}
