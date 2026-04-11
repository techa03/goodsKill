package com.goodskill.order.repository;

import com.goodskill.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    void deleteBySeckillId(BigInteger seckillId);

    long countBySeckillId(BigInteger seckillId);

    List<Order> findByUserPhone(String userPhone);
    
    Page<Order> findByUserPhone(String userPhone, Pageable pageable);
}
