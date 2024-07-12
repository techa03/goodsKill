package com.goodskill.service.controller;

import com.goodskill.order.api.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Created by heng on 17/1/18.
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/seckill/goods")
public class GoodsController {
    @Resource
    private OrderService orderService;

    @GetMapping("/orders/count")
    public Long countOrders(@RequestParam("seckillId") long seckillId) {
        return orderService.count(seckillId);
    }

}
