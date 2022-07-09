package com.goodskill.web.controller;

import com.goodskill.api.dto.SeckillExecutionDTO;
import com.goodskill.api.service.*;
import com.goodskill.common.info.Result;
import com.goodskill.es.api.GoodsEsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

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
    void testExecute() {
        when(seckillService.executeSeckill(anyLong(), anyString(), anyString())).thenReturn(new SeckillExecutionDTO(0L, "statDesc", null, null));

        Result<SeckillExecutionDTO> result = seckillController.execute(Long.valueOf(1), "md5", "phone");
        Assertions.assertEquals(new Result<SeckillExecutionDTO>("200", true, new SeckillExecutionDTO(0L, "statDesc", null, null), null), result);
    }

    @Test
    void testTime() {
        Result<Long> result = seckillController.time();
        Assertions.assertEquals(new Result<Long>("200", true, Long.valueOf(1), null), result);
    }

    @Test
    void testUserPhoneCode() {
//        seckillController.userPhoneCode(Long.valueOf(1));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme