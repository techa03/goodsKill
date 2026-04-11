package com.goodskill.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.goodskill.order.entity.Order;
import com.goodskill.order.entity.OrderDTO;
import com.goodskill.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageImpl;

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

    public Page<Order> list(String userId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        if (userId != null) {
            return orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        }
        // 当userId为空时，返回空的分页结果
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    public Order findById(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    public boolean updateOrderStatus(String orderId, Byte status, String stateDesc, String alipayTradeNo) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            order.setStateDesc(stateDesc);
            if (alipayTradeNo != null) {
                order.setAlipayTradeNo(alipayTradeNo);
            }
            orderRepository.save(order);
            log.info("更新订单状态成功: orderId={}, status={}, stateDesc={}, alipayTradeNo={}",
                    orderId, status, stateDesc, alipayTradeNo);
            return true;
        } else {
            log.warn("更新订单状态失败: 订单不存在, orderId={}", orderId);
            return false;
        }
    }
}
