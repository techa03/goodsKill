package com.goodskill.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.goodskill.order.entity.Order;
import com.goodskill.order.entity.OrderDTO;
import com.goodskill.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl {
    @Autowired
    private OrderRepository orderRepository;

    public Boolean deleteRecord(long seckillId) {
        orderRepository.deleteBySeckillId(BigInteger.valueOf(seckillId));
        return true;
    }

    public String saveRecord(OrderDTO orderDTO) {
        Order order = Order.builder()
                .id(IdUtil.getSnowflake().nextIdStr())
                .seckillId(orderDTO.getSeckillId())
                .userPhone(orderDTO.getUserPhone())
                .status(orderDTO.getStatus())
                .createTime(orderDTO.getCreateTime())
                .serverIp(orderDTO.getServerIp())
                .userIp(orderDTO.getUserIp())
                .userId(orderDTO.getUserId())
                .goodsName(orderDTO.getGoodsName())
                .goodsTitle(orderDTO.getGoodsTitle())
                .goodsImg(orderDTO.getGoodsImg())
                .seckillPrice(orderDTO.getSeckillPrice())
                .stateDesc(orderDTO.getStateDesc())
                .build();
        orderRepository.insert(order);
        log.info("保存成功,{}", order);
        return order.getId();
    }

    public Long count(long seckillId) {
        return orderRepository.countBySeckillId(BigInteger.valueOf(seckillId));
    }

    public Page<Order> list(String userPhone, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        if (userPhone != null && !userPhone.isEmpty()) {
            return orderRepository.findByUserPhone(userPhone, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public Order findById(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

}
