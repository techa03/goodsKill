package com.goodskill.service.impl;

import com.goodskill.api.bo.UserBO;
import com.goodskill.entity.UserAuthAccount;
import com.goodskill.mp.dao.mapper.UserAuthAccountMapper;
import com.goodskill.service.common.UserService;
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
        when(userService.getById(any())).thenReturn(new UserBO());
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