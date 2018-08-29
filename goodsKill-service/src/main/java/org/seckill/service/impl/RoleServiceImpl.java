package org.seckill.service.impl;

import org.seckill.api.service.RoleService;
import org.seckill.dao.RoleMapper;
import org.seckill.entity.Role;
import org.seckill.entity.RoleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends AbstractServiceImpl<RoleMapper, RoleExample, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
}
