package org.seckill.service.mp.impl;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Role;
import org.seckill.entity.RolePermission;
import org.seckill.entity.User;
import org.seckill.entity.UserRole;
import org.seckill.mp.dao.mapper.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountServiceImplTest {
    @InjectMocks
    private UserAccountServiceImpl userAccountService;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private RolePermissionMapper rolePermissionMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private UserMapper baseMapper;

    @Test
    public void register() {
        User user = new User();
        userAccountService.register(user);
        assertTrue(true);
    }

    @Test
    public void findRoles() {
        when(baseMapper.selectList(any())).thenReturn(Lists.newArrayList(new User()));
        when(userRoleMapper.selectList(any())).thenReturn(Lists.newArrayList(new UserRole()));
        assertNotNull(userAccountService.findRoles("1"));
    }

    @Test
    public void findPermissions() {
        when(baseMapper.selectList(any())).thenReturn(Lists.newArrayList(new User()));
        when(userRoleMapper.selectList(any())).thenReturn(Lists.newArrayList(new UserRole()));
        when(roleMapper.selectById(any())).thenReturn(new Role());
        when(rolePermissionMapper.selectList(any())).thenReturn(Lists.newArrayList(new RolePermission()));
        assertNotNull(userAccountService.findPermissions("1"));
    }

    @Test
    public void findByUserAccount() {
        assertNotNull(userAccountService.findByUserAccount("1"));
    }
}