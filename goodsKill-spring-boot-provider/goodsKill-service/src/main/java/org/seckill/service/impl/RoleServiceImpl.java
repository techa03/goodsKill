package org.seckill.service.impl;

import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.RoleService;
import org.seckill.dao.RoleMapper;
import org.seckill.entity.Role;
import org.seckill.entity.RoleExample;
import org.springframework.beans.factory.annotation.Autowired;

@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class RoleServiceImpl extends AbstractServiceImpl<RoleMapper, RoleExample, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
}
