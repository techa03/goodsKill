package com.goodskill.order.repository;

import com.goodskill.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface OrderRepository extends MongoRepository<Order, String> {

    void deleteBySeckillId(BigInteger seckillId);

    long countBySeckillId(BigInteger seckillId);
}
