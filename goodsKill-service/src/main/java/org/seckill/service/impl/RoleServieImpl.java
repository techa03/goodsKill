package org.seckill.service.impl;

import org.seckill.api.service.RoleServie;
import org.seckill.dao.RoleMapper;
import org.seckill.entity.Role;
import org.seckill.entity.RoleExample;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleServieImpl extends CommonServiceImpl<RoleMapper, RoleExample, Role> implements RoleServie {
    @Autowired
    private RoleMapper roleMapper;
}
