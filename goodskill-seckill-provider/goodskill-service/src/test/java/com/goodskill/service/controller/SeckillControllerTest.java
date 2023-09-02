package com.goodskill.service.controller;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.auth.service.PermissionService;
import com.goodskill.auth.service.RolePermissionService;
import com.goodskill.auth.service.UserAccountService;
import com.goodskill.auth.service.UserRoleService;
import com.goodskill.es.api.GoodsEsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

class SeckillControllerTest {
    @Mock
    Logger logger;
    @Mock
    SeckillService seckillService;
    @Mock
    GoodsService goodsService;
    @Mock
    RolePermissionService rolePermissionService;
    @Mock
    UserRoleService userRoleService;
    @Mock
    UserAccountService userAccountService;
    @Mock
    PermissionService permissionService;
    @Mock
    GoodsEsService goodsEsService;
    @InjectMocks
    SeckillController seckillController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testUserPhoneCode() {
//        seckillController.userPhoneCode(Long.valueOf(1));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
