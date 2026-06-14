package com.goodskill.order.repository;

import com.goodskill.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    void deleteBySeckillId(Long seckillId);

    long countBySeckillId(Long seckillId);

    List<Order> findByUserPhone(String userPhone);

    Page<Order> findByUserPhoneOrderByCreateTimeDesc(String userPhone, Pageable pageable);

    Page<Order> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

}
