package com.goodskill.auth.service.impl;

import com.goodskill.auth.entity.Permission;
import com.goodskill.auth.mapper.PermissionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * PermissionServiceImpl 单元测试
 * 测试权限服务的基础功能
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    private PermissionServiceImpl permissionService;

    @Mock
    private PermissionMapper permissionMapper;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionServiceImpl();
        // 通过反射设置baseMapper
        ReflectionTestUtils.setField(permissionService, "baseMapper", permissionMapper);
    }

    /**
     * 测试：保存权限
     */
    @Test
    void shouldSavePermission() {
        // Given
        Permission permission = new Permission();
        permission.setPermissionName("user:create");
        permission.setPermissionCode("user:create");

        when(permissionMapper.insert(any(Permission.class))).thenReturn(1);

        // When
        boolean result = permissionService.save(permission);

        // Then
        assert result;
        verify(permissionMapper).insert(any(Permission.class));
    }

    /**
     * 测试：根据ID获取权限
     */
    @Test
    void shouldGetPermissionById() {
        // Given
        Integer permissionId = 1;
        Permission permission = new Permission();
        permission.setPermissionId(permissionId);
        permission.setPermissionName("user:read");

        when(permissionMapper.selectById(permissionId)).thenReturn(permission);

        // When
        Permission result = permissionService.getById(permissionId);

        // Then
        assertNotNull(result);
        verify(permissionMapper).selectById(permissionId);
    }

    /**
     * 测试：删除权限
     */
    @Test
    void shouldDeletePermissionById() {
        // Given
        Integer permissionId = 1;
        when(permissionMapper.deleteById(permissionId)).thenReturn(1);

        // When
        boolean result = permissionService.removeById(permissionId);

        // Then
        assert result;
        verify(permissionMapper).deleteById(permissionId);
    }

    /**
     * 测试：更新权限
     */
    @Test
    void shouldUpdatePermission() {
        // Given
        Permission permission = new Permission();
        permission.setPermissionId(1);
        permission.setPermissionName("user:update");

        when(permissionMapper.updateById(any(Permission.class))).thenReturn(1);

        // When
        boolean result = permissionService.updateById(permission);

        // Then
        assert result;
        verify(permissionMapper).updateById(any(Permission.class));
    }
}
