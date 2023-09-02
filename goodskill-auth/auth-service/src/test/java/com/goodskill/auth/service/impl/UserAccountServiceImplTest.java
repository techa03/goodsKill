package com.goodskill.auth.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.entity.RolePermission;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.entity.UserRole;
import com.goodskill.auth.mapper.*;
import com.goodskill.auth.pojo.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private RolePermissionMapper rolePermissionMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private UserMapper baseMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void register() {
        UserDTO user = new UserDTO();
        userService.register(user);
        verify(baseMapper,only()).insert(any());
    }

    @Test
    public void findRoles() {
        when(baseMapper.selectList(any())).thenReturn(Lists.newArrayList(new User()));
        when(userRoleMapper.selectList(any())).thenReturn(Lists.newArrayList(new UserRole()));
        assertNotNull(userService.findRoles("1"));
    }

    @Test
    public void findPermissions() {
        when(baseMapper.selectList(any())).thenReturn(Lists.newArrayList(new User()));
        when(userRoleMapper.selectList(any())).thenReturn(Lists.newArrayList(new UserRole()));
        when(roleMapper.selectById(any())).thenReturn(new Role());
        when(rolePermissionMapper.selectList(any())).thenReturn(Lists.newArrayList(new RolePermission()));
        assertNotNull(userService.findPermissions("1"));
    }

    @Test
    public void findByUserAccount() {
        assertNull(userService.findByUserAccount("1"));
    }
}
