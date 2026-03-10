package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.entity.UserRole;
import com.goodskill.auth.mapper.RoleMapper;
import com.goodskill.auth.mapper.UserMapper;
import com.goodskill.auth.mapper.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 补充单元测试
 * 测试用户服务的核心功能
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserMapper baseMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "baseMapper", baseMapper);
        ReflectionTestUtils.setField(userService, "userRoleMapper", userRoleMapper);
        ReflectionTestUtils.setField(userService, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
    }

    /**
     * 测试：创建用户成功
     */
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        when(baseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(baseMapper.insert(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.createUser(user);

        // Then
        assertTrue(result);
        verify(passwordEncoder).encode("password123");
        verify(baseMapper).insert(any(User.class));
    }

    /**
     * 测试：创建用户时用户名已存在
     */
    @Test
    void shouldFailWhenUsernameDuplicate() {
        // Given
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password123");

        User existingUser = new User();
        existingUser.setUsername("existinguser");
        when(baseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(existingUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });
        assertEquals("用户名已存在", exception.getMessage());
        verify(baseMapper, never()).insert(any(User.class));
    }

    /**
     * 测试：更新用户成功
     */
    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        User user = new User();
        user.setId(1);
        user.setUsername("newusername");

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("oldusername");

        when(baseMapper.selectById(1)).thenReturn(existingUser);
        when(baseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(baseMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.updateUser(user);

        // Then
        assertTrue(result);
        verify(baseMapper).updateById(any(User.class));
    }

    /**
     * 测试：更新用户时用户不存在
     */
    @Test
    void shouldFailUpdateWhenUserNotExists() {
        // Given
        User user = new User();
        user.setId(999);
        user.setUsername("testuser");

        when(baseMapper.selectById(999)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(user);
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试：删除用户成功
     */
    @Test
    void shouldDeleteUserSuccessfully() {
        // Given
        when(baseMapper.selectById(1)).thenReturn(new User());
        when(baseMapper.deleteById(1)).thenReturn(1);

        // When
        boolean result = userService.deleteUser(1);

        // Then
        assertTrue(result);
        verify(baseMapper).deleteById(1);
    }

    /**
     * 测试：删除用户时用户不存在
     */
    @Test
    void shouldFailDeleteWhenUserNotExists() {
        // Given
        when(baseMapper.selectById(999)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999);
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试：批量删除用户成功
     */
    @Test
    void shouldBatchDeleteUsersSuccessfully() {
        // Given
        List<Integer> ids = Arrays.asList(1, 2, 3);
        when(baseMapper.deleteBatchIds(ids)).thenReturn(3);

        // When
        boolean result = userService.batchDeleteUsers(ids);

        // Then
        assertTrue(result);
        verify(baseMapper).deleteBatchIds(ids);
    }

    /**
     * 测试：批量删除用户时ID列表为空
     */
    @Test
    void shouldReturnFalseWhenBatchDeleteEmptyList() {
        // When
        boolean result = userService.batchDeleteUsers(Collections.emptyList());

        // Then
        assertFalse(result);
        verify(baseMapper, never()).deleteBatchIds(any());
    }

    /**
     * 测试：密码验证成功
     */
    @Test
    void shouldCheckPasswordSuccessfully() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When
        boolean result = userService.checkPassword(encodedPassword, rawPassword);

        // Then
        assertTrue(result);
    }

    /**
     * 测试：密码验证失败
     */
    @Test
    void shouldFailPasswordCheck() {
        // Given
        String rawPassword = "wrongpassword";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // When
        boolean result = userService.checkPassword(encodedPassword, rawPassword);

        // Then
        assertFalse(result);
    }

    /**
     * 测试：添加角色成功
     */
    @Test
    void shouldAddRoleSuccessfully() {
        // Given
        int userId = 1;
        int roleId = 2;

        when(roleMapper.selectById(roleId)).thenReturn(new Role());
        when(userRoleMapper.insert(any(UserRole.class))).thenReturn(1);

        // When
        boolean result = userService.addRole(userId, roleId);

        // Then
        assertTrue(result);
        verify(userRoleMapper).insert(any(UserRole.class));
    }

    /**
     * 测试：添加角色时角色不存在
     */
    @Test
    void shouldFailAddRoleWhenRoleNotExists() {
        // Given
        int userId = 1;
        int roleId = 999;

        when(roleMapper.selectById(roleId)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addRole(userId, roleId);
        });
        assertEquals("角色id不存在", exception.getMessage());
    }

    /**
     * 测试：根据ID获取用户信息（清除密码）
     */
    @Test
    void shouldGetUserInfoByIdWithoutPassword() {
        // Given
        String userId = "1";
        User user = new User();
        user.setId(1);
        user.setPassword("secretPassword");

        when(baseMapper.selectById(userId)).thenReturn(user);

        // When
        User result = userService.getUserInfoById(userId);

        // Then
        assertNotNull(result);
        assertNull(result.getPassword());
    }

    /**
     * 测试：根据ID获取用户
     */
    @Test
    void shouldGetUserById() {
        // Given
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        user.setPassword("secretPassword");

        when(baseMapper.selectById(userId)).thenReturn(user);

        // When
        User result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertNull(result.getPassword());
    }

    /**
     * 测试：根据ID获取用户不存在
     */
    @Test
    void shouldReturnNullWhenUserNotExists() {
        // Given
        Integer userId = 999;
        when(baseMapper.selectById(userId)).thenReturn(null);

        // When
        User result = userService.getUserById(userId);

        // Then
        assertNull(result);
    }
}
