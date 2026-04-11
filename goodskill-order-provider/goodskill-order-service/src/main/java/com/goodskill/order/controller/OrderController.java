package com.goodskill.order.controller;

import com.goodskill.order.entity.Order;
import com.goodskill.order.entity.OrderDTO;
import com.goodskill.order.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @DeleteMapping("/deleteRecord")
    public Boolean deleteRecord(long seckillId) {
        return orderService.deleteRecord(seckillId);
    }

    @PostMapping("/saveRecord")
    public String saveRecord(@RequestBody OrderDTO orderDTO) {
        return orderService.saveRecord(orderDTO);
    }

    @GetMapping("/count")
    public Long count(long seckillId) {
        return orderService.count(seckillId);
    }

    @GetMapping("/list")
    public Page<Order> list(
            @RequestParam(required = false) String userPhone,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.list(userPhone, pageNum, pageSize);
    }

    @GetMapping("/detail/{orderId}")
    public Order detail(@PathVariable String orderId) {
        return orderService.findById(orderId);
    }

}
