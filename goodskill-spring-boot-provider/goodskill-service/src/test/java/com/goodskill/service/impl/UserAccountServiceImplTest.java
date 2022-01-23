package com.goodskill.service.impl;

import com.goodskill.entity.Role;
import com.goodskill.entity.RolePermission;
import com.goodskill.entity.User;
import com.goodskill.entity.UserRole;
import com.goodskill.mp.dao.mapper.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        verify(baseMapper,only()).insert(any());
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
        assertNull(userAccountService.findByUserAccount("1"));
    }
}