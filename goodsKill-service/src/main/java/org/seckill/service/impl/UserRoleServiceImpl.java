package org.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.seckill.api.service.UserRoleService;
import org.seckill.dao.UserRoleMapper;
import org.seckill.entity.UserRole;
import org.seckill.entity.UserRoleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service
@Component
public class UserRoleServiceImpl extends AbstractServiceImpl<UserRoleMapper, UserRoleExample, UserRole> implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
}
