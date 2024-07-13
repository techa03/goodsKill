package com.goodskill.service.controller;

import com.goodskill.api.service.GoodsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GoodsControllerTest {
    @Mock
    GoodsService goodsService;
    @InjectMocks
    GoodsController goodsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
