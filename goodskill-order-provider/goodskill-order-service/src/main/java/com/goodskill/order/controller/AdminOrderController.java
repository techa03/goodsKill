package com.goodskill.order.controller;

import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.OrderDTO;
import com.goodskill.order.entity.Order;
import com.goodskill.order.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminOrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Long seckillId,
            @RequestParam(required = false) String userPhone,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Page<Order> orderPage = orderService.adminList(page, size, orderId, seckillId, userPhone, userId, status, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", orderPage.getContent());
        result.put("total", orderPage.getTotalElements());
        result.put("size", orderPage.getSize());
        result.put("current", orderPage.getNumber() + 1);
        result.put("pages", orderPage.getTotalPages());
        
        return Result.ok(result);
    }

    @GetMapping("/detail")
    public Order detail(@RequestParam String id) {
        return orderService.findById(id);
    }

    @DeleteMapping("/deleteById")
    public Boolean deleteById(@RequestParam String id) {
        return orderService.deleteById(id);
    }

    @DeleteMapping("/batch")
    public Boolean batchDelete(@RequestBody List<String> ids) {
        return orderService.batchDelete(ids);
    }
}
