package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goodskill.entity.RolePermission;
import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import java.util.List;

class RolePermissionServiceImplTest {
    @Mock
    Log log;
    @Mock
    BaseMapper baseMapper;
    //Field entityClass of type Class - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field mapperClass of type Class - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @InjectMocks
    RolePermissionServiceImpl rolePermissionServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRemove() {
        boolean result = rolePermissionServiceImpl.remove(0);
        Assertions.assertEquals(false, result);
    }

    @Test
    void testSave() {
        boolean result = rolePermissionServiceImpl.save(new RolePermission());
        Assertions.assertEquals(false, result);
    }

    @Test
    void testList() {
        List<RolePermission> result = rolePermissionServiceImpl.list();
        Assertions.assertTrue(CollectionUtils.isEmpty(result));
    }

    @Test
    void testList2() {
        List<RolePermission> result = rolePermissionServiceImpl.list(Integer.valueOf(0));
        Assertions.assertTrue(CollectionUtils.isEmpty(result));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme