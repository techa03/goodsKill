package org.seckill.service.impl;

import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.UserRoleService;
import org.seckill.dao.UserRoleMapper;
import org.seckill.entity.UserRole;
import org.seckill.entity.UserRoleExample;
import org.springframework.beans.factory.annotation.Autowired;

@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class UserRoleServiceImpl extends AbstractServiceImpl<UserRoleMapper, UserRoleExample, UserRole> implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
}
