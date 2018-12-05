package org.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.seckill.api.service.PermissionService;
import org.seckill.dao.PermissionMapper;
import org.seckill.entity.Permission;
import org.seckill.entity.PermissionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Component
public class PermissionServiceImpl extends AbstractServiceImpl<PermissionMapper, PermissionExample, Permission> implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
}
