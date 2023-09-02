package com.goodskill.auth.service.impl;

import com.goodskill.auth.entity.User;
import com.goodskill.auth.entity.UserAuthAccount;
import com.goodskill.auth.mapper.UserAuthAccountMapper;
import com.goodskill.auth.service.UserService;
import com.goodskill.common.core.pojo.bo.UserBO;
import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class UserAuthAccountServiceImplTest {
    @Mock
    UserService userService;
    @Mock
    Log log;
    @Mock
    UserAuthAccountMapper baseMapper;
    @InjectMocks
    UserAuthAccountServiceImpl userAuthAccountServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByThirdAccount() {
        when(baseMapper.selectOne(any())).thenReturn(new UserAuthAccount());
        when(userService.getById(any())).thenReturn(new User());
        UserBO result = userAuthAccountServiceImpl.findByThirdAccount("account", "sourceType");
        Assertions.assertEquals(new UserBO(), result);
    }

    @Test
    void testIfThirdAccountExists() {
        Boolean result = userAuthAccountServiceImpl.ifThirdAccountExists("account", "sourceType");
        Assertions.assertEquals(Boolean.FALSE, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
